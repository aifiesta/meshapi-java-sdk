package com.meshapi.sdk.types.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a document resource returned by the MeshAPI documents endpoints.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentResponse {
    @JsonProperty("document_id") public String documentId;
    @JsonProperty("status") public String status;
    @JsonProperty("format") public String format;
    @JsonProperty("model") public String model;

    // Nullable fields
    @JsonProperty("title") public String title;
    @JsonProperty("download_url") public String downloadUrl;
    @JsonProperty("expires_at") public String expiresAt;
    @JsonProperty("size_bytes") public Long sizeBytes;
    @JsonProperty("prompt_tokens") public Integer promptTokens;
    @JsonProperty("completion_tokens") public Integer completionTokens;
    @JsonProperty("total_tokens") public Integer totalTokens;
    @JsonProperty("failure_reason") public String failureReason;
    @JsonProperty("created_at") public String createdAt;
    @JsonProperty("updated_at") public String updatedAt;
}
