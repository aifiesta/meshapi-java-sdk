package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One image returned in the {@code images} list of a {@code POST /v1/web/search} response.
 *
 * <p>The {@code images} list is {@code null} when the request did not set
 * {@code include_images}, and empty when no images were found. {@link #description} is only
 * populated when {@code include_image_descriptions} is also set.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchImage {

    /** The image URL. */
    @JsonProperty("url")
    public String url;

    /** The image title, when available. */
    @JsonProperty("title")
    public String title;

    /** A text description of the image, when requested and available. */
    @JsonProperty("description")
    public String description;
}
