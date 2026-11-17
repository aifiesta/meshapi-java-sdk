package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the request.
 *
 * <p>Animated output: set {@code outputFormat("gif")} and optionally {@link Builder#frameCount(Integer)}
 * and {@link Builder#frameDurationMs(Integer)}. GIF output is only available on models that support it;
 * other models return a 400.
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

    @JsonProperty("quality")
    private final String quality;

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
        this.quality = b.quality;
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** Returns a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getModel() { return model; }

    public String getPrompt() { return prompt; }

    public Integer getN() { return n; }

    public String getSize() { return size; }

    public String getQuality() { return quality; }

    public String getOutputFormat() { return outputFormat; }

    /** Number of frames in an animated GIF, or {@code null} if unset. */
    public Integer getFrameCount() { return frameCount; }

    /** Duration of each GIF frame in milliseconds, or {@code null} if unset. */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private Integer n;
        private String size;
        private String quality;
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

        /** Image quality. */
        public Builder quality(String quality) { this.quality = quality; return this; }

        /** Output format, for example {@code "png"} or {@code "gif"}. */
        public Builder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }

        /**
         * Number of frames in an animated GIF (2 to 16). Applies when {@code output_format} is {@code "gif"}.
         *
         * @param frameCount frame count, or {@code null} to omit
         * @return this builder
         */
        public Builder frameCount(Integer frameCount) { this.frameCount = frameCount; return this; }

        /**
         * Duration of each GIF frame in milliseconds (20 or more). Applies when {@code output_format} is {@code "gif"}.
         *
         * @param frameDurationMs frame duration in milliseconds, or {@code null} to omit
         * @return this builder
         */
        public Builder frameDurationMs(Integer frameDurationMs) { this.frameDurationMs = frameDurationMs; return this; }

        /** Builds the request. */
        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
