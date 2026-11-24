package com.meshapi.sdk.types.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class SearchRequestModeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialisesMode() throws Exception {
        SearchRequest req = SearchRequest.builder().mode("custom").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals("custom", json.get("mode").asText());
    }

    @Test
    void omitsUnsetMode() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(SearchRequest.builder().build()));
        assertFalse(json.has("mode"));
    }
}
