package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTaskContent {
    @JsonProperty("video_url") public String videoUrl;
    @JsonProperty("last_frame_url") public String lastFrameUrl;
}
