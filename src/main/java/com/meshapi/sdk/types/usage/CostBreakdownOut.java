package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Cost breakdown attached to a usage record.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostBreakdownOut {

    /** Line items that make up the charge. */
    @JsonProperty("lines")
    public List<CostLineOut> lines;

    /** Rounding adjustment in USD applied to the total, as returned by the API. */
    @JsonProperty("rounding_usd")
    public JsonNode roundingUsd;
}
