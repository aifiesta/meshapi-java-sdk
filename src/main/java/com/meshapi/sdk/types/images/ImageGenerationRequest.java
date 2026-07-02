package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Request body for POST /v1/images/generations.
 *
 * <p>Build with {@link #builder()}:
 * <pre>{@code
 * ImageGenerationRequest req = ImageGenerationRequest.builder()
 *     .prompt("A futuristic cityscape at sunset")
 *     .model("openai/dall-e-3")
 *     .n(1)
 *     .size("1024x1024")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageGenerationRequest {
    // Core fields
    @JsonProperty("prompt") public String prompt;
    @JsonProperty("model") public String model;
    @JsonProperty("n") public Integer n;
    @JsonProperty("size") public String size;
    @JsonProperty("quality") public String quality;
    @JsonProperty("response_format") public String responseFormat;
    @JsonProperty("output_format") public String outputFormat;
    @JsonProperty("stream") public Boolean stream;

    // Additional spec fields (finding #11/#12)
    @JsonProperty("aspect_ratio") public String aspectRatio;
    @JsonProperty("resolution") public String resolution;
    @JsonProperty("output_compression") public Integer outputCompression;
    /** Background handling: {@code transparent}, {@code opaque}, or {@code auto}. */
    @JsonProperty("background") public String background;
    /** Moderation level: {@code low} or {@code auto}. */
    @JsonProperty("moderation") public String moderation;
    /** Number of partial images to stream (0–3). */
    @JsonProperty("partial_images") public Integer partialImages;
    /** Input image(s): a String or List of Strings (base64 data-URLs or URLs). */
    @JsonProperty("image") public Object image;
    /** Random seed (-1 to 2147483647). */
    @JsonProperty("seed") public Integer seed;
    /** Sequential generation mode: {@code auto} or {@code disabled}. */
    @JsonProperty("sequential_image_generation") public String sequentialImageGeneration;
    /** Options for sequential image generation. */
    @JsonProperty("sequential_image_generation_options") public Map<String, Object> sequentialImageGenerationOptions;
    /** Guidance scale (1–10). */
    @JsonProperty("guidance_scale") public Double guidanceScale;
    /** Whether to watermark the output. */
    @JsonProperty("watermark") public Boolean watermark;
    /** Options for prompt optimisation. */
    @JsonProperty("optimize_prompt_options") public Map<String, Object> optimizePromptOptions;

    private ImageGenerationRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final ImageGenerationRequest req = new ImageGenerationRequest();

        public Builder prompt(String prompt) { req.prompt = prompt; return this; }
        public Builder model(String model) { req.model = model; return this; }
        public Builder n(Integer n) { req.n = n; return this; }
        public Builder size(String size) { req.size = size; return this; }
        public Builder quality(String quality) { req.quality = quality; return this; }
        public Builder responseFormat(String responseFormat) { req.responseFormat = responseFormat; return this; }
        public Builder outputFormat(String outputFormat) { req.outputFormat = outputFormat; return this; }
        public Builder stream(Boolean stream) { req.stream = stream; return this; }
        public Builder aspectRatio(String aspectRatio) { req.aspectRatio = aspectRatio; return this; }
        public Builder resolution(String resolution) { req.resolution = resolution; return this; }
        public Builder outputCompression(Integer outputCompression) { req.outputCompression = outputCompression; return this; }
        public Builder background(String background) { req.background = background; return this; }
        public Builder moderation(String moderation) { req.moderation = moderation; return this; }
        public Builder partialImages(Integer partialImages) { req.partialImages = partialImages; return this; }
        public Builder image(Object image) { req.image = image; return this; }
        public Builder seed(Integer seed) { req.seed = seed; return this; }
        public Builder sequentialImageGeneration(String sequentialImageGeneration) {
            req.sequentialImageGeneration = sequentialImageGeneration; return this;
        }
        public Builder sequentialImageGenerationOptions(Map<String, Object> opts) {
            req.sequentialImageGenerationOptions = opts; return this;
        }
        public Builder guidanceScale(Double guidanceScale) { req.guidanceScale = guidanceScale; return this; }
        public Builder watermark(Boolean watermark) { req.watermark = watermark; return this; }
        public Builder optimizePromptOptions(Map<String, Object> opts) {
            req.optimizePromptOptions = opts; return this;
        }

        public ImageGenerationRequest build() {
            return req;
        }
    }
}
