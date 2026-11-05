package com.meshapi.sdk.types.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class RouterSelectRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialisesWeightProfile() throws Exception {
        RouterSelectRequest request = RouterSelectRequest.builder()
                .weightProfile("cost_first")
                .build();
        assertEquals("cost_first", request.getWeightProfile());
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));
        assertEquals("cost_first", node.get("weight_profile").asText());
    }

    @Test
    void omitsWeightProfileWhenUnset() throws Exception {
        RouterSelectRequest request = RouterSelectRequest.builder().build();
        assertNull(request.getWeightProfile());
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));
        assertFalse(node.has("weight_profile"));
    }
}
