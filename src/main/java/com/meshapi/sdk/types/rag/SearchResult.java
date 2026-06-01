package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
    @JsonProperty("score") public double score;
    @JsonProperty("text") public String text;
    @JsonProperty("parent_text") public String parentText;
    @JsonProperty("file_id") public String fileId;
    @JsonProperty("file_name") public String fileName;
    @JsonProperty("file_type") public String fileType;
    @JsonProperty("mime_type") public String mimeType;
    @JsonProperty("chunk_index") public Integer chunkIndex;
    @JsonProperty("created_at") public Long createdAt;
    @JsonProperty("metadata") public Map<String, Object> metadata;
}
