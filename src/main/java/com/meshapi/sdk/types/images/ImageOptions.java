package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image output options used on the chat image path.
 *
 * <p>Set {@code outputFormat("gif")} with optional {@link Builder#frameCount(Integer)} and
 * {@link Builder#frameDurationMs(Integer)} to request an animated result. Unset fields are omitted
 * from the JSON body.
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

    /** @return the requested output format, or {@code null}. */
    public String getOutputFormat() {
        return outputFormat;
    }

    /** @return the number of GIF frames, or {@code null} if unset. */
    public Integer getFrameCount() {
        return frameCount;
    }

    /** @return the duration of each GIF frame in milliseconds, or {@code null} if unset. */
    public Integer getFrameDurationMs() {
        return frameDurationMs;
    }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        /** Sets the output format, for example {@code "gif"}. */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /** Sets the number of GIF frames. Accepted range is 2 to 16. */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /** Sets each GIF frame's duration in milliseconds. Minimum is 20. */
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
