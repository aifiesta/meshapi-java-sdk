package com.meshapi.sdk.types.router;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response from POST /v1/router/select.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouterSelectResponse {
    /** The selected model ID. */
    @JsonProperty("model") public String model;

    /** Auto-router metadata. */
    @JsonProperty("auto_router") public AutoRouterMeta autoRouter;

    /**
     * Reasoning effort level for the selected model.
     * Values: {@code "high"}, {@code "medium"}, {@code "low"}, {@code "none"}.
     */
    @JsonProperty("reasoning_effort") public String reasoningEffort;
}
