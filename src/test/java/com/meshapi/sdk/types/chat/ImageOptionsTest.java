package com.meshapi.sdk.types.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageOptionsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesGifFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder()
                .outputFormat("gif")
                .frameCount(2)
                .frameDurationMs(20)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));

        assertEquals("gif", json.get("output_format").asText());
        assertEquals(2, json.get("frame_count").asInt());
        assertEquals(20, json.get("frame_duration_ms").asInt());
    }

    @Test
    void omitsFrameFieldsWhenUnset() throws Exception {
        ImageOptions opts = ImageOptions.builder().build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));

        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }
}
