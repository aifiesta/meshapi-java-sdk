package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchListResponse {
    @JsonProperty("object") public String object;
    @JsonProperty("data") public List<BatchObject> data;
    @JsonProperty("has_more") public boolean hasMore;
    @JsonProperty("first_id") public String firstId;
    @JsonProperty("last_id") public String lastId;
}
