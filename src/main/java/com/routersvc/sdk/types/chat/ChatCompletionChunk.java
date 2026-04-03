package com.routersvc.sdk.types.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionChunk {
    @JsonProperty("id") public String id;
    @JsonProperty("object") public String object;
    @JsonProperty("created") public long created;
    @JsonProperty("model") public String model;
    @JsonProperty("choices") public List<ChunkChoice> choices;
    @JsonProperty("usage") public UsageInfo usage;
    @JsonProperty("cost") public String cost;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChunkChoice {
        @JsonProperty("index") public int index;
        @JsonProperty("delta") public Delta delta;
        @JsonProperty("finish_reason") public String finishReason;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Delta {
        @JsonProperty("role") public String role;
        @JsonProperty("content") public String content;
        @JsonProperty("tool_calls") public List<ToolCall> toolCalls;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageInfo {
        @JsonProperty("prompt_tokens") public int promptTokens;
        @JsonProperty("completion_tokens") public int completionTokens;
        @JsonProperty("total_tokens") public int totalTokens;
    }
}
