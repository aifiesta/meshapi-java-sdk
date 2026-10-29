package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

/**
 * One line item in a request's cost breakdown.
 *
 * <p>A line describes what was billed, for example "5 seconds at 720p with audio" or
 * "2 images at 2048x2048": the {@link #quantity} in a {@link #unit}, the {@link #rateUsd}
 * that applied and any {@link #modifiers} on the rate card.
 *
 * <p><b>Breaking change:</b> {@link #tokens} and {@link #ratePer1mUsd} are now optional and may
 * be {@code null} on lines that are not priced per token (image, video, platform fee). Read
 * {@link #quantity}, {@link #unit} and {@link #rateUsd} instead, which are present on every kind
 * of line.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostLineOut {

    /**
     * Token count for token-priced lines. {@code null} on lines not priced per token.
     */
    @JsonProperty("tokens")
    public Integer tokens;

    /**
     * Rate per one million tokens in USD, as a decimal string. {@code null} on lines not priced
     * per token.
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

    /**
     * Whether this line was charged to your balance. {@code false} marks a line that is shown for
     * reference only, such as model cost on a bring-your-own-key request.
     */
    @JsonProperty("charged")
    public Boolean charged;

    /**
     * Rate-card modifiers that applied to this line, such as a resolution tier or audio.
     * Kept as raw JSON so new modifiers do not break deserialisation.
     */
    @JsonProperty("modifiers")
    public JsonNode modifiers;

    /** @return {@code true} unless the line is explicitly marked as not charged. */
    public boolean isCharged() {
        return charged == null || charged;
    }
}
