package com.meshapi.sdk.types.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class RouterSelectRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesWeightProfile() throws Exception {
        RouterSelectRequest request = RouterSelectRequest.builder()
                .weightProfile("cost_first")
                .build();
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));
        assertEquals("cost_first", node.get("weight_profile").asText());
        assertEquals("cost_first", request.getWeightProfile());
    }

    @Test
    void omitsWeightProfileWhenUnset() throws Exception {
        RouterSelectRequest request = RouterSelectRequest.builder().build();
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));
        assertFalse(node.has("weight_profile"));
    }
}
