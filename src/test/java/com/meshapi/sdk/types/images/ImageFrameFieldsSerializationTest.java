package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ImageFrameFieldsSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void generationRequestSerialisesFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a bouncing ball")
                .frameCount(8)
                .frameDurationMs(120)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(8, json.get("frame_count").asInt());
        assertEquals(120, json.get("frame_duration_ms").asInt());
        assertEquals(8, req.getFrameCount());
        assertEquals(120, req.getFrameDurationMs());
    }

    @Test
    void generationRequestOmitsUnsetFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder().prompt("a cat").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void imageOptionsSerialisesFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder().frameCount(4).frameDurationMs(250).build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));
        assertEquals(4, json.get("frame_count").asInt());
        assertEquals(250, json.get("frame_duration_ms").asInt());
    }

    @Test
    void imageOptionsOmitsUnsetFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder().size("1024x1024").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
