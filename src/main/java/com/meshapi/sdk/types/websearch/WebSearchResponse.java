package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.meshapi.sdk.types.ApiResponse;

/** Response from POST /v1/web/search. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResponse extends ApiResponse {
    /** The search query that was used. */
    @JsonProperty("query") public String query;
    /** The engine that served the request — "native" or "tavily". */
    @JsonProperty("provider") public String provider;
    /** Synthesized answer (only present when include_answer=true). */
    @JsonProperty("answer") public String answer;
    /** The list of result items. */
    @JsonProperty("results") public List<WebSearchResultItem> results;
    /** Opaque request identifier. */
    @JsonProperty("request_id") public String requestId;
}
