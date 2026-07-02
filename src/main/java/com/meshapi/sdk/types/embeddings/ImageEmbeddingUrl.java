package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image URL reference for multimodal embedding input.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageEmbeddingUrl {
    @JsonProperty("url") public String url;

    public ImageEmbeddingUrl() {}

    public ImageEmbeddingUrl(String url) {
        this.url = url;
    }
}
