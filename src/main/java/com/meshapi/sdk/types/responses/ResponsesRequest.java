package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.chat.ChatMessage;

import java.util.List;

/**
 * Request body for POST /v1/responses.
 * Use {@link #builder()} to construct.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsesRequest {

    @JsonProperty("input")             private Object input;   // String or List<ChatMessage>
    @JsonProperty("model")             private String model;
    @JsonProperty("stream")            private Boolean stream;
    @JsonProperty("session_id")        private String sessionId;
    @JsonProperty("max_output_tokens") private Integer maxOutputTokens;
    @JsonProperty("temperature")       private Double temperature;
    @JsonProperty("top_p")             private Double topP;
    @JsonProperty("seed")              private Integer seed;
    @JsonProperty("reasoning")         private ReasoningConfig reasoning;
    @JsonProperty("tools")             private List<Object> tools;
    @JsonProperty("tool_choice")       private Object toolChoice;
    @JsonProperty("response_format")   private Object responseFormat;
    @JsonProperty("plugins")           private List<Object> plugins;
    @JsonProperty("user")              private String user;

    private ResponsesRequest() {}

    /**
     * Returns a shallow copy of this request with the {@code stream} field overridden.
     * Used internally by {@link com.meshapi.sdk.resources.ResponsesResource} so that
     * the caller's original object is never mutated.
     */
    ResponsesRequest withStream(boolean stream) {
        ResponsesRequest copy = new ResponsesRequest();
        copy.input = this.input;
        copy.model = this.model;
        copy.stream = stream;
        copy.sessionId = this.sessionId;
        copy.maxOutputTokens = this.maxOutputTokens;
        copy.temperature = this.temperature;
        copy.topP = this.topP;
        copy.seed = this.seed;
        copy.reasoning = this.reasoning;
        copy.tools = this.tools;
        copy.toolChoice = this.toolChoice;
        copy.responseFormat = this.responseFormat;
        copy.plugins = this.plugins;
        copy.user = this.user;
        return copy;
    }

    public static Builder builder() { return new Builder(); }

    /** Controls chain-of-thought depth for supported models. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReasoningConfig {
        /** {@code "minimal"}, {@code "low"}, {@code "medium"}, or {@code "high"} */
        @JsonProperty("effort") public String effort;

        public ReasoningConfig(String effort) { this.effort = effort; }
    }

    public static final class Builder {
        private final ResponsesRequest req = new ResponsesRequest();

        /** Plain text query. */
        public Builder input(String text) { req.input = text; return this; }

        /** Structured message list — same format as chat/completions. */
        public Builder input(List<ChatMessage> messages) { req.input = messages; return this; }

        public Builder model(String model)           { req.model = model; return this; }
        public Builder sessionId(String id)          { req.sessionId = id; return this; }
        public Builder maxOutputTokens(int n)        { req.maxOutputTokens = n; return this; }
        public Builder temperature(double t)         { req.temperature = t; return this; }
        public Builder topP(double p)                { req.topP = p; return this; }
        public Builder seed(int seed)                { req.seed = seed; return this; }

        /** Enable chain-of-thought reasoning with the given effort level. */
        public Builder reasoning(String effort) {
            req.reasoning = new ReasoningConfig(effort);
            return this;
        }

        public Builder tools(List<Object> tools)     { req.tools = tools; return this; }
        public Builder toolChoice(Object tc)         { req.toolChoice = tc; return this; }
        public Builder responseFormat(Object rf)     { req.responseFormat = rf; return this; }
        public Builder plugins(List<Object> plugins) { req.plugins = plugins; return this; }
        public Builder user(String user)             { req.user = user; return this; }

        public ResponsesRequest build() { return req; }
    }
}
