package com.meshapi.sdk.types.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image output options for image generation through {@code POST /v1/chat/completions}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the JSON body.
 *
 * <p>Animated output: set {@link Builder#outputFormat(String)} to {@code "gif"} and optionally
 * {@link Builder#frameCount(Integer)} and {@link Builder#frameDurationMs(Integer)}.
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

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the output format, or {@code null}. */
    public String getOutputFormat() {
        return outputFormat;
    }

    /** @return the number of GIF frames, or {@code null}. */
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return the per-frame GIF duration in milliseconds, or {@code null}. */
    public Integer getFrameDurationMs() {
        return frameDurationMs;
    }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Sets the output format, for example {@code "gif"} for animated output. */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Sets the number of frames in a GIF result ({@code frame_count}).
         * The server accepts values from 2 to 16 when {@code output_format} is {@code "gif"}.
         */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /**
         * Sets how long each GIF frame is shown, in milliseconds ({@code frame_duration_ms}).
         * The server accepts values of 20 or more when {@code output_format} is {@code "gif"}.
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
