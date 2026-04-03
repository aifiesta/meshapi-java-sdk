package com.meshapi.sdk.types.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest {

    @JsonProperty("messages") private List<ChatMessage> messages;
    @JsonProperty("model") private String model;
    @JsonProperty("stream") private Boolean stream;
    @JsonProperty("template") private String template;
    @JsonProperty("variables") private Map<String, String> variables;
    @JsonProperty("session_id") private String sessionId;
    @JsonProperty("temperature") private Double temperature;
    @JsonProperty("max_tokens") private Integer maxTokens;
    @JsonProperty("top_p") private Double topP;
    @JsonProperty("frequency_penalty") private Double frequencyPenalty;
    @JsonProperty("presence_penalty") private Double presencePenalty;
    @JsonProperty("stop") private Object stop;
    @JsonProperty("seed") private Integer seed;
    @JsonProperty("tools") private List<Object> tools;
    @JsonProperty("tool_choice") private Object toolChoice;
    @JsonProperty("transforms") private List<String> transforms;
    @JsonProperty("models") private List<String> models;
    @JsonProperty("user") private String user;

    private ChatCompletionRequest() {}

    public List<ChatMessage> getMessages() { return messages; }
    public String getModel() { return model; }
    public Boolean getStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final ChatCompletionRequest req = new ChatCompletionRequest();
        private final List<ChatMessage> messages = new ArrayList<>();

        public Builder model(String model) { req.model = model; return this; }
        public Builder stream(boolean stream) { req.stream = stream; return this; }
        public Builder template(String template) { req.template = template; return this; }
        public Builder variables(Map<String, String> vars) { req.variables = vars; return this; }
        public Builder sessionId(String id) { req.sessionId = id; return this; }
        public Builder temperature(double t) { req.temperature = t; return this; }
        public Builder maxTokens(int n) { req.maxTokens = n; return this; }
        public Builder topP(double p) { req.topP = p; return this; }
        public Builder frequencyPenalty(double fp) { req.frequencyPenalty = fp; return this; }
        public Builder presencePenalty(double pp) { req.presencePenalty = pp; return this; }
        public Builder stop(Object stop) { req.stop = stop; return this; }
        public Builder seed(int seed) { req.seed = seed; return this; }
        public Builder toolChoice(Object tc) { req.toolChoice = tc; return this; }
        public Builder user(String user) { req.user = user; return this; }

        public Builder addMessage(ChatMessage msg) {
            messages.add(msg);
            return this;
        }

        public Builder messages(List<ChatMessage> msgs) {
            messages.clear();
            messages.addAll(msgs);
            return this;
        }

        public ChatCompletionRequest build() {
            req.messages = new ArrayList<>(messages);
            return req;
        }
    }
}
