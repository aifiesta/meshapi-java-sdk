package com.meshapi.sdk.types.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class McpToolTest {

    @Test
    void serializesAsMcpTool() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        McpTool tool = McpTool.builder().serverLabel("docs").requireApproval("never").build();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(tool));
        assertEquals("mcp", json.get("type").asText());
        assertEquals("docs", json.get("server_label").asText());
        assertEquals("never", json.get("require_approval").asText());
    }
}
