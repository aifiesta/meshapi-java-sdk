package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An MCP tool entry for the {@code tools} list of a {@code POST /v1/responses} request.
 *
 * <p>Names a registered MCP server by its label. The {@code type} is always {@code mcp}.
 *
 * <pre>{@code
 * McpTool tool = McpTool.builder()
 *     .serverLabel("docs")
 *     .requireApproval("never")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpTool {

    /** Always {@code mcp}. */
    @JsonProperty("type")
    public String type = "mcp";

    /** Label of a registered MCP server. */
    @JsonProperty("server_label")
    public String serverLabel;

    /** Approval mode for tool calls, for example {@code never}. */
    @JsonProperty("require_approval")
    public String requireApproval;

    public McpTool() {}

    /** Returns a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link McpTool}. */
    public static final class Builder {
        private final McpTool t = new McpTool();

        private Builder() {}

        /** Sets {@code server_label}. */
        public Builder serverLabel(String v) { t.serverLabel = v; return this; }

        /** Sets {@code require_approval}. */
        public Builder requireApproval(String v) { t.requireApproval = v; return this; }

        public McpTool build() { return t; }
    }
}
