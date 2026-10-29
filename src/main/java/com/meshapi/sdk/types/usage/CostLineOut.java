package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * One line item of a request's cost breakdown.
 *
 * <p>A line describes a single charge: the {@link #quantity} billed, its {@link #unit},
 * the {@link #rateUsd} that applied and whether the line was {@link #charged}.
 *
 * <p><b>Breaking change in v1.0.371:</b> {@link #tokens} and {@link #ratePer1mUsd} are now
 * optional and may be {@code null} on lines that are not billed per token (for example image
 * or video lines). Read {@link #quantity}, {@link #unit} and {@link #rateUsd} instead, which are
 * set for every kind of line.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostLineOut {

    /**
     * Token count for token-billed lines. {@code null} on lines that are not billed per token.
     */
    @JsonProperty("tokens")
    public Integer tokens;

    /**
     * Rate per one million tokens in USD, as a decimal string. {@code null} on lines that are
     * not billed per token.
     */
    @JsonProperty("rate_per_1m_usd")
    public String ratePer1mUsd;

    /** Quantity billed on this line, expressed in {@link #unit}. */
    @JsonProperty("quantity")
    public JsonNode quantity;

    /** Unit the {@link #quantity} and {@link #rateUsd} are expressed in. */
    @JsonProperty("unit")
    public String unit;

    /** Rate per {@link #unit} in USD that applied to this line. */
    @JsonProperty("rate_usd")
    public JsonNode rateUsd;

    /** Whether this line was charged to the caller. */
    @JsonProperty("charged")
    public Boolean charged;

    /** Rate-card modifiers that applied to this line, as returned by the API. */
    @JsonProperty("modifiers")
    public JsonNode modifiers;
}
