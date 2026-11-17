package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Animated output: set {@code outputFormat} to {@code "gif"} and optionally
 * {@link Builder#frameCount(Integer)} (2-16) and
 * {@link Builder#frameDurationMs(Integer)} (at least 20). Only models that support
 * GIF output accept these fields; other models return a 400. A GIF request cannot be
 * combined with a mask, {@code output_compression} or {@code partial_images}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImageGenerationRequest {

    @JsonProperty("model")
    public String model;

    @JsonProperty("prompt")
    public String prompt;

    @JsonProperty("output_format")
    public String outputFormat;

    /** Number of frames in a GIF result (2-16). */
    @JsonProperty("frame_count")
    public Integer frameCount;

    /** Duration of each GIF frame in milliseconds (at least 20). */
    @JsonProperty("frame_duration_ms")
    public Integer frameDurationMs;

    /** Required by Jackson. */
    public ImageGenerationRequest() {
    }

    private ImageGenerationRequest(Builder b) {
        this.model = b.model;
        this.prompt = b.prompt;
        this.outputFormat = b.outputFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private String outputFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        /** @param model model id from {@code GET /v1/models} */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /** @param prompt text prompt */
        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
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

        /** @return the built request */
        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
