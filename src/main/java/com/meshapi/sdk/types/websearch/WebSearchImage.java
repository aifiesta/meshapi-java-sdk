package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One image result returned by {@code POST /v1/web/search} when {@code include_images} is set.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchImage {

    /** Image URL. */
    @JsonProperty("url")
    public String url;

    /** Image title, or {@code null} if the source provides none. */
    @JsonProperty("title")
    public String title;

    /** Image description, or {@code null} unless {@code include_image_descriptions} was set. */
    @JsonProperty("description")
    public String description;
}
