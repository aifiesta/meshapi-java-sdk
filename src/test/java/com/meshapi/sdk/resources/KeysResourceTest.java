package com.meshapi.sdk.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.keys.ApiKeyDetails;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeysResourceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void retrieveCallsKeyPath() {
        AtomicReference<String> seen = new AtomicReference<>();
        ApiKeyDetails expected = new ApiKeyDetails();
        KeysResource keys = new KeysResource(new KeysResource.Getter() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T get(String path, Class<T> type) {
                seen.set(path);
                assertEquals(ApiKeyDetails.class, type);
                return (T) expected;
            }
        });

        ApiKeyDetails result = keys.retrieve("key_123");

        assertEquals("/v1/keys/key_123", seen.get());
        assertSame(expected, result);
    }

    @Test
    void retrieveEncodesKeyId() {
        AtomicReference<String> seen = new AtomicReference<>();
        KeysResource keys = new KeysResource(new KeysResource.Getter() {
            @Override
            public <T> T get(String path, Class<T> type) {
                seen.set(path);
                return null;
            }
        });

        keys.retrieve("a/b c");

        assertEquals("/v1/keys/a%2Fb%20c", seen.get());
    }

    @Test
    void retrieveRejectsBlankId() {
        KeysResource keys = new KeysResource(new KeysResource.Getter() {
            @Override
            public <T> T get(String path, Class<T> type) {
                throw new AssertionError("should not be called");
            }
        });

        assertThrows(IllegalArgumentException.class, () -> keys.retrieve(null));
        assertThrows(IllegalArgumentException.class, () -> keys.retrieve("  "));
    }

    @Test
    void deserialisesDetailsAndIgnoresUnknownFields() throws Exception {
        String json = "{\"id\":\"key_123\",\"org_id\":\"org_1\",\"label\":\"ci\","
                + "\"masked_key\":\"rsk_...abcd\",\"scopes\":[\"usage:read\"],"
                + "\"allowed_models\":[\"openai/gpt-4o-mini\"],\"blocked_models\":[],"
                + "\"model_limits\":{\"openai/gpt-4o-mini\":{\"rpm\":10}},"
                + "\"effective_limits\":{\"rpm\":10},\"new_field\":true}";

        ApiKeyDetails d = MAPPER.readValue(json, ApiKeyDetails.class);

        assertEquals("key_123", d.id);
        assertEquals("org_1", d.orgId);
        assertEquals("ci", d.label);
        assertEquals("rsk_...abcd", d.maskedKey);
        assertEquals(List.of("usage:read"), d.scopes);
        assertEquals(List.of("openai/gpt-4o-mini"), d.allowedModels);
        assertTrue(d.blockedModels.isEmpty());
        assertEquals(10, d.modelLimits.get("openai/gpt-4o-mini").get("rpm").asInt());
        assertEquals(10, d.effectiveLimits.get("rpm").asInt());
    }

    @Test
    void missingOptionalFieldsAreNull() throws Exception {
        ApiKeyDetails d = MAPPER.readValue("{\"id\":\"key_1\"}", ApiKeyDetails.class);
        assertEquals("key_1", d.id);
        assertNull(d.label);
        assertNull(d.maskedKey);
        assertNull(d.allowedModels);
    }
}
