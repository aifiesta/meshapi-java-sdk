package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single item in a multimodal embeddings request.
 *
 * <p>Pass a {@code List<MultimodalEmbeddingInput>} as the {@code input} field of
 * {@link EmbeddingsRequest} to embed text, images, and video together.
 *
 * <pre>{@code
 * List<MultimodalEmbeddingInput> items = List.of(
 *     MultimodalEmbeddingInput.text("Hello, world!"),
 *     MultimodalEmbeddingInput.imageUrl("https://example.com/photo.jpg")
 * );
 * EmbeddingsRequest req = new EmbeddingsRequest();
 * req.input = items;
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultimodalEmbeddingInput {
    /**
     * Required. Content type: {@code "text"}, {@code "image_url"}, or {@code "video_url"}.
     */
    @JsonProperty("type") public String type;

    /** Text content. Populated when {@code type} is {@code "text"}. */
    @JsonProperty("text") public String text;

    /** Image reference. Populated when {@code type} is {@code "image_url"}. */
    @JsonProperty("image_url") public ImageEmbeddingUrl imageUrl;

    /** Video reference. Populated when {@code type} is {@code "video_url"}. */
    @JsonProperty("video_url") public VideoEmbeddingUrl videoUrl;

    public MultimodalEmbeddingInput() {}

    /** Convenience factory for a text item. */
    public static MultimodalEmbeddingInput text(String text) {
        MultimodalEmbeddingInput item = new MultimodalEmbeddingInput();
        item.type = "text";
        item.text = text;
        return item;
    }

    /** Convenience factory for an image URL item. */
    public static MultimodalEmbeddingInput imageUrl(String url) {
        MultimodalEmbeddingInput item = new MultimodalEmbeddingInput();
        item.type = "image_url";
        item.imageUrl = new ImageEmbeddingUrl(url);
        return item;
    }

    /** Convenience factory for a video URL item. */
    public static MultimodalEmbeddingInput videoUrl(String url) {
        MultimodalEmbeddingInput item = new MultimodalEmbeddingInput();
        item.type = "video_url";
        item.videoUrl = new VideoEmbeddingUrl(url);
        return item;
    }
}
