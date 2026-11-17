package com.meshapi.sdk.types.images;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageGenerationRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesGifFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a spinning cube")
                .outputFormat("gif")
                .frameCount(8)
                .frameDurationMs(100)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertEquals("gif", json.get("output_format").asText());
        assertEquals(8, json.get("frame_count").asInt());
        assertEquals(100, json.get("frame_duration_ms").asInt());
        assertEquals(8, req.getFrameCount());
        assertEquals(100, req.getFrameDurationMs());
    }

    @Test
    void omitsFrameFieldsWhenUnset() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a still image")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
