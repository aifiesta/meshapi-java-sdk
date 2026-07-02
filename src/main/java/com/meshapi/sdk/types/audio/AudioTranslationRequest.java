package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for POST /v1/audio/translations.
 * Multipart/form-data upload — model and file are required.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AudioTranslationRequest {
    /** Required: the model to use for translation (e.g. "openai/whisper-1"). */
    @JsonProperty("model") public String model;
    /** Optional: guidance text for the model. */
    @JsonProperty("prompt") public String prompt;
    /** Optional: response format ("json", "text", "verbose_json"). */
    @JsonProperty("response_format") public String responseFormat;
    /** Optional: sampling temperature in [0, 2]. */
    @JsonProperty("temperature") public Double temperature;

    public AudioTranslationRequest() {}
}
