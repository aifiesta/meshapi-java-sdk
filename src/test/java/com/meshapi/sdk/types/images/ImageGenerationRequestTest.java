package com.meshapi.sdk.types.images;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageGenerationRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesFrameFields() throws Exception {
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
    void omitsFrameFieldsWhenUnset() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder().prompt("a cat").build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void acceptsBoundaryValues() {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .frameCount(2).frameCount(16).frameDurationMs(20).build();
        assertEquals(16, req.getFrameCount());
        assertEquals(20, req.getFrameDurationMs());
    }

    @Test
    void rejectsOutOfRangeFrameCount() {
        assertThrows(IllegalArgumentException.class,
                () -> ImageGenerationRequest.builder().frameCount(1));
        assertThrows(IllegalArgumentException.class,
                () -> ImageGenerationRequest.builder().frameCount(17));
    }

    @Test
    void rejectsShortFrameDuration() {
        assertThrows(IllegalArgumentException.class,
                () -> ImageGenerationRequest.builder().frameDurationMs(19));
    }
}
