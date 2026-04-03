package com.meshapi.sdk.types.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private Object content; // String or List<ContentPart>

    @JsonProperty("name")
    private String name;

    @JsonProperty("tool_call_id")
    private String toolCallId;

    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    private ChatMessage() {}

    public String getRole() { return role; }
    public Object getContent() { return content; }
    public String getName() { return name; }
    public String getToolCallId() { return toolCallId; }
    public List<ToolCall> getToolCalls() { return toolCalls; }

    /** Convenience factory for a user message with string content. */
    public static ChatMessage user(String content) {
        return new Builder().role("user").content(content).build();
    }

    /** Convenience factory for a system message. */
    public static ChatMessage system(String content) {
        return new Builder().role("system").content(content).build();
    }

    /** Convenience factory for an assistant message. */
    public static ChatMessage assistant(String content) {
        return new Builder().role("assistant").content(content).build();
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final ChatMessage msg = new ChatMessage();

        public Builder role(String role) { msg.role = role; return this; }
        public Builder content(Object content) { msg.content = content; return this; }
        public Builder name(String name) { msg.name = name; return this; }
        public Builder toolCallId(String id) { msg.toolCallId = id; return this; }
        public Builder toolCalls(List<ToolCall> calls) { msg.toolCalls = calls; return this; }
        public ChatMessage build() { return msg; }
    }
}
