package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image output options used on the chat image path.
 *
 * <p>Set {@code outputFormat} to {@code "gif"} to request an animated GIF, and optionally set
 * {@link Builder#frameCount(Integer)} and {@link Builder#frameDurationMs(Integer)}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageOptions {

    @JsonProperty("size")
    private final String size;

    @JsonProperty("output_format")
    private final String outputFormat;

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageOptions(Builder b) {
        this.size = b.size;
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    public String getSize() { return size; }

    public String getOutputFormat() { return outputFormat; }

    /** @return number of frames in a GIF result, or {@code null} if unset */
    public Integer getFrameCount() { return frameCount; }

    /** @return duration of each GIF frame in milliseconds, or {@code null} if unset */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String size;
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Image size, for example {@code "1024x1024"}. */
        public Builder size(String size) { this.size = size; return this; }

        /** Output format. Use {@code "gif"} for an animated GIF. */
        public Builder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }

        /** Number of frames in a GIF result (2-16). */
        public Builder frameCount(Integer frameCount) { this.frameCount = frameCount; return this; }

        /** Duration of each GIF frame in milliseconds (20 or more). */
        public Builder frameDurationMs(Integer frameDurationMs) { this.frameDurationMs = frameDurationMs; return this; }

        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
