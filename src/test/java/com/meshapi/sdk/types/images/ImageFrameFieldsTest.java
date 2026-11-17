package com.meshapi.sdk.types.images;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageFrameFieldsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void generationRequestSerialisesFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a spinning cube")
                .frameCount(8)
                .frameDurationMs(100)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertEquals(8, json.get("frame_count").asInt());
        assertEquals(100, json.get("frame_duration_ms").asInt());
        assertEquals("a spinning cube", json.get("prompt").asText());
    }

    @Test
    void generationRequestOmitsUnsetFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a cube")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
        assertNull(req.getFrameCount());
        assertNull(req.getFrameDurationMs());
    }

    @Test
    void imageOptionsSerialisesFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder()
                .frameCount(2)
                .frameDurationMs(20)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));

        assertEquals(2, json.get("frame_count").asInt());
        assertEquals(20, json.get("frame_duration_ms").asInt());
    }

    @Test
    void imageOptionsOmitsUnsetFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder().build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
