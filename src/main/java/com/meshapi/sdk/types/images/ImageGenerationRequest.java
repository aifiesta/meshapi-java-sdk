package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Set {@code outputFormat} to {@code "gif"} to request an animated GIF. Use
 * {@link Builder#frameCount(Integer)} and {@link Builder#frameDurationMs(Integer)} to control
 * the animation. Both are optional.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageGenerationRequest {

    @JsonProperty("model")
    private final String model;

    @JsonProperty("prompt")
    private final String prompt;

    @JsonProperty("output_format")
    private final String outputFormat;

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageGenerationRequest(Builder b) {
        this.model = b.model;
        this.prompt = b.prompt;
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getModel() {
        return model;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    /** @return number of frames in a GIF result, or {@code null} if not set. */
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return duration of each GIF frame in milliseconds, or {@code null} if not set. */
    public Integer getFrameDurationMs() {
        return frameDurationMs;
    }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        /** Sets the model id. Use an id returned by {@code GET /v1/models}. */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /** Sets the text prompt. */
        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        /** Sets the output format, for example {@code "gif"}. */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Sets the number of frames in a GIF result.
         *
         * @param frameCount between 2 and 16 inclusive, or {@code null} to omit
         * @throws IllegalArgumentException if outside 2-16
         */
        public Builder frameCount(Integer frameCount) {
            if (frameCount != null && (frameCount < 2 || frameCount > 16)) {
                throw new IllegalArgumentException("frameCount must be between 2 and 16");
            }
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets how long each GIF frame is shown, in milliseconds.
         *
         * @param frameDurationMs at least 20, or {@code null} to omit
         * @throws IllegalArgumentException if below 20
         */
        public Builder frameDurationMs(Integer frameDurationMs) {
            if (frameDurationMs != null && frameDurationMs < 20) {
                throw new IllegalArgumentException("frameDurationMs must be at least 20");
            }
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        /** @return the built request. */
        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
