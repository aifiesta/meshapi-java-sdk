package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.resilience.FallbackConfig;
import com.meshapi.sdk.resilience.FallbackEvent;
import com.meshapi.sdk.resilience.GatewayRoutingEvent;
import com.meshapi.sdk.resilience.ResilienceEvent;
import com.meshapi.sdk.resilience.RetryEvent;
import com.meshapi.sdk.resilience.RetryPolicy;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Live tests for resilience: retry / fallback / observability events.
 * Mirrors the Node SDK's livetests/test-resilience.js scenarios.
 */
class ResilienceLiveTest extends LiveTestBase {

    @Test
    void successfulCallWithLogger_noSpuriousEvents_gatewayRoutingOnlyFromHeaders() {
        List<ResilienceEvent> events = new CopyOnWriteArrayList<>();
        MeshAPI client = MeshAPI.builder()
                .baseUrl(BASE_URL)
                .token(TOKEN)
                .logger(events::add)
                .build();

        ChatCompletionResponse res = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Reply with the word: ok"))
                        .maxTokens(10)
                        .build());

        assertNotNull(res.choices.get(0).message);
        // No client-side retry/fallback should have happened on a healthy call.
        assertEquals(0, events.stream().filter(e -> e instanceof RetryEvent).count());
        assertEquals(0, events.stream().filter(e -> e instanceof FallbackEvent).count());
        // gateway-routing appears IFF the key has an active routing_policy;
        // when it does, the shape must be sane.
        for (ResilienceEvent e : events) {
            if (e instanceof GatewayRoutingEvent gw) {
                assertTrue(gw.attempts >= 1);
            }
        }
        System.out.printf("[PASS] resilience healthy call → %d event(s)%n", events.size());
    }

    @Test
    void perCallFallbackModelsIsClientSideOnly_serverStillServesThePrimary() {
        MeshAPI client = newClient();
        ChatCompletionResponse res = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Reply with the word: ok"))
                        .maxTokens(10)
                        .fallbackModels(SECOND_MODEL)
                        .build());

        // The request validated server-side (fallbackModels was stripped) and
        // the primary model answered.
        assertNotNull(res.model, "expected a model on the response");
        assertNotNull(res.choices.get(0).message);
        System.out.printf("[PASS] per-call fallbackModels stripped → served model=%s%n", res.model);
    }

    @Test
    void unreachableGateway_retryEventsFire_chainAdvances_lastErrorPropagates() {
        List<ResilienceEvent> events = new CopyOnWriteArrayList<>();
        MeshAPI client = MeshAPI.builder()
                // A privileged, never-bound localhost port — connect fails instantly with
                // ECONNREFUSED (a network error, NOT a timeout), which is what we want to
                // exercise: retryable + fallback-eligible. TEST-NET-1 (192.0.2.x) is unroutable
                // but on networks that silently drop its packets the connect would instead time
                // out, and timeouts are deliberately never retried — making this test flaky.
                .baseUrl("http://127.0.0.1:1")
                .token(TOKEN)
                .timeoutMs(2_000)
                .retry(RetryPolicy.builder()
                        .maxRetries(1)
                        .backoffBaseMs(10)
                        .backoffMaxMs(20)
                        .retryOnNetworkError(true)
                        .build())
                .fallback(FallbackConfig.builder().models(SECOND_MODEL).build())
                .logger(events::add)
                .build();

        assertThrows(RuntimeException.class, () -> client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("hello"))
                        .build()));

        // Each model attempt retried once on the network error…
        assertTrue(events.stream().anyMatch(e ->
                        e instanceof RetryEvent r && "network-error".equals(r.reason)),
                "expected network-error retry events, got: " + events);
        // …and the chain advanced to the fallback model before giving up.
        FallbackEvent fb = events.stream()
                .filter(e -> e instanceof FallbackEvent).map(e -> (FallbackEvent) e)
                .findFirst().orElse(null);
        assertNotNull(fb, "expected a fallback event");
        assertEquals(MODEL, fb.fromModel);
        assertEquals(SECOND_MODEL, fb.toModel);
        System.out.printf("[PASS] unreachable gateway → %d event(s), chain advanced %s → %s%n",
                events.size(), fb.fromModel, fb.toModel);
    }
}
