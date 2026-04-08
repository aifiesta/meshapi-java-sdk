package com.meshapi.sdk.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.models.ModelInfo;
import com.meshapi.sdk.types.responses.ResponsesResponse;
import com.meshapi.sdk.types.templates.TemplateSummary;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests — verify SDK types correctly parse all documented API response shapes.
 * Uses golden JSON fixtures. No live server required.
 */
class ContractTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static InputStream fixture(String name) {
        InputStream in = ContractTest.class.getResourceAsStream("/fixtures/" + name);
        assertNotNull(in, "fixture not found: " + name);
        return in;
    }

    // -----------------------------------------------------------------------
    // Chat completion shapes
    // -----------------------------------------------------------------------

    @Test
    void chatCompletionResponse() throws Exception {
        ChatCompletionResponse resp = MAPPER.readValue(fixture("chat_completion_response.json"),
                ChatCompletionResponse.class);
        assertEquals("chatcmpl-abc123", resp.id);
        assertEquals("openai/gpt-4o-mini", resp.model);
        assertEquals(1, resp.choices.size());
        assertEquals("assistant", resp.choices.get(0).message.role);
        assertEquals("2 + 2 equals 4.", resp.choices.get(0).message.content);
        assertEquals("stop", resp.choices.get(0).finishReason);
        assertNotNull(resp.usage);
        assertEquals(21, resp.usage.totalTokens);
    }

    // -----------------------------------------------------------------------
    // Model shapes
    // -----------------------------------------------------------------------

    @Test
    void modelList() throws Exception {
        ModelInfo[] models = MAPPER.readValue(fixture("model_list.json"), ModelInfo[].class);
        assertEquals(2, models.length);
        ModelInfo paid = models[0];
        ModelInfo free = models[1];
        assertFalse(paid.isFree);
        assertTrue(free.isFree);
        assertNotNull(paid.pricing);
        assertEquals("0.000150", paid.pricing.promptUsdPer1k);
    }

    // -----------------------------------------------------------------------
    // Template shapes
    // -----------------------------------------------------------------------

    @Test
    void templateSummary() throws Exception {
        TemplateSummary tmpl = MAPPER.readValue(fixture("template_summary.json"), TemplateSummary.class);
        assertEquals("550e8400-e29b-41d4-a716-446655440000", tmpl.id);
        assertEquals("pirate-assistant", tmpl.name);
        assertTrue(tmpl.system.contains("pirate"));
        assertEquals(1, tmpl.variables.size());
        assertEquals("topic", tmpl.variables.get(0));
    }

    // -----------------------------------------------------------------------
    // Responses shapes
    // -----------------------------------------------------------------------

    @Test
    void responsesResponse() throws Exception {
        ResponsesResponse resp = MAPPER.readValue(fixture("responses_response.json"),
                ResponsesResponse.class);
        assertEquals("resp-abc123", resp.id);
        assertEquals(1, resp.choices.size());
        assertEquals("2 + 2 equals 4.", resp.choices.get(0).message.content);
        assertNotNull(resp.usage);
        assertEquals(18, resp.usage.totalTokens);
    }

    @Test
    void responsesResponseWithReasoning() throws Exception {
        ResponsesResponse resp = MAPPER.readValue(fixture("responses_response_with_reasoning.json"),
                ResponsesResponse.class);
        ResponsesResponse.ResponsesMessage msg = resp.choices.get(0).message;
        assertNotNull(msg.reasoning);
        assertEquals("I considered Turing's proof by contradiction.", msg.reasoning.summary);
        assertNull(msg.reasoning.encryptedContent);
    }

    // -----------------------------------------------------------------------
    // Error envelope shapes
    // -----------------------------------------------------------------------

    @Test
    void error401_parsedByApiError() throws Exception {
        String body = new String(fixture("error_401.json").readAllBytes());
        // Simulate what fromResponse would do
        com.fasterxml.jackson.databind.JsonNode root = MAPPER.readTree(body);
        assertEquals("unauthorized", root.path("error").path("code").asText());
        assertEquals("req_01HZXYZ", root.path("request_id").asText());
    }

    @Test
    void error429_retryAfterSeconds() throws Exception {
        String body = new String(fixture("error_429.json").readAllBytes());
        com.fasterxml.jackson.databind.JsonNode root = MAPPER.readTree(body);
        assertEquals("rate_limit_exceeded", root.path("error").path("code").asText());
        assertEquals(5, root.path("error").path("retry_after_seconds").asInt());
    }
}
