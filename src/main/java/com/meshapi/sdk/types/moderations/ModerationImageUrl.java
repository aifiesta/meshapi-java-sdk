package com.meshapi.sdk.types.moderations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationImageUrl {
    @JsonProperty("url") public String url;

    public ModerationImageUrl() {}

    public ModerationImageUrl(String url) {
        this.url = url;
    }
}
