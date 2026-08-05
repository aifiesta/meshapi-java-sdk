package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateVideoGenerationResponse extends ApiResponse {
    @JsonProperty("id") public String id;
}
