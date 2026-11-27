package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * An MCP tool entry for the {@code tools} array of a {@code POST /v1/responses} request.
 *
 * <p>It names an MCP server registered for your organization by its label. MeshAPI runs the
 * server's tools on your behalf and returns the model's final answer.
 *
 * <pre>{@code
 * McpTool tool = McpTool.builder()
 *     .serverLabel("docs")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class McpTool {

    /** The tool type discriminator; always {@code "mcp"}. */
    public static final String TYPE = "mcp";

    /** The only approval mode currently accepted. */
    public static final String REQUIRE_APPROVAL_NEVER = "never";

    @JsonProperty("type")
    private final String type = TYPE;

    @JsonProperty("server_label")
    private final String serverLabel;

    @JsonProperty("require_approval")
    private final String requireApproval;

    private McpTool(Builder builder) {
        this.serverLabel = builder.serverLabel;
        this.requireApproval = builder.requireApproval;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the tool type, always {@code "mcp"} */
    public String getType() {
        return type;
    }

    /** @return the label of the registered MCP server */
    public String getServerLabel() {
        return serverLabel;
    }

    /** @return the approval mode sent with the tool */
    public String getRequireApproval() {
        return requireApproval;
    }

    /** Builder for {@link McpTool}. */
    public static final class Builder {
        private String serverLabel;
        private String requireApproval = REQUIRE_APPROVAL_NEVER;

        private Builder() {
        }

        /**
         * Sets the label of the registered MCP server whose tools the model may call.
         *
         * @param serverLabel the server label (required)
         * @return this builder
         */
        public Builder serverLabel(String serverLabel) {
            this.serverLabel = serverLabel;
            return this;
        }

        /**
         * Sets the approval mode. Defaults to {@code "never"}, the value the API requires.
         *
         * @param requireApproval the approval mode
         * @return this builder
         */
        public Builder requireApproval(String requireApproval) {
            this.requireApproval = requireApproval;
            return this;
        }

        /**
         * Builds the tool.
         *
         * @return the tool
         * @throws NullPointerException if {@code serverLabel} is not set
         */
        public McpTool build() {
            Objects.requireNonNull(serverLabel, "serverLabel is required");
            return new McpTool(this);
        }
    }
}
