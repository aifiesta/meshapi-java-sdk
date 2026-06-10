package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.audio.SpeechRequest;
import com.meshapi.sdk.types.audio.TranscriptionRequest;
import com.meshapi.sdk.types.audio.TranscriptionResponse;
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
    void audio_stt_from_tts() {
        MeshAPI client = newClient();
        SpeechRequest ttsReq = SpeechRequest.builder()
                .input("Hello from MeshAPI audio test.")
                .model(TTS_MODEL)
                .build();
        byte[] audio = client.audio().synthesize(ttsReq);
        assertNotNull(audio, "TTS step returned null");
        assertTrue(audio.length > 0, "TTS step returned empty bytes");

        TranscriptionRequest sttReq = TranscriptionRequest.builder()
                .model(STT_MODEL)
                .build();
        TranscriptionResponse result = client.audio().transcribe(audio, "tts_output.wav", sttReq);
        assertNotNull(result, "expected non-null transcription response");
        assertNotNull(result.text, "expected non-null transcription text");
        assertFalse(result.text.isEmpty(), "expected non-empty transcription text");
        System.out.printf("[PASS] audio.transcribe (via TTS audio) -> %s%n", result.text);
    }

    @Test
    void audio_listVoices() {
        MeshAPI client = newClient();
        Map<String, Object> voices = client.audio().listVoices(Map.of("page_size", "5"));
        assertNotNull(voices);
        System.out.printf("[PASS] audio.listVoices -> %s%n", voices.getClass().getSimpleName());
    }
}
