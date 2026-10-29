package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CostLineOutTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserialisesNonTokenLineWithNullTokenFields() throws Exception {
        String json = "{\"tokens\":null,\"rate_per_1m_usd\":null,\"quantity\":5,"
            + "\"unit\":\"per_second\",\"rate_usd\":\"0.10\",\"charged\":true,"
            + "\"modifiers\":{\"resolution\":\"720p\",\"with_audio\":true}}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertEquals(0, new BigDecimal("5").compareTo(line.quantity));
        assertEquals("per_second", line.unit);
        assertEquals("0.10", line.rateUsd);
        assertTrue(line.isCharged());
        assertEquals("720p", line.modifiers.get("resolution").asText());
    }

    @Test
    void deserialisesTokenLineWithoutNewFields() throws Exception {
        String json = "{\"tokens\":1200,\"rate_per_1m_usd\":\"2.50\"}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertEquals(1200, line.tokens);
        assertEquals("2.50", line.ratePer1mUsd);
        assertNull(line.quantity);
        assertTrue(line.isCharged());
    }

    @Test
    void uncharged_line_reports_false() throws Exception {
        CostLineOut line = mapper.readValue("{\"charged\":false}", CostLineOut.class);
        assertFalse(line.isCharged());
    }

    @Test
    void breakdownReadsRoundingAndIgnoresUnknown() throws Exception {
        String json = "{\"lines\":[{\"quantity\":\"2\",\"unit\":\"per_image\"}],"
            + "\"rounding_usd\":\"0.000001\",\"v\":2}";
        CostBreakdownOut b = mapper.readValue(json, CostBreakdownOut.class);
        assertEquals("0.000001", b.roundingUsd);
        assertEquals(1, b.lines.size());
        assertEquals("per_image", b.lines.get(0).unit);
    }
}
