package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the JSON body.
 *
 * <p>Animated output: set {@code outputFormat("gif")} and optionally {@link Builder#frameCount(Integer)}
 * and {@link Builder#frameDurationMs(Integer)}. Only some models support GIF output; check
 * {@code GET /v1/models} before choosing one. The server rejects GIF requests sent to other models.
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

    /** @return the duration of each GIF frame in milliseconds, or {@code null}. */
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

        /** @param model a model id returned by {@code GET /v1/models}. */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /** @param prompt the text prompt describing the image. */
        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        /** @param outputFormat the output format, for example {@code "gif"} for animated output. */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Sets the number of frames in a GIF result. The server accepts values from 2 to 16.
         *
         * @param frameCount number of frames, or {@code null} to omit
         */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets how long each GIF frame is shown, in milliseconds. The server accepts values of 20 or more.
         *
         * @param frameDurationMs per-frame duration in milliseconds, or {@code null} to omit
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
