package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Set {@code outputFormat} to {@code "gif"} to request an animated GIF. When producing a GIF,
 * {@link Builder#frameCount(Integer)} sets the number of frames (2-16) and
 * {@link Builder#frameDurationMs(Integer)} sets how long each frame is shown, in milliseconds
 * (at least 20). Both are optional.
 *
 * <p>GIF output is only available on models that support it. Other models, or
 * {@code model="auto"}, return a 400. A GIF request combined with {@code output_compression}
 * or {@code partial_images} returns a 422.
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

    public static Builder builder() {
        return new Builder();
    }

    public String getModel() { return model; }

    public String getPrompt() { return prompt; }

    public String getOutputFormat() { return outputFormat; }

    /** Number of frames in a GIF result (2-16), or {@code null} to use the default. */
    public Integer getFrameCount() { return frameCount; }

    /** Duration of each GIF frame in milliseconds (at least 20), or {@code null} to use the default. */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Model id to use. Pick one returned by {@code GET /v1/models}. */
        public Builder model(String model) { this.model = model; return this; }

        /** Text prompt describing the image. */
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }

        /** Output format, for example {@code "png"} or {@code "gif"}. */
        public Builder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }

        /**
         * Number of frames for GIF output.
         *
         * @param frameCount between 2 and 16 inclusive, or {@code null}
         * @throws IllegalArgumentException if out of range
         */
        public Builder frameCount(Integer frameCount) {
            if (frameCount != null && (frameCount < 2 || frameCount > 16)) {
                throw new IllegalArgumentException("frameCount must be between 2 and 16");
            }
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Duration of each GIF frame in milliseconds.
         *
         * @param frameDurationMs at least 20, or {@code null}
         * @throws IllegalArgumentException if below 20
         */
        public Builder frameDurationMs(Integer frameDurationMs) {
            if (frameDurationMs != null && frameDurationMs < 20) {
                throw new IllegalArgumentException("frameDurationMs must be at least 20");
            }
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
