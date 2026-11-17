package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image generation options for the chat image path.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
 *
 * <p>Animated output: set {@code outputFormat("gif")} and optionally {@link Builder#frameCount(Integer)}
 * and {@link Builder#frameDurationMs(Integer)}. GIF output is only available on models that support it.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageOptions {

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

    private ImageOptions(Builder b) {
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

    public String getSize() { return size; }

    public String getQuality() { return quality; }

    public String getOutputFormat() { return outputFormat; }

    /** Number of frames in a GIF result, or {@code null} if unset. */
    public Integer getFrameCount() { return frameCount; }

    /** Duration of each GIF frame in milliseconds, or {@code null} if unset. */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String size;
        private String quality;
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Image size, for example {@code "1024x1024"}. */
        public Builder size(String size) { this.size = size; return this; }

        /** Image quality. */
        public Builder quality(String quality) { this.quality = quality; return this; }

        /** Output format, for example {@code "png"} or {@code "gif"}. */
        public Builder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }

        /** Number of frames in an animated GIF result (2 to 16). */
        public Builder frameCount(Integer frameCount) { this.frameCount = frameCount; return this; }

        /** Duration of each GIF frame in milliseconds (20 or more). */
        public Builder frameDurationMs(Integer frameDurationMs) { this.frameDurationMs = frameDurationMs; return this; }

        /** Builds the options. */
        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
