package com.meshapi.sdk.types.moderations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single item in a moderation request's input array.
 *
 * <p>Set {@code type} to {@code "text"} and populate {@code text}, or
 * set {@code type} to {@code "image_url"} and populate {@code imageUrl}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationInputItem {
    /** Required. Either {@code "text"} or {@code "image_url"}. */
    @JsonProperty("type") public String type;

    /** The text to classify. Populated when {@code type} is {@code "text"}. */
    @JsonProperty("text") public String text;

    /** The image URL. Populated when {@code type} is {@code "image_url"}. */
    @JsonProperty("image_url") public ModerationImageUrl imageUrl;

    public ModerationInputItem() {}

    /** Convenience factory for a text item. */
    public static ModerationInputItem text(String text) {
        ModerationInputItem item = new ModerationInputItem();
        item.type = "text";
        item.text = text;
        return item;
    }

    /** Convenience factory for an image URL item. */
    public static ModerationInputItem imageUrl(String url) {
        ModerationInputItem item = new ModerationInputItem();
        item.type = "image_url";
        item.imageUrl = new ModerationImageUrl(url);
        return item;
    }
}
