package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

/**
 * One line item of a request's cost breakdown.
 *
 * <p>Every charge carries a line that states the quantity billed, its unit, the rate that
 * applied and whether the line was charged. Token-billed lines also carry {@link #tokens}
 * and {@link #ratePer1mUsd}; lines billed per image, per second or per other unit leave
 * both {@code null}.
 *
 * <p><b>Breaking change:</b> {@link #tokens} and {@link #ratePer1mUsd} are optional and may
 * be {@code null}. {@code tokens} is now a boxed {@link Integer}. Read {@link #quantity},
 * {@link #unit} and {@link #rateUsd} for lines that are not token-billed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostLineOut {

    /** Token count for token-billed lines; {@code null} for other units. */
    @JsonProperty("tokens")
    public Integer tokens;

    /** Rate per one million tokens in USD, as a decimal string; {@code null} for other units. */
    @JsonProperty("rate_per_1m_usd")
    public String ratePer1mUsd;

    /** Quantity billed on this line, expressed in {@link #unit}. */
    @JsonProperty("quantity")
    public BigDecimal quantity;

    /** Unit the {@link #quantity} is measured in. */
    @JsonProperty("unit")
    public String unit;

    /** Rate per {@link #unit} in USD, as a decimal string. */
    @JsonProperty("rate_usd")
    public String rateUsd;

    /** Whether this line was charged to the caller; {@code false} for lines shown for reference only. */
    @JsonProperty("charged")
    public Boolean charged;

    /** Rate-card modifiers that applied to this line, as returned by the API. */
    @JsonProperty("modifiers")
    public JsonNode modifiers;

    /** @return {@code true} when this line is token-billed and carries a token count. */
    public boolean isTokenBilled() {
        return tokens != null;
    }
}
