package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for POST /v1/web/search.
 *
 * <p>{@code query} is the only required field. Use the builder to construct:
 * <pre>{@code
 * WebSearchRequest req = WebSearchRequest.builder()
 *     .query("latest Mars rover news")
 *     .includeAnswer(true)
 *     .maxResults(10)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSearchRequest {
    /** Required: the search query (1–2000 characters). */
    @JsonProperty("query") private String query;
    /** Optional: native engine model id (e.g. "perplexity/sonar"). */
    @JsonProperty("model") private String model;
    /**
     * Optional: pin a specific engine — "native", "tavily" or "tinyfish". Omit for
     * native-first with automatic failover; pinning turns failover off.
     */
    @JsonProperty("provider") private String provider;
    /** Optional: maximum results to return (1–20, default 5). */
    @JsonProperty("max_results") private Integer maxResults;
    /** Optional: Tavily search depth — "basic" (default) or "advanced". Ignored by native engine. */
    @JsonProperty("search_depth") private String searchDepth;
    /** Optional: restrict results to these domains. */
    @JsonProperty("include_domains") private List<String> includeDomains;
    /** Optional: exclude results from these domains. */
    @JsonProperty("exclude_domains") private List<String> excludeDomains;
    /** Optional: ask the engine for a synthesized answer alongside results (default false). */
    @JsonProperty("include_answer") private Boolean includeAnswer;
    /**
     * Optional: also return each result's extracted page text in
     * {@link WebSearchResultItem#pageContent}, so one call replaces
     * search-then-fetch (default false).
     *
     * <p>Honoured by the {@code tinyfish} engine only, and the native engine is
     * tried first, so pin {@code provider("tinyfish")} or expect no page text at
     * all. It costs nothing extra, but the search takes seconds rather than under
     * one and returns tens of kilobytes per result.
     *
     * <p>Left unset it is omitted from the body entirely, so an existing call is
     * unchanged on the wire.
     */
    @JsonProperty("include_page_content") private Boolean includePageContent;

    private WebSearchRequest() {}

    public String getQuery() { return query; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final WebSearchRequest req = new WebSearchRequest();

        /** Required. */
        public Builder query(String query) { req.query = query; return this; }
        public Builder model(String model) { req.model = model; return this; }
        public Builder provider(String provider) { req.provider = provider; return this; }
        public Builder maxResults(int maxResults) { req.maxResults = maxResults; return this; }
        public Builder searchDepth(String searchDepth) { req.searchDepth = searchDepth; return this; }
        public Builder includeDomains(List<String> domains) { req.includeDomains = domains; return this; }
        public Builder excludeDomains(List<String> domains) { req.excludeDomains = domains; return this; }
        public Builder includeAnswer(boolean includeAnswer) { req.includeAnswer = includeAnswer; return this; }
        /** Return each result's extracted page text. Requires the tinyfish engine — see the field. */
        public Builder includePageContent(boolean includePageContent) { req.includePageContent = includePageContent; return this; }

        public WebSearchRequest build() {
            if (req.query == null || req.query.isBlank()) {
                throw new IllegalArgumentException("query is required");
            }
            return req;
        }
    }
}
