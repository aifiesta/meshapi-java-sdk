package com.meshapi.sdk.types.moderations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.meshapi.sdk.types.ApiResponse;

/**
 * Response from POST /v1/moderations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationResponse extends ApiResponse {
    @JsonProperty("id") public String id;
    @JsonProperty("model") public String model;
    @JsonProperty("results") public List<ModerationResult> results;
}
