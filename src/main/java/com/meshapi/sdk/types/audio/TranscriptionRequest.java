package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptionRequest {
    @JsonProperty("model") public String model;
    @JsonProperty("language_code") public String languageCode;
    @JsonProperty("tag_audio_events") public Boolean tagAudioEvents;
    @JsonProperty("num_speakers") public Integer numSpeakers;
    @JsonProperty("timestamps_granularity") public String timestampsGranularity;
    @JsonProperty("diarize") public Boolean diarize;
    @JsonProperty("diarization_threshold") public Double diarizationThreshold;
    @JsonProperty("additional_formats") public String additionalFormats;
    @JsonProperty("file_format") public String fileFormat;
    @JsonProperty("cloud_storage_url") public String cloudStorageUrl;
    @JsonProperty("source_url") public String sourceUrl;
    @JsonProperty("webhook") public Boolean webhook;
    @JsonProperty("webhook_id") public String webhookId;
    @JsonProperty("temperature") public Double temperature;
    @JsonProperty("seed") public Integer seed;
    @JsonProperty("use_multi_channel") public Boolean useMultiChannel;
    @JsonProperty("webhook_metadata") public String webhookMetadata;
    @JsonProperty("entity_detection") public String entityDetection;
    @JsonProperty("no_verbatim") public Boolean noVerbatim;
    @JsonProperty("detect_speaker_roles") public Boolean detectSpeakerRoles;
    @JsonProperty("entity_redaction") public String entityRedaction;
    @JsonProperty("entity_redaction_mode") public String entityRedactionMode;
    @JsonProperty("keyterms") public List<String> keyterms;
    @JsonProperty("with_timestamps") public Boolean withTimestamps;
    @JsonProperty("debug_mode") public Boolean debugMode;

    private TranscriptionRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final TranscriptionRequest req = new TranscriptionRequest();

        public Builder model(String model) { req.model = model; return this; }
        public Builder languageCode(String languageCode) { req.languageCode = languageCode; return this; }
        public Builder diarize(Boolean diarize) { req.diarize = diarize; return this; }
        public Builder numSpeakers(Integer numSpeakers) { req.numSpeakers = numSpeakers; return this; }
        public Builder fileFormat(String fileFormat) { req.fileFormat = fileFormat; return this; }
        public Builder temperature(Double temperature) { req.temperature = temperature; return this; }
        public Builder seed(Integer seed) { req.seed = seed; return this; }
        public Builder withTimestamps(Boolean withTimestamps) { req.withTimestamps = withTimestamps; return this; }
        public Builder debugMode(Boolean debugMode) { req.debugMode = debugMode; return this; }
        public Builder keyterms(List<String> keyterms) { req.keyterms = keyterms; return this; }

        public TranscriptionRequest build() { return req; }
    }
}
