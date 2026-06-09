package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranscriptionResponse {
    @JsonProperty("text") public String text;
}
