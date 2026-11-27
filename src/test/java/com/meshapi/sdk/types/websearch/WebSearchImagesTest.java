package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSearchImagesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialisesImageFlags() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder()
                .query("mountain lakes")
                .includeImages(true)
                .includeImageDescriptions(true)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals("mountain lakes", json.get("query").asText());
        assertTrue(json.get("include_images").asBoolean());
        assertTrue(json.get("include_image_descriptions").asBoolean());
    }

    @Test
    void omitsUnsetFlags() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder().query("q").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("include_images"));
        assertFalse(json.has("include_image_descriptions"));
        assertFalse(json.has("search_depth"));
    }

    @Test
    void requiresQuery() {
        assertThrows(IllegalStateException.class, () -> WebSearchRequest.builder().build());
    }

    @Test
    void parsesImages() throws Exception {
        String body = "{\"results\":[],\"images\":[{\"url\":\"https://example.com/a.png\",\"title\":\"A\",\"description\":\"An image\",\"extra\":1}]}";
        WebSearchResponse resp = mapper.readValue(body, WebSearchResponse.class);
        assertEquals(1, resp.images.size());
        assertEquals("https://example.com/a.png", resp.images.get(0).url);
        assertEquals("A", resp.images.get(0).title);
        assertEquals("An image", resp.images.get(0).description);
    }

    @Test
    void imagesNullWhenNotRequestedAndEmptyWhenNoneFound() throws Exception {
        WebSearchResponse notAsked = mapper.readValue("{\"results\":[],\"images\":null}", WebSearchResponse.class);
        assertNull(notAsked.images);
        WebSearchResponse none = mapper.readValue("{\"results\":[],\"images\":[]}", WebSearchResponse.class);
        assertTrue(none.images.isEmpty());
    }
}
