package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.websearch.WebSearchRequest;
import com.meshapi.sdk.types.websearch.WebSearchResponse;

/**
 * Web search resource — wraps POST /v1/web/search.
 *
 * <p>Gated server-side by {@code WEB_SEARCH_ENABLED}; disabled deployments
 * return 403/404. Native-first with Tavily fallback — inspect
 * {@link WebSearchResponse#provider} to see which engine served the request.
 *
 * <pre>{@code
 * WebSearchResponse resp = client.web().search(
 *     WebSearchRequest.builder()
 *         .query("latest Mars rover news")
 *         .includeAnswer(true)
 *         .build()
 * );
 * System.out.println(resp.provider + ": " + resp.answer);
 * }</pre>
 */
public class WebResource {
    private final HttpClient http;

    public WebResource(HttpClient http) {
        this.http = http;
    }

    /**
     * POST /v1/web/search — run a live web search.
     *
     * @param request the search request; {@code query} is required
     * @return the search response with provider, optional answer, and result items
     */
    public WebSearchResponse search(WebSearchRequest request) {
        return http.post("/v1/web/search", request, WebSearchResponse.class);
    }
}
