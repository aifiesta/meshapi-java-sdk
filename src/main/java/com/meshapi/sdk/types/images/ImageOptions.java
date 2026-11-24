package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image options.
 *
 * <p>Use {@link #builder()} to construct an instance. Fields left unset are omitted
 * from the serialised JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageOptions {

    private final Integer frameCount;
    private final Integer frameDurationMs;

    private ImageOptions(Builder builder) {
        this.frameCount = builder.frameCount;
        this.frameDurationMs = builder.frameDurationMs;
    }

    /** @return the number of frames to generate, or {@code null} if unset. */
    @JsonProperty("frame_count")
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return the duration of each frame in milliseconds, or {@code null} if unset. */
    @JsonProperty("frame_duration_ms")
    public Integer getFrameDurationMs() {
        return frameDurationMs;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        /**
         * Sets {@code frame_count}: the number of frames to generate.
         *
         * @param frameCount frame count
         * @return this builder
         */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets {@code frame_duration_ms}: how long each frame is shown, in milliseconds.
         *
         * @param frameDurationMs frame duration in milliseconds
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
