package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request body for image generation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImageGenerationRequest {

    private final String model;
    private final String prompt;
    private final Integer frameCount;
    private final Integer frameDurationMs;

    private ImageGenerationRequest(Builder b) {
        this.model = b.model;
        this.prompt = b.prompt;
        this.frameCount = b.frameCount;
        this.frameDurationMs = b.frameDurationMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    @JsonProperty("prompt")
    public String getPrompt() {
        return prompt;
    }

    /** Number of frames to generate. */
    @JsonProperty("frame_count")
    public Integer getFrameCount() {
        return frameCount;
    }

    /** Duration of each frame in milliseconds. */
    @JsonProperty("frame_duration_ms")
    public Integer getFrameDurationMs() {
        return frameDurationMs;
    }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private Integer frameCount;
        private Integer frameDurationMs;

        private Builder() {
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        /** Number of frames to generate. */
        public Builder frameCount(Integer frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        /** Duration of each frame in milliseconds. */
        public Builder frameDurationMs(Integer frameDurationMs) {
            this.frameDurationMs = frameDurationMs;
            return this;
        }

        public ImageGenerationRequest build() {
            return new ImageGenerationRequest(this);
        }
    }
}
