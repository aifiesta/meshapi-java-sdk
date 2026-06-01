package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateBatchRequest {
    @JsonProperty("requests") public List<BatchRequestItem> requests;
    @JsonProperty("completion_window") public String completionWindow;
    @JsonProperty("metadata") public Map<String, Object> metadata;
}
