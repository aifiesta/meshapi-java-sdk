package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cost breakdown of a request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostBreakdownOut {

    /**
     * Rounding adjustment in USD applied to the summed lines, as a decimal string.
     * Added in v1.0.372; {@code null} when the server does not report it.
     */
    @JsonProperty("rounding_usd")
    public String roundingUsd;
}
