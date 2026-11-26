package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSearchImagesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesImageFlags() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder()
                .query("aurora")
                .includeImages(true)
                .includeImageDescriptions(true)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals("aurora", json.get("query").asText());
        assertTrue(json.get("include_images").asBoolean());
        assertTrue(json.get("include_image_descriptions").asBoolean());
    }

    @Test
    void omitsUnsetImageFlags() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder().query("aurora").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("include_images"));
        assertFalse(json.has("include_image_descriptions"));
        assertFalse(json.has("search_depth"));
    }

    @Test
    void requiresQuery() {
        assertThrows(NullPointerException.class, () -> WebSearchRequest.builder().build());
    }

    @Test
    void deserializesImages() throws Exception {
        String body = "{\"results\":[],\"images\":[{\"url\":\"https://example.com/a.jpg\",\"title\":\"A\",\"description\":\"An aurora\",\"extra\":1}]}";
        WebSearchResponse resp = mapper.readValue(body, WebSearchResponse.class);
        assertNotNull(resp.images);
        assertEquals(1, resp.images.size());
        WebSearchImage img = resp.images.get(0);
        assertEquals("https://example.com/a.jpg", img.url);
        assertEquals("A", img.title);
        assertEquals("An aurora", img.description);
    }

    @Test
    void imagesNullWhenNotRequested() throws Exception {
        WebSearchResponse resp = mapper.readValue("{\"results\":[],\"images\":null}", WebSearchResponse.class);
        assertNull(resp.images);
        WebSearchResponse absent = mapper.readValue("{\"results\":[]}", WebSearchResponse.class);
        assertNull(absent.images);
    }

    @Test
    void imagesEmptyWhenNoneFound() throws Exception {
        WebSearchResponse resp = mapper.readValue("{\"results\":[],\"images\":[]}", WebSearchResponse.class);
        assertNotNull(resp.images);
        assertTrue(resp.images.isEmpty());
    }
}
