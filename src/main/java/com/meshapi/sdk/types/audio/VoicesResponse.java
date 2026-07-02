package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoicesResponse {
    @JsonProperty("voices")
    public List<Voice> voices;

    @JsonProperty("has_more")
    public boolean hasMore;

    @JsonProperty("total_count")
    public int totalCount;

    @JsonProperty("next_page_token")
    public String nextPageToken;
}
