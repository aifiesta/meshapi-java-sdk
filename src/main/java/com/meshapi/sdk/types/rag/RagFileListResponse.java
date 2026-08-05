package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.ArrayList;
import java.util.List;
import com.meshapi.sdk.types.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RagFileListResponse extends ApiResponse {
    @JsonProperty("files")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public List<RagFileStatus> files = new ArrayList<>();

    @JsonProperty("total") public int total;
    @JsonProperty("limit") public int limit;
    @JsonProperty("offset") public int offset;
}
