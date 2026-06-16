package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.compare.CompareRequest;
import com.meshapi.sdk.types.compare.CompareResponse;
import com.meshapi.sdk.types.compare.CompareStreamEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CompareLiveTest extends LiveTestBase {

    @Test
    void nonStreaming_returnsResultsForBothModels() {
        MeshAPI client = newClient();

        CompareRequest req = new CompareRequest();
        req.models = Arrays.asList(MODEL, SECOND_MODEL);
        req.messages = Arrays.asList(ChatMessage.user("What is 2+2? Reply in one word."));
        req.maxTokens = 20;
        req.skipComparison = true;

        CompareResponse result = client.compare().create(req);

        assertNotNull(result.comparisonId, "expected comparison_id");
        assertEquals(2, result.results.size(), "expected 2 results");
        List<String> models = result.results.stream()
                .map(r -> r.model)
                .collect(Collectors.toList());
        assertTrue(models.contains(MODEL), "expected " + MODEL + " in results");
        assertTrue(models.contains(SECOND_MODEL), "expected " + SECOND_MODEL + " in results");
        for (CompareResponse.ModelCompareResult r : result.results) {
            assertTrue(r.content != null || r.error != null,
                    "expected content or error for " + r.model);
            System.out.printf("[PASS] model=%s content=%s%n", r.model, r.content);
        }
    }

    @Test
    void streaming_receivesEvents() {
        MeshAPI client = newClient();

        CompareRequest req = new CompareRequest();
        req.models = Arrays.asList(MODEL, SECOND_MODEL);
        req.messages = Arrays.asList(ChatMessage.user("Tell me a joke."));
        req.maxTokens = 50;
        req.skipComparison = true;

        Iterator<CompareStreamEvent> iter = client.compare().stream(req);
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        assertTrue(count > 0, "expected at least one streaming event");
        System.out.printf("[PASS] compare.stream → received %d events%n", count);
    }
}
