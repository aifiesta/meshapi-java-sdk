package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTaskResponse extends ApiResponse {
    @JsonProperty("id") public String id;
    @JsonProperty("status") public String status;
    @JsonProperty("model") public String model;
    @JsonProperty("error") public VideoTaskError error;
    @JsonProperty("created_at") public Long createdAt;
    @JsonProperty("updated_at") public Long updatedAt;
    @JsonProperty("content") public VideoTaskContent content;
    @JsonProperty("seed") public Integer seed;
    @JsonProperty("resolution") public String resolution;
    @JsonProperty("ratio") public String ratio;
    @JsonProperty("duration") public Integer duration;
    @JsonProperty("frames") public Integer frames;
    @JsonProperty("framespersecond") public Integer framesPerSecond;
    @JsonProperty("generate_audio") public Boolean generateAudio;
    @JsonProperty("safety_identifier") public String safetyIdentifier;
    @JsonProperty("priority") public Integer priority;
    @JsonProperty("draft") public Boolean draft;
    @JsonProperty("draft_task_id") public String draftTaskId;
    @JsonProperty("service_tier") public String serviceTier;
    @JsonProperty("execution_expires_after") public Integer executionExpiresAfter;
    @JsonProperty("usage") public VideoTaskUsage usage;
}
