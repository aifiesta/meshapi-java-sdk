package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for video generation.
 *
 * <p>Use {@link #builder()} to construct an instance. Fields left unset are omitted
 * from the serialised JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class VideoGenerationRequest {

    private final Integer width;
    private final Integer height;

    private VideoGenerationRequest(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
    }

    /** @return the output width in pixels, or {@code null} if unset. */
    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    /** @return the output height in pixels, or {@code null} if unset. */
    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link VideoGenerationRequest}. */
    public static final class Builder {
        private Integer width;
        private Integer height;

        private Builder() {
        }

        /**
         * Sets {@code width} in pixels. Set together with {@link #height(Integer)}.
         *
         * @param width output width
         * @return this builder
         */
        public Builder width(Integer width) {
            this.width = width;
            return this;
        }

        /**
         * Sets {@code height} in pixels. Set together with {@link #width(Integer)}.
         *
         * @param height output height
         * @return this builder
         */
        public Builder height(Integer height) {
            this.height = height;
            return this;
        }

        /**
         * Builds the request.
         *
         * @return the built request
         * @throws IllegalArgumentException if only one of {@code width} and {@code height} is set
         */
        public VideoGenerationRequest build() {
            if ((width == null) != (height == null)) {
                throw new IllegalArgumentException("width and height must be set together");
            }
            return new VideoGenerationRequest(this);
        }
    }
}
