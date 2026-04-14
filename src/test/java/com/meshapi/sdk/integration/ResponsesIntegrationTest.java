package com.meshapi.sdk.integration;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.responses.ResponsesRequest;
import com.meshapi.sdk.types.responses.ResponsesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import org.junit.jupiter.api.Assumptions;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class ResponsesIntegrationTest {

    private MeshAPI client;

    @BeforeEach
    void setUp() {
        String baseUrl = System.getenv().getOrDefault("MESHAPI_BASE_URL", "http://localhost:8000");
        String token = System.getenv("MESHAPI_TOKEN");
        Assumptions.assumeTrue(token != null, "MESHAPI_TOKEN environment variable is not set; skipping integration tests");
        client = MeshAPI.builder().baseUrl(baseUrl).token(token).build();
    }

    @Test
    void create_returnsResponse() {
        ResponsesResponse resp = client.responses().create(
                ResponsesRequest.builder()
                        .model("openai/o4-mini")
                        .input("What is 2 + 2?")
                        .reasoning("low")
                        .build()
        );
        assertNotNull(resp.id);
        assertFalse(resp.id.isBlank());
        assertFalse(resp.choices.isEmpty());
    }

    @Test
    void stream_returnsChunks() {
        Iterator<ChatCompletionChunk> it = client.responses().stream(
                ResponsesRequest.builder()
                        .model("openai/o4-mini")
                        .input("Count 1 to 3.")
                        .build()
        );
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertTrue(count > 0, "expected at least one chunk");
    }
}
