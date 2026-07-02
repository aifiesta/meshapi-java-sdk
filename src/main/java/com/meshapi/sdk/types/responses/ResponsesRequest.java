package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsesRequest {
    @JsonProperty("model") public String model;
    @JsonProperty("input") public Object input;
    @JsonProperty("template") public String template;
    @JsonProperty("variables") public Map<String, String> variables;
    @JsonProperty("session_id") public String sessionId;
    @JsonProperty("stream") public Boolean stream;
    @JsonProperty("max_output_tokens") public Integer maxOutputTokens;
    @JsonProperty("temperature") public Double temperature;
    @JsonProperty("top_p") public Double topP;
    @JsonProperty("seed") public Integer seed;
    @JsonProperty("reasoning") public Map<String, Object> reasoning;
    @JsonProperty("tools") public List<Object> tools;
    @JsonProperty("tool_choice") public Object toolChoice;
    @JsonProperty("response_format") public Map<String, Object> responseFormat;
    @JsonProperty("plugins") public List<Object> plugins;
    @JsonProperty("user") public String user;
    // Fields added in second audit pass
    @JsonProperty("previous_response_id") public String previousResponseId;
    @JsonProperty("instructions") public String instructions;
    @JsonProperty("thinking") public Map<String, Object> thinking;
    @JsonProperty("caching") public Map<String, Object> caching;
    @JsonProperty("store") public Boolean store;
    @JsonProperty("include") public List<Object> include;
    @JsonProperty("expire_at") public Long expireAt;
    @JsonProperty("max_tool_calls") public Integer maxToolCalls;
    @JsonProperty("context_management") public Map<String, Object> contextManagement;
    @JsonProperty("timeout") public Integer timeout;
}
