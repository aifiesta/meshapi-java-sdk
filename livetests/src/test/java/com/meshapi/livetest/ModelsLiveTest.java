package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.models.ModelInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelsLiveTest extends LiveTestBase {

    @Test
    void listAll() {
        MeshAPI client = newClient();
        List<ModelInfo> models = client.models().list(null);
        assertNotNull(models);
        System.out.printf("[PASS] models.list() → %d models%n", models.size());
        for (ModelInfo m : models) {
            assertNotNull(m.id, "model id should not be null");
        }
    }

    @Test
    void listFree_allAreFree() {
        MeshAPI client = newClient();
        List<ModelInfo> models = client.models().free();
        System.out.printf("[PASS] models.free() → %d models%n", models.size());
        for (ModelInfo m : models) {
            assertTrue(m.isFree, "expected is_free=true for: " + m.id);
        }
    }

    @Test
    void listPaid_allArePaid() {
        MeshAPI client = newClient();
        List<ModelInfo> models = client.models().paid();
        System.out.printf("[PASS] models.paid() → %d models%n", models.size());
        for (ModelInfo m : models) {
            assertFalse(m.isFree, "expected is_free=false for: " + m.id);
        }
    }

    @Test
    void filterByFree() {
        MeshAPI client = newClient();
        List<ModelInfo> free = client.models().list(Boolean.TRUE);
        List<ModelInfo> paid = client.models().list(Boolean.FALSE);
        for (ModelInfo m : free) assertTrue(m.isFree, "paid in free list: " + m.id);
        for (ModelInfo m : paid) assertFalse(m.isFree, "free in paid list: " + m.id);
        System.out.printf("[PASS] filter free=%d paid=%d%n", free.size(), paid.size());
    }
}
