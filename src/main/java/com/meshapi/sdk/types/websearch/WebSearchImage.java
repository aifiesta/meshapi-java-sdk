package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An image returned by {@code POST /v1/web/search} when
 * {@link WebSearchRequest.Builder#includeImages(Boolean)} is {@code true}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class WebSearchImage {

    /** The image URL. */
    @JsonProperty("url")
    public String url;

    /** The image title, or {@code null} when none is available. */
    @JsonProperty("title")
    public String title;

    /**
     * The image description, or {@code null} unless
     * {@link WebSearchRequest.Builder#includeImageDescriptions(Boolean)} was {@code true}.
     */
    @JsonProperty("description")
    public String description;
}
