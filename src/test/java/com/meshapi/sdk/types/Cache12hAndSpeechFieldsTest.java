package com.meshapi.sdk.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.audio.SpeechRequest;
import com.meshapi.sdk.types.chat.ChatPromptTokensDetails;
import com.meshapi.sdk.types.models.ModelPricing;
import org.junit.jupiter.api.Test;

class Cache12hAndSpeechFieldsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void speechRequestSerializesVolAndEmotion() throws Exception {
        SpeechRequest req = SpeechRequest.builder()
                .model("example/tts-model")
                .input("Hello")
                .voice("alloy")
                .vol(2.5)
                .emotion("happy")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(2.5, json.get("vol").asDouble());
        assertEquals("happy", json.get("emotion").asText());
    }

    @Test
    void speechRequestOmitsUnsetVolAndEmotion() throws Exception {
        SpeechRequest req = SpeechRequest.builder()
                .model("example/tts-model")
                .input("Hello")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("vol"));
        assertFalse(json.has("emotion"));
    }

    @Test
    void promptTokensDetailsReadsCacheWrite12hTokens() throws Exception {
        ChatPromptTokensDetails d = mapper.readValue(
                "{\"cached_tokens\":10,\"cache_write_12h_tokens\":42,\"unknown\":1}",
                ChatPromptTokensDetails.class);
        assertEquals(42, d.cacheWrite12hTokens);
        assertEquals(10, d.cachedTokens);
    }

    @Test
    void promptTokensDetailsToleratesMissingCacheWrite12hTokens() throws Exception {
        ChatPromptTokensDetails d = mapper.readValue("{}", ChatPromptTokensDetails.class);
        assertNull(d.cacheWrite12hTokens);
    }

    @Test
    void modelPricingReadsCacheWrite12hRates() throws Exception {
        String body = "{"
                + "\"cache_write_12h_input_usd_per_1m\":1.25,"
                + "\"long_context_cache_write_12h_input_usd_per_1m\":2.5,"
                + "\"flex_cache_write_12h_input_usd_per_1m\":0.625,"
                + "\"flex_long_context_cache_write_12h_input_usd_per_1m\":1.25"
                + "}";
        ModelPricing p = mapper.readValue(body, ModelPricing.class);
        assertEquals(1.25, p.cacheWrite12hInputUsdPer1m);
        assertEquals(2.5, p.longContextCacheWrite12hInputUsdPer1m);
        assertEquals(0.625, p.flexCacheWrite12hInputUsdPer1m);
        assertEquals(1.25, p.flexLongContextCacheWrite12hInputUsdPer1m);
    }

    @Test
    void modelPricingLeavesAbsentRatesNull() throws Exception {
        ModelPricing p = mapper.readValue("{}", ModelPricing.class);
        assertNull(p.cacheWrite12hInputUsdPer1m);
        assertNull(p.flexLongContextCacheWrite12hInputUsdPer1m);
    }
}
