package com.meshapi.sdk.types.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ModelPricingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserialisesLongContextCacheWrite1hRate() throws Exception {
        String json = "{\"long_context_cache_write_1h_input_usd_per_1m\": 12.5}";
        ModelPricing pricing = mapper.readValue(json, ModelPricing.class);
        assertEquals(12.5, pricing.longContextCacheWrite1hInputUsdPer1m);
    }

    @Test
    void missingRateIsNull() throws Exception {
        ModelPricing pricing = mapper.readValue("{}", ModelPricing.class);
        assertNull(pricing.longContextCacheWrite1hInputUsdPer1m);
    }

    @Test
    void ignoresUnknownFields() throws Exception {
        String json = "{\"some_future_field\": 1, \"long_context_cache_write_1h_input_usd_per_1m\": null}";
        ModelPricing pricing = mapper.readValue(json, ModelPricing.class);
        assertNull(pricing.longContextCacheWrite1hInputUsdPer1m);
    }
}
