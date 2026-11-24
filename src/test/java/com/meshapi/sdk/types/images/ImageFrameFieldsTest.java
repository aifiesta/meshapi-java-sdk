package com.meshapi.sdk.types.images;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageFrameFieldsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void generationRequestSerialisesFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .frameCount(4)
                .frameDurationMs(250)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(4, json.get("frame_count").asInt());
        assertEquals(250, json.get("frame_duration_ms").asInt());
    }

    @Test
    void generationRequestOmitsUnsetFrameFields() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(ImageGenerationRequest.builder().build()));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void imageOptionsSerialisesFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder()
                .frameCount(2)
                .frameDurationMs(100)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));
        assertEquals(2, json.get("frame_count").asInt());
        assertEquals(100, json.get("frame_duration_ms").asInt());
    }

    @Test
    void imageOptionsOmitsUnsetFrameFields() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(ImageOptions.builder().build()));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
