package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingsResponse {
    @JsonProperty("object") public String object;
    @JsonProperty("data") public List<EmbeddingItem> data;
    @JsonProperty("model") public String model;
    @JsonProperty("usage") public Usage usage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingItem {
        @JsonProperty("object") public String object;
        @JsonProperty("index") public int index;
        @JsonProperty("embedding") public Object embedding;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("prompt_tokens") public int promptTokens;
        @JsonProperty("total_tokens") public int totalTokens;
    }
}
