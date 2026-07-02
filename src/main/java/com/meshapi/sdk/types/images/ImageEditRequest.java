package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for POST /v1/images/edits.
 *
 * <p>Response shape is the same as image generation: {@link ImageGenerationResponse}.
 *
 * <pre>{@code
 * ImageEditRequest req = ImageEditRequest.builder()
 *     .model("openai/gpt-image-1")
 *     .image("data:image/png;base64,...")
 *     .prompt("Make the background white")
 *     .build();
 * ImageGenerationResponse resp = client.images().edit(req);
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageEditRequest {
    /** Required. Model ID to use for editing. */
    @JsonProperty("model") private String model;

    /**
     * Required. The image to edit.
     * Accepts a base64 data-URL string or an {@link ImageRef} with a {@code url} field.
     */
    @JsonProperty("image") private Object image;

    /** Edit instruction prompt. Defaults to {@code ""}. */
    @JsonProperty("prompt") private String prompt;

    /**
     * Edit operation.
     * One of: {@code edit}, {@code inpaint}, {@code outpaint}, {@code mix}, {@code reframe},
     * {@code upscale}, {@code remove_background}. Defaults to {@code "edit"}.
     */
    @JsonProperty("operation") private String operation;

    /** Mask image (base64 data-URL or {@link ImageRef}). Optional. */
    @JsonProperty("mask") private Object mask;

    /** Reference images for mix/reframe operations. Each item is a String or {@link ImageRef}. */
    @JsonProperty("reference_images") private List<Object> referenceImages;

    /** Number of images to generate. Default: 1. */
    @JsonProperty("n") private Integer n;

    /** Output size (e.g. {@code "1024x1024"}). */
    @JsonProperty("size") private String size;

    /** Response format: {@code url} or {@code b64_json}. */
    @JsonProperty("response_format") private String responseFormat;

    /** Background handling: {@code transparent}, {@code opaque}, or {@code auto}. */
    @JsonProperty("background") private String background;

    /** Upscale factor (used with {@code operation=upscale}). */
    @JsonProperty("upscale_factor") private String upscaleFactor;

    /** Quality tier hint. */
    @JsonProperty("quality_tier") private String qualityTier;

    /** Aspect ratio for the output (e.g. {@code "16:9"}). */
    @JsonProperty("aspect_ratio") private String aspectRatio;

    /** Resolution string (e.g. {@code "1920x1080"}). */
    @JsonProperty("resolution") private String resolution;

    /**
     * How much to expand the canvas for outpaint operations.
     * Accepts a {@code Double} or a {@code String} like {@code "0.25"}.
     */
    @JsonProperty("expand_factor") private Object expandFactor;

    /** Feathering in pixels for the mask edge. Default: 0. */
    @JsonProperty("mask_feather") private Integer maskFeather;

    private ImageEditRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final ImageEditRequest req = new ImageEditRequest();

        public Builder model(String model) { req.model = model; return this; }
        public Builder image(String image) { req.image = image; return this; }
        public Builder image(ImageRef image) { req.image = image; return this; }
        public Builder prompt(String prompt) { req.prompt = prompt; return this; }
        public Builder operation(String operation) { req.operation = operation; return this; }
        public Builder mask(String mask) { req.mask = mask; return this; }
        public Builder mask(ImageRef mask) { req.mask = mask; return this; }
        public Builder referenceImages(List<Object> referenceImages) { req.referenceImages = referenceImages; return this; }
        public Builder n(Integer n) { req.n = n; return this; }
        public Builder size(String size) { req.size = size; return this; }
        public Builder responseFormat(String responseFormat) { req.responseFormat = responseFormat; return this; }
        public Builder background(String background) { req.background = background; return this; }
        public Builder upscaleFactor(String upscaleFactor) { req.upscaleFactor = upscaleFactor; return this; }
        public Builder qualityTier(String qualityTier) { req.qualityTier = qualityTier; return this; }
        public Builder aspectRatio(String aspectRatio) { req.aspectRatio = aspectRatio; return this; }
        public Builder resolution(String resolution) { req.resolution = resolution; return this; }
        public Builder expandFactor(Double expandFactor) { req.expandFactor = expandFactor; return this; }
        public Builder expandFactor(String expandFactor) { req.expandFactor = expandFactor; return this; }
        public Builder maskFeather(Integer maskFeather) { req.maskFeather = maskFeather; return this; }

        public ImageEditRequest build() {
            if (req.model == null || req.model.isBlank()) throw new IllegalStateException("model is required");
            if (req.image == null) throw new IllegalStateException("image is required");
            return req;
        }
    }
}
