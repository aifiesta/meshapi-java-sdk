package com.meshapi.sdk.types.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class SpeechRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesSpeedWhenSet() throws Exception {
        SpeechRequest req = SpeechRequest.builder()
                .model("openai/tts-1")
                .input("Hello")
                .voice("alloy")
                .speed(1.5)
                .build();
        JsonNode node = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(1.5, node.get("speed").asDouble(), 0.0);
        assertEquals("Hello", node.get("input").asText());
    }

    @Test
    void omitsSpeedWhenUnset() throws Exception {
        SpeechRequest req = SpeechRequest.builder()
                .model("openai/tts-1")
                .input("Hello")
                .voice("alloy")
                .build();
        JsonNode node = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(node.has("speed"));
    }
}
