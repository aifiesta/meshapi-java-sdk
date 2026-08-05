package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsesResponse extends ApiResponse {
    @JsonProperty("id") public String id;
    @JsonProperty("object") public String object;
    @JsonProperty("model") public String model;
    @JsonProperty("output") public List<Object> output;
    @JsonProperty("usage") public Map<String, Object> usage;
    @JsonProperty("status") public String status;
    public Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        extra.put(key, value);
    }
}
