package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkEmbedResult {
    @JsonProperty("file_id") public String fileId;
    @JsonProperty("embedding_status") public String embeddingStatus;
    @JsonProperty("chunk_count") public Integer chunkCount;
    @JsonProperty("error") public String error;
}
