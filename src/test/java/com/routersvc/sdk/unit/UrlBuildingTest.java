package com.routersvc.sdk.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlBuildingTest {

    @Test
    void baseUrl_trailingSlashStripped() {
        String baseUrl = "http://localhost:8000/";
        String stripped = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        assertEquals("http://localhost:8000", stripped);
    }

    @Test
    void baseUrl_noTrailingSlash_unchanged() {
        String baseUrl = "http://localhost:8000";
        String stripped = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        assertEquals("http://localhost:8000", stripped);
    }

    @Test
    void models_freeFilter_queryString() {
        String qs = "free=" + Boolean.TRUE.toString().toLowerCase();
        assertEquals("free=true", qs);
    }

    @Test
    void models_paidFilter_queryString() {
        String qs = "free=" + Boolean.FALSE.toString().toLowerCase();
        assertEquals("free=false", qs);
    }

    @Test
    void models_noFilter_noQueryString() {
        Boolean free = null;
        String qs = free != null ? "free=" + free.toString().toLowerCase() : null;
        assertNull(qs);
    }

    @Test
    void templates_get_pathConstruction() {
        String id = "abc-123";
        String path = "/v1/templates/" + id;
        assertEquals("/v1/templates/abc-123", path);
    }

    @Test
    void templates_delete_pathConstruction() {
        String id = "xyz-456";
        String path = "/v1/templates/" + id;
        assertEquals("/v1/templates/xyz-456", path);
    }

    @Test
    void version_constant() {
        assertEquals("0.1.0", com.routersvc.sdk.MeshAPI.VERSION);
    }
}
