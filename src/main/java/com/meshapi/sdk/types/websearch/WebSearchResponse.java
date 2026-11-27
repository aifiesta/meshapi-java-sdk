package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Response body for {@code POST /v1/web/search}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResponse {

    /**
     * Image results. {@code null} when images were not requested; an empty list
     * when images were requested but none were found.
     */
    @JsonProperty("images")
    public List<WebSearchImage> images;
}
