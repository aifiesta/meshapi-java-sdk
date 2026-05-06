package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateBatchRequest {
    @JsonProperty("input_file_id") public String inputFileId;
    @JsonProperty("endpoint") public String endpoint;
    @JsonProperty("completion_window") public String completionWindow;
    @JsonProperty("metadata") public Map<String, Object> metadata;
}
