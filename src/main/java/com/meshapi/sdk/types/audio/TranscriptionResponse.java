package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meshapi.sdk.types.ApiResponse;

public class TranscriptionResponse extends ApiResponse {
    @JsonProperty("text") public String text;
}
