package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/audio/speech}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SpeechRequest {

    @JsonProperty("model")
    private final String model;

    @JsonProperty("input")
    private final String input;

    @JsonProperty("voice")
    private final String voice;

    @JsonProperty("response_format")
    private final String responseFormat;

    @JsonProperty("speed")
    private final Double speed;

    @JsonProperty("pitch")
    private final Double pitch;

    @JsonProperty("vol")
    private final Double vol;

    @JsonProperty("emotion")
    private final String emotion;

    private SpeechRequest(Builder b) {
        this.model = b.model;
        this.input = b.input;
        this.voice = b.voice;
        this.responseFormat = b.responseFormat;
        this.speed = b.speed;
        this.pitch = b.pitch;
        this.vol = b.vol;
        this.emotion = b.emotion;
    }

    /** Returns a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getModel() { return model; }

    public String getInput() { return input; }

    public String getVoice() { return voice; }

    public String getResponseFormat() { return responseFormat; }

    public Double getSpeed() { return speed; }

    public Double getPitch() { return pitch; }

    /** Output volume, or {@code null} when not set. */
    public Double getVol() { return vol; }

    /** Speaking emotion, or {@code null} when not set. */
    public String getEmotion() { return emotion; }

    /** Builder for {@link SpeechRequest}. */
    public static final class Builder {
        private String model;
        private String input;
        private String voice;
        private String responseFormat;
        private Double speed;
        private Double pitch;
        private Double vol;
        private String emotion;

        private Builder() {}

        /** Model id, as returned by {@code GET /v1/models}. */
        public Builder model(String model) { this.model = model; return this; }

        /** Text to synthesize. */
        public Builder input(String input) { this.input = input; return this; }

        /** Voice to use. */
        public Builder voice(String voice) { this.voice = voice; return this; }

        /** Audio output format. */
        public Builder responseFormat(String responseFormat) { this.responseFormat = responseFormat; return this; }

        /** Playback speed. */
        public Builder speed(Double speed) { this.speed = speed; return this; }

        /**
         * Voice pitch. MiniMax models require a whole number in [-12, 12].
         */
        public Builder pitch(Double pitch) { this.pitch = pitch; return this; }

        /**
         * Output volume, in the range {@code 0 < vol <= 10}. Optional.
         *
         * <p>Only MiniMax speech models read this field; other models ignore it.
         * Out-of-range values are refused with a 422 before the request reaches the provider.
         */
        public Builder vol(Double vol) { this.vol = vol; return this; }

        /**
         * Speaking emotion, one of MiniMax's emotion values (for example {@code "happy"},
         * {@code "sad"}, {@code "neutral"}). Optional.
         *
         * <p>Only MiniMax speech models read this field. {@code "fluent"} and {@code "whisper"}
         * are accepted only on speech-2.6 models and are refused with a 422 otherwise.
         */
        public Builder emotion(String emotion) { this.emotion = emotion; return this; }

        /** Builds the request. */
        public SpeechRequest build() {
            return new SpeechRequest(this);
        }
    }
}
