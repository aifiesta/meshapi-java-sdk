package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Animated output: set {@code outputFormat("gif")} and optionally {@link Builder#frameCount(Integer)}
 * and {@link Builder#frameDurationMs(Integer)}. GIF output is only available on models that support it.
 * The server rejects a GIF request that also sets a mask, {@code output_compression} or
 * {@code partial_images}.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
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

    /** @return the model id, or {@code null} if unset. */
    public String getModel() {
        return model;
    }

    /** @return the text prompt. */
    public String getPrompt() {
        return prompt;
    }

    /** @return the requested output format (for example {@code "gif"}), or {@code null}. */
    public String getOutputFormat() {
        return outputFormat;
    }

    /** @return the number of GIF frames, or {@code null} if unset. */
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return the duration of each GIF frame in milliseconds, or {@code null} if unset. */
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

        /** Sets the output format, for example {@code "gif"} for animated output. */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Sets the number of frames in a GIF result. Accepted range is 2 to 16.
         * Only applies when the output format is {@code "gif"}.
         */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets how long each GIF frame is shown, in milliseconds. Minimum is 20.
         * Only applies when the output format is {@code "gif"}.
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
