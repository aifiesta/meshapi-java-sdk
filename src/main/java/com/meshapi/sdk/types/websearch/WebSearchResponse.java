package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** Response body of {@code POST /v1/web/search}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class WebSearchResponse {

    /** The search results. */
    @JsonProperty("results")
    public List<JsonNode> results;

    /**
     * Image results.
     *
     * <p>{@code null} when images were not requested, and an empty list when images were
     * requested but none were found.
     */
    @JsonProperty("images")
    public List<WebSearchImage> images;
}
