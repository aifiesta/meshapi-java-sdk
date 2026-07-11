package com.meshapi.sdk.unit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.StructuredOutputError;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.internal.StructuredOutputs;
import com.meshapi.sdk.resources.CompletionsResource;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.chat.StructuredParseOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StructuredParseTest {

    public static class Country {
        @JsonProperty("country") public String country;
        @JsonProperty("capital") public String capital;
    }

    enum Color { RED, BLUE }

    public static class Address {
        @JsonProperty("city") public String city;
    }

    public static class Person {
        @JsonProperty("name") public String name;
        @JsonProperty("age") public int age;
        @JsonProperty("tags") public List<String> tags;
        @JsonProperty("address") public Address address;
        @JsonProperty("color") public Color color;
    }

    private static ChatCompletionResponse resp(String content) {
        ChatCompletionResponse r = new ChatCompletionResponse();
        ChatCompletionResponse.ResponseMessage m = new ChatCompletionResponse.ResponseMessage();
        m.content = content;
        ChatCompletionResponse.Choice c = new ChatCompletionResponse.Choice();
        c.message = m;
        r.choices = List.of(c);
        return r;
    }

    private static ChatCompletionRequest req() {
        return ChatCompletionRequest.builder()
                .model("openai/gpt-4o-mini")
                .addMessage(ChatMessage.user("Give me facts about France."))
                .build();
    }

    @SuppressWarnings("unchecked")
    private static CompletionsResource resourceReturning(HttpClient http, ChatCompletionResponse... responses) {
        when(http.getObjectMapper()).thenReturn(new ObjectMapper());
        var stub = when(http.post(eq("/v1/chat/completions"), any(), eq(ChatCompletionResponse.class)));
        for (ChatCompletionResponse r : responses) {
            stub = stub.thenReturn(r);
        }
        return new CompletionsResource(http);
    }

    // ── reflector ─────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void schemaForClassReflectsFields() {
        Map<String, Object> s = StructuredOutputs.schemaForClass(Person.class);
        assertEquals("object", s.get("type"));
        assertEquals(false, s.get("additionalProperties"));
        Map<String, Object> props = (Map<String, Object>) s.get("properties");
        assertEquals("string", ((Map<String, Object>) props.get("name")).get("type"));
        assertEquals("integer", ((Map<String, Object>) props.get("age")).get("type"));
        assertEquals("array", ((Map<String, Object>) props.get("tags")).get("type"));
        assertEquals("object", ((Map<String, Object>) props.get("address")).get("type"));
        Map<String, Object> color = (Map<String, Object>) props.get("color");
        assertEquals("string", color.get("type"));
        assertTrue(((List<String>) color.get("enum")).containsAll(List.of("RED", "BLUE")));
        List<String> required = (List<String>) s.get("required");
        assertTrue(required.containsAll(List.of("name", "age", "tags", "address", "color")));
    }

    // ── parse ─────────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void parseSuccessReturnsTypedAndSendsSchema() {
        HttpClient http = mock(HttpClient.class);
        CompletionsResource comp = resourceReturning(http, resp("{\"country\":\"France\",\"capital\":\"Paris\"}"));
        ChatCompletionRequest request = req();

        Country got = comp.parse(request, Country.class);

        assertEquals("Paris", got.capital);
        assertEquals("France", got.country);
        Map<String, Object> rf = request.getResponseFormat();
        assertEquals("json_schema", rf.get("type"));
        Map<String, Object> js = (Map<String, Object>) rf.get("json_schema");
        assertEquals("response", js.get("name"));
        assertNotNull(((Map<String, Object>) js.get("schema")).get("properties"));
    }

    @Test
    void parseProseHintsModelSupport() {
        HttpClient http = mock(HttpClient.class);
        CompletionsResource comp = resourceReturning(http, resp("Sure! The capital of France is Paris."));
        StructuredOutputError err = assertThrows(StructuredOutputError.class,
                () -> comp.parse(req(), Country.class));
        assertTrue(err.getMessage().contains("does not support structured outputs"), err.getMessage());
        assertTrue(err.getMessage().contains("app.meshapi.ai") && err.getMessage().contains("/models"));
        assertEquals("structured_output_parse_error", err.getErrorCode());
        assertNotNull(err.getCause());
    }

    @Test
    void parseShapeMismatch() {
        HttpClient http = mock(HttpClient.class);
        // object where a string is expected -> Jackson MismatchedInputException (valid JSON, wrong shape)
        CompletionsResource comp = resourceReturning(http,
                resp("{\"country\":{\"nested\":true},\"capital\":\"Paris\"}"));
        StructuredOutputError err = assertThrows(StructuredOutputError.class,
                () -> comp.parse(req(), Country.class));
        assertTrue(err.getMessage().contains("did not match the requested type"), err.getMessage());
    }

    @Test
    void parseDefaultNoRetry() {
        HttpClient http = mock(HttpClient.class);
        CompletionsResource comp = resourceReturning(http, resp("not json"));
        assertThrows(StructuredOutputError.class, () -> comp.parse(req(), Country.class));
        verify(http, times(1)).post(eq("/v1/chat/completions"), any(), eq(ChatCompletionResponse.class));
    }

    @Test
    void parseRetryRecoversAndAppendsCorrection() {
        HttpClient http = mock(HttpClient.class);
        CompletionsResource comp = resourceReturning(http,
                resp("not json"),
                resp("{\"country\":\"France\",\"capital\":\"Paris\"}"));
        ChatCompletionRequest request = req();

        Country got = comp.parse(request, Country.class, StructuredParseOptions.create().maxRetries(1));

        assertEquals("Paris", got.capital);
        verify(http, times(2)).post(eq("/v1/chat/completions"), any(), eq(ChatCompletionResponse.class));
        // original user + assistant(bad) + user(correction)
        assertEquals(3, request.getMessages().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void parseWithSchemaOverride() {
        HttpClient http = mock(HttpClient.class);
        CompletionsResource comp = resourceReturning(http, resp("{\"country\":\"France\",\"capital\":\"Paris\"}"));
        ChatCompletionRequest request = req();

        comp.parse(request, Country.class, StructuredParseOptions.create()
                .schema(Map.of("type", "object"))
                .schemaName("custom"));

        Map<String, Object> js = (Map<String, Object>) request.getResponseFormat().get("json_schema");
        assertEquals("custom", js.get("name"));
        assertEquals("object", ((Map<String, Object>) js.get("schema")).get("type"));
    }
}
