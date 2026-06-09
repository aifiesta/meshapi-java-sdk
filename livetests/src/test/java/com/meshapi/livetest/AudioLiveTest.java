package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.audio.SpeechRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AudioLiveTest extends LiveTestBase {

    private static final String TTS_MODEL = System.getenv().getOrDefault("MESHAPI_TTS_MODEL", "sarvam/bulbul:v2");
    private static final String STT_MODEL = System.getenv().getOrDefault("MESHAPI_STT_MODEL", "sarvam/saaras:v3");

    @Test
    void audio_synthesize() {
        MeshAPI client = newClient();
        SpeechRequest req = SpeechRequest.builder()
                .input("Hello from MeshAPI audio test.")
                .model(TTS_MODEL)
                .build();
        byte[] audio = client.audio().synthesize(req);
        assertNotNull(audio);
        assertTrue(audio.length > 0);
        System.out.printf("[PASS] audio.synthesize -> %d bytes%n", audio.length);
    }

    @Test
    void audio_listVoices() {
        MeshAPI client = newClient();
        Map<String, Object> voices = client.audio().listVoices(Map.of("page_size", "5"));
        assertNotNull(voices);
        System.out.printf("[PASS] audio.listVoices -> %s%n", voices.getClass().getSimpleName());
    }
}
