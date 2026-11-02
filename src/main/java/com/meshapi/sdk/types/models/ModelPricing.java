package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pricing for a model, as returned by {@code GET /v1/models}. Rates are in USD per 1M tokens;
 * a {@code null} rate means the model has no price for that line.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPricing {

    /** Standard input rate. */
    @JsonProperty("input_usd_per_1m")
    public Double inputUsdPer1m;

    /** Standard output rate. */
    @JsonProperty("output_usd_per_1m")
    public Double outputUsdPer1m;

    /** Rate for prompt tokens written to cache with a 12-hour TTL. */
    @JsonProperty("cache_write_12h_input_usd_per_1m")
    public Double cacheWrite12hInputUsdPer1m;

    /** 12-hour cache-write rate once a request passes the model's long-context threshold. */
    @JsonProperty("long_context_cache_write_12h_input_usd_per_1m")
    public Double longContextCacheWrite12hInputUsdPer1m;

    /** 12-hour cache-write rate on the {@code flex} service tier. */
    @JsonProperty("flex_cache_write_12h_input_usd_per_1m")
    public Double flexCacheWrite12hInputUsdPer1m;

    /** 12-hour cache-write rate on the {@code flex} service tier past the long-context threshold. */
    @JsonProperty("flex_long_context_cache_write_12h_input_usd_per_1m")
    public Double flexLongContextCacheWrite12hInputUsdPer1m;
}
