package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image output options used on the chat image path.
 *
 * <p>Set {@code outputFormat} to {@code "gif"} to request an animated GIF, and optionally
 * {@link Builder#frameCount(Integer)} (2-16) and {@link Builder#frameDurationMs(Integer)}
 * (at least 20 ms per frame).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageOptions {

    @JsonProperty("output_format")
    private final String outputFormat;

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageOptions(Builder b) {
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getOutputFormat() { return outputFormat; }

    /** Number of frames in a GIF result (2-16), or {@code null}. */
    public Integer getFrameCount() { return frameCount; }

    /** Duration of each GIF frame in milliseconds (at least 20), or {@code null}. */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

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

        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
