package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchObject {
    @JsonProperty("id") public String id;
    @JsonProperty("object") public String object;
    @JsonProperty("endpoint") public String endpoint;
    @JsonProperty("input_file_id") public String inputFileId;
    @JsonProperty("output_file_id") public String outputFileId;
    @JsonProperty("status") public String status;
    @JsonProperty("model") public String model;
    @JsonProperty("provider") public String provider;
    @JsonProperty("created_at") public Long createdAt;
    @JsonProperty("completed_at") public Long completedAt;
    @JsonProperty("usage_synced") public Boolean usageSynced;
}
