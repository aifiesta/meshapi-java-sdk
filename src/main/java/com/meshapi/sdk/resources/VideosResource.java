package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.video.CreateVideoGenerationResponse;
import com.meshapi.sdk.types.video.VideoGenerationRequest;
import com.meshapi.sdk.types.video.VideoTaskListResponse;
import com.meshapi.sdk.types.video.VideoTaskResponse;

public class VideosResource {
    private final HttpClient http;

    public VideosResource(HttpClient http) {
        this.http = http;
    }

    /** POST /v1/video/generations — submit a video generation task. */
    public CreateVideoGenerationResponse generate(VideoGenerationRequest params) {
        return http.post("/v1/video/generations", params, CreateVideoGenerationResponse.class);
    }

    /** GET /v1/video/generations — list video generation tasks. */
    public VideoTaskListResponse list(
            String status, String model, String createdAfter, String createdBefore,
            Integer limit, Integer offset) {
        StringBuilder qs = new StringBuilder();
        if (status != null) append(qs, "status", status);
        if (model != null) append(qs, "model", model);
        if (createdAfter != null) append(qs, "created_after", createdAfter);
        if (createdBefore != null) append(qs, "created_before", createdBefore);
        if (limit != null) append(qs, "limit", String.valueOf(limit));
        if (offset != null) append(qs, "offset", String.valueOf(offset));
        return http.get("/v1/video/generations", qs.length() > 0 ? qs.toString() : null, VideoTaskListResponse.class);
    }

    /** GET /v1/video/generations/{task_id} — get a single video generation task. */
    public VideoTaskResponse retrieve(String taskId) {
        return http.get("/v1/video/generations/" + taskId, VideoTaskResponse.class);
    }

    private static void append(StringBuilder sb, String key, String value) {
        if (sb.length() > 0) sb.append("&");
        sb.append(key).append("=").append(value);
    }
}
