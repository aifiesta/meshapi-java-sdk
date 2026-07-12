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
    @JsonProperty("modality") private String modality;
    @JsonProperty("image") private Map<String, Object> image;
    @JsonProperty("async_mode") private Boolean asyncMode;
    @JsonProperty("modalities") private List<String> modalities;
    @JsonProperty("audio") private Map<String, Object> audio;
    @JsonProperty("cache") private Boolean cache;
    @JsonProperty("reasoning_effort") private String reasoningEffort;
    @JsonProperty("timeout") private Integer timeout;
    @JsonProperty("response_format") private Map<String, Object> responseFormat;

    private ChatCompletionRequest() {}

    public List<ChatMessage> getMessages() { return messages; }
    public String getModel() { return model; }
    public Boolean getStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }
    public Map<String, Object> getResponseFormat() { return responseFormat; }
    public void setResponseFormat(Map<String, Object> responseFormat) { this.responseFormat = responseFormat; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    /**
     * Shallow copy with an independent messages list. {@code parse()} works on a
     * copy so it never mutates the request handed in by the caller — the original
     * stays safe to reuse concurrently with {@code create()}, {@code stream()},
     * or another {@code parse()}.
     */
    public ChatCompletionRequest copy() {
        ChatCompletionRequest r = new ChatCompletionRequest();
        r.messages = this.messages == null ? null : new ArrayList<>(this.messages);
        r.model = this.model;
        r.stream = this.stream;
        r.template = this.template;
        r.variables = this.variables;
        r.sessionId = this.sessionId;
        r.temperature = this.temperature;
        r.maxTokens = this.maxTokens;
        r.topP = this.topP;
        r.frequencyPenalty = this.frequencyPenalty;
        r.presencePenalty = this.presencePenalty;
        r.stop = this.stop;
        r.seed = this.seed;
        r.tools = this.tools;
        r.toolChoice = this.toolChoice;
        r.transforms = this.transforms;
        r.models = this.models;
        r.user = this.user;
        r.modality = this.modality;
        r.image = this.image;
        r.asyncMode = this.asyncMode;
        r.modalities = this.modalities;
        r.audio = this.audio;
        r.cache = this.cache;
        r.reasoningEffort = this.reasoningEffort;
        r.timeout = this.timeout;
        r.responseFormat = this.responseFormat;
        return r;
    }

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
        public Builder modality(String modality) { req.modality = modality; return this; }
        public Builder image(Map<String, Object> image) { req.image = image; return this; }
        public Builder asyncMode(boolean asyncMode) { req.asyncMode = asyncMode; return this; }
        public Builder modalities(List<String> modalities) { req.modalities = modalities; return this; }
        public Builder audio(Map<String, Object> audio) { req.audio = audio; return this; }
        public Builder cache(boolean cache) { req.cache = cache; return this; }
        /** Reasoning effort hint: "high", "medium", "low", or "none". */
        public Builder reasoningEffort(String reasoningEffort) { req.reasoningEffort = reasoningEffort; return this; }
        public Builder timeout(int timeoutSeconds) { req.timeout = timeoutSeconds; return this; }
        /** Structured-output schema, e.g. {@code {"type":"json_schema","json_schema":{...}}}. */
        public Builder responseFormat(Map<String, Object> responseFormat) { req.responseFormat = responseFormat; return this; }

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
