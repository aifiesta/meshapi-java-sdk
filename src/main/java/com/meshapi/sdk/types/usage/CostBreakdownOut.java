package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Itemised cost of a request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostBreakdownOut {

    /** Line items that make up the charge. */
    @JsonProperty("lines")
    public List<CostLineOut> lines;

    /** Rounding adjustment in USD applied to the total, as a decimal string. */
    @JsonProperty("rounding_usd")
    public String roundingUsd;
}
