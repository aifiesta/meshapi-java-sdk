package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image options for image output on the chat image path.
 *
 * <p>Set {@code outputFormat} to {@code "gif"} and optionally
 * {@link Builder#frameCount(Integer)} (2-16) and
 * {@link Builder#frameDurationMs(Integer)} (at least 20) to request an animated GIF.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImageOptions {

    @JsonProperty("output_format")
    public String outputFormat;

    /** Number of frames in a GIF result (2-16). */
    @JsonProperty("frame_count")
    public Integer frameCount;

    /** Duration of each GIF frame in milliseconds (at least 20). */
    @JsonProperty("frame_duration_ms")
    public Integer frameDurationMs;

    /** Required by Jackson. */
    public ImageOptions() {
    }

    private ImageOptions(Builder b) {
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ImageOptions}. */
    public static final class Builder {
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        /** @param outputFormat output format, for example {@code "gif"} */
        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /** @param frameCount number of GIF frames (2-16) */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /** @param frameDurationMs duration of each GIF frame in milliseconds (at least 20) */
        public Builder frameDurationMs(Integer frameDurationMs) {
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        /** @return the built options */
        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
