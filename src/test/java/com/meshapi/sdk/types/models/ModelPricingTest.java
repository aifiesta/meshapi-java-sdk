package com.meshapi.sdk.types.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ModelPricingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesLongContextCacheWrite1h() throws Exception {
        ModelPricing p = mapper.readValue(
                "{\"long_context_cache_write_1h_input_usd_per_1m\":1.0,\"unknown_field\":2}",
                ModelPricing.class);
        assertEquals(1.0, p.longContextCacheWrite1hInputUsdPer1m);
    }

    @Test
    void nullWhenAbsentOrNull() throws Exception {
        assertNull(mapper.readValue("{}", ModelPricing.class).longContextCacheWrite1hInputUsdPer1m);
        assertNull(mapper.readValue(
                "{\"long_context_cache_write_1h_input_usd_per_1m\":null}",
                ModelPricing.class).longContextCacheWrite1hInputUsdPer1m);
    }
}
