package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.chat.ToolCall;

import java.util.List;

/** Full non-streaming response body for POST /v1/responses. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsesResponse {
    @JsonProperty("id")      public String id;
    @JsonProperty("object")  public String object;
    @JsonProperty("created") public long created;
    @JsonProperty("model")   public String model;
    @JsonProperty("choices") public List<Choice> choices;
    @JsonProperty("usage")   public UsageInfo usage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        @JsonProperty("index")         public int index;
        @JsonProperty("message")       public ResponsesMessage message;
        @JsonProperty("finish_reason") public String finishReason;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponsesMessage {
        @JsonProperty("role")       public String role;
        @JsonProperty("content")    public String content;
        @JsonProperty("tool_calls") public List<ToolCall> toolCalls;
        @JsonProperty("reasoning")  public Reasoning reasoning;
    }

    /** Chain-of-thought reasoning trace returned when reasoning was enabled. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Reasoning {
        @JsonProperty("encrypted_content") public String encryptedContent;
        @JsonProperty("summary")           public String summary;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageInfo {
        @JsonProperty("prompt_tokens")     public int promptTokens;
        @JsonProperty("completion_tokens") public int completionTokens;
        @JsonProperty("total_tokens")      public int totalTokens;
    }
}
