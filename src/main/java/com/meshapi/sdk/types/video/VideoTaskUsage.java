package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTaskUsage {
    @JsonProperty("completion_tokens") public int completionTokens;
    @JsonProperty("total_tokens") public int totalTokens;
}
