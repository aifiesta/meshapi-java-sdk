package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the JSON body.
 *
 * <p>Animated GIF output: set {@code outputFormat("gif")} and optionally
 * {@link Builder#frameCount(Integer)} (2-16) and {@link Builder#frameDurationMs(Integer)} (at least 20).
 * GIF output is only accepted by models that support it; other models return a 400.
 * GIF cannot be combined with {@code output_compression} or {@code partial_images} (422).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageGenerationRequest {

    /** Minimum accepted value for {@code frame_count}. */
    public static final int MIN_FRAME_COUNT = 2;
    /** Maximum accepted value for {@code frame_count}. */
    public static final int MAX_FRAME_COUNT = 16;
    /** Minimum accepted value for {@code frame_duration_ms}. */
    public static final int MIN_FRAME_DURATION_MS = 20;

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

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    public String getModel() { return model; }

    public String getPrompt() { return prompt; }

    public Integer getN() { return n; }

    public String getSize() { return size; }

    public String getQuality() { return quality; }

    public String getOutputFormat() { return outputFormat; }

    /** @return number of frames in an animated GIF result, or {@code null} if unset */
    public Integer getFrameCount() { return frameCount; }

    /** @return duration of each GIF frame in milliseconds, or {@code null} if unset */
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

        /** Model id from {@code GET /v1/models}. */
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
         * Number of frames in an animated GIF result. Used with {@code outputFormat("gif")}.
         *
         * @param frameCount frame count between 2 and 16, or {@code null} to omit
         * @throws IllegalArgumentException if outside 2-16
         */
        public Builder frameCount(Integer frameCount) {
            if (frameCount != null && (frameCount < MIN_FRAME_COUNT || frameCount > MAX_FRAME_COUNT)) {
                throw new IllegalArgumentException("frameCount must be between 2 and 16");
            }
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Duration of each frame of an animated GIF result, in milliseconds. Used with {@code outputFormat("gif")}.
         *
         * @param frameDurationMs duration of at least 20 ms, or {@code null} to omit
         * @throws IllegalArgumentException if below 20
         */
        public Builder frameDurationMs(Integer frameDurationMs) {
            if (frameDurationMs != null && frameDurationMs < MIN_FRAME_DURATION_MS) {
                throw new IllegalArgumentException("frameDurationMs must be at least 20");
            }
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        /** @return the built request */
        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
