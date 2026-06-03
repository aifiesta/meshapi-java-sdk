package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.videos.CreateVideoGenerationResponse;
import com.meshapi.sdk.types.videos.VideoGenerationRequest;
import com.meshapi.sdk.types.videos.VideoTaskResponse;

public class VideosResource {
    private final HttpClient http;

    public VideosResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Submits a video generation task and returns the task ID immediately.
     *
     * <p>Video generation is asynchronous. Poll {@link #get(String)} until
     * {@code status} is {@code "succeeded"}, {@code "failed"}, or {@code "expired"}.
     *
     * <p>When {@code status == "succeeded"}, {@code response.content.videoUrl}
     * is populated and valid for 24 hours.
     */
    public CreateVideoGenerationResponse create(VideoGenerationRequest params) {
        return http.post("/v1/video/generations", params, CreateVideoGenerationResponse.class);
    }

    /**
     * Retrieves the current status (and result) of a video generation task.
     *
     * <p>Call this after {@link #create(VideoGenerationRequest)} and repeat
     * until {@link VideoTaskResponse#status} reaches a terminal value.
     */
    public VideoTaskResponse get(String taskId) {
        return http.get("/v1/video/generations/" + taskId, VideoTaskResponse.class);
    }
}
