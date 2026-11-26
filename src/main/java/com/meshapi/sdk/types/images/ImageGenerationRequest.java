package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/images/generations}.
 *
 * <p>Build with {@link #builder()}. Fields left {@code null} are omitted from the JSON body.
 *
 * <pre>{@code
 * ImageGenerationRequest req = ImageGenerationRequest.builder()
 *     .model("<image model id from GET /v1/models>")
 *     .prompt("A paper boat on a pond")
 *     .frameCount(4)
 *     .frameDurationMs(250)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageGenerationRequest {

    /** Model id, as returned by {@code GET /v1/models}. */
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

    /** Quality tier, where the model supports it. */
    @JsonProperty("quality")
    public String quality;

    /** {@code url} or {@code b64_json}. */
    @JsonProperty("response_format")
    public String responseFormat;

    /** Style hint, where the model supports it. */
    @JsonProperty("style")
    public String style;

    /** End-user identifier. */
    @JsonProperty("user")
    public String user;

    /** Background mode, where the model supports it. */
    @JsonProperty("background")
    public String background;

    /** Moderation level, where the model supports it. */
    @JsonProperty("moderation")
    public String moderation;

    /** Output file format, where the model supports it. */
    @JsonProperty("output_format")
    public String outputFormat;

    /** Output compression level, where the model supports it. */
    @JsonProperty("output_compression")
    public Integer outputCompression;

    /** Negative prompt, where the model supports it. */
    @JsonProperty("negative_prompt")
    public String negativePrompt;

    /** Aspect ratio, where the model supports it. */
    @JsonProperty("aspect_ratio")
    public String aspectRatio;

    /** Reference image(s): a URL or data URI string, or a list of them. */
    @JsonProperty("image")
    public Object image;

    /** Random seed, where the model supports it. */
    @JsonProperty("seed")
    public Integer seed;

    /** Sequential image generation mode, where the model supports it. */
    @JsonProperty("sequential_image_generation")
    public String sequentialImageGeneration;

    /** Options for sequential image generation. */
    @JsonProperty("sequential_image_generation_options")
    public Object sequentialImageGenerationOptions;

    /** Guidance scale, where the model supports it. */
    @JsonProperty("guidance_scale")
    public Number guidanceScale;

    /** Whether to add a watermark, where the model supports it. */
    @JsonProperty("watermark")
    public Boolean watermark;

    /** Prompt optimisation options, where the model supports it. */
    @JsonProperty("optimize_prompt_options")
    public Object optimizePromptOptions;

    /** Number of frames to generate, for models that produce animated output. */
    @JsonProperty("frame_count")
    public Integer frameCount;

    /** Duration of each frame in milliseconds, for models that produce animated output. */
    @JsonProperty("frame_duration_ms")
    public Integer frameDurationMs;

    public ImageGenerationRequest() {}

    /** Returns a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ImageGenerationRequest}. */
    public static final class Builder {
        private final ImageGenerationRequest r = new ImageGenerationRequest();

        private Builder() {}

        public Builder model(String v) { r.model = v; return this; }
        public Builder prompt(String v) { r.prompt = v; return this; }
        public Builder n(Integer v) { r.n = v; return this; }
        public Builder size(String v) { r.size = v; return this; }
        public Builder quality(String v) { r.quality = v; return this; }
        public Builder responseFormat(String v) { r.responseFormat = v; return this; }
        public Builder style(String v) { r.style = v; return this; }
        public Builder user(String v) { r.user = v; return this; }
        public Builder background(String v) { r.background = v; return this; }
        public Builder moderation(String v) { r.moderation = v; return this; }
        public Builder outputFormat(String v) { r.outputFormat = v; return this; }
        public Builder outputCompression(Integer v) { r.outputCompression = v; return this; }
        public Builder negativePrompt(String v) { r.negativePrompt = v; return this; }
        public Builder aspectRatio(String v) { r.aspectRatio = v; return this; }
        public Builder image(Object v) { r.image = v; return this; }
        public Builder seed(Integer v) { r.seed = v; return this; }
        public Builder sequentialImageGeneration(String v) { r.sequentialImageGeneration = v; return this; }
        public Builder sequentialImageGenerationOptions(Object v) { r.sequentialImageGenerationOptions = v; return this; }
        public Builder guidanceScale(Number v) { r.guidanceScale = v; return this; }
        public Builder watermark(Boolean v) { r.watermark = v; return this; }
        public Builder optimizePromptOptions(Object v) { r.optimizePromptOptions = v; return this; }

        /** Sets {@code frame_count}: the number of frames to generate. */
        public Builder frameCount(Integer v) { r.frameCount = v; return this; }

        /** Sets {@code frame_duration_ms}: how long each frame is shown, in milliseconds. */
        public Builder frameDurationMs(Integer v) { r.frameDurationMs = v; return this; }

        public ImageGenerationRequest build() { return r; }
    }
}
