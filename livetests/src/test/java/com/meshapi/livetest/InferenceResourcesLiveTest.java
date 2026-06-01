package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.batch.BatchListResponse;
import com.meshapi.sdk.types.batch.BatchObject;
import com.meshapi.sdk.types.batch.BatchRequestItem;
import com.meshapi.sdk.types.batch.CreateBatchRequest;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.compare.CompareRequest;
import com.meshapi.sdk.types.compare.CompareResponse;
import com.meshapi.sdk.types.embeddings.EmbeddingsRequest;
import com.meshapi.sdk.types.embeddings.EmbeddingsResponse;
import com.meshapi.sdk.types.responses.ResponsesRequest;
import com.meshapi.sdk.types.responses.ResponsesResponse;
import com.meshapi.sdk.types.responses.ResponsesStreamEvent;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InferenceResourcesLiveTest extends LiveTestBase {

    private static List<BatchRequestItem> batchRequests(String tag) {
        BatchRequestItem first = new BatchRequestItem();
        first.customId = tag + "-1";
        first.body = Map.of(
                "model", MODEL,
                "messages", List.of(Map.of("role", "user", "content", "Reply with the single word: hello")),
                "max_tokens", 10
        );

        BatchRequestItem second = new BatchRequestItem();
        second.customId = tag + "-2";
        second.body = Map.of(
                "model", MODEL,
                "messages", List.of(Map.of("role", "user", "content", "Reply with the single word: world")),
                "max_tokens", 10
        );

        return List.of(first, second);
    }

    @Test
    void embeddings_create() {
        MeshAPI client = newClient();
        EmbeddingsRequest req = new EmbeddingsRequest();
        req.model = envOrShared("MESHAPI_EMBEDDINGS_MODEL", "openai/text-embedding-3-small");
        req.input = "MeshAPI embeddings smoke test";

        EmbeddingsResponse resp = client.embeddings().create(req);
        assertNotNull(resp.data);
        assertFalse(resp.data.isEmpty());
        System.out.printf("[PASS] embeddings.create -> items=%d model=%s%n", resp.data.size(), resp.model);
    }

    @Test
    void responses_create_and_stream() {
        MeshAPI client = newClient();

        ResponsesRequest req = new ResponsesRequest();
        req.model = MODEL;
        req.input = "Reply with exactly the word: ok";
        req.maxOutputTokens = 16;

        ResponsesResponse resp = client.responses().create(req);
        assertNotNull(resp);
        System.out.printf("[PASS] responses.create -> id=%s status=%s%n", resp.id, resp.status);

        ResponsesRequest streamReq = new ResponsesRequest();
        streamReq.model = MODEL;
        streamReq.input = "Count from 1 to 3.";
        streamReq.maxOutputTokens = 20;

        try {
            Iterator<ResponsesStreamEvent> it = client.responses().stream(streamReq);
            int count = 0;
            while (it.hasNext()) {
                it.next();
                count++;
            }
            assertTrue(count > 0, "expected at least one response stream event");
            System.out.printf("[PASS] responses.stream -> %d event(s)%n", count);
        } catch (com.meshapi.sdk.MeshAPIError e) {
            if (e.getStatus() == 501) {
                System.out.println("[SKIP] responses.stream -> 501 Not Implemented (model may not support native responses streaming fallback)");
                return;
            }
            throw e;
        }

    }

    @Test
    void compare_create_and_stream() {
        MeshAPI client = newClient();

        CompareRequest req = new CompareRequest();
        req.models = List.of(MODEL, SECOND_MODEL);
        req.messages = List.of(ChatMessage.user("Reply with the word: compare"));
        req.maxTokens = 16;
        req.skipComparison = true;

        CompareResponse resp = client.compare().create(req);
        assertNotNull(resp.results);
        assertEquals(2, resp.results.size());
        System.out.printf("[PASS] compare.create -> results=%d partial=%s%n", resp.results.size(), resp.partial);

        // compare.stream skipped: server-side SQLAlchemy session concurrency issue when compare tests run back-to-back
        System.out.println("[SKIP] compare.stream -> server-side concurrency issue");
    }

    @Test
    void batches_lifecycle() {
        MeshAPI client = newClient();
        String tag = uniqueName("java-batch");

        // Create batch with inline requests (no file upload step required)
        CreateBatchRequest batchReq = new CreateBatchRequest();
        batchReq.requests = batchRequests(tag);
        batchReq.metadata = Map.of("suite", "java-livetest");

        BatchObject batch = client.batches().create(batchReq);
        assertNotNull(batch.id);
        System.out.printf("[PASS] batches.create -> id=%s status=%s%n", batch.id, batch.status);

        BatchListResponse listed = client.batches().list(null, 10);
        assertTrue(listed.data.stream().anyMatch(item -> batch.id.equals(item.id)));
        System.out.printf("[PASS] batches.list -> count=%d%n", listed.data.size());

        BatchObject fetchedBatch = client.batches().get(batch.id);
        assertEquals(batch.id, fetchedBatch.id);
        System.out.printf("[PASS] batches.get -> status=%s%n", fetchedBatch.status);

        BatchObject cancelled = client.batches().cancel(batch.id);
        assertEquals(batch.id, cancelled.id);
        System.out.printf("[PASS] batches.cancel -> status=%s%n", cancelled.status);
    }

    @Test
    void images_generate() {
        MeshAPI client = newClient();
        String imageGenModel = envOrShared("MESHAPI_IMAGE_GEN_MODEL", "");
        if (imageGenModel.isEmpty()) {
            System.out.println("[SKIP] images.generate -> MESHAPI_IMAGE_GEN_MODEL not set");
            return;
        }

        com.meshapi.sdk.types.images.ImageGenerationRequest req = com.meshapi.sdk.types.images.ImageGenerationRequest.builder()
                .model(imageGenModel)
                .prompt("A small blue square on a white background.")
                .n(1)
                .size("1024x1024")
                .build();


        com.meshapi.sdk.types.images.ImageGenerationResponse resp = client.images().generate(req);
        assertNotNull(resp.created);
        assertNotNull(resp.data);
        assertFalse(resp.data.isEmpty());
        assertTrue(resp.data.get(0).b64Json != null || resp.data.get(0).url != null, "expected image data");
        System.out.printf("[PASS] images.generate -> created=%d images=%d%n", resp.created, resp.data.size());
    }
}

