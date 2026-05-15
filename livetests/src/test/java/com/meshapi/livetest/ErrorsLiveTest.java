package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorsLiveTest extends LiveTestBase {

    @Test
    void unauthorized_chat() {
        MeshAPI bad = badClient();
        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> bad.chat().completions().create(
                        ChatCompletionRequest.builder()
                                .model(MODEL)
                                .addMessage(ChatMessage.user("hello"))
                                .build()));
        assertEquals(401, err.getStatus());
        assertEquals("unauthorized", err.getErrorCode());
        assertNotNull(err.getRequestId());
        System.out.printf("[PASS] unauthorized → status=%d code=%s requestId=%s%n",
                err.getStatus(), err.getErrorCode(), err.getRequestId());
    }

    @Test
    void unauthorized_models() {
        MeshAPI bad = badClient();
        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> bad.models().list(null));
        assertEquals(401, err.getStatus());
        System.out.printf("[PASS] models unauthorized → status=%d code=%s%n",
                err.getStatus(), err.getErrorCode());
    }

    @Test
    void notFound_template() {
        MeshAPI client = newClient();
        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> client.templates().get("00000000-0000-0000-0000-000000000000"));
        assertEquals(404, err.getStatus());
        System.out.printf("[PASS] not_found → status=%d code=%s%n",
                err.getStatus(), err.getErrorCode());
    }

    @Test
    void errorIsRuntimeException() {
        MeshAPI bad = badClient();
        Exception thrown = assertThrows(RuntimeException.class,
                () -> bad.models().list(null));
        assertInstanceOf(MeshAPIError.class, thrown);
        System.out.printf("[PASS] MeshAPIError is RuntimeException: %s%n", thrown.getClass().getSimpleName());
    }
}
