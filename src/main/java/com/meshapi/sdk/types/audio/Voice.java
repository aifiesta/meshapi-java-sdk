package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Voice extends ApiResponse {
    @JsonProperty("voice_id")
    public String voiceId;

    @JsonProperty("name")
    public String name;

    @JsonProperty("category")
    public String category;

    @JsonProperty("description")
    public String description;

    @JsonProperty("preview_url")
    public String previewUrl;

    @JsonProperty("labels")
    public Map<String, String> labels;
}
