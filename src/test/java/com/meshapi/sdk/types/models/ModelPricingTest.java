package com.meshapi.sdk.types.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ModelPricingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesLongContextCacheWrite1hPrice() throws Exception {
        String json = "{\"long_context_cache_write_1h_input_usd_per_1m\": 1.0}";
        ModelPricing pricing = mapper.readValue(json, ModelPricing.class);
        assertEquals(1.0, pricing.longContextCacheWrite1hInputUsdPer1m);
        assertEquals(1.0, pricing.getLongContextCacheWrite1hInputUsdPer1m());
    }

    @Test
    void nullWhenAbsentOrNull() throws Exception {
        assertNull(mapper.readValue("{}", ModelPricing.class).longContextCacheWrite1hInputUsdPer1m);
        assertNull(mapper.readValue(
                "{\"long_context_cache_write_1h_input_usd_per_1m\": null}",
                ModelPricing.class).longContextCacheWrite1hInputUsdPer1m);
    }

    @Test
    void ignoresUnknownFields() throws Exception {
        ModelPricing pricing = mapper.readValue("{\"some_future_field\": 2}", ModelPricing.class);
        assertNull(pricing.longContextCacheWrite1hInputUsdPer1m);
    }
}
