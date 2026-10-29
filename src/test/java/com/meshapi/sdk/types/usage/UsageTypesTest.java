package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsageTypesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void costLineAcceptsNullTokensAndRate() throws Exception {
        String json = "{\"tokens\":null,\"rate_per_1m_usd\":null,\"quantity\":5,"
                + "\"unit\":\"per_second\",\"rate_usd\":\"0.10\",\"charged\":true,"
                + "\"modifiers\":[\"with_audio\"],\"future\":1}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertEquals(5, line.quantity.asInt());
        assertEquals("per_second", line.unit);
        assertEquals("0.10", line.rateUsd.asText());
        assertTrue(line.charged);
        assertTrue(line.modifiers.isArray());
    }

    @Test
    void costLineStillReadsTokenFields() throws Exception {
        String json = "{\"tokens\":120,\"rate_per_1m_usd\":\"2.50\"}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertEquals(120, line.tokens);
        assertEquals("2.50", line.ratePer1mUsd);
        assertNull(line.charged);
    }

    @Test
    void costBreakdownReadsRounding() throws Exception {
        String json = "{\"lines\":[{\"unit\":\"per_image\"}],\"rounding_usd\":\"0.000001\"}";
        CostBreakdownOut b = mapper.readValue(json, CostBreakdownOut.class);
        assertEquals(1, b.lines.size());
        assertEquals("per_image", b.lines.get(0).unit);
        assertEquals("0.000001", b.roundingUsd.asText());
    }

    @Test
    void usageFilterSerialisesNewFields() throws Exception {
        UsageFilter f = UsageFilter.builder()
                .provider(List.of("openai"))
                .faultOwner(List.of("provider"))
                .isByok(false)
                .stream(true)
                .trafficType(List.of("external"))
                .isRetryAttempt(List.of(true, false))
                .build();
        JsonNode n = mapper.readTree(mapper.writeValueAsString(f));
        assertEquals("openai", n.get("provider").get(0).asText());
        assertEquals("provider", n.get("fault_owner").get(0).asText());
        assertFalse(n.get("is_byok").asBoolean());
        assertTrue(n.get("stream").asBoolean());
        assertEquals("external", n.get("traffic_type").get(0).asText());
        assertEquals(2, n.get("is_retry_attempt").size());
    }

    @Test
    void usageFilterOmitsUnsetFields() throws Exception {
        String json = mapper.writeValueAsString(UsageFilter.builder().build());
        assertEquals("{}", json);
    }
}
