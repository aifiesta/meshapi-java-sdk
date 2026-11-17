package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Set {@code outputFormat} to {@code "gif"} to request an animated GIF. With GIF output,
 * {@link Builder#frameCount(Integer)} and {@link Builder#frameDurationMs(Integer)} control the
 * animation. Both are optional.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageGenerationRequest {

    @JsonProperty("model")
    private final String model;

    @JsonProperty("prompt")
    private final String prompt;

    @JsonProperty("n")
    private final Integer n;

    @JsonProperty("size")
    private final String size;

    @JsonProperty("output_format")
    private final String outputFormat;

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageGenerationRequest(Builder b) {
        this.model = b.model;
        this.prompt = b.prompt;
        this.n = b.n;
        this.size = b.size;
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    public String getModel() { return model; }

    public String getPrompt() { return prompt; }

    public Integer getN() { return n; }

    public String getSize() { return size; }

    public String getOutputFormat() { return outputFormat; }

    /** @return number of frames in a GIF result, or {@code null} if unset */
    public Integer getFrameCount() { return frameCount; }

    /** @return duration of each GIF frame in milliseconds, or {@code null} if unset */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private Integer n;
        private String size;
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Model id, as returned by {@code GET /v1/models}. */
        public Builder model(String model) { this.model = model; return this; }

        /** Text prompt describing the image. */
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }

        /** Number of images to generate. */
        public Builder n(Integer n) { this.n = n; return this; }

        /** Image size, for example {@code "1024x1024"}. */
        public Builder size(String size) { this.size = size; return this; }

        /** Output format. Use {@code "gif"} for an animated GIF. */
        public Builder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }

        /**
         * Number of frames in a GIF result (2-16). Only applies when {@code outputFormat} is
         * {@code "gif"}.
         */
        public Builder frameCount(Integer frameCount) { this.frameCount = frameCount; return this; }

        /**
         * Duration of each GIF frame in milliseconds (20 or more). Only applies when
         * {@code outputFormat} is {@code "gif"}.
         */
        public Builder frameDurationMs(Integer frameDurationMs) { this.frameDurationMs = frameDurationMs; return this; }

        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
