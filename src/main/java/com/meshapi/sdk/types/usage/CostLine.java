package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * One line item of a request's cost breakdown ({@code CostLineOut}).
 *
 * <p>A line describes a single charge: the quantity and unit billed, the rate that applied,
 * and whether the amount was charged to the caller.
 *
 * <p><b>Breaking in v1.0.364:</b> {@link #tokens} and {@link #ratePer1mUsd} are no longer
 * required and may be {@code null} for lines that are not billed per token. Read
 * {@link #quantity}, {@link #unit} and {@link #rateUsd} for those lines instead.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostLine {

    /** Token count for token-billed lines; {@code null} for other units. */
    @JsonProperty("tokens")
    public Integer tokens;

    /** Rate per one million tokens in USD, as a decimal string; {@code null} for non-token lines. */
    @JsonProperty("rate_per_1m_usd")
    public String ratePer1mUsd;

    /** Quantity billed on this line, in {@link #unit}. */
    @JsonProperty("quantity")
    public Double quantity;

    /** Unit the {@link #quantity} is measured in. */
    @JsonProperty("unit")
    public String unit;

    /** Rate per {@link #unit} in USD, as a decimal string. */
    @JsonProperty("rate_usd")
    public String rateUsd;

    /** Whether this line's amount was charged to the caller. */
    @JsonProperty("charged")
    public Boolean charged;

    /** Rate modifiers that applied to this line, as returned by the API. */
    @JsonProperty("modifiers")
    public JsonNode modifiers;

    /** Returns {@code true} when this line carries a per-token rate. */
    public boolean isTokenBilled() {
        return tokens != null && ratePer1mUsd != null;
    }
}
