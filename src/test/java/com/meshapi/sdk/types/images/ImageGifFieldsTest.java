package com.meshapi.sdk.types.images;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ImageGifFieldsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void generationRequestSerializesFrameFields() throws Exception {
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
        ImageGenerationRequest req = ImageGenerationRequest.builder().prompt("a cat").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("frame_count"));
        assertFalse(json.has("frame_duration_ms"));
    }

    @Test
    void imageOptionsSerializesFrameFields() throws Exception {
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
    void imageOptionsDeserializesAndIgnoresUnknown() throws Exception {
        ImageOptions opts = mapper.readValue(
                "{\"output_format\":\"gif\",\"frame_count\":16,\"frame_duration_ms\":50,\"extra\":true}",
                ImageOptions.class);
        assertEquals("gif", opts.outputFormat);
        assertEquals(16, opts.frameCount);
        assertEquals(50, opts.frameDurationMs);
    }

    @Test
    void imageOptionsUnsetFieldsAreNull() throws Exception {
        ImageOptions opts = mapper.readValue("{}", ImageOptions.class);
        assertNull(opts.frameCount);
        assertNull(opts.frameDurationMs);
    }
}
