package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResponsesInputTokensResponseTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesInputTokens() throws Exception {
        String json = "{\"object\":\"response.input_tokens\",\"input_tokens\":42}";
        ResponsesInputTokensResponse r = mapper.readValue(json, ResponsesInputTokensResponse.class);
        assertEquals("response.input_tokens", r.object);
        assertEquals(42, r.inputTokens);
    }

    @Test
    void ignoresUnknownFields() throws Exception {
        String json = "{\"input_tokens\":7,\"extra\":true}";
        ResponsesInputTokensResponse r = mapper.readValue(json, ResponsesInputTokensResponse.class);
        assertEquals(7, r.inputTokens);
        assertNull(r.object);
    }
}
