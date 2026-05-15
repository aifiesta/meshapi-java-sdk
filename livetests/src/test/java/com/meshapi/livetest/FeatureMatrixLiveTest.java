package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.compare.CompareRequest;
import com.meshapi.sdk.types.compare.CompareResponse;
import com.meshapi.sdk.types.embeddings.EmbeddingsRequest;
import com.meshapi.sdk.types.embeddings.EmbeddingsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeatureMatrixLiveTest extends LiveTestBase {

    @Test
    void stableOptions() {
        MeshAPI client = newClient();

        ChatCompletionResponse chat = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Reply with exactly the word: seeded"))
                        .seed(42)
                        .temperature(0)
                        .topP(1)
                        .user("java-feature-matrix")
                        .maxTokens(10)
                        .build());
        assertNotNull(chat.id);
        System.out.printf("[PASS] chat options -> id=%s model=%s%n", chat.id, chat.model);

        // responses with reasoning requires a reasoning-capable model; skip with default model
        System.out.println("[SKIP] responses stable options -> reasoning.effort not supported by default model");

        EmbeddingsRequest embeddingsRequest = new EmbeddingsRequest();
        embeddingsRequest.model = envOrShared("MESHAPI_EMBEDDINGS_MODEL", "openai/text-embedding-3-small");
        embeddingsRequest.input = List.of("alpha", "beta");
        embeddingsRequest.user = "java-feature-matrix";
        EmbeddingsResponse embeddings = client.embeddings().create(embeddingsRequest);
        assertNotNull(embeddings.data);
        assertFalse(embeddings.data.isEmpty());
        System.out.printf("[PASS] embeddings options -> items=%d%n", embeddings.data.size());

        CompareRequest compareRequest = new CompareRequest();
        compareRequest.models = List.of(MODEL, SECOND_MODEL);
        compareRequest.messages = List.of(ChatMessage.user("Reply with compare"));
        compareRequest.comparisonInstructions = "Do not add extra prose.";
        compareRequest.maxTokens = 10;
        compareRequest.skipComparison = true;
        CompareResponse compare = client.compare().create(compareRequest);
        assertNotNull(compare.results);
        assertEquals(2, compare.results.size());
        System.out.printf("[PASS] compare options -> results=%d%n", compare.results.size());
    }

    @Test
    void multimodalImageInput() {
        if (!hasEnvOrShared("MESHAPI_IMAGE_URL")) {
            System.out.println("[SKIP] chat image input -> set MESHAPI_IMAGE_URL");
            return;
        }

        MeshAPI client = newClient();
        String imageUrl = envOrShared("MESHAPI_IMAGE_URL", "");
        ChatCompletionResponse response = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(envOrShared("MESHAPI_IMAGE_MODEL", MODEL))
                        .addMessage(ChatMessage.builder()
                                .role("user")
                                .content(List.of(
                                        Map.of("type", "text", "text", "Describe this image in three words."),
                                        Map.of("type", "image_url", "image_url", Map.of("url", imageUrl))
                                ))
                                .build())
                        .maxTokens(30)
                        .build());
        assertNotNull(response.id);
        System.out.printf("[PASS] chat image input -> id=%s%n", response.id);
    }
}
