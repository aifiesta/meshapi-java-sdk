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
        UsageFilter f = UsageFilter.builder()
                .provider(List.of("openai"))
                .faultOwner(List.of("provider"))
                .isByok(true)
                .stream(false)
                .trafficType(List.of("external"))
                .isRetryAttempt(List.of(true, false))
                .build();
        JsonNode n = mapper.readTree(mapper.writeValueAsString(f));
        assertEquals("openai", n.get("provider").get(0).asText());
        assertEquals("provider", n.get("fault_owner").get(0).asText());
        assertTrue(n.get("is_byok").asBoolean());
        assertFalse(n.get("stream").asBoolean());
        assertEquals("external", n.get("traffic_type").get(0).asText());
        assertEquals(2, n.get("is_retry_attempt").size());
    }

    @Test
    void omitsUnsetFields() throws Exception {
        String json = mapper.writeValueAsString(UsageFilter.builder().build());
        assertEquals("{}", json);
    }

    @Test
    void builderCopiesLists() {
        java.util.ArrayList<String> providers = new java.util.ArrayList<>(List.of("openai"));
        UsageFilter f = UsageFilter.builder().provider(providers).build();
        providers.add("anthropic");
        assertEquals(List.of("openai"), f.getProvider());
    }
}
