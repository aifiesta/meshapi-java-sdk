package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InitUploadResponse extends ApiResponse {
    @JsonProperty("file_id") public String fileId;
    @JsonProperty("signed_url") public String signedUrl;
    @JsonProperty("expires_at") public String expiresAt;
}
