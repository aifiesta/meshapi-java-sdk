package com.meshapi.sdk.integration;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.models.ModelInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelsIntegrationTest {

    private static MeshAPI client;

    @BeforeAll
    static void setup() {
        String baseUrl = System.getenv().getOrDefault("MESHAPI_BASE_URL", "http://localhost:8000");
        String token = System.getenv().getOrDefault("MESHAPI_TOKEN", "rsk_01KN96KQWDPF2X1E9CP8567JY4");
        client = MeshAPI.builder().baseUrl(baseUrl).token(token).build();
    }

    @Test
    void listAll() {
        List<ModelInfo> models = client.models().list(null);
        assertNotNull(models);
        for (ModelInfo m : models) {
            assertNotNull(m.id, "model id should not be null");
        }
    }

    @Test
    void listFree_allAreFree() {
        List<ModelInfo> models = client.models().free();
        for (ModelInfo m : models) {
            assertTrue(m.isFree, "expected is_free=true for: " + m.id);
        }
    }

    @Test
    void listPaid_allArePaid() {
        List<ModelInfo> models = client.models().paid();
        for (ModelInfo m : models) {
            assertFalse(m.isFree, "expected is_free=false for: " + m.id);
        }
    }
}
