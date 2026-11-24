package com.meshapi.sdk.types.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class VideoDimensionsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void generateRequestSerialisesDimensions() throws Exception {
        VideoGenerateRequest req = VideoGenerateRequest.builder().width(720).height(1280).build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(720, json.get("width").asInt());
        assertEquals(1280, json.get("height").asInt());
    }

    @Test
    void generationRequestSerialisesDimensions() throws Exception {
        VideoGenerationRequest req = VideoGenerationRequest.builder().width(1080).height(1080).build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(1080, json.get("width").asInt());
        assertEquals(1080, json.get("height").asInt());
    }

    @Test
    void editRequestSerialisesDimensions() throws Exception {
        VideoEditRequest req = VideoEditRequest.builder().width(1920).height(1080).build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(1920, json.get("width").asInt());
        assertEquals(1080, json.get("height").asInt());
    }

    @Test
    void unsetDimensionsAreOmitted() throws Exception {
        JsonNode gen = mapper.readTree(mapper.writeValueAsString(VideoGenerateRequest.builder().build()));
        JsonNode genn = mapper.readTree(mapper.writeValueAsString(VideoGenerationRequest.builder().build()));
        JsonNode edit = mapper.readTree(mapper.writeValueAsString(VideoEditRequest.builder().build()));
        for (JsonNode json : new JsonNode[] {gen, genn, edit}) {
            assertFalse(json.has("width"));
            assertFalse(json.has("height"));
        }
    }

    @Test
    void widthWithoutHeightIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> VideoGenerateRequest.builder().width(720).build());
        assertThrows(IllegalArgumentException.class, () -> VideoGenerationRequest.builder().width(720).build());
        assertThrows(IllegalArgumentException.class, () -> VideoEditRequest.builder().width(720).build());
    }

    @Test
    void heightWithoutWidthIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> VideoGenerateRequest.builder().height(720).build());
        assertThrows(IllegalArgumentException.class, () -> VideoGenerationRequest.builder().height(720).build());
        assertThrows(IllegalArgumentException.class, () -> VideoEditRequest.builder().height(720).build());
    }
}
