package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pricing for a model, as returned by {@code GET /v1/models}.
 *
 * <p>Unknown fields are ignored, so new pricing fields added by the API do not break
 * deserialisation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPricing {

    /**
     * Price in USD per 1M input tokens for a 1-hour cache write on a prompt above the
     * model's long-context threshold.
     *
     * <p>{@code null} when the model publishes no such rate.
     */
    @JsonProperty("long_context_cache_write_1h_input_usd_per_1m")
    public Double longContextCacheWrite1hInputUsdPer1m;

    /** Creates an empty pricing object. Used by Jackson. */
    public ModelPricing() {
    }

    /**
     * Returns the long-context 1-hour cache write price in USD per 1M input tokens.
     *
     * @return the price, or {@code null} if the model publishes no such rate
     */
    public Double getLongContextCacheWrite1hInputUsdPer1m() {
        return longContextCacheWrite1hInputUsdPer1m;
    }
}
