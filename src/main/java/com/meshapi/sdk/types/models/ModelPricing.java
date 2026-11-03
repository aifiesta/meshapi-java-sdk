package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pricing published for a model in {@code GET /v1/models}.
 *
 * <p>All rates are in USD. Fields are {@code null} when the model has no rate for that line.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPricing {

    /** Input token rate, USD per 1M tokens. */
    @JsonProperty("input_usd_per_1m")
    public Double inputUsdPer1m;

    /** Output token rate, USD per 1M tokens. */
    @JsonProperty("output_usd_per_1m")
    public Double outputUsdPer1m;

    /**
     * Rate for a 1-hour cache write on a prompt above the model's long-context threshold,
     * USD per 1M input tokens. {@code null} when the model publishes no such rate.
     */
    @JsonProperty("long_context_cache_write_1h_input_usd_per_1m")
    public Double longContextCacheWrite1hInputUsdPer1m;
}
