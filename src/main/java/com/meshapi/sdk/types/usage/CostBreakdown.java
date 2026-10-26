package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cost breakdown for a request ({@code CostBreakdownOut}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostBreakdown {

    /** Rounding adjustment applied to the total, in USD, as a decimal string. May be {@code null}. */
    @JsonProperty("rounding_usd")
    public String roundingUsd;
}
