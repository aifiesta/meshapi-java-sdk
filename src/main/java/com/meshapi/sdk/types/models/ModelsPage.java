package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.meshapi.sdk.types.ApiResponse;

/**
 * Response from GET /v1/models/search.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelsPage extends ApiResponse {
    @JsonProperty("items") public List<ModelInfo> items;
    @JsonProperty("total") public int total;
    @JsonProperty("limit") public int limit;
    @JsonProperty("offset") public int offset;
    @JsonProperty("brands") public List<String> brands;
}
