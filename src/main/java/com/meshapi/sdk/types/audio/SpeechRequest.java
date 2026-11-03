package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/audio/speech}.
 *
 * <p>Build instances with {@link #builder()}.
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

    private SpeechRequest(Builder b) {
        this.model = b.model;
        this.input = b.input;
        this.voice = b.voice;
        this.responseFormat = b.responseFormat;
        this.speed = b.speed;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the model id. */
    public String getModel() {
        return model;
    }

    /** @return the text to synthesize. */
    public String getInput() {
        return input;
    }

    /** @return the voice name. */
    public String getVoice() {
        return voice;
    }

    /** @return the audio output format. */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * @return the playback speed multiplier, or {@code null} when unset.
     */
    public Double getSpeed() {
        return speed;
    }

    /** Builder for {@link SpeechRequest}. */
    public static final class Builder {
        private String model;
        private String input;
        private String voice;
        private String responseFormat;
        private Double speed;

        private Builder() {
        }

        /** Sets the model id. */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /** Sets the text to synthesize. */
        public Builder input(String input) {
            this.input = input;
            return this;
        }

        /** Sets the voice name. */
        public Builder voice(String voice) {
            this.voice = voice;
            return this;
        }

        /** Sets the audio output format (for example {@code mp3}). */
        public Builder responseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        /**
         * Sets the playback speed multiplier sent as the top-level {@code speed} field.
         *
         * <p>The API accepts values from 0.25 to 4.0. The value is forwarded as sent and is
         * never clamped; whether it takes effect depends on the provider serving the model.
         *
         * @param speed speed multiplier, or {@code null} to omit the field
         */
        public Builder speed(Double speed) {
            this.speed = speed;
            return this;
        }

        /** @return the built request. */
        public SpeechRequest build() {
            return new SpeechRequest(this);
        }
    }
}
