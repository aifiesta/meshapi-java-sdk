package com.meshapi.sdk.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.audio.AudioTranslationRequest;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.responses.ResponsesRequest;
import com.meshapi.sdk.types.templates.CreateTemplateRequest;
import com.meshapi.sdk.types.websearch.WebSearchRequest;
import com.meshapi.sdk.types.websearch.WebSearchResponse;
import com.meshapi.sdk.types.websearch.WebSearchResultItem;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests for pass-2 additions.
 * Verifies serialisation shapes and no-network wiring. No live server required.
 */
class Pass2ContractTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static InputStream fixture(String name) {
        InputStream in = Pass2ContractTest.class.getResourceAsStream("/fixtures/" + name);
        assertNotNull(in, "fixture not found: " + name);
        return in;
    }

    // -----------------------------------------------------------------------
    // WebSearchResponse — parse fixture
    // -----------------------------------------------------------------------

    @Test
    void webSearchResponse_parsedCorrectly() throws Exception {
        WebSearchResponse resp = MAPPER.readValue(fixture("web_search_response.json"), WebSearchResponse.class);
        assertEquals("latest James Webb telescope discoveries", resp.query);
        assertEquals("native", resp.provider);
        assertNotNull(resp.answer);
        assertTrue(resp.answer.contains("James Webb"));
        assertNotNull(resp.results);
        assertEquals(1, resp.results.size());
        WebSearchResultItem item = resp.results.get(0);
        assertEquals("JWST spots earliest known galaxy", item.title);
        assertEquals("https://example.com/jwst", item.url);
        assertEquals(0.93, item.score, 0.001);
        assertEquals("2024-06-11", item.publishedDate);
        assertEquals("req_01J123456789", resp.requestId);
        // Both page-content keys arrive on every response. Unasked-for, they read
        // null and false — not absent, which is what the published contract pins.
        assertNull(item.pageContent, "page_content should be null when not requested");
        assertFalse(item.pageContentTruncated);
    }

    // -----------------------------------------------------------------------
    // Page content — MESH-1220
    // -----------------------------------------------------------------------

    @Test
    void webSearch_pageContentAndTruncationFlagParsed() throws Exception {
        String raw = """
            {
              "query": "mars rovers",
              "provider": "tinyfish",
              "request_id": "req_01J",
              "results": [
                {
                  "title": "Perseverance",
                  "url": "https://example.com/p",
                  "content": "a short snippet",
                  "score": 0.9,
                  "page_content": "the full extracted page text",
                  "page_content_truncated": true
                }
              ]
            }
            """;
        WebSearchResponse resp = MAPPER.readValue(raw, WebSearchResponse.class);
        WebSearchResultItem item = resp.results.get(0);
        assertEquals("the full extracted page text", item.pageContent);
        assertTrue(item.pageContentTruncated);
        // The snippet is a separate field; page text never replaces it.
        assertEquals("a short snippet", item.content);
    }

    @Test
    void webSearch_responseWithoutTheNewKeysStillParses() throws Exception {
        // An older gateway omits them entirely; that must not be a parse error.
        String raw = """
            {"query":"q","provider":"native","request_id":"req_01J",
             "results":[{"title":"t","url":"https://example.com","content":"snippet"}]}
            """;
        WebSearchResponse resp = MAPPER.readValue(raw, WebSearchResponse.class);
        WebSearchResultItem item = resp.results.get(0);
        assertNull(item.pageContent);
        assertFalse(item.pageContentTruncated, "an absent flag reads false, which is the correct answer");
    }

    @Test
    void webSearchRequest_pageContentFlagAndTinyfishSerialise() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder()
                .query("mars rovers")
                .provider("tinyfish")
                .includePageContent(true)
                .build();

        var node = MAPPER.readTree(MAPPER.writeValueAsString(req));
        assertEquals("tinyfish", node.get("provider").asText());
        assertTrue(node.get("include_page_content").asBoolean());
    }

    // -----------------------------------------------------------------------
    // WebSearchRequest — builder + serialisation
    // -----------------------------------------------------------------------

    @Test
    void webSearchRequest_builderAndSerialization() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder()
                .query("Mars rover news")
                .maxResults(10)
                .includeAnswer(true)
                .searchDepth("advanced")
                .provider("tavily")
                .includeDomains(List.of("nasa.gov"))
                .excludeDomains(List.of("spam.com"))
                .build();

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertEquals("Mars rover news", node.get("query").asText());
        assertEquals(10, node.get("max_results").asInt());
        assertTrue(node.get("include_answer").asBoolean());
        assertEquals("advanced", node.get("search_depth").asText());
        assertEquals("tavily", node.get("provider").asText());
        assertEquals("nasa.gov", node.get("include_domains").get(0).asText());
        assertEquals("spam.com", node.get("exclude_domains").get(0).asText());
    }

    @Test
    void webSearchRequest_requiresQuery() {
        assertThrows(IllegalArgumentException.class, () ->
                WebSearchRequest.builder().build()
        );
    }

    @Test
    void webSearchRequest_nullOptionalFieldsOmitted() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder().query("test").build();
        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertFalse(node.has("model"), "model should be omitted when null");
        assertFalse(node.has("provider"), "provider should be omitted when null");
        assertFalse(node.has("max_results"), "max_results should be omitted when null");
        assertFalse(node.has("include_answer"), "include_answer should be omitted when null");
        // Additive means additive: a caller who never set the flag sends exactly
        // the body it sent before this field existed.
        assertFalse(
                node.has("include_page_content"),
                "include_page_content should be omitted when null");
    }

    // -----------------------------------------------------------------------
    // MeshAPI client — web() accessor is wired
    // -----------------------------------------------------------------------

    @Test
    void meshApiClient_hasWebAccessor() {
        MeshAPI client = MeshAPI.builder()
                .baseUrl("https://api.meshapi.ai")
                .token("test-token")
                .build();
        assertNotNull(client.web());
    }

    // -----------------------------------------------------------------------
    // AudioTranslationRequest — serialisation check
    // -----------------------------------------------------------------------

    @Test
    void audioTranslationRequest_serialization() throws Exception {
        AudioTranslationRequest req = new AudioTranslationRequest();
        req.model = "openai/whisper-1";
        req.prompt = "Technical transcription";
        req.responseFormat = "json";
        req.temperature = 0.2;

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertEquals("openai/whisper-1", node.get("model").asText());
        assertEquals("Technical transcription", node.get("prompt").asText());
        assertEquals("json", node.get("response_format").asText());
        assertEquals(0.2, node.get("temperature").asDouble(), 0.001);
    }

    @Test
    void audioTranslationRequest_nullFieldsOmitted() throws Exception {
        AudioTranslationRequest req = new AudioTranslationRequest();
        req.model = "openai/whisper-1";

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertFalse(node.has("prompt"), "prompt should be omitted when null");
        assertFalse(node.has("response_format"), "response_format should be omitted when null");
        assertFalse(node.has("temperature"), "temperature should be omitted when null");
    }

    // -----------------------------------------------------------------------
    // ResponsesRequest — new fields present in JSON
    // -----------------------------------------------------------------------

    @Test
    void responsesRequest_newFieldsSerialized() throws Exception {
        ResponsesRequest req = new ResponsesRequest();
        req.model = "openai/gpt-4o";
        req.input = "hello";
        req.previousResponseId = "resp_prev_123";
        req.instructions = "Be concise.";
        req.store = true;
        req.expireAt = 1700000000L;
        req.maxToolCalls = 3;
        req.timeout = 30;

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertEquals("resp_prev_123", node.get("previous_response_id").asText());
        assertEquals("Be concise.", node.get("instructions").asText());
        assertTrue(node.get("store").asBoolean());
        assertEquals(1700000000L, node.get("expire_at").asLong());
        assertEquals(3, node.get("max_tool_calls").asInt());
        assertEquals(30, node.get("timeout").asInt());
    }

    @Test
    void responsesRequest_nullNewFieldsOmitted() throws Exception {
        ResponsesRequest req = new ResponsesRequest();
        req.model = "openai/gpt-4o";
        req.input = "hello";

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertFalse(node.has("previous_response_id"));
        assertFalse(node.has("instructions"));
        assertFalse(node.has("store"));
        assertFalse(node.has("expire_at"));
        assertFalse(node.has("max_tool_calls"));
        assertFalse(node.has("context_management"));
        assertFalse(node.has("thinking"));
        assertFalse(node.has("caching"));
        assertFalse(node.has("include"));
        assertFalse(node.has("timeout"));
    }

    // -----------------------------------------------------------------------
    // ChatCompletionRequest — new fields
    // -----------------------------------------------------------------------

    @Test
    void chatCompletionRequest_newFieldsSerialized() throws Exception {
        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("openai/gpt-4o")
                .addMessage(ChatMessage.user("hi"))
                .cache(true)
                .reasoningEffort("high")
                .timeout(60)
                .build();

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertTrue(node.get("cache").asBoolean());
        assertEquals("high", node.get("reasoning_effort").asText());
        assertEquals(60, node.get("timeout").asInt());
    }

    @Test
    void chatCompletionRequest_nullNewFieldsOmitted() throws Exception {
        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("openai/gpt-4o")
                .addMessage(ChatMessage.user("hi"))
                .build();

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertFalse(node.has("cache"));
        assertFalse(node.has("reasoning_effort"));
        assertFalse(node.has("timeout"));
    }

    // -----------------------------------------------------------------------
    // CreateTemplateRequest — team_id
    // -----------------------------------------------------------------------

    @Test
    void createTemplateRequest_teamIdSerialized() throws Exception {
        CreateTemplateRequest req = CreateTemplateRequest.builder()
                .name("my-template")
                .teamId("team_abc123")
                .build();

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertEquals("team_abc123", node.get("team_id").asText());
    }

    @Test
    void createTemplateRequest_nullTeamIdOmitted() throws Exception {
        CreateTemplateRequest req = CreateTemplateRequest.builder()
                .name("my-template")
                .build();

        String json = MAPPER.writeValueAsString(req);
        var node = MAPPER.readTree(json);
        assertFalse(node.has("team_id"));
    }
}
