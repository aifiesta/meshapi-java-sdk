package com.meshapi.sdk.types.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageOptionsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesFrameFields() throws Exception {
        ImageOptions opts = ImageOptions.builder()
                .outputFormat("gif")
                .frameCount(4)
                .frameDurationMs(250)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(opts));
        assertEquals("gif", json.get("output_format").asText());
        assertEquals(4, json.get("frame_count").asInt());
        assertEquals(250, json.get("frame_duration_ms").asInt());
    }

    @Test
    void omitsFrameFieldsWhenUnset() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(ImageOptions.builder().build()));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> ImageOptions.builder().frameCount(1));
        assertThrows(IllegalArgumentException.class, () -> ImageOptions.builder().frameCount(17));
        assertThrows(IllegalArgumentException.class, () -> ImageOptions.builder().frameDurationMs(19));
    }
}
