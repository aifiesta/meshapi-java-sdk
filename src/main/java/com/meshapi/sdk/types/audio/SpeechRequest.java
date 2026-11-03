package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/audio/speech}.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SpeechRequest {

    /** Smallest accepted value for {@code speed}. */
    public static final double MIN_SPEED = 0.25;

    /** Largest accepted value for {@code speed}. */
    public static final double MAX_SPEED = 4.0;

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

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the model id */
    public String getModel() {
        return model;
    }

    /** @return the text to synthesize */
    public String getInput() {
        return input;
    }

    /** @return the voice name, or {@code null} */
    public String getVoice() {
        return voice;
    }

    /** @return the audio response format, or {@code null} */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * @return the playback speed multiplier, or {@code null} when not set
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

        /** Sets the audio response format. */
        public Builder responseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        /**
         * Sets the speech rate multiplier, between {@value SpeechRequest#MIN_SPEED} and
         * {@value SpeechRequest#MAX_SPEED} inclusive.
         *
         * <p>The value is forwarded to the provider as sent and is never clamped. A model that
         * does not read a rate control serves audio at its default rate.
         *
         * @param speed the multiplier, or {@code null} to omit the field
         * @return this builder
         * @throws IllegalArgumentException if the value is outside the accepted range or not finite
         */
        public Builder speed(Double speed) {
            if (speed != null
                    && (speed.isNaN() || speed < MIN_SPEED || speed > MAX_SPEED)) {
                throw new IllegalArgumentException(
                        "speed must be between " + MIN_SPEED + " and " + MAX_SPEED + ", got " + speed);
            }
            this.speed = speed;
            return this;
        }

        /** @return the built request */
        public SpeechRequest build() {
            return new SpeechRequest(this);
        }
    }
}
