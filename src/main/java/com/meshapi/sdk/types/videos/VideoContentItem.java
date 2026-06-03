package com.meshapi.sdk.types.videos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoContentItem {
    @JsonProperty("type") public String type;
    @JsonProperty("text") public String text;
    @JsonProperty("image_url") public Map<String, Object> imageUrl;
    @JsonProperty("video_url") public Map<String, Object> videoUrl;
    @JsonProperty("audio_url") public Map<String, Object> audioUrl;
    @JsonProperty("draft_task") public Map<String, Object> draftTask;
    @JsonProperty("role") public String role;
}
