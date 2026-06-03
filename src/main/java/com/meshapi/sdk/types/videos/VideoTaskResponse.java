package com.meshapi.sdk.types.videos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Response from GET /v1/video/generations/{task_id}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTaskResponse {
    @JsonProperty("id") public String id;
    @JsonProperty("model") public String model;
    /** Task lifecycle status: queued | running | cancelled | succeeded | failed | expired */
    @JsonProperty("status") public String status;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoTaskError {
        @JsonProperty("code") public String code;
        @JsonProperty("message") public String message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoTaskContent {
        @JsonProperty("video_url") public String videoUrl;
        @JsonProperty("last_frame_url") public String lastFrameUrl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoTaskUsage {
        @JsonProperty("completion_tokens") public int completionTokens;
        @JsonProperty("total_tokens") public int totalTokens;
    }
}
