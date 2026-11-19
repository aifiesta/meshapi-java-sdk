package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image output options.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageOptions {

    @JsonProperty("size")
    private final String size;

    @JsonProperty("quality")
    private final String quality;

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageOptions(Builder b) {
        this.size = b.size;
        this.quality = b.quality;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getSize() { return size; }

    public String getQuality() { return quality; }

    /** @return the requested number of frames, or {@code null} if unset. */
    public Integer getFrameCount() { return frameCount; }

    /** @return the requested duration of each frame in milliseconds, or {@code null} if unset. */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String size;
        private String quality;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Output size, for example {@code 1024x1024}. */
        public Builder size(String size) { this.size = size; return this; }

        /** Output quality. */
        public Builder quality(String quality) { this.quality = quality; return this; }

        /**
         * Number of frames to generate. Sent as {@code frame_count}.
         *
         * @param frameCount frame count, or {@code null} to omit
         */
        public Builder frameCount(Integer frameCount) { this.frameCount = frameCount; return this; }

        /**
         * Duration of each frame in milliseconds. Sent as {@code frame_duration_ms}.
         *
         * @param frameDurationMs per-frame duration, or {@code null} to omit
         */
        public Builder frameDurationMs(Integer frameDurationMs) { this.frameDurationMs = frameDurationMs; return this; }

        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
