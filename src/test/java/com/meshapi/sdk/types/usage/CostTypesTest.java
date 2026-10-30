package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostTypesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void tokenPricedLineDeserializes() throws Exception {
        String json = "{\"tokens\":1200,\"rate_per_1m_usd\":\"0.15\",\"unit\":\"per_1m_tokens\",\"quantity\":1200}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertEquals(1200, line.tokens);
        assertEquals("0.15", line.ratePer1mUsd);
        assertEquals("per_1m_tokens", line.unit);
        assertEquals(1200, line.quantity.asInt());
        assertTrue(line.isTokenPriced());
    }

    @Test
    void nonTokenLineAllowsNullTokensAndRate() throws Exception {
        String json = "{\"tokens\":null,\"rate_per_1m_usd\":null,\"unit\":\"per_image\",\"quantity\":2,\"rate_usd\":\"0.04\",\"charged\":\"0.08\",\"modifiers\":[]}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertFalse(line.isTokenPriced());
        assertEquals("per_image", line.unit);
        assertEquals("0.04", line.rateUsd.asText());
        assertEquals("0.08", line.charged.asText());
        assertTrue(line.modifiers.isArray());
    }

    @Test
    void missingTokensAndRateDeserialize() throws Exception {
        CostLineOut line = mapper.readValue("{\"unit\":\"per_second\"}", CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
    }

    @Test
    void breakdownReadsRoundingAndIgnoresUnknown() throws Exception {
        CostBreakdownOut b = mapper.readValue("{\"rounding_usd\":\"0.000001\",\"other\":1}", CostBreakdownOut.class);
        assertEquals("0.000001", b.roundingUsd);
    }
}
