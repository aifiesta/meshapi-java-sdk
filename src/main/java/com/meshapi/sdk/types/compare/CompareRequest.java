package com.meshapi.sdk.types.compare;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.chat.ChatMessage;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareRequest {
    @JsonProperty("models") public List<String> models;
    @JsonProperty("messages") public List<ChatMessage> messages;
    @JsonProperty("model_overrides") public List<Map<String, Object>> modelOverrides;
    @JsonProperty("comparison_model") public String comparisonModel;
    @JsonProperty("comparison_instructions") public String comparisonInstructions;
    @JsonProperty("temperature") public Double temperature;
    @JsonProperty("max_tokens") public Integer maxTokens;
    @JsonProperty("stream") public Boolean stream;
    @JsonProperty("template") public String template;
    @JsonProperty("variables") public Map<String, String> variables;
    @JsonProperty("skip_comparison") public Boolean skipComparison;
}
