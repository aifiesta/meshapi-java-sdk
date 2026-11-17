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
                .prompt("a spinning cube")
                .outputFormat("gif")
                .frameCount(8)
                .frameDurationMs(100)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertEquals("gif", json.get("output_format").asText());
        assertEquals(8, json.get("frame_count").asInt());
        assertEquals(100, json.get("frame_duration_ms").asInt());
    }

    @Test
    void generationRequestOmitsUnsetFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a still image")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void imageOptionsSerialisesFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder()
                .outputFormat("gif")
                .frameCount(2)
                .frameDurationMs(20)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));

        assertEquals(2, json.get("frame_count").asInt());
        assertEquals(20, json.get("frame_duration_ms").asInt());
    }

    @Test
    void imageOptionsOmitsUnsetFrameFields() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(ImageOptions.builder().build()));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
