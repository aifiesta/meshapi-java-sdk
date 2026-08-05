package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RagFileStatus extends ApiResponse {
    @JsonProperty("file_id") public String fileId;
    @JsonProperty("upload_status") public String uploadStatus;
    @JsonProperty("file_name") public String fileName;
    @JsonProperty("file_type") public String fileType;
    @JsonProperty("mime_type") public String mimeType;
    @JsonProperty("size_bytes") public Long sizeBytes;
    @JsonProperty("asset_url") public String assetUrl;
    @JsonProperty("signed_url") public String signedUrl;
    @JsonProperty("signed_url_expires_at") public String signedUrlExpiresAt;
    @JsonProperty("embedding_status") public String embeddingStatus;
    @JsonProperty("created_at") public String createdAt;
    @JsonProperty("updated_at") public String updatedAt;
    @JsonProperty("total_tokens") public Long totalTokens;
    @JsonProperty("total_cost_usd") public Double totalCostUsd;
    @JsonProperty("last_error_code") public String lastErrorCode;
}
