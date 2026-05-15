package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class StreamLiveTest extends LiveTestBase {

    @Test
    void stream_basic() {
        MeshAPI client = newClient();
        Iterator<ChatCompletionChunk> it = client.chat().completions().stream(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Count from 1 to 5, one number per line."))
                        .maxTokens(40)
                        .build());

        int count = 0;
        StringBuilder text = new StringBuilder();
        while (it.hasNext()) {
            ChatCompletionChunk chunk = it.next();
            count++;
            if (!chunk.choices.isEmpty()
                    && chunk.choices.get(0).delta != null
                    && chunk.choices.get(0).delta.content != null) {
                text.append(chunk.choices.get(0).delta.content);
            }
        }

        assertTrue(count > 0, "expected at least one chunk");
        assertFalse(text.isEmpty(), "expected non-empty reconstructed text");
        System.out.printf("[PASS] stream → %d chunks, text=%s%n", count, text);
    }

    @Test
    void stream_chunkStructure() {
        MeshAPI client = newClient();
        Iterator<ChatCompletionChunk> it = client.chat().completions().stream(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Say hello."))
                        .maxTokens(10)
                        .build());

        boolean first = true;
        int count = 0;
        while (it.hasNext()) {
            ChatCompletionChunk chunk = it.next();
            count++;
            assertNotNull(chunk.id, "chunk.id is null");
            assertNotNull(chunk.model, "chunk.model is null");
            if (first && !chunk.choices.isEmpty() && chunk.choices.get(0).delta != null) {
                System.out.printf("first chunk: id=%s model=%s role=%s content=%s%n",
                        chunk.id, chunk.model,
                        chunk.choices.get(0).delta.role,
                        chunk.choices.get(0).delta.content);
                first = false;
            }
        }
        System.out.printf("[PASS] stream chunk structure valid (%d chunks)%n", count);
    }

    @Test
    void stream_earlyStop() {
        MeshAPI client = newClient();
        Iterator<ChatCompletionChunk> it = client.chat().completions().stream(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Write a very long essay about the ocean."))
                        .maxTokens(200)
                        .build());

        int received = 0;
        // Consume only first 3 chunks, then stop iterating
        while (it.hasNext() && received < 3) {
            it.next();
            received++;
        }
        // Close the underlying stream by trying to close the iterator if it's AutoCloseable
        if (it instanceof AutoCloseable) {
            try { ((AutoCloseable) it).close(); } catch (Exception ignored) {}
        }

        assertTrue(received > 0, "expected to receive some chunks before stopping");
        System.out.printf("[PASS] stream early stop after %d chunks — no deadlock%n", received);
    }

    @Test
    void stream_authError() {
        MeshAPI bad = badClient();
        MeshAPIError err = assertThrows(MeshAPIError.class, () -> {
            Iterator<ChatCompletionChunk> it = bad.chat().completions().stream(
                    ChatCompletionRequest.builder()
                            .model(MODEL)
                            .addMessage(ChatMessage.user("Hello"))
                            .build());
            // Force evaluation — stream() call itself throws for 401
            while (it.hasNext()) { it.next(); }
        });
        assertEquals(401, err.getStatus());
        assertEquals("unauthorized", err.getErrorCode());
        System.out.printf("[PASS] stream auth error → status=%d code=%s%n",
                err.getStatus(), err.getErrorCode());
    }
}
