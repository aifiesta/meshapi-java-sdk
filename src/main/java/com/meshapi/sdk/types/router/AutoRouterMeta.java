package com.meshapi.sdk.types.router;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auto-router metadata included in a {@link RouterSelectResponse}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoRouterMeta {
    /** Whether a fallback model was used instead of the top choice. */
    @JsonProperty("fallback_used") public boolean fallbackUsed;

    /** Reason a fallback model was used, if {@code fallbackUsed} is {@code true}. */
    @JsonProperty("fallback_reason") public String fallbackReason;
}
