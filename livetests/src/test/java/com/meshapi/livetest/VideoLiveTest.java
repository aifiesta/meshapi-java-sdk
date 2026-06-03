package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.videos.CreateVideoGenerationResponse;
import com.meshapi.sdk.types.videos.VideoContentItem;
import com.meshapi.sdk.types.videos.VideoGenerationRequest;
import com.meshapi.sdk.types.videos.VideoTaskResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VideoLiveTest extends LiveTestBase {

    @Test
    void video_createAndPoll() throws InterruptedException {
        String videoModel = envOrShared("MESHAPI_VIDEO_GEN_MODEL", "");
        if (videoModel.isEmpty()) {
            System.out.println("[SKIP] videos.create -> MESHAPI_VIDEO_GEN_MODEL not set");
            return;
        }

        MeshAPI client = newClient();

        VideoContentItem textItem = new VideoContentItem();
        textItem.type = "text";
        textItem.text = "A calm ocean wave at sunset.";

        VideoGenerationRequest req = VideoGenerationRequest.builder()
                .model(videoModel)
                .content(List.of(textItem))
                .duration(4)
                .resolution("480p")
                .ratio("16:9")
                .build();

        // Create task
        CreateVideoGenerationResponse task = client.videos().create(req);
        assertNotNull(task.id, "expected task id");
        System.out.printf("[PASS] videos.create -> id=%s%n", task.id);

        // Poll up to 3 minutes
        long deadline = System.currentTimeMillis() + 180_000;
        VideoTaskResponse result = null;

        while (System.currentTimeMillis() < deadline) {
            result = client.videos().get(task.id);
            assertNotNull(result);

            String status = result.status;
            if ("succeeded".equals(status) || "failed".equals(status)
                    || "expired".equals(status) || "cancelled".equals(status)) {
                break;
            }
            Thread.sleep(10_000);
        }

        assertNotNull(result, "no result from polling");
        assertEquals("succeeded", result.status,
                "expected status=succeeded, got " + result.status
                        + (result.error != null ? " (error=" + result.error.message + ")" : ""));
        assertNotNull(result.content, "expected content on succeeded task");
        assertNotNull(result.content.videoUrl, "expected video_url on succeeded task");
        assertFalse(result.content.videoUrl.isBlank(), "expected non-empty video_url");

        System.out.printf("[PASS] videos.create+poll -> id=%s video_url=%s%n",
                task.id, result.content.videoUrl);
    }
}
