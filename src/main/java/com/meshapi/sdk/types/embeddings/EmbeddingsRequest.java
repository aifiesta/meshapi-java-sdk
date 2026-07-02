package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Request body for POST /v1/embeddings.
 *
 * <p>Public fields allow direct assignment (backward-compatible). A {@link #builder()} is also
 * provided to match the convention of other request POJOs in this SDK.
 *
 * <p>{@code provider} accepts either a provider-name {@code String} (e.g. {@code "openai"})
 * or a {@link ProviderPreferences} object for fine-grained routing control.
 *
 * <p>{@code input} accepts a {@code String}, {@code List<String>}, {@code int[]},
 * {@code int[][]}, or a {@code List<MultimodalEmbeddingInput>}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddingsRequest {
    @JsonProperty("model") public String model;
    @JsonProperty("input") public Object input;
    @JsonProperty("dimensions") public Integer dimensions;
    @JsonProperty("encoding_format") public String encodingFormat;
    @JsonProperty("input_type") public String inputType;
    /**
     * Provider routing: a provider-name {@code String} or a {@link ProviderPreferences} object.
     */
    @JsonProperty("provider") public Object provider;
    @JsonProperty("user") public String user;

    // Fields added per spec (finding #10/#16)
    /** Optional instructions to guide the embedding model (e.g. for BytePlus multimodal). */
    @JsonProperty("instructions") public String instructions;
    /** Optional sparse embedding configuration. */
    @JsonProperty("sparse_embedding") public Map<String, Object> sparseEmbedding;

    public EmbeddingsRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final EmbeddingsRequest req = new EmbeddingsRequest();

        public Builder model(String model) { req.model = model; return this; }
        public Builder input(Object input) { req.input = input; return this; }
        public Builder dimensions(Integer dimensions) { req.dimensions = dimensions; return this; }
        public Builder encodingFormat(String encodingFormat) { req.encodingFormat = encodingFormat; return this; }
        public Builder inputType(String inputType) { req.inputType = inputType; return this; }
        public Builder provider(Object provider) { req.provider = provider; return this; }
        public Builder user(String user) { req.user = user; return this; }
        public Builder instructions(String instructions) { req.instructions = instructions; return this; }
        public Builder sparseEmbedding(Map<String, Object> sparseEmbedding) {
            req.sparseEmbedding = sparseEmbedding; return this;
        }

        public EmbeddingsRequest build() { return req; }
    }
}
