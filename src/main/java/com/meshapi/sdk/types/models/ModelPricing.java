package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pricing for a model, as returned by {@code GET /v1/models}.
 *
 * <p>Rates are in US dollars per one million tokens. A {@code null} rate means the model
 * publishes no price for that line.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPricing {

    /**
     * Rate for a one-hour cache write on a prompt past the model's long-context threshold,
     * in USD per one million input tokens. {@code null} when the model publishes no such rate.
     */
    @JsonProperty("long_context_cache_write_1h_input_usd_per_1m")
    public Double longContextCacheWrite1hInputUsdPer1m;
}
