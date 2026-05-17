package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageGenerationChunk {
    @JsonProperty("id") public String id;
    @JsonProperty("object") public String object;
    @JsonProperty("created") public long created;
    @JsonProperty("model") public String model;
    @JsonProperty("data") public List<ImageGenerationResponse.ImageItem> data;
    @JsonProperty("status") public String status;
}
