package com.meshapi.sdk.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.images.ImageGenerationRequest;
import com.meshapi.sdk.types.responses.McpTool;
import com.meshapi.sdk.types.video.VideoGenerationRequest;
import com.meshapi.sdk.types.websearch.WebSearchRequest;
import com.meshapi.sdk.types.websearch.WebSearchResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class V1_0_437TypesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void webSearchRequestSerializesImageFlags() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder()
                .query("java records")
                .includeImages(true)
                .includeImageDescriptions(true)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals("java records", json.get("query").asText());
        assertTrue(json.get("include_images").asBoolean());
        assertTrue(json.get("include_image_descriptions").asBoolean());
    }

    @Test
    void webSearchRequestOmitsUnsetFlags() throws Exception {
        WebSearchRequest req = WebSearchRequest.builder().query("q").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertFalse(json.has("include_images"));
        assertFalse(json.has("include_image_descriptions"));
    }

    @Test
    void webSearchResponseParsesImages() throws Exception {
        String body = "{\"images\":[{\"url\":\"https://example.com/a.png\",\"title\":\"A\",\"description\":\"desc\"}],\"extra\":1}";
        WebSearchResponse resp = mapper.readValue(body, WebSearchResponse.class);
        assertEquals(1, resp.images.size());
        assertEquals("https://example.com/a.png", resp.images.get(0).url);
        assertEquals("A", resp.images.get(0).title);
        assertEquals("desc", resp.images.get(0).description);
    }

    @Test
    void webSearchResponseImagesNullWhenAbsent() throws Exception {
        WebSearchResponse resp = mapper.readValue("{}", WebSearchResponse.class);
        assertNull(resp.images);
        WebSearchResponse empty = mapper.readValue("{\"images\":[]}", WebSearchResponse.class);
        assertNotNull(empty.images);
        assertTrue(empty.images.isEmpty());
    }

    @Test
    void mcpToolSerializes() throws Exception {
        McpTool tool = McpTool.builder().serverLabel("docs").requireApproval("never").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(tool));
        assertEquals("mcp", json.get("type").asText());
        assertEquals("docs", json.get("server_label").asText());
        assertEquals("never", json.get("require_approval").asText());
    }

    @Test
    void mcpToolRequiresLabel() {
        assertThrows(IllegalStateException.class, () -> McpTool.builder().build());
    }

    @Test
    void imageGenerationRequestSerializesFrameFields() throws Exception {
        ImageGenerationRequest req = ImageGenerationRequest.builder()
                .prompt("a spinning cube")
                .frameCount(8)
                .frameDurationMs(120)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(8, json.get("frame_count").asInt());
        assertEquals(120, json.get("frame_duration_ms").asInt());
        assertFalse(json.has("model"));
    }

    @Test
    void videoGenerationRequestSerializesDimensions() throws Exception {
        VideoGenerationRequest req = VideoGenerationRequest.builder()
                .prompt("waves")
                .width(1280)
                .height(720)
                .build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(req));
        assertEquals(1280, json.get("width").asInt());
        assertEquals(720, json.get("height").asInt());
    }
}
