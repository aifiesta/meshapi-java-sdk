package com.meshapi.sdk.types.videos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Response from POST /v1/video/generations — just the task ID. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateVideoGenerationResponse {
    @JsonProperty("id") public String id;
}
