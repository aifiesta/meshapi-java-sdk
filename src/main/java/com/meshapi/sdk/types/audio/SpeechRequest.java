package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeechRequest {
    @JsonProperty("input") public String input;
    @JsonProperty("model") public String model;
    @JsonProperty("voice") public String voice;
    @JsonProperty("stream") public Boolean stream;
    @JsonProperty("response_format") public String responseFormat;
    @JsonProperty("language_code") public String languageCode;
    @JsonProperty("voice_settings") public VoiceSettings voiceSettings;
    @JsonProperty("pronunciation_dictionary_locators") public List<PronunciationDictionaryLocator> pronunciationDictionaryLocators;
    @JsonProperty("seed") public Integer seed;
    @JsonProperty("previous_text") public String previousText;
    @JsonProperty("next_text") public String nextText;
    @JsonProperty("previous_request_ids") public List<String> previousRequestIds;
    @JsonProperty("next_request_ids") public List<String> nextRequestIds;
    @JsonProperty("apply_text_normalization") public String applyTextNormalization;
    @JsonProperty("apply_language_text_normalization") public Boolean applyLanguageTextNormalization;
    @JsonProperty("use_pvc_as_ivc") public Boolean usePvcAsIvc;
    @JsonProperty("enable_logging") public Boolean enableLogging;
    @JsonProperty("optimize_streaming_latency") public Integer optimizeStreamingLatency;
    @JsonProperty("speaker") public String speaker;
    @JsonProperty("target_language_code") public String targetLanguageCode;
    @JsonProperty("pitch") public Double pitch;
    @JsonProperty("pace") public Double pace;
    @JsonProperty("loudness") public Double loudness;
    @JsonProperty("speech_sample_rate") public Integer speechSampleRate;
    @JsonProperty("enable_preprocessing") public Boolean enablePreprocessing;

    private SpeechRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final SpeechRequest req = new SpeechRequest();

        public Builder input(String input) { req.input = input; return this; }
        public Builder model(String model) { req.model = model; return this; }
        public Builder voice(String voice) { req.voice = voice; return this; }
        public Builder stream(Boolean stream) { req.stream = stream; return this; }
        public Builder responseFormat(String responseFormat) { req.responseFormat = responseFormat; return this; }
        public Builder languageCode(String languageCode) { req.languageCode = languageCode; return this; }
        public Builder voiceSettings(VoiceSettings voiceSettings) { req.voiceSettings = voiceSettings; return this; }
        public Builder seed(Integer seed) { req.seed = seed; return this; }
        public Builder speaker(String speaker) { req.speaker = speaker; return this; }
        public Builder targetLanguageCode(String targetLanguageCode) { req.targetLanguageCode = targetLanguageCode; return this; }
        public Builder pitch(Double pitch) { req.pitch = pitch; return this; }
        public Builder pace(Double pace) { req.pace = pace; return this; }
        public Builder loudness(Double loudness) { req.loudness = loudness; return this; }
        public Builder speechSampleRate(Integer speechSampleRate) { req.speechSampleRate = speechSampleRate; return this; }
        public Builder enablePreprocessing(Boolean enablePreprocessing) { req.enablePreprocessing = enablePreprocessing; return this; }

        public SpeechRequest build() { return req; }
    }
}
