package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageGenerationResponse extends ApiResponse {
    @JsonProperty("created") public long created;
    @JsonProperty("data") public List<ImageItem> data;
    @JsonProperty("background") public String background;
    @JsonProperty("output_format") public String outputFormat;
    @JsonProperty("quality") public String quality;
    @JsonProperty("size") public String size;
    @JsonProperty("usage") public ImageUsage usage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageItem {
        @JsonProperty("url") public String url;
        @JsonProperty("b64_json") public String b64Json;
        @JsonProperty("revised_prompt") public String revisedPrompt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageUsage {
        @JsonProperty("prompt_tokens") public int promptTokens;
        @JsonProperty("completion_tokens") public int completionTokens;
        @JsonProperty("total_tokens") public int totalTokens;
        @JsonProperty("input_tokens_details") public Map<String, Object> inputTokensDetails;
        @JsonProperty("output_tokens_details") public Map<String, Object> outputTokensDetails;
    }
}
