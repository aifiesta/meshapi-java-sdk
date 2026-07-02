package com.meshapi.sdk.types.moderations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for POST /v1/moderations.
 *
 * <p>{@code input} accepts:
 * <ul>
 *   <li>A single {@code String}</li>
 *   <li>A {@code List<String>}</li>
 *   <li>A {@code List<ModerationInputItem>} for multimodal inputs</li>
 * </ul>
 *
 * <p>{@code model} is optional; the server defaults to {@code "omni-moderation-latest"}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationRequest {
    /** Required. String, List&lt;String&gt;, or List&lt;ModerationInputItem&gt;. */
    @JsonProperty("input") private Object input;

    /** Optional. Server defaults to {@code "omni-moderation-latest"}. */
    @JsonProperty("model") private String model;

    private ModerationRequest() {}

    public Object getInput() { return input; }
    public String getModel() { return model; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final ModerationRequest req = new ModerationRequest();

        /** Set input to a single string. */
        public Builder input(String input) { req.input = input; return this; }

        /** Set input to a list of strings. */
        public Builder input(List<String> input) { req.input = input; return this; }

        /** Set input to a list of ModerationInputItems (multimodal). */
        public Builder inputItems(List<ModerationInputItem> input) { req.input = input; return this; }

        /** Override the moderation model. */
        public Builder model(String model) { req.model = model; return this; }

        public ModerationRequest build() {
            if (req.input == null) throw new IllegalStateException("input is required");
            return req;
        }
    }
}
