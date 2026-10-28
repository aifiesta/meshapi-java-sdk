package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsageTypesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void costLineAcceptsNullTokensAndRate() throws Exception {
        String json = "{\"tokens\":null,\"rate_per_1m_usd\":null,\"quantity\":5,\"unit\":\"per_second\","
                + "\"rate_usd\":\"0.10\",\"charged\":true,\"modifiers\":[\"with_audio\"],\"extra\":1}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertNull(line.tokens);
        assertNull(line.ratePer1mUsd);
        assertFalse(line.isTokenBilled());
        assertEquals(0, new BigDecimal("5").compareTo(line.quantity));
        assertEquals("per_second", line.unit);
        assertEquals("0.10", line.rateUsd);
        assertTrue(line.charged);
        assertTrue(line.modifiers.isArray());
    }

    @Test
    void costLineReadsTokenBilledLine() throws Exception {
        String json = "{\"tokens\":1200,\"rate_per_1m_usd\":\"2.50\"}";
        CostLineOut line = mapper.readValue(json, CostLineOut.class);
        assertEquals(1200, line.tokens);
        assertEquals("2.50", line.ratePer1mUsd);
        assertTrue(line.isTokenBilled());
    }

    @Test
    void costBreakdownReadsRounding() throws Exception {
        String json = "{\"lines\":[{\"unit\":\"per_image\",\"charged\":false}],\"rounding_usd\":\"0.0000001\"}";
        CostBreakdownOut out = mapper.readValue(json, CostBreakdownOut.class);
        assertEquals("0.0000001", out.roundingUsd);
        assertEquals(1, out.lines.size());
        assertFalse(out.lines.get(0).charged);
    }

    @Test
    void usageFilterSerialisesNewFields() throws Exception {
        UsageFilter filter = UsageFilter.builder()
                .provider("openai")
                .faultOwner("provider")
                .isByok(false)
                .stream(true)
                .trafficType("external")
                .isRetryAttempt(List.of(true, false))
                .build();
        JsonNode node = mapper.valueToTree(filter);
        assertEquals("openai", node.get("provider").asText());
        assertEquals("provider", node.get("fault_owner").asText());
        assertFalse(node.get("is_byok").asBoolean());
        assertTrue(node.get("stream").asBoolean());
        assertEquals("external", node.get("traffic_type").asText());
        assertEquals(2, node.get("is_retry_attempt").size());
    }

    @Test
    void usageFilterOmitsUnsetFields() {
        JsonNode node = mapper.valueToTree(UsageFilter.builder().build());
        assertEquals(0, node.size());
    }
}
