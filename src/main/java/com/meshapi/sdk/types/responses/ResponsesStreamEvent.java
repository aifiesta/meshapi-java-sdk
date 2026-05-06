package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsesStreamEvent {
    public Map<String, Object> fields = new HashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) {
        fields.put(key, value);
    }
}
