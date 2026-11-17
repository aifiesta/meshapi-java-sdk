package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the JSON body.
 *
 * <p>Animated output: set {@code outputFormat("gif")} and optionally {@link Builder#frameCount(Integer)}
 * (2-16) and {@link Builder#frameDurationMs(Integer)} (at least 20). GIF output is only available on
 * models that support it; other models return a 400.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageGenerationRequest {

    /** Model id, for example {@code openai/gpt-image-2.6-flare}. */
    @JsonProperty("model")
    public String model;

    /** Text prompt describing the image. */
    @JsonProperty("prompt")
    public String prompt;

    /** Number of images to generate. */
    @JsonProperty("n")
    public Integer n;

    /** Output size, for example {@code 1024x1024}. */
    @JsonProperty("size")
    public String size;

    /** Output quality. */
    @JsonProperty("quality")
    public String quality;

    /** Output style. */
    @JsonProperty("style")
    public String style;

    /** Response format, for example {@code url} or {@code b64_json}. */
    @JsonProperty("response_format")
    public String responseFormat;

    /** Output image format, for example {@code png}, {@code webp} or {@code gif}. */
    @JsonProperty("output_format")
    public String outputFormat;

    /** Output compression level. Not accepted together with GIF output. */
    @JsonProperty("output_compression")
    public Integer outputCompression;

    /** Background setting. */
    @JsonProperty("background")
    public String background;

    /** Moderation level. */
    @JsonProperty("moderation")
    public String moderation;

    /** Number of partial images to stream. Not accepted together with GIF output. */
    @JsonProperty("partial_images")
    public Integer partialImages;

    /** Whether to stream the result. */
    @JsonProperty("stream")
    public Boolean stream;

    /** End-user identifier. */
    @JsonProperty("user")
    public String user;

    /** Reference image(s): a URL / data URI string or a list of them. */
    @JsonProperty("image")
    public Object image;

    /** Random seed. */
    @JsonProperty("seed")
    public Integer seed;

    /** Sequential image generation mode. */
    @JsonProperty("sequential_image_generation")
    public String sequentialImageGeneration;

    /** Options for sequential image generation. */
    @JsonProperty("sequential_image_generation_options")
    public Object sequentialImageGenerationOptions;

    /** Guidance scale. */
    @JsonProperty("guidance_scale")
    public Double guidanceScale;

    /** Whether to add a watermark. */
    @JsonProperty("watermark")
    public Boolean watermark;

    /** Prompt optimisation options. */
    @JsonProperty("optimize_prompt_options")
    public Object optimizePromptOptions;

    /** Number of frames in an animated GIF (2-16). */
    @JsonProperty("frame_count")
    public Integer frameCount;

    /** Duration of each GIF frame in milliseconds (at least 20). */
    @JsonProperty("frame_duration_ms")
    public Integer frameDurationMs;

    public ImageGenerationRequest() {
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private final ImageGenerationRequest r = new ImageGenerationRequest();

        private Builder() {
        }

        public Builder model(String model) { r.model = model; return this; }
        public Builder prompt(String prompt) { r.prompt = prompt; return this; }
        public Builder n(Integer n) { r.n = n; return this; }
        public Builder size(String size) { r.size = size; return this; }
        public Builder quality(String quality) { r.quality = quality; return this; }
        public Builder style(String style) { r.style = style; return this; }
        public Builder responseFormat(String responseFormat) { r.responseFormat = responseFormat; return this; }
        public Builder outputFormat(String outputFormat) { r.outputFormat = outputFormat; return this; }
        public Builder outputCompression(Integer outputCompression) { r.outputCompression = outputCompression; return this; }
        public Builder background(String background) { r.background = background; return this; }
        public Builder moderation(String moderation) { r.moderation = moderation; return this; }
        public Builder partialImages(Integer partialImages) { r.partialImages = partialImages; return this; }
        public Builder stream(Boolean stream) { r.stream = stream; return this; }
        public Builder user(String user) { r.user = user; return this; }
        public Builder image(Object image) { r.image = image; return this; }
        public Builder seed(Integer seed) { r.seed = seed; return this; }
        public Builder sequentialImageGeneration(String mode) { r.sequentialImageGeneration = mode; return this; }
        public Builder sequentialImageGenerationOptions(Object options) { r.sequentialImageGenerationOptions = options; return this; }
        public Builder guidanceScale(Double guidanceScale) { r.guidanceScale = guidanceScale; return this; }
        public Builder watermark(Boolean watermark) { r.watermark = watermark; return this; }
        public Builder optimizePromptOptions(Object options) { r.optimizePromptOptions = options; return this; }

        /**
         * Sets the number of frames for GIF output.
         *
         * @param frameCount frame count, 2-16
         * @return this builder
         */
        public Builder frameCount(Integer frameCount) { r.frameCount = frameCount; return this; }

        /**
         * Sets the per-frame duration for GIF output.
         *
         * @param frameDurationMs frame duration in milliseconds, at least 20
         * @return this builder
         */
        public Builder frameDurationMs(Integer frameDurationMs) { r.frameDurationMs = frameDurationMs; return this; }

        public ImageGenerationRequest build() {
            return r;
        }
    }
}
