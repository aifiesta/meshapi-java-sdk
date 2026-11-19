package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for image generation.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageGenerationRequest {

    @JsonProperty("model")
    private final String model;

    @JsonProperty("prompt")
    private final String prompt;

    @JsonProperty("n")
    private final Integer n;

    @JsonProperty("size")
    private final String size;

    @JsonProperty("quality")
    private final String quality;

    @JsonProperty("response_format")
    private final String responseFormat;

    @JsonProperty("frame_count")
    private final Integer frameCount;

    @JsonProperty("frame_duration_ms")
    private final Integer frameDurationMs;

    private ImageGenerationRequest(Builder b) {
        this.model = b.model;
        this.prompt = b.prompt;
        this.n = b.n;
        this.size = b.size;
        this.quality = b.quality;
        this.responseFormat = b.responseFormat;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getModel() { return model; }

    public String getPrompt() { return prompt; }

    public Integer getN() { return n; }

    public String getSize() { return size; }

    public String getQuality() { return quality; }

    public String getResponseFormat() { return responseFormat; }

    /** @return the requested number of frames, or {@code null} if unset. */
    public Integer getFrameCount() { return frameCount; }

    /** @return the requested duration of each frame in milliseconds, or {@code null} if unset. */
    public Integer getFrameDurationMs() { return frameDurationMs; }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private Integer n;
        private String size;
        private String quality;
        private String responseFormat;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {}

        /** Model id; use an id returned by {@code GET /v1/models}. */
        public Builder model(String model) { this.model = model; return this; }

        /** Text prompt describing the image. */
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }

        /** Number of images to generate. */
        public Builder n(Integer n) { this.n = n; return this; }

        /** Output size, for example {@code 1024x1024}. */
        public Builder size(String size) { this.size = size; return this; }

        /** Output quality. */
        public Builder quality(String quality) { this.quality = quality; return this; }

        /** Response format, for example {@code url} or {@code b64_json}. */
        public Builder responseFormat(String responseFormat) { this.responseFormat = responseFormat; return this; }

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

        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
