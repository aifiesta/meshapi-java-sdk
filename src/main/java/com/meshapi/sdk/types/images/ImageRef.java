package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A reference to an image by URL, for use in image edit operations.
 *
 * <pre>{@code
 * ImageRef ref = new ImageRef("https://example.com/photo.jpg");
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageRef {
    @JsonProperty("url") public String url;

    public ImageRef() {}

    public ImageRef(String url) {
        this.url = url;
    }
}
