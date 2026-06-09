package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptionTranslateRequest {
    @JsonProperty("model") public String model;
    @JsonProperty("prompt") public String prompt;

    public TranscriptionTranslateRequest() {}
}
