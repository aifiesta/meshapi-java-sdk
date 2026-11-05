package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pricing for a model, as returned by {@code GET /v1/models}.
 *
 * <p>All prices are in US dollars per one million tokens. A {@code null} value means the
 * model's offering does not publish that price.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPricing {

    /**
     * Price of a 1-hour cache write on a prompt above the model's long-context threshold,
     * in USD per 1M input tokens.
     *
     * <p>{@code null} when the offering has no long-context 1-hour cache-write price.
     */
    @JsonProperty("long_context_cache_write_1h_input_usd_per_1m")
    public Double longContextCacheWrite1hInputUsdPer1m;

    /** Creates an empty pricing object; used by Jackson. */
    public ModelPricing() {
    }

    /**
     * Returns the long-context 1-hour cache-write input price.
     *
     * @return USD per 1M input tokens, or {@code null} if not published
     */
    public Double getLongContextCacheWrite1hInputUsdPer1m() {
        return longContextCacheWrite1hInputUsdPer1m;
    }
}
