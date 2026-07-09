package com.meshapi.sdk.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.resilience.FallbackConfig;
import com.meshapi.sdk.resilience.FallbackEvent;
import com.meshapi.sdk.resilience.GatewayRoutingEvent;
import com.meshapi.sdk.resilience.ResilienceEvent;
import com.meshapi.sdk.resilience.ResilienceEvents;
import com.meshapi.sdk.resilience.RetryEvent;
import com.meshapi.sdk.resilience.RetryPolicy;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for resilience: configurable transport retry, the chat
 * client-side model-fallback chain, and observability events (retry /
 * fallback / gateway-routing). Mirrors the Node SDK's resilience.test.ts
 * contract. Zero backoff everywhere so tests don't sleep.
 */
class ResilienceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String OK_CHAT_BODY = """
            {"id":"chatcmpl-1","object":"chat.completion","created":0,
             "model":"openai/gpt-4o-mini",
             "choices":[{"index":0,"message":{"role":"assistant","content":"hi"},"finish_reason":"stop"}],
             "usage":{"prompt_tokens":1,"completion_tokens":1,"total_tokens":2}}""";

    // ── Test doubles ─────────────────────────────────────────────────────────

    /** A canned HTTP response for the fake transport. */
    record Canned(int status, String body, Map<String, String> headers) {}

    static Canned ok(String body) { return new Canned(200, body, Map.of()); }

    static Canned ok(String body, Map<String, String> headers) { return new Canned(200, body, headers); }

    static Canned error(int status) { return error(status, "provider_not_available", "req_err"); }

    static Canned error(int status, String code, String requestId) {
        return new Canned(status,
                "{\"error\":{\"code\":\"" + code + "\",\"message\":\"boom\"},\"request_id\":\"" + requestId + "\"}",
                Map.of());
    }

    /** Records one request the fake transport received. */
    record Recorded(String method, String url, String body) {}

    /**
     * A {@link java.net.http.HttpClient} fed by a queue of canned responses /
     * exceptions; records every request (URL + parsed body). The Java analogue
     * of the Node tests' fetch-queue mock, injected via
     * {@code MeshAPI.builder().httpClient(...)}.
     */
    static final class FakeHttp extends java.net.http.HttpClient {
        final Deque<Object> queue = new ArrayDeque<>();
        final List<Recorded> calls = new ArrayList<>();

        FakeHttp enqueue(Object... items) {
            for (Object item : items) queue.add(item);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler)
                throws IOException, InterruptedException {
            calls.add(new Recorded(request.method(), request.uri().toString(), bodyOf(request)));
            Object next = queue.poll();
            if (next == null) {
                throw new AssertionError("fake HTTP queue exhausted");
            }
            if (next instanceof IOException e) throw e;
            if (next instanceof InterruptedException e) throw e;
            if (next instanceof RuntimeException e) throw e;
            Canned canned = (Canned) next;
            Map<String, List<String>> headerMap = new HashMap<>();
            headerMap.put("content-type", List.of("application/json"));
            canned.headers().forEach((k, v) -> headerMap.put(k, List.of(v)));
            HttpHeaders headers = HttpHeaders.of(headerMap, (a, b) -> true);
            return (HttpResponse<T>) new HttpResponse<String>() {
                @Override public int statusCode() { return canned.status(); }
                @Override public HttpRequest request() { return request; }
                @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
                @Override public HttpHeaders headers() { return headers; }
                @Override public String body() { return canned.body(); }
                @Override public Optional<javax.net.ssl.SSLSession> sslSession() { return Optional.empty(); }
                @Override public URI uri() { return request.uri(); }
                @Override public Version version() { return Version.HTTP_1_1; }
            };
        }

        private static String bodyOf(HttpRequest request) {
            return request.bodyPublisher().map(publisher -> {
                HttpResponse.BodySubscriber<String> subscriber =
                        HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
                publisher.subscribe(new Flow.Subscriber<>() {
                    @Override public void onSubscribe(Flow.Subscription s) { subscriber.onSubscribe(s); }
                    @Override public void onNext(ByteBuffer item) { subscriber.onNext(List.of(item)); }
                    @Override public void onError(Throwable t) { subscriber.onError(t); }
                    @Override public void onComplete() { subscriber.onComplete(); }
                });
                return subscriber.getBody().toCompletableFuture().join();
            }).orElse("");
        }

        // Unused abstract members.
        @Override public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
        @Override public Optional<Duration> connectTimeout() { return Optional.empty(); }
        @Override public Redirect followRedirects() { return Redirect.NEVER; }
        @Override public Optional<ProxySelector> proxy() { return Optional.empty(); }
        @Override public javax.net.ssl.SSLContext sslContext() { return null; }
        @Override public javax.net.ssl.SSLParameters sslParameters() { return new javax.net.ssl.SSLParameters(); }
        @Override public Optional<Authenticator> authenticator() { return Optional.empty(); }
        @Override public Version version() { return Version.HTTP_1_1; }
        @Override public Optional<Executor> executor() { return Optional.empty(); }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request, HttpResponse.BodyHandler<T> handler) {
            throw new UnsupportedOperationException();
        }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request, HttpResponse.BodyHandler<T> handler,
                HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            throw new UnsupportedOperationException();
        }
    }

    /** Zero-backoff retry policy builder pre-seeded like the Node tests' makeClient. */
    private static RetryPolicy.Builder zeroBackoffRetry(int maxRetries) {
        return RetryPolicy.builder().maxRetries(maxRetries).backoffBaseMs(0).backoffMaxMs(0);
    }

    private static MeshAPI.Builder clientBuilder(FakeHttp http, List<ResilienceEvent> events) {
        return MeshAPI.builder()
                .baseUrl("https://gw.test")
                .token("rsk_test")
                .httpClient(http)
                .logger(events::add)
                .retry(zeroBackoffRetry(2).build());
    }

    private static ChatCompletionRequest chatParams() {
        return ChatCompletionRequest.builder()
                .model("openai/gpt-4o-mini")
                .addMessage(ChatMessage.user("hello"))
                .build();
    }

    private static JsonNode bodyJson(Recorded call) throws Exception {
        return MAPPER.readTree(call.body());
    }

    // ── RetryPolicy.resolve ──────────────────────────────────────────────────

    @Test
    void resolveRetryPolicy_appliesDefaults() {
        RetryPolicy p = RetryPolicy.resolve(null, null);
        assertEquals(3, p.maxRetries());
        assertEquals(java.util.Set.of(429, 502, 503, 504), p.retryOnStatus());
        assertEquals(500, p.backoffBaseMs());
        assertEquals(30_000, p.backoffMaxMs());
        assertTrue(p.respectRetryAfter());
        assertFalse(p.retryOnNetworkError());
    }

    @Test
    void resolveRetryPolicy_policyMaxRetriesWinsOverDeprecatedAlias() {
        assertEquals(5, RetryPolicy.resolve(RetryPolicy.builder().maxRetries(5).build(), 1).maxRetries());
        assertEquals(1, RetryPolicy.resolve(null, 1).maxRetries());
    }

    // ── Transport retry ──────────────────────────────────────────────────────

    @Test
    void retriesA503ThenSucceeds_emittingARetryEvent() {
        FakeHttp http = new FakeHttp().enqueue(error(503), ok(OK_CHAT_BODY));
        List<ResilienceEvent> events = new ArrayList<>();
        MeshAPI client = clientBuilder(http, events).build();

        ChatCompletionResponse res = client.chat().completions().create(chatParams());

        assertEquals("hi", res.choices.get(0).message.content);
        assertEquals(2, http.calls.size());
        RetryEvent retry = events.stream()
                .filter(e -> e instanceof RetryEvent).map(e -> (RetryEvent) e)
                .findFirst().orElseThrow();
        assertEquals(503, retry.status);
        assertEquals(1, retry.attempt);
        assertEquals("status", retry.reason);
        assertNull(retry.requestId); // no x-request-id header on the mock
    }

    @Test
    void honoursACustomRetryOnStatusSet() {
        // 500 is not retryable by default; opt in explicitly.
        FakeHttp http = new FakeHttp().enqueue(error(500), ok(OK_CHAT_BODY));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(2).retryOnStatus(500).build())
                .build();

        client.chat().completions().create(chatParams());
        assertEquals(2, http.calls.size());
    }

    @Test
    void givesUpAfterMaxRetriesAndThrowsTheApiError() {
        FakeHttp http = new FakeHttp().enqueue(error(503), error(503), error(503));
        List<ResilienceEvent> events = new ArrayList<>();
        MeshAPI client = clientBuilder(http, events).build();

        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> client.chat().completions().create(chatParams()));
        assertEquals(503, err.getStatus());
        assertEquals(3, http.calls.size()); // 1 initial + 2 retries
        assertEquals(2, events.stream().filter(e -> e instanceof RetryEvent).count());
    }

    /**
     * THE BEHAVIOUR CHANGE: the Java SDK used to retry every exception,
     * including network errors. Like Node/Python/Go, network-error retry is
     * now OPT-IN via {@code RetryPolicy.retryOnNetworkError} (default false).
     */
    @Test
    void doesNotRetryNetworkErrorsByDefault() {
        FakeHttp http = new FakeHttp().enqueue(new IOException("connection refused"));
        MeshAPI client = clientBuilder(http, new ArrayList<>()).build();

        RuntimeException err = assertThrows(RuntimeException.class,
                () -> client.chat().completions().create(chatParams()));
        assertInstanceOf(IOException.class, rootNetworkCause(err));
        assertEquals(1, http.calls.size());
    }

    @Test
    void retriesNetworkErrorsWhenRetryOnNetworkErrorIsSet() {
        FakeHttp http = new FakeHttp().enqueue(new IOException("connection refused"), ok(OK_CHAT_BODY));
        List<ResilienceEvent> events = new ArrayList<>();
        MeshAPI client = clientBuilder(http, events)
                .retry(zeroBackoffRetry(2).retryOnNetworkError(true).build())
                .build();

        client.chat().completions().create(chatParams());
        assertEquals(2, http.calls.size());
        RetryEvent retry = events.stream()
                .filter(e -> e instanceof RetryEvent).map(e -> (RetryEvent) e)
                .findFirst().orElseThrow();
        assertEquals("network-error", retry.reason);
        assertNull(retry.status);
    }

    @Test
    void neverRetriesATimeout_evenWithRetryOnNetworkError() {
        FakeHttp http = new FakeHttp().enqueue(new HttpTimeoutException("request timed out"));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(3).retryOnNetworkError(true).build())
                .build();

        RuntimeException err = assertThrows(RuntimeException.class,
                () -> client.chat().completions().create(chatParams()));
        assertInstanceOf(HttpTimeoutException.class, rootNetworkCause(err));
        assertEquals(1, http.calls.size());
    }

    @Test
    void neverRetriesAnInterrupt_evenWithRetryOnNetworkError() {
        FakeHttp http = new FakeHttp().enqueue(new InterruptedException("interrupted"));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(3).retryOnNetworkError(true).build())
                .build();

        RuntimeException err = assertThrows(RuntimeException.class,
                () -> client.chat().completions().create(chatParams()));
        assertInstanceOf(InterruptedException.class, rootNetworkCause(err));
        assertEquals(1, http.calls.size());
        assertTrue(Thread.interrupted(), "interrupt flag should be restored (and is cleared here)");
    }

    /** Walk the cause chain to the underlying transport failure. */
    private static Throwable rootNetworkCause(Throwable err) {
        Throwable t = err;
        while (t.getCause() != null) t = t.getCause();
        return t;
    }

    // ── Chat model-fallback chain ────────────────────────────────────────────

    @Test
    void advancesToTheNextModelAfterRetriesExhaust_emittingAFallbackEvent() throws Exception {
        FakeHttp http = new FakeHttp().enqueue(
                error(503),          // primary attempt 1
                error(503),          // primary retry 1
                ok(OK_CHAT_BODY));   // fallback model
        List<ResilienceEvent> events = new ArrayList<>();
        MeshAPI client = clientBuilder(http, events)
                .retry(zeroBackoffRetry(1).build())
                .fallback(FallbackConfig.builder().models("anthropic/claude-sonnet-5").build())
                .build();

        ChatCompletionResponse res = client.chat().completions().create(chatParams());

        assertEquals("hi", res.choices.get(0).message.content);
        assertEquals(3, http.calls.size());
        assertEquals("anthropic/claude-sonnet-5", bodyJson(http.calls.get(2)).path("model").asText());
        FallbackEvent fb = events.stream()
                .filter(e -> e instanceof FallbackEvent).map(e -> (FallbackEvent) e)
                .findFirst().orElseThrow();
        assertEquals("openai/gpt-4o-mini", fb.fromModel);
        assertEquals("anthropic/claude-sonnet-5", fb.toModel);
        assertEquals(503, fb.status);
        assertEquals("provider_not_available", fb.errorCode);
    }

    @Test
    void perCallFallbackModelsOverridesTheClientConfig_andIsNeverSentToTheServer() throws Exception {
        FakeHttp http = new FakeHttp().enqueue(error(502), ok(OK_CHAT_BODY));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(0).build())
                .fallback(FallbackConfig.builder().models("ignored/config-model").build())
                .build();

        client.chat().completions().create(ChatCompletionRequest.builder()
                .model("openai/gpt-4o-mini")
                .addMessage(ChatMessage.user("hello"))
                .fallbackModels("mistral/mistral-large")
                .build());

        assertEquals("mistral/mistral-large", bodyJson(http.calls.get(1)).path("model").asText());
        for (Recorded call : http.calls) {
            JsonNode body = bodyJson(call);
            assertFalse(body.has("fallbackModels"), "fallbackModels leaked to the wire");
            assertFalse(body.has("fallback_models"), "fallback_models leaked to the wire");
        }
    }

    @Test
    void terminalErrors401_neverAdvanceTheChain() {
        FakeHttp http = new FakeHttp().enqueue(error(401, "unauthorized", "req_err"));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(0).build())
                .fallback(FallbackConfig.builder().models("anthropic/claude-sonnet-5").build())
                .build();

        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> client.chat().completions().create(chatParams()));
        assertEquals(401, err.getStatus());
        assertEquals(1, http.calls.size());
    }

    @Test
    void exhaustingTheWholeChainThrowsTheLastError() {
        FakeHttp http = new FakeHttp().enqueue(
                error(503), error(503), error(504, "gateway_timeout", "req_last"));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(0).build())
                .fallback(FallbackConfig.builder().models("m/a", "m/b").build())
                .build();

        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> client.chat().completions().create(chatParams()));
        assertEquals(504, err.getStatus());
        assertEquals("req_last", err.getRequestId());
        assertEquals(3, http.calls.size());
    }

    @Test
    void skipsThePrimaryModelWhenItAlsoAppearsInTheChain() throws Exception {
        FakeHttp http = new FakeHttp().enqueue(error(503), ok(OK_CHAT_BODY));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(0).build())
                .fallback(FallbackConfig.builder().models("openai/gpt-4o-mini", "m/b").build())
                .build();

        client.chat().completions().create(chatParams());
        assertEquals(2, http.calls.size());
        assertEquals("m/b", bodyJson(http.calls.get(1)).path("model").asText());
    }

    @Test
    void customFallbackOnStatusControlsEligibility() {
        // 429 not in the default fallback set — opt in.
        FakeHttp http = new FakeHttp().enqueue(
                error(429, "rate_limit_exceeded", "req_err"), ok(OK_CHAT_BODY));
        MeshAPI client = clientBuilder(http, new ArrayList<>())
                .retry(zeroBackoffRetry(0).build())
                .fallback(FallbackConfig.builder().models("m/b").onStatus(429).build())
                .build();

        client.chat().completions().create(chatParams());
        assertEquals(2, http.calls.size());
    }

    // ── Gateway routing observability ────────────────────────────────────────

    @Test
    void parsesXMeshRoutingHeadersIntoAGatewayRoutingEvent() {
        // The gateway does not send a served-provider header; even if one sneaks
        // through, the event carries no provider name (the field doesn't exist).
        FakeHttp http = new FakeHttp().enqueue(ok(OK_CHAT_BODY, Map.of(
                "x-mesh-routing-attempts", "2",
                "x-mesh-routing-fallback", "true",
                "x-mesh-served-provider", "bedrock",
                "x-request-id", "req_routed")));
        List<ResilienceEvent> events = new ArrayList<>();
        MeshAPI client = clientBuilder(http, events).build();

        client.chat().completions().create(chatParams());

        GatewayRoutingEvent gw = events.stream()
                .filter(e -> e instanceof GatewayRoutingEvent).map(e -> (GatewayRoutingEvent) e)
                .findFirst().orElseThrow();
        assertEquals(2, gw.attempts);
        assertTrue(gw.fallback);
        assertEquals("req_routed", gw.requestId);
        assertEquals("/v1/chat/completions", gw.path);
        // The provider name never appears anywhere on the event.
        assertFalse(gw.toString().contains("bedrock"));
    }

    @Test
    void emitsNothingWhenTheHeadersAreAbsent() {
        FakeHttp http = new FakeHttp().enqueue(ok(OK_CHAT_BODY));
        List<ResilienceEvent> events = new ArrayList<>();
        MeshAPI client = clientBuilder(http, events).build();

        client.chat().completions().create(chatParams());
        assertEquals(0, events.stream().filter(e -> e instanceof GatewayRoutingEvent).count());
    }

    // ── Debug formatting ─────────────────────────────────────────────────────

    @Test
    void formatsARetryLine() {
        String line = ResilienceEvents.format(new RetryEvent(
                "POST", "/v1/chat/completions", 1, 3, 503, "req_1", 512.4, "status"));
        assertEquals(
                "retrying POST /v1/chat/completions (attempt 1/4 failed: 503, next in 512ms) [req_1]",
                line);
    }

    @Test
    void formatsAFallbackLine() {
        String line = ResilienceEvents.format(new FallbackEvent(
                "openai/gpt-4o", "anthropic/claude-sonnet-5", 0, 2,
                503, "provider_not_available", null));
        assertEquals(
                "falling back openai/gpt-4o → anthropic/claude-sonnet-5 (1/2: 503 provider_not_available)",
                line);
    }

    @Test
    void formatsAGatewayRoutingLine() {
        String line = ResilienceEvents.format(new GatewayRoutingEvent(
                "/v1/chat/completions", 2, true, "req_2"));
        assertEquals(
                "gateway served /v1/chat/completions (2 attempts, provider fallback) [req_2]",
                line);
    }
}
