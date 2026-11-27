package com.meshapi.sdk.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.responses.McpTool;
import com.meshapi.sdk.types.websearch.WebSearchImage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class McpToolAndWebSearchImageTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void mcpToolSerialisesTypeLabelAndDefaultApproval() throws Exception {
        McpTool tool = McpTool.builder().serverLabel("docs").build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(tool));

        assertEquals("mcp", json.get("type").asText());
        assertEquals("docs", json.get("server_label").asText());
        assertEquals("never", json.get("require_approval").asText());
    }

    @Test
    void mcpToolOmitsNullApproval() throws Exception {
        McpTool tool = McpTool.builder().serverLabel("docs").requireApproval(null).build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(tool));

        assertNull(json.get("require_approval"));
    }

    @Test
    void mcpToolRequiresServerLabel() {
        assertThrows(NullPointerException.class, () -> McpTool.builder().build());
    }

    @Test
    void webSearchImageDeserialisesAndIgnoresUnknownFields() throws Exception {
        String body = "{\"url\":\"https://example.com/a.png\",\"title\":\"A\","
                + "\"description\":\"An image\",\"extra\":1}";

        WebSearchImage image = mapper.readValue(body, WebSearchImage.class);

        assertEquals("https://example.com/a.png", image.url);
        assertEquals("A", image.title);
        assertEquals("An image", image.description);
    }

    @Test
    void webSearchImageDescriptionMayBeAbsent() throws Exception {
        WebSearchImage image = mapper.readValue("{\"url\":\"https://example.com/b.png\"}", WebSearchImage.class);

        assertEquals("https://example.com/b.png", image.url);
        assertNull(image.title);
        assertNull(image.description);
    }
}
