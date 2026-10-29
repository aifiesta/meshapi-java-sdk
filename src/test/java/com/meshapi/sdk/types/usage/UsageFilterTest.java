package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsageFilterTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialisesAllNewFields() throws Exception {
        UsageFilter filter = UsageFilter.builder()
            .provider(List.of("openai"))
            .faultOwner(List.of("provider"))
            .isByok(false)
            .stream(true)
            .trafficType(List.of("external"))
            .isRetryAttempt(List.of(true, false))
            .build();
        JsonNode node = mapper.readTree(mapper.writeValueAsString(filter));
        assertEquals("openai", node.get("provider").get(0).asText());
        assertEquals("provider", node.get("fault_owner").get(0).asText());
        assertFalse(node.get("is_byok").asBoolean());
        assertTrue(node.get("stream").asBoolean());
        assertEquals("external", node.get("traffic_type").get(0).asText());
        assertEquals(2, node.get("is_retry_attempt").size());
    }

    @Test
    void omitsUnsetFields() throws Exception {
        String json = mapper.writeValueAsString(UsageFilter.builder().build());
        assertEquals("{}", json);
    }
}
