package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Cost breakdown attached to a usage record.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostBreakdownOut {

    /** Line items that make up the charge. */
    @JsonProperty("lines")
    public List<CostLineOut> lines;

    /**
     * Rounding adjustment in USD, as a decimal string, applied so the lines reconcile with the
     * charged total. May be {@code null}.
     */
    @JsonProperty("rounding_usd")
    public String roundingUsd;
}
