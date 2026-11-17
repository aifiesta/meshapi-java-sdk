package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image generation options.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the
 * serialised JSON.
 *
 * <p>Animated output: when requesting a GIF, {@code frame_count} sets how many frames
 * the animation has (2 to 16) and {@code frame_duration_ms} sets how long each frame is
 * shown, in milliseconds (20 or more). Both are optional.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageOptions {

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageOptions(Builder b) {
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the number of animation frames, or {@code null} if unset. */
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return the per-frame duration in milliseconds, or {@code null} if unset. */
    public Integer getFrameDurationMs() {
        return frameDurationMs;
    }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        /**
         * Sets the number of frames in an animated GIF result. The API accepts 2 to 16.
         *
         * @param frameCount the frame count
         * @return this builder
         */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets how long each frame of an animated GIF result is shown, in milliseconds.
         * The API accepts 20 or more.
         *
         * @param frameDurationMs the per-frame duration in milliseconds
         * @return this builder
         */
        public Builder frameDurationMs(Integer frameDurationMs) {
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        /** @return the built options. */
        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
