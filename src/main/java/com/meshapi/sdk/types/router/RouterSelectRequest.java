package com.meshapi.sdk.types.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Request body for POST /v1/router/select.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterSelectRequest {
    /** Required. The conversation messages used to select a model. */
    @JsonProperty("messages") private List<ChatMessage> messages;

    /**
     * Optional. API type to route for.
     * Valid values: {@code "completions"}, {@code "responses"}, {@code "embeddings"}.
     * Server defaults to {@code "completions"}.
     */
    @JsonProperty("api_type") private String apiType;

    /**
     * Optional. Model IDs to exclude from selection.
     */
    @JsonProperty("exclude_models") private List<String> excludeModels;

    private RouterSelectRequest() {}

    public List<ChatMessage> getMessages() { return messages; }
    public String getApiType() { return apiType; }
    public List<String> getExcludeModels() { return excludeModels; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final RouterSelectRequest req = new RouterSelectRequest();
        private final List<ChatMessage> messages = new ArrayList<>();

        public Builder messages(List<ChatMessage> messages) {
            this.messages.clear();
            this.messages.addAll(messages);
            return this;
        }

        public Builder addMessage(ChatMessage message) {
            this.messages.add(message);
            return this;
        }

        public Builder apiType(String apiType) { req.apiType = apiType; return this; }
        public Builder excludeModels(List<String> excludeModels) { req.excludeModels = excludeModels; return this; }

        public RouterSelectRequest build() {
            if (messages.isEmpty()) throw new IllegalStateException("messages is required");
            req.messages = new ArrayList<>(messages);
            return req;
        }
    }
}
