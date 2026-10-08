package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.types.websearch.WebSearchRequest;
import com.meshapi.sdk.types.websearch.WebSearchResponse;
import com.meshapi.sdk.types.websearch.WebSearchResultItem;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Live tests for POST /v1/web/search.
 *
 * <p>The repo's CLAUDE.md has listed a WebSearchLiveTest for some time, but no
 * such class existed — this fills that gap alongside the page-content work.
 */
class WebSearchLiveTest extends LiveTestBase {

    private static final Set<String> ENGINES = Set.of("native", "tavily", "tinyfish");

    /**
     * Web search switched off on this deployment, or the pinned engine has no
     * credential and so was never registered. Both are deployment configuration
     * rather than a defect in this SDK, so they skip instead of failing.
     */
    private static final Set<Integer> UNAVAILABLE = Set.of(400, 403, 404, 422, 501, 503);

    private static WebSearchResponse searchOrSkip(MeshAPI client, WebSearchRequest request, String what) {
        try {
            return client.web().search(request);
        } catch (MeshAPIError e) {
            Assumptions.assumeFalse(
                    UNAVAILABLE.contains(e.getStatus()),
                    what + " unavailable on " + BASE_URL + ": " + e.getErrorCode());
            throw e;
        }
    }

    @Test
    void basicSearch() {
        MeshAPI client = newClient();
        WebSearchResponse resp = searchOrSkip(
                client,
                WebSearchRequest.builder().query("what is the capital of France").maxResults(3).build(),
                "web search (WEB_SEARCH_ENABLED)");

        assertNotNull(resp.query, "expected the query echoed back");
        assertTrue(ENGINES.contains(resp.provider), "unexpected provider: " + resp.provider);

        List<WebSearchResultItem> results = resp.results;
        assertNotNull(results, "results should not be null");
        assertTrue(results.size() <= 3, "must not exceed max_results");
        for (WebSearchResultItem item : results) {
            assertNotNull(item.title, "result should have a title");
            assertNotNull(item.url, "result should have a url");
            // Not asking must never produce page text, whichever engine served.
            assertNull(item.pageContent, "page text arrived unasked-for from " + resp.provider);
            assertFalse(item.pageContentTruncated, "truncation flag should be false when not requested");
        }
        System.out.printf("[PASS] web.search() → %s, %d results%n", resp.provider, results.size());
    }

    @Test
    void searchWithAnswer() {
        MeshAPI client = newClient();
        WebSearchResponse resp = searchOrSkip(
                client,
                WebSearchRequest.builder()
                        .query("who wrote the book Dune")
                        .maxResults(5)
                        .includeAnswer(true)
                        .build(),
                "web search (WEB_SEARCH_ENABLED)");

        assertNotNull(resp.query);
        // `answer` is best-effort: assert the field is reachable, not that it is set.
        System.out.printf("[PASS] web.search(include_answer) → %s, answer=%s%n",
                resp.provider, resp.answer != null);
    }

    /**
     * Page text comes from the tinyfish engine only, and the native engine is
     * tried first, so the engine is pinned rather than hoped for — an unpinned
     * request would prove nothing.
     */
    @Test
    void pageContentWithTheEnginePinned() {
        MeshAPI client = newClient();
        WebSearchResponse resp = searchOrSkip(
                client,
                WebSearchRequest.builder()
                        .query("James Webb telescope earliest galaxy")
                        .provider("tinyfish")
                        .includePageContent(true)
                        .maxResults(2)
                        .build(),
                "tinyfish web search engine");

        assertEquals("tinyfish", resp.provider, "pinning was ignored");
        assertNotNull(resp.results, "results should not be null");
        assertFalse(resp.results.isEmpty(), "expected at least one result");

        int withText = 0;
        for (WebSearchResultItem item : resp.results) {
            assertNotNull(item.title, "result should have a title");
            assertNotNull(item.url, "result should have a url");
            if (item.pageContent != null) {
                assertNotEquals("", item.pageContent, "pageContent should be null, never empty");
                withText++;
            }
        }

        // A per-URL fetch failing upstream is normal and silent, so no text on any
        // row is a skip rather than a failure — the shape above is what this SDK owns.
        Assumptions.assumeTrue(withText > 0, "upstream returned no page text for any result on this run");
        System.out.printf("[PASS] web.search(include_page_content) → %d of %d results carried page text%n",
                withText, resp.results.size());
    }
}
