package com.meshapi.sdk.types.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

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

    // Fields added per finding #15 — batch output retrieval
    /** Batch output records (present on completed batches). */
    @JsonProperty("results") public List<Map<String, Object>> results;
    /** Per-request error detail (present when some requests failed). */
    @JsonProperty("errors_detail") public List<Map<String, Object>> errorsDetail;
    /** Error output file ID. */
    @JsonProperty("error_file_id") public String errorFileId;
    /** Per-request status counts (provider passthrough). */
    @JsonProperty("request_counts") public Map<String, Object> requestCounts;
    /** User-supplied metadata. */
    @JsonProperty("metadata") public Map<String, Object> metadata;
    /** When the batch will expire (Unix timestamp). */
    @JsonProperty("expires_at") public Long expiresAt;
    /** Completion window for the batch (e.g. {@code "24h"}). */
    @JsonProperty("completion_window") public String completionWindow;
}
