package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CostTypesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void tokenLineDeserialises() throws Exception {
        String json = "{\"tokens\":1200,\"rate_per_1m_usd\":\"2.50\",\"quantity\":1200,"
                + "\"unit\":\"per_1m_tokens\",\"rate_usd\":\"2.50\",\"charged\":true}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertEquals(1200, line.tokens);
        assertEquals("2.50", line.ratePer1mUsd);
        assertEquals(0, new BigDecimal("1200").compareTo(line.quantity));
        assertEquals("per_1m_tokens", line.unit);
        assertEquals("2.50", line.rateUsd);
        assertTrue(line.charged);
        assertTrue(line.hasTokens());
    }

    @Test
    void nonTokenLineAllowsNullTokensAndRate() throws Exception {
        String json = "{\"tokens\":null,\"rate_per_1m_usd\":null,\"quantity\":5,"
                + "\"unit\":\"per_second\",\"rate_usd\":\"0.10\",\"charged\":false,"
                + "\"modifiers\":[\"with_audio\"]}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertFalse(line.hasTokens());
        assertEquals("per_second", line.unit);
        assertFalse(line.charged);
        assertNotNull(line.modifiers);
        assertTrue(line.modifiers.isArray());
    }

    @Test
    void missingTokenFieldsAreNull() throws Exception {
        CostLineOut line = mapper.readValue("{\"unit\":\"per_image\",\"unknown\":1}", CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
    }

    @Test
    void breakdownReadsRounding() throws Exception {
        CostBreakdownOut b = mapper.readValue("{\"rounding_usd\":\"0.000001\"}", CostBreakdownOut.class);
        assertEquals("0.000001", b.roundingUsd);
    }
}
