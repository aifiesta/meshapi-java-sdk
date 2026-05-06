package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchRequestItem {
    @JsonProperty("custom_id") public String customId;
    @JsonProperty("method") public String method;
    @JsonProperty("url") public String url;
    @JsonProperty("body") public Map<String, Object> body;
}
