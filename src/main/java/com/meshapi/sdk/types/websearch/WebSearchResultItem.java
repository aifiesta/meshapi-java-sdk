package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A single search result item in a WebSearchResponse. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResultItem {
    @JsonProperty("title") public String title;
    @JsonProperty("url") public String url;
    /** The short snippet. Page text never replaces it — see {@link #pageContent}. */
    @JsonProperty("content") public String content;
    @JsonProperty("score") public Double score;
    @JsonProperty("published_date") public String publishedDate;
    /**
     * The result's extracted PAGE text, as opposed to the {@link #content} snippet.
     *
     * <p>{@code null} is normal, not an error: the key arrives on every response
     * from every engine, and carries text only when the request set
     * {@code includePageContent} AND the serving engine produced some. A
     * {@code tinyfish} result whose page could not be read is also {@code null},
     * per result, without failing the search — so check it per result.
     */
    @JsonProperty("page_content") public String pageContent;
    /**
     * True when {@link #pageContent} was cut at the server's per-result ceiling.
     * Check it before treating the text as a whole page.
     *
     * <p>A primitive, so a response that omits the key reads false — which is the
     * correct answer, not a missing one.
     */
    @JsonProperty("page_content_truncated") public boolean pageContentTruncated;
}
