package com.meshapi.sdk.types.chat;

import java.util.Map;

/**
 * Options for {@code chat.completions().parse(...)}.
 */
public class StructuredParseOptions {

    private int maxRetries = 0;
    private Map<String, Object> schema;
    private String schemaName;

    public static StructuredParseOptions create() {
        return new StructuredParseOptions();
    }

    /** Re-prompt with the decode error up to n times (default 0). Each retry is a billed call. */
    public StructuredParseOptions maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    /** Override the auto-derived JSON schema with an explicit one (a bare JSON schema object). */
    public StructuredParseOptions schema(Map<String, Object> schema) {
        this.schema = schema;
        return this;
    }

    /** Set the json_schema name (default "response"). */
    public StructuredParseOptions schemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    public int getMaxRetries() { return maxRetries; }
    public Map<String, Object> getSchema() { return schema; }
    public String getSchemaName() { return schemaName; }
}
