package com.meshapi.sdk.integration;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests — run against a live MeshAPI server.
 * Excluded from the default Maven surefire run; enable with -Pintegration.
 */
class ChatIntegrationTest {

    private static MeshAPI client;

    @BeforeAll
    static void setup() {
        String baseUrl = System.getenv().getOrDefault("MESHAPI_BASE_URL", "http://localhost:8000");
        String token = System.getenv().getOrDefault("MESHAPI_TOKEN", "rsk_01KN96KQWDPF2X1E9CP8567JY4");
        client = MeshAPI.builder().baseUrl(baseUrl).token(token).build();
    }

    @Test
    void chatCreate_nonStreaming() {
        ChatCompletionResponse resp = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model("openai/gpt-4o-mini")
                        .addMessage(ChatMessage.user("Say 'pong' only."))
                        .maxTokens(5)
                        .build()
        );
        assertNotNull(resp.id);
        assertFalse(resp.choices.isEmpty());
        assertNotNull(resp.choices.get(0).message);
    }

    @Test
    void chatCreate_streaming() {
        Iterator<ChatCompletionChunk> it = client.chat().completions().stream(
                ChatCompletionRequest.builder()
                        .model("openai/gpt-4o-mini")
                        .addMessage(ChatMessage.user("Count from 1 to 3."))
                        .maxTokens(20)
                        .build()
        );
        int count = 0;
        StringBuilder text = new StringBuilder();
        while (it.hasNext()) {
            ChatCompletionChunk chunk = it.next();
            count++;
            if (!chunk.choices.isEmpty() && chunk.choices.get(0).delta != null
                    && chunk.choices.get(0).delta.content != null) {
                text.append(chunk.choices.get(0).delta.content);
            }
        }
        assertTrue(count > 0, "Expected at least one chunk");
        assertFalse(text.isEmpty(), "Expected non-empty streamed text");
    }
}
