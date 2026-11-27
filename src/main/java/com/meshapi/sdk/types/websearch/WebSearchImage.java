package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An image returned by {@code POST /v1/web/search} when {@code include_images} is set.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchImage {

    /** Image URL. */
    public String url;

    /** Image title, when available. */
    public String title;

    /** Image description, present when {@code include_image_descriptions} is set. */
    public String description;
}
