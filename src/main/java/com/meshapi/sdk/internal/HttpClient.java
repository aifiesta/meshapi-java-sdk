package com.meshapi.sdk.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.resilience.FallbackConfig;
import com.meshapi.sdk.resilience.GatewayRoutingEvent;
import com.meshapi.sdk.resilience.ResilienceEvent;
import com.meshapi.sdk.resilience.ResilienceEvents;
import com.meshapi.sdk.resilience.RetryEvent;
import com.meshapi.sdk.resilience.RetryPolicy;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Internal HTTP client wrapping {@link java.net.http.HttpClient} with
 * retry/backoff, auth headers, and JSON helpers.
 */
public class HttpClient {

    private static final String SDK_VERSION_HEADER = "X-MeshAPI-SDK";
    private static final String SDK_VERSION_VALUE = "java/" + MeshAPI.VERSION;

    // Gateway routing-outcome headers (FT-244) — present when the API key's
    // routing_policy is active. See GatewayRoutingEvent.
    private static final String ROUTING_ATTEMPTS_HEADER = "x-mesh-routing-attempts";
    private static final String ROUTING_FALLBACK_HEADER = "x-mesh-routing-fallback";
    private static final String REQUEST_ID_HEADER = "x-request-id";

    private final java.net.http.HttpClient javaClient;
    private final ObjectMapper mapper;
    private final String baseUrl;
    private final String token;
    private final RetryPolicy retry;
    private final FallbackConfig fallback;
    private final Consumer<ResilienceEvent> logger;
    private final boolean debug;

    public HttpClient(java.net.http.HttpClient javaClient, ObjectMapper mapper,
                      String baseUrl, String token, RetryPolicy retry,
                      FallbackConfig fallback, Consumer<ResilienceEvent> logger,
                      boolean debug, Duration timeout) {
        this.javaClient = javaClient != null ? javaClient :
                java.net.http.HttpClient.newBuilder()
                        .connectTimeout(timeout)
                        .version(java.net.http.HttpClient.Version.HTTP_1_1)
                        .build();
        this.mapper = mapper;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.token = token;
        this.retry = retry != null ? retry : RetryPolicy.resolve(null, null);
        this.fallback = fallback;
        this.logger = logger;
        this.debug = debug;
    }

    /** Chat's client-side model-fallback chain (read by CompletionsResource). May be null. */
    public FallbackConfig fallback() { return fallback; }

    /**
     * Publish a resilience event to the configured logger and, with
     * {@code debug(true)}, as a readable stderr line. Gateway-routing lines are
     * only printed when a server-side retry/fallback actually happened; the
     * logger receives every event. Also used by CompletionsResource for
     * fallback hops.
     */
    public void emit(ResilienceEvent event) {
        if (logger != null) {
            logger.accept(event);
        }
        if (!debug) {
            return;
        }
        if (event instanceof GatewayRoutingEvent g && g.attempts <= 1 && !g.fallback) {
            return;
        }
        System.err.println("[meshapi] " + ResilienceEvents.format(event));
    }

    // -----------------------------------------------------------------------
    // Public HTTP methods
    // -----------------------------------------------------------------------

    public <T> T get(String path, Class<T> responseType) {
        return get(path, null, responseType);
    }

    public <T> T get(String path, String queryString, Class<T> responseType) {
        String url = baseUrl + path + (queryString != null && !queryString.isBlank() ? "?" + queryString : "");
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .build();
        return execute(req, responseType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        return jsonRequest("POST", path, body, responseType);
    }

    public <T> T patch(String path, Object body, Class<T> responseType) {
        return jsonRequest("PATCH", path, body, responseType);
    }

    public void delete(String path) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Bearer " + token)
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .build();
        executeRaw(req); // returns response, we don't need the body
    }

    public Iterator<ChatCompletionChunk> stream(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                    .build();

            HttpResponse<java.io.InputStream> response =
                    javaClient.send(req, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body().readAllBytes());
                HttpResponse<String> errResp = toStringResponse(response, errorBody);
                throw MeshAPIError.fromResponse(errResp);
            }

