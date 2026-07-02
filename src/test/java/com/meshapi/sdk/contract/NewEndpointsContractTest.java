package com.meshapi.sdk.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.batch.BatchObject;
import com.meshapi.sdk.types.moderations.ModerationResponse;
import com.meshapi.sdk.types.models.ModelsPage;
import com.meshapi.sdk.types.router.RouterSelectResponse;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests for newly added types.
 * Uses golden JSON fixtures — no live server required.
 */
class NewEndpointsContractTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static InputStream fixture(String name) {
        InputStream in = NewEndpointsContractTest.class.getResourceAsStream("/fixtures/" + name);
        assertNotNull(in, "fixture not found: " + name);
        return in;
    }

    // -----------------------------------------------------------------------
    // Moderations
    // -----------------------------------------------------------------------

    @Test
    void moderationResponse_parsedCorrectly() throws Exception {
        ModerationResponse resp = MAPPER.readValue(fixture("moderation_response.json"), ModerationResponse.class);
        assertEquals("modr-abc123", resp.id);
        assertEquals("omni-moderation-latest", resp.model);
        assertNotNull(resp.results);
        assertEquals(1, resp.results.size());
        assertFalse(resp.results.get(0).flagged);
        assertNotNull(resp.results.get(0).categories);
        assertFalse(resp.results.get(0).categories.get("violence"));
        assertNotNull(resp.results.get(0).categoryScores);
        assertTrue(resp.results.get(0).categoryScores.get("hate") instanceof Double);
    }

    // -----------------------------------------------------------------------
    // Router
    // -----------------------------------------------------------------------

    @Test
    void routerSelectResponse_parsedCorrectly() throws Exception {
        RouterSelectResponse resp = MAPPER.readValue(fixture("router_select_response.json"), RouterSelectResponse.class);
        assertEquals("openai/gpt-4o-mini", resp.model);
        assertNotNull(resp.autoRouter);
        assertFalse(resp.autoRouter.fallbackUsed);
        assertNull(resp.autoRouter.fallbackReason);
        assertEquals("medium", resp.reasoningEffort);
    }

    // -----------------------------------------------------------------------
    // Batch — completed batch with results and errors_detail
    // -----------------------------------------------------------------------

    @Test
    void batchObject_completedWithResults_parsedCorrectly() throws Exception {
        BatchObject batch = MAPPER.readValue(fixture("batch_completed.json"), BatchObject.class);
        assertEquals("batch_abc123", batch.id);
        assertEquals("completed", batch.status);
        assertNotNull(batch.results);
        assertEquals(1, batch.results.size());
        assertNotNull(batch.errorsDetail);
        assertTrue(batch.errorsDetail.isEmpty());
        assertNotNull(batch.requestCounts);
        assertEquals(3, ((Number) batch.requestCounts.get("total")).intValue());
        assertNotNull(batch.metadata);
        assertEquals("test-run-1", batch.metadata.get("job"));
        assertEquals("24h", batch.completionWindow);
        assertEquals(1700086400L, batch.expiresAt);
    }

    // -----------------------------------------------------------------------
    // Models — search page
    // -----------------------------------------------------------------------

    @Test
    void modelsPage_parsedCorrectly() throws Exception {
        ModelsPage page = MAPPER.readValue(fixture("models_page.json"), ModelsPage.class);
        assertEquals(1, page.total);
        assertEquals(20, page.limit);
        assertEquals(0, page.offset);
        assertNotNull(page.brands);
        assertEquals(1, page.brands.size());
        assertEquals("openai", page.brands.get(0));
        assertNotNull(page.items);
        assertEquals(1, page.items.size());
        assertEquals("openai/gpt-4o-mini", page.items.get(0).id);
        assertTrue(page.items.get(0).supportsCompletionsApi);
        assertEquals("openai", page.items.get(0).brand);
    }
}
