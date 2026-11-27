package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** An image result returned by {@code POST /v1/web/search}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchImage {

    /** The image URL. */
    @JsonProperty("url")
    public String url;

    /** The image title, when available. */
    @JsonProperty("title")
    public String title;

    /** The image description, present when {@code include_image_descriptions} was set. */
    @JsonProperty("description")
    public String description;
}
