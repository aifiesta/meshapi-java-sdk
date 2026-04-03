package com.routersvc.sdk.integration;

import com.routersvc.sdk.RouterSvcApiError;
import com.routersvc.sdk.MeshAPI;
import com.routersvc.sdk.types.templates.CreateTemplateRequest;
import com.routersvc.sdk.types.templates.TemplateSummary;
import com.routersvc.sdk.types.templates.UpdateTemplateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TemplatesIntegrationTest {

    private static MeshAPI client;
    private String createdId;

    @BeforeAll
    static void setup() {
        String baseUrl = System.getenv().getOrDefault("ROUTERSVC_BASE_URL", "http://localhost:8000");
        String token = System.getenv().getOrDefault("ROUTERSVC_TOKEN", "rsk_01KN96KQWDPF2X1E9CP8567JY4");
        client = MeshAPI.builder().baseUrl(baseUrl).token(token).build();
    }

    @AfterEach
    void cleanup() {
        if (createdId != null) {
            try { client.templates().delete(createdId); } catch (Exception ignored) {}
            createdId = null;
        }
    }

    @Test
    void createAndGet() {
        String name = "java-sdk-test-" + UUID.randomUUID().toString().substring(0, 8);
        TemplateSummary tmpl = client.templates().create(
                CreateTemplateRequest.builder().name(name).system("Test system.").build());
        createdId = tmpl.id;

        assertNotNull(tmpl.id);
        assertNotNull(tmpl.owner);
        assertEquals("Test system.", tmpl.system);

        TemplateSummary got = client.templates().get(tmpl.id);
        assertEquals(tmpl.id, got.id);
    }

    @Test
    void listContainsCreated() {
        String name = "java-sdk-list-" + UUID.randomUUID().toString().substring(0, 8);
        TemplateSummary tmpl = client.templates().create(
                CreateTemplateRequest.builder().name(name).build());
        createdId = tmpl.id;

        List<TemplateSummary> all = client.templates().list();
        assertTrue(all.stream().anyMatch(t -> t.id.equals(tmpl.id)));
    }

    @Test
    void updateDescription() {
        String name = "java-sdk-upd-" + UUID.randomUUID().toString().substring(0, 8);
        TemplateSummary tmpl = client.templates().create(
                CreateTemplateRequest.builder().name(name).build());
        createdId = tmpl.id;

        TemplateSummary updated = client.templates().update(tmpl.id,
                UpdateTemplateRequest.builder().description("Updated via Java SDK").build());
        assertEquals("Updated via Java SDK", updated.description);
    }

    @Test
    void deleteReturns404() {
        String name = "java-sdk-del-" + UUID.randomUUID().toString().substring(0, 8);
        TemplateSummary tmpl = client.templates().create(
                CreateTemplateRequest.builder().name(name).build());
        client.templates().delete(tmpl.id);

        RouterSvcApiError err = assertThrows(RouterSvcApiError.class,
                () -> client.templates().get(tmpl.id));
        assertEquals(404, err.getStatus());
    }
}
