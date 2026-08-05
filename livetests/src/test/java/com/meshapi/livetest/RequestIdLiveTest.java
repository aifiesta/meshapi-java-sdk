package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.RequestOptions;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Request-ID round-trips against a real backend: a valid client-supplied
 * X-Request-Id is echoed back; without one the backend mints a req_&lt;ULID&gt; id.
 */
class RequestIdLiveTest extends LiveTestBase {

    private static ChatCompletionRequest smallChatRequest() {
        return ChatCompletionRequest.builder()
                .model(MODEL)
                .addMessage(ChatMessage.user("Say 'pong' only."))
                .maxTokens(5)
                .build();
    }

    @Test
    void customRequestId_echoedOnResponse() {
        MeshAPI client = newClient();
        String customId = "sdk-java-live-" + System.currentTimeMillis();
        ChatCompletionResponse resp = client.chat().completions().create(
                smallChatRequest(), RequestOptions.withRequestId(customId));
        assertEquals(customId, resp.getRequestId(),
                "backend must echo a valid client-supplied X-Request-Id");
        System.out.printf("[PASS] custom request id echoed → %s%n", resp.getRequestId());
    }

    @Test
    void defaultRequestId_hasReqPrefix() {
        MeshAPI client = newClient();
        ChatCompletionResponse resp = client.chat().completions().create(smallChatRequest());
        assertNotNull(resp.getRequestId(), "X-Request-Id must be set on every response");
        assertTrue(resp.getRequestId().startsWith("req_"),
                "server-minted request id must have req_ prefix, got: " + resp.getRequestId());
        System.out.printf("[PASS] default request id → %s%n", resp.getRequestId());
    }

    @Test
    void errorResponse_carriesRequestId() {
        MeshAPI bad = badClient();
        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> bad.chat().completions().create(smallChatRequest()));
        assertNotNull(err.getRequestId());
        assertTrue(err.getRequestId().startsWith("req_"),
                "error request id must have req_ prefix, got: " + err.getRequestId());
        System.out.printf("[PASS] error request id → %s%n", err.getRequestId());
    }

    @Test
    void invalidRequestId_rejectedClientSide() {
        // The backend silently ignores invalid ids; the SDK fails fast instead —
        // no request is ever sent.
        assertThrows(IllegalArgumentException.class,
                () -> RequestOptions.withRequestId("not valid: has spaces!"));
        System.out.println("[PASS] invalid request id rejected before any network call");
    }
}
