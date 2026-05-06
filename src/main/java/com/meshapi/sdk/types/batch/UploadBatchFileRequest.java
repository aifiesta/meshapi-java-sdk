package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadBatchFileRequest {
    @JsonProperty("purpose") public String purpose;
    @JsonProperty("requests") public List<BatchRequestItem> requests;
}
