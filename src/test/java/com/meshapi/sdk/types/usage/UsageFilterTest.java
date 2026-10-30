package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsageFilterTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void emptyFilterSerializesToEmptyObject() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(UsageFilter.builder().build()));
    }

    @Test
    void newFieldsSerializeWithWireNames() throws Exception {
        UsageFilter filter = UsageFilter.builder()
            .provider("openai")
            .faultOwner("provider")
            .isByok(false)
            .stream(true)
            .trafficType("external")
            .isRetryAttempt(true, false)
            .build();
        JsonNode node = mapper.readTree(mapper.writeValueAsString(filter));
        assertEquals("openai", node.get("provider").get(0).asText());
        assertEquals("provider", node.get("fault_owner").get(0).asText());
        assertFalse(node.get("is_byok").asBoolean());
        assertTrue(node.get("stream").asBoolean());
        assertEquals("external", node.get("traffic_type").get(0).asText());
        assertEquals(2, node.get("is_retry_attempt").size());
        assertTrue(node.get("is_retry_attempt").get(0).asBoolean());
    }

    @Test
    void omittedFieldsAreNotSent() throws Exception {
        JsonNode node = mapper.readTree(mapper.writeValueAsString(UsageFilter.builder().stream(false).build()));
        assertEquals(1, node.size());
        assertFalse(node.has("is_retry_attempt"));
    }
}
