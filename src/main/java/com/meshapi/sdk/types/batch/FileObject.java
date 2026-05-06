package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileObject {
    @JsonProperty("id") public String id;
    @JsonProperty("object") public String object;
    @JsonProperty("bytes") public Integer bytes;
    @JsonProperty("created_at") public Long createdAt;
    @JsonProperty("filename") public String filename;
    @JsonProperty("purpose") public String purpose;
    @JsonProperty("status") public String status;
    @JsonProperty("status_details") public Object statusDetails;
}
