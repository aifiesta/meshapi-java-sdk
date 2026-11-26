package com.meshapi.sdk.types.images;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageGenerationRequestFrameTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a paper boat")
                .frameCount(4)
                .frameDurationMs(250)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(4, json.get("frame_count").asInt());
        assertEquals(250, json.get("frame_duration_ms").asInt());
    }

    @Test
    void omitsFrameFieldsWhenUnset() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder().prompt("a paper boat").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
