package com.meshapi.sdk.types.compare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompareResponse extends ApiResponse {
    @JsonProperty("comparison_id") public String comparisonId;
    @JsonProperty("object") public String object;
    @JsonProperty("created") public long created;
    @JsonProperty("models") public List<String> models;
    @JsonProperty("results") public List<ModelCompareResult> results;
    @JsonProperty("comparison") public String comparison;
    @JsonProperty("comparison_model") public String comparisonModel;
    @JsonProperty("comparison_usage") public Map<String, Object> comparisonUsage;
    @JsonProperty("comparison_fallback_used") public boolean comparisonFallbackUsed;
    @JsonProperty("total_latency_ms") public int totalLatencyMs;
    @JsonProperty("partial") public boolean partial;
    @JsonProperty("skip_comparison") public boolean skipComparison;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelCompareResult {
        @JsonProperty("model") public String model;
        @JsonProperty("response_body") public Map<String, Object> responseBody;
        @JsonProperty("content") public String content;
        @JsonProperty("latency_ms") public int latencyMs;
        @JsonProperty("error") public String error;
        @JsonProperty("error_code") public String errorCode;
        @JsonProperty("usage") public Map<String, Object> usage;
        @JsonProperty("request_id") public String requestId;
    }
}
