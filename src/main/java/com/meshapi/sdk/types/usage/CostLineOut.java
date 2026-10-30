package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * One line of a cost breakdown.
 *
 * <p>Since v1.0.372 a line is no longer always token-priced. {@link #tokens} and
 * {@link #ratePer1mUsd} are {@code null} on lines that are not priced per token; read
 * {@link #quantity}, {@link #unit} and {@link #rateUsd} for those lines instead.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostLineOut {

    /**
     * Token count for this line, or {@code null} when the line is not priced per token.
     *
     * <p><b>Breaking:</b> this was a primitive integer before v1.0.372 and is now a nullable
     * {@link Integer}. Use {@link #quantity} for the billed amount on any unit.
     */
    @JsonProperty("tokens")
    public Integer tokens;

    /**
     * Rate per one million tokens in USD, as a decimal string, or {@code null} when the line
     * is not priced per token.
     *
     * <p><b>Breaking:</b> this field may now be {@code null}. Use {@link #rateUsd} together
     * with {@link #unit} for the rate on any unit.
     */
    @JsonProperty("rate_per_1m_usd")
    public String ratePer1mUsd;

    /** Billed quantity for this line, expressed in {@link #unit}. */
    @JsonProperty("quantity")
    public JsonNode quantity;

    /** Unit the {@link #quantity} and {@link #rateUsd} are expressed in. */
    @JsonProperty("unit")
    public String unit;

    /** Rate per {@link #unit} in USD. */
    @JsonProperty("rate_usd")
    public JsonNode rateUsd;

    /** Amount charged for this line, as returned by the API. */
    @JsonProperty("charged")
    public JsonNode charged;

    /** Pricing modifiers applied to this line, as returned by the API. */
    @JsonProperty("modifiers")
    public JsonNode modifiers;

    /** @return {@code true} when this line carries a token count. */
    public boolean isTokenPriced() {
        return tokens != null;
    }
}
