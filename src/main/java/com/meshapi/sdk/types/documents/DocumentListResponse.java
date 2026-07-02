package com.meshapi.sdk.types.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response from GET /v1/documents.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentListResponse {
    @JsonProperty("documents") public List<DocumentResponse> documents;
    @JsonProperty("total") public int total;
    @JsonProperty("limit") public int limit;
    @JsonProperty("offset") public int offset;
}
