package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SearchRequestModeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialisesMode() throws Exception {
        SearchRequest req = SearchRequest.builder()
                .query("quarterly revenue")
                .fileIds(List.of("file_123"))
                .topK(5)
                .mode(SearchRequest.MODE_AGENTIC)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals("agentic", json.get("mode").asText());
        assertEquals("file_123", json.get("file_ids").get(0).asText());
        assertEquals(5, json.get("top_k").asInt());
    }

    @Test
    void omitsModeWhenUnset() throws Exception {
        SearchRequest req = SearchRequest.builder().query("hello").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("mode"));
    }

    @Test
    void modeConstantsMatchWireValues() {
        assertEquals("vector", SearchRequest.MODE_VECTOR);
        assertEquals("graph", SearchRequest.MODE_GRAPH);
        assertEquals("agentic", SearchRequest.MODE_AGENTIC);
    }
}
