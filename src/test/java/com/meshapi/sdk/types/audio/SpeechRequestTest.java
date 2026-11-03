package com.meshapi.sdk.types.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class SpeechRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesSpeed() throws Exception {
        SpeechRequest req = SpeechRequest.builder()
                .model("openai/gpt-4o-mini-tts")
                .input("Hello")
                .voice("alloy")
                .speed(1.5)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(1.5, json.get("speed").asDouble());
    }

    @Test
    void omitsSpeedWhenUnset() throws Exception {
        SpeechRequest req = SpeechRequest.builder().model("openai/gpt-4o-mini-tts").input("Hello").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("speed"));
    }

    @Test
    void acceptsBounds() {
        assertEquals(0.25, SpeechRequest.builder().speed(0.25).build().getSpeed());
        assertEquals(4.0, SpeechRequest.builder().speed(4.0).build().getSpeed());
    }

    @Test
    void rejectsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> SpeechRequest.builder().speed(0.2));
        assertThrows(IllegalArgumentException.class, () -> SpeechRequest.builder().speed(4.1));
        assertThrows(IllegalArgumentException.class, () -> SpeechRequest.builder().speed(Double.NaN));
    }
}
