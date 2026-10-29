package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

/**
 * One line item of a request's cost breakdown.
 *
 * <p>A line describes what was billed, for example a number of tokens, seconds or images,
 * together with the unit, the rate that applied and whether the line was charged.
 *
 * <p><strong>Breaking change:</strong> {@link #tokens} and {@link #ratePer1mUsd} are now
 * nullable and may be absent. They are only populated for token-priced lines. Read
 * {@link #quantity}, {@link #unit} and {@link #rateUsd} to handle every line kind.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostLineOut {

    /**
     * Token count for token-priced lines; {@code null} for lines billed in other units.
     * Use {@link #quantity} for a unit-agnostic amount.
     */
    @JsonProperty("tokens")
    public Integer tokens;

    /**
     * Rate per one million tokens in USD, as a decimal string; {@code null} for lines
     * not priced per token. Use {@link #rateUsd} for a unit-agnostic rate.
     */
    @JsonProperty("rate_per_1m_usd")
    public String ratePer1mUsd;

    /** Billed quantity, expressed in {@link #unit}. */
    @JsonProperty("quantity")
    public BigDecimal quantity;

    /** Unit the {@link #quantity} and {@link #rateUsd} are expressed in. */
    @JsonProperty("unit")
    public String unit;

    /** Rate per {@link #unit} in USD, as a decimal string. */
    @JsonProperty("rate_usd")
    public String rateUsd;

    /** Whether this line contributed to the amount charged. */
    @JsonProperty("charged")
    public Boolean charged;

    /** Modifiers that affected the rate for this line, as returned by the API. */
    @JsonProperty("modifiers")
    public JsonNode modifiers;

    /** @return {@code true} when this line is token-priced and carries a token count. */
    public boolean hasTokens() {
        return tokens != null;
    }
}
