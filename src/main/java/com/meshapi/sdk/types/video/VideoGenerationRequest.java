package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request body for video generation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class VideoGenerationRequest {

    private final String model;
    private final String prompt;
    private final Integer width;
    private final Integer height;

    private VideoGenerationRequest(Builder b) {
        this.model = b.model;
        this.prompt = b.prompt;
        this.width = b.width;
        this.height = b.height;
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

    /** Output width in pixels. */
    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    /** Output height in pixels. */
    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    /** Builder for {@link VideoGenerationRequest}. */
    public static final class Builder {
        private String model;
        private String prompt;
        private Integer width;
        private Integer height;

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

        /** Output width in pixels. */
        public Builder width(Integer width) {
            this.width = width;
            return this;
        }

        /** Output height in pixels. */
        public Builder height(Integer height) {
            this.height = height;
            return this;
        }

        public VideoGenerationRequest build() {
            return new VideoGenerationRequest(this);
        }
    }
}
