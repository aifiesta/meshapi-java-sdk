package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pricing information for a model, as returned by {@code GET /v1/models}.
 *
 * <p>All rates are expressed in US dollars per one million units. A field is {@code null}
 * when the model has no price for that dimension.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPricing {

    /**
     * Price in USD per 1M input tokens written to a one-hour prompt cache when the request
     * falls in the model's long-context tier. {@code null} when the model does not publish
     * this rate.
     *
     * @since 1.0.383
     */
    @JsonProperty("long_context_cache_write_1h_input_usd_per_1m")
    public Double longContextCacheWrite1hInputUsdPer1m;

    /** Creates an empty instance; used by Jackson during deserialisation. */
    public ModelPricing() {
    }
}
