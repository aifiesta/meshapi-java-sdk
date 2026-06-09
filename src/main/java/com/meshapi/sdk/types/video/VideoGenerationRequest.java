package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoGenerationRequest {
    @JsonProperty("model") public String model;
    @JsonProperty("content") public List<VideoContentItem> content;
    @JsonProperty("callback_url") public String callbackUrl;
    @JsonProperty("return_last_frame") public Boolean returnLastFrame;
    @JsonProperty("service_tier") public String serviceTier;
    @JsonProperty("execution_expires_after") public Integer executionExpiresAfter;
    @JsonProperty("generate_audio") public Boolean generateAudio;
    @JsonProperty("draft") public Boolean draft;
    @JsonProperty("resolution") public String resolution;
    @JsonProperty("ratio") public String ratio;
    @JsonProperty("duration") public Integer duration;
    @JsonProperty("frames") public Integer frames;
    @JsonProperty("seed") public Integer seed;
    @JsonProperty("camera_fixed") public Boolean cameraFixed;
    @JsonProperty("watermark") public Boolean watermark;
    @JsonProperty("safety_identifier") public String safetyIdentifier;
    @JsonProperty("priority") public Integer priority;

    private VideoGenerationRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final VideoGenerationRequest req = new VideoGenerationRequest();

        public Builder model(String model) { req.model = model; return this; }
        public Builder content(List<VideoContentItem> content) { req.content = content; return this; }
        public Builder callbackUrl(String callbackUrl) { req.callbackUrl = callbackUrl; return this; }
        public Builder returnLastFrame(Boolean returnLastFrame) { req.returnLastFrame = returnLastFrame; return this; }
        public Builder serviceTier(String serviceTier) { req.serviceTier = serviceTier; return this; }
        public Builder generateAudio(Boolean generateAudio) { req.generateAudio = generateAudio; return this; }
        public Builder draft(Boolean draft) { req.draft = draft; return this; }
        public Builder resolution(String resolution) { req.resolution = resolution; return this; }
        public Builder ratio(String ratio) { req.ratio = ratio; return this; }
        public Builder duration(Integer duration) { req.duration = duration; return this; }
        public Builder frames(Integer frames) { req.frames = frames; return this; }
        public Builder seed(Integer seed) { req.seed = seed; return this; }
        public Builder cameraFixed(Boolean cameraFixed) { req.cameraFixed = cameraFixed; return this; }
        public Builder watermark(Boolean watermark) { req.watermark = watermark; return this; }
        public Builder safetyIdentifier(String safetyIdentifier) { req.safetyIdentifier = safetyIdentifier; return this; }
        public Builder priority(Integer priority) { req.priority = priority; return this; }

        public VideoGenerationRequest build() { return req; }
    }
}
