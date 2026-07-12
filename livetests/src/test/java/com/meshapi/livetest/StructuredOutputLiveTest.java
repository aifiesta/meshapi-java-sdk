package com.meshapi.livetest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Live coverage for the structured-output ergonomic API: the JSON schema is
 * derived from the POJO by reflection and the reply is decoded into a typed value.
 */
class StructuredOutputLiveTest extends LiveTestBase {

    public static class Country {
        @JsonProperty("country") public String country;
        @JsonProperty("capital") public String capital;
    }

    @Test
    void parse_typedResult() {
        MeshAPI client = newClient();
        Country got = client.chat().completions().parse(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("What is the capital of France?"))
                        .maxTokens(1000)
                        .temperature(0)
                        .build(),
                Country.class);

        assertNotNull(got.capital);
        assertTrue(got.capital.toLowerCase().contains("paris"),
                "expected Paris, got: " + got.capital);
        System.out.printf("[PASS] parse → %s / %s%n", got.country, got.capital);
    }
}
