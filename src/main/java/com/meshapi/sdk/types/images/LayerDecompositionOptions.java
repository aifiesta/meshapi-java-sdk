package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Options for layer decomposition on an image generation request.
 *
 * <p>Layer decomposition splits an input image into a base image plus separate layers.
 * Set these options through the {@code layer_decomposition_options} field of an image
 * generation request or image options, together with {@code layer_decomposition = true}.
 *
 * <p>The feature is only available on models that support it. Requesting a decomposition
 * on a model that does not support it returns a 400 error.
 *
 * <pre>{@code
 * LayerDecompositionOptions options = LayerDecompositionOptions.builder()
 *     .maxImages(10)
 *     .textRecognition(true)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class LayerDecompositionOptions {

    @JsonProperty("max_images")
    private final Integer maxImages;

    @JsonProperty("text_recognition")
    private final Boolean textRecognition;

    @JsonCreator
    LayerDecompositionOptions(
            @JsonProperty("max_images") Integer maxImages,
            @JsonProperty("text_recognition") Boolean textRecognition) {
        this.maxImages = maxImages;
        this.textRecognition = textRecognition;
    }

    private LayerDecompositionOptions(Builder builder) {
        this(builder.maxImages, builder.textRecognition);
    }

    /**
     * Returns a new builder.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Maximum number of images the decomposition may return.
     *
     * @return the maximum number of images, or {@code null} when not set
     */
    public Integer getMaxImages() {
        return maxImages;
    }

    /**
     * Whether text recognition runs on the decomposed layers.
     *
     * @return the text recognition flag, or {@code null} when not set
     */
    public Boolean getTextRecognition() {
        return textRecognition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LayerDecompositionOptions)) {
            return false;
        }
        LayerDecompositionOptions that = (LayerDecompositionOptions) o;
        return Objects.equals(maxImages, that.maxImages)
                && Objects.equals(textRecognition, that.textRecognition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxImages, textRecognition);
    }

    @Override
    public String toString() {
        return "LayerDecompositionOptions{maxImages=" + maxImages
                + ", textRecognition=" + textRecognition + "}";
    }

    /** Builder for {@link LayerDecompositionOptions}. */
    public static final class Builder {
        private Integer maxImages;
        private Boolean textRecognition;

        private Builder() {
        }

        /**
         * Sets the maximum number of images the decomposition may return.
         *
         * @param maxImages maximum number of images; must be positive when set
         * @return this builder
         * @throws IllegalArgumentException if {@code maxImages} is not positive
         */
        public Builder maxImages(Integer maxImages) {
            if (maxImages != null && maxImages < 1) {
                throw new IllegalArgumentException("maxImages must be positive");
            }
            this.maxImages = maxImages;
            return this;
        }

        /**
         * Sets whether text recognition runs on the decomposed layers.
         *
         * @param textRecognition the text recognition flag
         * @return this builder
         */
        public Builder textRecognition(Boolean textRecognition) {
            this.textRecognition = textRecognition;
            return this;
        }

        /**
         * Builds the options.
         *
         * @return a new {@link LayerDecompositionOptions}
         */
        public LayerDecompositionOptions build() {
            return new LayerDecompositionOptions(this);
        }
    }
}
