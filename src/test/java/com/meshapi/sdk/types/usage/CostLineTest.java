package com.meshapi.sdk.types.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class CostLineTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesTokenLine() throws Exception {
        String json = "{\"tokens\":1200,\"rate_per_1m_usd\":\"0.40\",\"charged\":true}";
        CostLine line = mapper.readValue(json, CostLine.class);
        assertEquals(1200, line.tokens);
        assertEquals("0.40", line.ratePer1mUsd);
        assertTrue(line.charged);
        assertTrue(line.isTokenBilled());
    }

    @Test
    void deserializesNonTokenLineWithNullTokenFields() throws Exception {
        String json = "{\"tokens\":null,\"rate_per_1m_usd\":null,\"quantity\":5,"
                + "\"unit\":\"second\",\"rate_usd\":\"0.05\",\"charged\":false,"
                + "\"modifiers\":[\"with_audio\"],\"future_field\":1}";
        CostLine line = mapper.readValue(json, CostLine.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertEquals(5.0, line.quantity);
        assertEquals("second", line.unit);
        assertEquals("0.05", line.rateUsd);
        assertFalse(line.charged);
        assertTrue(line.modifiers.isArray());
        assertFalse(line.isTokenBilled());
    }

    @Test
    void toleratesMissingTokenFields() throws Exception {
        CostLine line = mapper.readValue("{\"unit\":\"image\",\"quantity\":2}", CostLine.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertEquals(2.0, line.quantity);
    }

    @Test
    void deserializesBreakdownRounding() throws Exception {
        CostBreakdown b = mapper.readValue("{\"rounding_usd\":\"0.000001\",\"v\":2}", CostBreakdown.class);
        assertEquals("0.000001", b.roundingUsd);
    }
}
