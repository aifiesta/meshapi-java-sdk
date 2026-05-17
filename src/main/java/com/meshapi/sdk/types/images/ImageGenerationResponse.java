package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageGenerationResponse {
    @JsonProperty("created") public long created;
    @JsonProperty("data") public List<ImageItem> data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageItem {
        @JsonProperty("url") public String url;
        @JsonProperty("b64_json") public String b64Json;
    }
}
