package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the JSON body.
 *
 * <p>Animated output: set {@link Builder#outputFormat(String)} to {@code "gif"} and optionally
 * {@link Builder#frameCount(Integer)} and {@link Builder#frameDurationMs(Integer)}. GIF output is
 * only accepted by models that support it; other models return a 400.
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

    /** @return the model id, or {@code null}. */
    public String getModel() {
        return model;
    }

    /** @return the prompt, or {@code null}. */
    public String getPrompt() {
        return prompt;
    }

    /** @return the output format, or {@code null}. */
    public String getOutputFormat() {
        return outputFormat;
    }

    /** @return the number of GIF frames, or {@code null}. */
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return the per-frame GIF duration in milliseconds, or {@code null}. */
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

        private Builder() {}

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

        /** Sets the output format, for example {@code "gif"} for animated output. */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Sets the number of frames in a GIF result ({@code frame_count}).
         * The server accepts values from 2 to 16 when {@code output_format} is {@code "gif"}.
         */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets how long each GIF frame is shown, in milliseconds ({@code frame_duration_ms}).
         * The server accepts values of 20 or more when {@code output_format} is {@code "gif"}.
         */
        public Builder frameDurationMs(Integer frameDurationMs) {
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        /** @return the built request. */
        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
