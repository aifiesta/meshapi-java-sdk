package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A single search result item in a WebSearchResponse. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResultItem {
    @JsonProperty("title") public String title;
    @JsonProperty("url") public String url;
    @JsonProperty("content") public String content;
    @JsonProperty("score") public Double score;
    @JsonProperty("published_date") public String publishedDate;
}
