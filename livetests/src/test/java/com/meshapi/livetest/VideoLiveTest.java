package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.video.VideoContentItem;
import com.meshapi.sdk.types.video.VideoGenerationRequest;
import com.meshapi.sdk.types.video.VideoTaskListResponse;
import com.meshapi.sdk.types.video.VideoTaskResponse;
import com.meshapi.sdk.types.video.CreateVideoGenerationResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VideoLiveTest extends LiveTestBase {

    @Test
    void video_list() {
        MeshAPI client = newClient();
        VideoTaskListResponse listing = client.videos().list(null, null, null, null, 5, null);
        assertNotNull(listing);
        assertNotNull(listing.data);
        System.out.printf("[PASS] videos.list -> total=%d items=%d%n", listing.total, listing.data.size());
    }

    private static final String VIDEO_MODEL =
            System.getenv().getOrDefault("MESHAPI_VIDEO_GEN_MODEL", "byteplus/dreamina-seedance-2-0");

    @Test
    void video_generate_and_retrieve() {
        MeshAPI client = newClient();
        String model = VIDEO_MODEL;

        VideoContentItem item = new VideoContentItem("text", "A serene mountain lake at sunrise");
        VideoGenerationRequest req = VideoGenerationRequest.builder()
                .model(model)
                .content(List.of(item))
                .build();

        CreateVideoGenerationResponse resp = client.videos().generate(req);
        assertNotNull(resp.id);
        System.out.printf("[PASS] videos.generate -> task_id=%s%n", resp.id);

        VideoTaskResponse task = client.videos().retrieve(resp.id);
        assertEquals(resp.id, task.id);
        System.out.printf("[PASS] videos.retrieve -> status=%s%n", task.status);
    }
}
