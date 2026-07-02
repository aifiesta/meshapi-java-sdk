package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Video URL reference for multimodal embedding input.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoEmbeddingUrl {
    @JsonProperty("url") public String url;

    public VideoEmbeddingUrl() {}

    public VideoEmbeddingUrl(String url) {
        this.url = url;
    }
}
