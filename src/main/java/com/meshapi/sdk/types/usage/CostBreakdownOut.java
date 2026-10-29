package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cost breakdown attached to a usage record.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostBreakdownOut {

    /**
     * Rounding adjustment in USD applied to the total, as a decimal string.
     * May be {@code null} when the API does not return it.
     */
    @JsonProperty("rounding_usd")
    public String roundingUsd;
}
