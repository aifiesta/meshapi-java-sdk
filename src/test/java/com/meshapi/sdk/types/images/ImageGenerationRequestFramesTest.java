package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ImageGenerationRequestFramesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesFrameFieldsInSnakeCase() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .model("openai/gpt-image-2.6-flare")
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
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .model("openai/gpt-image-2.6-flare")
                .prompt("a cube")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void exposesFrameFieldsOnPublicMembers() {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .frameCount(2)
                .frameDurationMs(20)
                .build();

        assertEquals(2, req.frameCount);
        assertEquals(20, req.frameDurationMs);
    }
}
