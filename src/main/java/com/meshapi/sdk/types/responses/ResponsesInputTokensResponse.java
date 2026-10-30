package com.meshapi.sdk.types.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of {@code POST /v1/responses/input_tokens}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsesInputTokensResponse {
    /** Object type, for example {@code response.input_tokens}. */
    @JsonProperty("object")
    public String object;

    /** Number of input tokens the request would consume. */
    @JsonProperty("input_tokens")
    public Integer inputTokens;
}
