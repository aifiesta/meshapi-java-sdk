package com.meshapi.sdk.types.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTaskListResponse extends ApiResponse {
    @JsonProperty("object") public String object;
    @JsonProperty("data") public List<VideoTaskResponse> data;
    @JsonProperty("has_more") public boolean hasMore;
    @JsonProperty("total") public int total;
    @JsonProperty("limit") public int limit;
    @JsonProperty("offset") public int offset;
}
