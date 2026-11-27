package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebSearchResponseImagesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesImages() throws Exception {
        String json = "{\"query\":\"cats\",\"provider\":\"tavily\",\"answer\":null,"
                + "\"results\":[{\"title\":\"Cats\",\"url\":\"https://example.com\",\"content\":\"c\"}],"
                + "\"images\":[{\"url\":\"https://example.com/a.png\",\"title\":\"A\",\"description\":\"A cat\"}]}";
        WebSearchResponse resp = mapper.readValue(json, WebSearchResponse.class);
        assertEquals("cats", resp.query);
        assertEquals(1, resp.results.size());
        assertEquals("https://example.com", resp.results.get(0).url);
        assertNotNull(resp.images);
        assertEquals(1, resp.images.size());
        assertEquals("https://example.com/a.png", resp.images.get(0).url);
        assertEquals("A", resp.images.get(0).title);
        assertEquals("A cat", resp.images.get(0).description);
    }

    @Test
    void imagesNullWhenNotRequested() throws Exception {
        String json = "{\"query\":\"cats\",\"results\":[],\"images\":null}";
        WebSearchResponse resp = mapper.readValue(json, WebSearchResponse.class);
        assertNull(resp.images);
    }

    @Test
    void imagesEmptyWhenNoneFound() throws Exception {
        String json = "{\"query\":\"cats\",\"results\":[],\"images\":[]}";
        WebSearchResponse resp = mapper.readValue(json, WebSearchResponse.class);
        assertNotNull(resp.images);
        assertTrue(resp.images.isEmpty());
    }
}
