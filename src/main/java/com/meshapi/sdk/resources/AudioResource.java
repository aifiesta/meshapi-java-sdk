package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.audio.SpeechRequest;
import com.meshapi.sdk.types.audio.TranscriptionRequest;
import com.meshapi.sdk.types.audio.TranscriptionResponse;
import com.meshapi.sdk.types.audio.TranscriptionTranslateRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class AudioResource {
    private final HttpClient http;

    public AudioResource(HttpClient http) {
        this.http = http;
    }

    /** POST /v1/audio/speech — returns raw audio bytes. */
    public byte[] synthesize(SpeechRequest params) {
        return http.postBytes("/v1/audio/speech", params);
    }

    /** POST /v1/audio/transcriptions — multipart file upload. */
    public TranscriptionResponse transcribe(byte[] fileData, String filename, TranscriptionRequest params) {
        Map<String, String> fields = transcriptionRequestToFields(params);
        return http.postMultipart("/v1/audio/transcriptions", fields, fileData, filename, TranscriptionResponse.class);
    }

    /** GET /v1/audio/transcriptions/{transcription_id} */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTranscription(String transcriptionId) {
        return http.get("/v1/audio/transcriptions/" + transcriptionId, Map.class);
    }

    /** POST /v1/audio/transcriptions/translate — multipart file upload, translates to English. */
    public TranscriptionResponse translate(byte[] fileData, String filename, TranscriptionTranslateRequest params) {
        Map<String, String> fields = new LinkedHashMap<>();
        if (params != null) {
            if (params.model != null) fields.put("model", params.model);
            if (params.prompt != null) fields.put("prompt", params.prompt);
        }
        return http.postMultipart("/v1/audio/transcriptions/translate", fields, fileData, filename, TranscriptionResponse.class);
    }

    /** GET /v1/audio/voices */
    @SuppressWarnings("unchecked")
    public Map<String, Object> listVoices(Map<String, String> queryParams) {
        StringBuilder qs = new StringBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> e : queryParams.entrySet()) {
                if (qs.length() > 0) qs.append("&");
                qs.append(e.getKey()).append("=").append(e.getValue());
            }
        }
        return http.get("/v1/audio/voices", qs.length() > 0 ? qs.toString() : null, Map.class);
    }

    /** GET /v1/audio/voices/{voice_id} */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getVoice(String voiceId) {
        return http.get("/v1/audio/voices/" + voiceId, Map.class);
    }

    private static Map<String, String> transcriptionRequestToFields(TranscriptionRequest p) {
        Map<String, String> fields = new LinkedHashMap<>();
        if (p == null) return fields;
        if (p.model != null) fields.put("model", p.model);
        if (p.languageCode != null) fields.put("language_code", p.languageCode);
        if (p.tagAudioEvents != null) fields.put("tag_audio_events", String.valueOf(p.tagAudioEvents));
        if (p.numSpeakers != null) fields.put("num_speakers", String.valueOf(p.numSpeakers));
        if (p.timestampsGranularity != null) fields.put("timestamps_granularity", p.timestampsGranularity);
        if (p.diarize != null) fields.put("diarize", String.valueOf(p.diarize));
        if (p.diarizationThreshold != null) fields.put("diarization_threshold", String.valueOf(p.diarizationThreshold));
        if (p.additionalFormats != null) fields.put("additional_formats", p.additionalFormats);
        if (p.fileFormat != null) fields.put("file_format", p.fileFormat);
        if (p.cloudStorageUrl != null) fields.put("cloud_storage_url", p.cloudStorageUrl);
        if (p.sourceUrl != null) fields.put("source_url", p.sourceUrl);
        if (p.webhook != null) fields.put("webhook", String.valueOf(p.webhook));
        if (p.webhookId != null) fields.put("webhook_id", p.webhookId);
        if (p.temperature != null) fields.put("temperature", String.valueOf(p.temperature));
        if (p.seed != null) fields.put("seed", String.valueOf(p.seed));
        if (p.useMultiChannel != null) fields.put("use_multi_channel", String.valueOf(p.useMultiChannel));
        if (p.webhookMetadata != null) fields.put("webhook_metadata", p.webhookMetadata);
        if (p.entityDetection != null) fields.put("entity_detection", p.entityDetection);
        if (p.noVerbatim != null) fields.put("no_verbatim", String.valueOf(p.noVerbatim));
        if (p.detectSpeakerRoles != null) fields.put("detect_speaker_roles", String.valueOf(p.detectSpeakerRoles));
        if (p.entityRedaction != null) fields.put("entity_redaction", p.entityRedaction);
        if (p.entityRedactionMode != null) fields.put("entity_redaction_mode", p.entityRedactionMode);
        if (p.withTimestamps != null) fields.put("with_timestamps", String.valueOf(p.withTimestamps));
        if (p.debugMode != null) fields.put("debug_mode", String.valueOf(p.debugMode));
        return fields;
    }
}
