package com.routersvc.sdk.types.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolCall {
    @JsonProperty("id") public String id;
    @JsonProperty("type") public String type;
    @JsonProperty("function") public ToolCallFunction function;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolCallFunction {
        @JsonProperty("name") public String name;
        @JsonProperty("arguments") public String arguments;
    }
}