            return new SseParser(response.body());
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("stream request failed: " + e.getMessage(), e);
        }
    }

    public <T> Iterator<T> streamJson(String path, Object body, Class<T> valueType) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                    .build();

            // Streaming: no retry
            HttpResponse<java.io.InputStream> response =
                    javaClient.send(req, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body().readAllBytes());
                HttpResponse<String> errResp = toStringResponse(response, errorBody);
                throw MeshAPIError.fromResponse(errResp);
            }

            return new JsonSseParser<>(response.body(), valueType);
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("stream request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the underlying JDK HttpClient.
     * Intended for use by SDK resources that need to make calls outside the standard JSON API
     * (e.g. PUT to a signed URL without Authorization headers).
     */
    public java.net.http.HttpClient javaClient() {
        return javaClient;
    }

    public byte[] getBytes(String path) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .header("Authorization", "Bearer " + token)
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .build();
        HttpResponse<byte[]> response = executeWithRetry(req, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() >= 400) {
            HttpResponse<String> errResp = toStringResponse(response, new String(response.body()));
            throw MeshAPIError.fromResponse(errResp);
        }
        return response.body();
    }

    public byte[] postBytes(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                    .build();
            HttpResponse<byte[]> response = executeWithRetry(req, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 400) {
                HttpResponse<String> errResp = toStringResponse(response, new String(response.body()));
                throw MeshAPIError.fromResponse(errResp);
            }
            return response.body();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("POST bytes failed: " + e.getMessage(), e);
        }
    }

    public <T> T postMultipart(String path, java.util.Map<String, String> fields, byte[] fileData, String filename, Class<T> responseType) {
        try {
            String boundary = "----MeshAPIBoundary" + System.currentTimeMillis();
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

            for (java.util.Map.Entry<String, String> entry : fields.entrySet()) {
                if (entry.getValue() == null) continue;
                baos.write(("--" + boundary + "\r\n").getBytes());
                baos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                baos.write((entry.getValue() + "\r\n").getBytes());
            }

            if (fileData != null) {
                baos.write(("--" + boundary + "\r\n").getBytes());
                baos.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n").getBytes());
                baos.write("Content-Type: application/octet-stream\r\n\r\n".getBytes());
                baos.write(fileData);
                baos.write("\r\n".getBytes());
            }

            baos.write(("--" + boundary + "--\r\n").getBytes());

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Accept", "application/json")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                    .build();

            HttpResponse<String> response = executeWithRetry(req, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw MeshAPIError.fromResponse(response);
            }
            return mapper.readValue(response.body(), responseType);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("POST multipart failed: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    private <T> T jsonRequest(String method, String path, Object body, Class<T> responseType) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE);

            if ("PATCH".equals(method)) {
                builder.method("PATCH", HttpRequest.BodyPublishers.ofString(json));
            } else {
                builder.POST(HttpRequest.BodyPublishers.ofString(json));
            }
            return execute(builder.build(), responseType);
        } catch (RuntimeException e) {
            // MeshAPIError and transport failures (already wrapped by
            // executeWithRetry, with the network cause preserved for the chat
            // fallback chain's eligibility check) propagate as-is.
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(method + " " + path + " failed: " + e.getMessage(), e);
        }
    }

    private <T> T execute(HttpRequest req, Class<T> responseType) {
        HttpResponse<String> response = executeWithRetry(req);
        if (response.statusCode() >= 400) {
            throw MeshAPIError.fromResponse(response);
        }
        if (response.statusCode() == 204) {
            return null;
        }
        String ct = response.headers().firstValue("content-type").orElse("");
        if (!ct.contains("application/json")) {
            String body = response.body();
            throw new MeshAPIError(
                    body != null && body.length() > 500 ? body.substring(0, 500) : body,
                    response.statusCode(), "parse_error", "", null, null);
        }
        try {
            return mapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new MeshAPIError("JSON parse error: " + e.getMessage(),
                    response.statusCode(), "parse_error", "", null, null);
        }
    }

    private HttpResponse<String> executeRaw(HttpRequest req) {
        return executeWithRetry(req);
    }

    private HttpResponse<String> executeWithRetry(HttpRequest req) {
        return executeWithRetry(req, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * The single transport retry loop shared by every non-streaming request
     * (JSON, raw-bytes, and multipart). Re-sends on the policy's status set
     * (and, opt-in, on pre-response network errors), with exponential backoff,
     * jitter, and {@code Retry-After} support. Timeouts and interrupts are
     * NEVER retried. Emits a {@link RetryEvent} per re-send and a
     * {@link GatewayRoutingEvent} when the final response carries
     * {@code X-Mesh-Routing-*} headers. Returns the final response — callers
     * handle non-2xx statuses.
     */
    private <T> HttpResponse<T> executeWithRetry(HttpRequest req, HttpResponse.BodyHandler<T> handler) {
        String method = req.method();
        String path = req.uri().getPath();
        int maxRetries = retry.maxRetries();
        int attempt = 0;
        while (true) {
            HttpResponse<T> response;
            try {
                response = javaClient.send(req, handler);
            } catch (InterruptedException e) {
                // Interrupts always propagate — never retried.
                Thread.currentThread().interrupt();
                throw new RuntimeException("request interrupted", e);
            } catch (HttpTimeoutException e) {
                // Timeouts always propagate — never retried.
                throw new RuntimeException("request timed out: " + e.getMessage(), e);
            } catch (Exception e) {
                // Other pre-response failures (DNS, connection refused/reset)
                // retry only when opted in — they are ambiguous for
                // non-idempotent POSTs.
                if (!retry.retryOnNetworkError() || attempt >= maxRetries) {
                    throw new RuntimeException("request failed: " + e.getMessage(), e);
                }
                long delayMs = computeDelay(attempt, null);
                emit(new RetryEvent(method, path, attempt + 1, maxRetries,
                        null, null, delayMs, "network-error"));
                sleepBeforeRetry(delayMs);
                attempt++;
                continue;
            }

            if (retry.retryOnStatus().contains(response.statusCode()) && attempt < maxRetries) {
                long delayMs = computeDelay(attempt, retryAfterFromResponse(response));
                emit(new RetryEvent(method, path, attempt + 1, maxRetries,
                        response.statusCode(),
                        response.headers().firstValue(REQUEST_ID_HEADER).orElse(null),
                        delayMs, "status"));
                sleepBeforeRetry(delayMs);
                attempt++;
                continue;
            }

            emitGatewayRouting(path, response);
            return response;
        }
    }

    /**
     * Surface the gateway's own routing outcome (server-side retry / provider
     * fallback, FT-244) when the response reports it. Header-absence means the
     * key has no active routing policy — nothing is emitted.
     */
    private void emitGatewayRouting(String path, HttpResponse<?> response) {
        String attempts = response.headers().firstValue(ROUTING_ATTEMPTS_HEADER).orElse(null);
        if (attempts == null) {
            return;
        }
        int parsed;
        try {
            parsed = Integer.parseInt(attempts.trim());
        } catch (NumberFormatException e) {
            parsed = 1;
        }
        emit(new GatewayRoutingEvent(
                path,
                parsed > 0 ? parsed : 1,
                "true".equals(response.headers().firstValue(ROUTING_FALLBACK_HEADER).orElse(null)),
                response.headers().firstValue(REQUEST_ID_HEADER).orElse(null)));
    }

    private static void sleepBeforeRetry(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("interrupted during retry", e);
        }
    }

    private long computeDelay(int attempt, Integer retryAfterSec) {
        long baseMs = retryAfterSec != null
                ? (long) retryAfterSec * 1000
                : (long) (retry.backoffBaseMs() * Math.pow(2, attempt));
        long capped = Math.min(baseMs, retry.backoffMaxMs());
        double jitter = capped * (0.8 + Math.random() * 0.4); // ±20%
        return (long) jitter;
    }

    private Integer retryAfterFromResponse(HttpResponse<?> resp) {
        if (!retry.respectRetryAfter()) {
            return null;
        }
        return resp.headers().firstValue("retry-after")
                .map(v -> {
                    try { return (int) Math.ceil(Double.parseDouble(v)); }
                    catch (NumberFormatException e) { return null; }
                })
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> HttpResponse<String> toStringResponse(HttpResponse<T> original, String body) {
        // Adapter to reuse MeshAPIError.fromResponse(HttpResponse<String>)
        return new HttpResponse<>() {
            public int statusCode() { return original.statusCode(); }
            public HttpRequest request() { return original.request(); }
            public java.util.Optional<HttpResponse<String>> previousResponse() { return java.util.Optional.empty(); }
            public java.net.http.HttpHeaders headers() { return original.headers(); }
            public String body() { return body; }
            public java.util.Optional<javax.net.ssl.SSLSession> sslSession() { return java.util.Optional.empty(); }
            public URI uri() { return original.uri(); }
            public java.net.http.HttpClient.Version version() { return original.version(); }
        };
    }
}
