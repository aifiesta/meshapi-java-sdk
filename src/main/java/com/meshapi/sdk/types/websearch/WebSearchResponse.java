package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response from {@code POST /v1/web/search}.
 *
 * <p>{@link #images} is {@code null} when the request did not ask for images, and an empty
 * list when images were requested but none were found.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResponse {

    /** The query that was searched. */
    public String query;

    /** The search provider that served the request. */
    public String provider;

    /** Generated answer, when one was requested. */
    public String answer;

    /** Search results. */
    public List<Result> results;

    /**
     * Images found for the query. {@code null} when images were not requested,
     * empty when none were found.
     */
    public List<WebSearchImage> images;

    /** A single web search result. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        /** Page title. */
        public String title;

        /** Page URL. */
        public String url;

        /** Extracted content snippet. */
        public String content;

        /** Relevance score, when the provider reports one. */
        public Double score;

        /** Publication date, when the provider reports one. */
        @JsonProperty("published_date")
        public String publishedDate;

        /** Full raw page content, when requested. */
        @JsonProperty("raw_content")
        public String rawContent;
    }
}
