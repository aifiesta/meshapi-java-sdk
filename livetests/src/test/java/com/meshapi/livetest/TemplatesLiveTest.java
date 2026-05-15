package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.types.templates.CreateTemplateRequest;
import com.meshapi.sdk.types.templates.TemplateSummary;
import com.meshapi.sdk.types.templates.UpdateTemplateRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemplatesLiveTest extends LiveTestBase {

    @Test
    void crudLifecycle() {
        MeshAPI client = newClient();
        String name = uniqueName("java-livetest");
        String createdId = null;

        try {
            // --- Create ---
            TemplateSummary tmpl = client.templates().create(
                    CreateTemplateRequest.builder()
                            .name(name)
                            .description("Java SDK live test")
                            .system("You are a test assistant.")
                            .build());
            createdId = tmpl.id;
            assertNotNull(tmpl.id);
            assertNotNull(tmpl.owner);
            assertEquals("You are a test assistant.", tmpl.system);
            System.out.printf("[PASS] templates.create → id=%s owner=%s%n", tmpl.id, tmpl.owner);

            // --- List ---
            List<TemplateSummary> all = client.templates().list();
            final String fId = createdId;
            assertTrue(all.stream().anyMatch(t -> t.id.equals(fId)),
                    "created template not in list");
            System.out.printf("[PASS] templates.list → %d templates, created found%n", all.size());

            // --- Get ---
            TemplateSummary got = client.templates().get(tmpl.id);
            assertEquals(tmpl.id, got.id);
            assertEquals(name, got.name);
            System.out.printf("[PASS] templates.get → name=%s%n", got.name);

            // --- Update ---
            TemplateSummary updated = client.templates().update(tmpl.id,
                    UpdateTemplateRequest.builder()
                            .description("Updated by Java SDK live test")
                            .build());
            assertEquals("Updated by Java SDK live test", updated.description);
            System.out.printf("[PASS] templates.update → description=%s%n", updated.description);

            // --- Delete ---
            client.templates().delete(tmpl.id);
            createdId = null;
            System.out.println("[PASS] templates.delete → 204 No Content");

            // --- Verify 404 ---
            final String deletedId = tmpl.id;
            MeshAPIError err = assertThrows(MeshAPIError.class,
                    () -> client.templates().get(deletedId));
            assertEquals(404, err.getStatus());
            System.out.printf("[PASS] templates.get(deleted) → 404 %s%n", err.getErrorCode());

        } finally {
            if (createdId != null) {
                try { client.templates().delete(createdId); } catch (Exception ignored) {}
            }
        }
    }
}
