package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An MCP tool entry for {@code ResponsesRequest.tools}.
 *
 * <p>Names a registered MCP server by its label. The gateway runs the server's
 * tools and returns the model's final answer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class McpTool {

    private final String serverLabel;
    private final String requireApproval;

    private McpTool(Builder b) {
        this.serverLabel = b.serverLabel;
        this.requireApproval = b.requireApproval;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Always {@code "mcp"}. */
    @JsonProperty("type")
    public String getType() {
        return "mcp";
    }

    /** Label of the registered MCP server. */
    @JsonProperty("server_label")
    public String getServerLabel() {
        return serverLabel;
    }

    /** Approval mode, for example {@code "never"}. */
    @JsonProperty("require_approval")
    public String getRequireApproval() {
        return requireApproval;
    }

    /** Builder for {@link McpTool}. */
    public static final class Builder {
        private String serverLabel;
        private String requireApproval;

        private Builder() {
        }

        /** Label of the registered MCP server to use. */
        public Builder serverLabel(String serverLabel) {
            this.serverLabel = serverLabel;
            return this;
        }

        /** Approval mode for tool calls, for example {@code "never"}. */
        public Builder requireApproval(String requireApproval) {
            this.requireApproval = requireApproval;
            return this;
        }

        public McpTool build() {
            if (serverLabel == null || serverLabel.isEmpty()) {
                throw new IllegalStateException("serverLabel is required");
            }
            return new McpTool(this);
        }
    }
}
