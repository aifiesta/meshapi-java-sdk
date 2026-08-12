package com.meshapi.sdk.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.models.ModelInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@code ModelPricing} must describe the pricing object the gateway actually returns.
 *
 * <p>Found during the MESH-472 versioning audit: this SDK declares
 * {@code prompt_usd_per_1k} / {@code completion_usd_per_1k} — which prod stopped
 * returning in {@code v1.0.135}, and which now have <b>zero</b> references anywhere in
 * the gateway — and does not declare {@code input_usd_per_unit} /
 * {@code output_usd_per_unit}, which are what replaced them.
 *
 * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} means the undeclared fields
 * were not merely undocumented: they were <b>discarded</b>. That matters most for
 * models which are not token-priced — per-second video, per-image, per-1k-chars —
 * because their per-1M fields are null <i>by design</i>, so the per-unit rate is the
 * only price on the wire. A caller listing such a model saw no price at all.
 *
 * <p>The pre-existing {@code model_list.json} fixture cannot catch this: it encodes the
 * old shape, so the existing contract test asserts the SDK parses a response the
 * gateway no longer sends. {@code model_list_current.json} is what prod sends today.
 */
@DisplayName("contract: ModelPricing, current shape")
class ModelPricingShapeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static List<ModelInfo> models(String fixture) throws IOException {
        InputStream in = ModelPricingShapeTest.class.getResourceAsStream("/fixtures/" + fixture);
        assertNotNull(in, "fixture not found: " + fixture);
        return MAPPER.readValue(in, MAPPER.getTypeFactory()
                .constructCollectionType(List.class, ModelInfo.class));
    }

    private static ModelInfo find(List<ModelInfo> models, String id) {
        return models.stream().filter(m -> id.equals(m.id)).findFirst()
                .orElseThrow(() -> new AssertionError("fixture has no model " + id));
    }

    @Test
    @DisplayName("a token-priced row exposes the per-unit rate")
    void tokenRowExposesThePerUnitRate() throws IOException {
        // For token rows the per-unit rate equals the per-1M rate, so either is usable.
        ModelInfo.ModelPricing p = find(models("model_list_current.json"), "openai/gpt-4o-mini").pricing;
        assertNotNull(p);

        assertEquals("per_1m_tokens", p.pricingUnit);
        assertEquals("0.15000000", p.promptUsdPer1m);
        assertEquals("0.15000000", p.inputUsdPerUnit);
        assertEquals("0.60000000", p.outputUsdPerUnit);
    }

    @Test
    @DisplayName("a non-token row has its rate ONLY in the per-unit fields")
    void nonTokenRowHasItsRateOnlyInPerUnit() throws IOException {
        // The case the missing fields actually broke. A per-second video model has null
        // for both per-1M fields — a per-1M-token figure is meaningless for it — so
        // inputUsdPerUnit is the only place the price exists.
        ModelInfo.ModelPricing p = find(models("model_list_current.json"), "bytedance/seedance-2-5").pricing;
        assertNotNull(p);

        assertEquals("per_second", p.pricingUnit);
        assertNull(p.promptUsdPer1m, "per-1M fields are null for a per-second row");
        assertNull(p.completionUsdPer1m, "per-1M fields are null for a per-second row");
        assertEquals("10.70000000", p.inputUsdPerUnit);
        assertEquals("6.40000000", p.outputUsdPerUnit);
    }

    @Test
    @DisplayName("the rate is only interpretable alongside pricingUnit")
    void rateIsOnlyInterpretableAlongsidePricingUnit() throws IOException {
        // inputUsdPerUnit is a bare number; pricingUnit is what makes it a price. A
        // response carrying one without the other is not usable.
        for (ModelInfo m : models("model_list_current.json")) {
            if (m.pricing != null && m.pricing.inputUsdPerUnit != null) {
                assertNotNull(m.pricing.pricingUnit, m.id + " has a rate but no pricing_unit");
            }
        }
    }

    @Test
    @DisplayName("the retired per-1k fields are absent from a current response")
    void retiredPer1kFieldsAreAbsentFromACurrentResponse() throws IOException {
        // The honest outcome of the drift: against a real gateway these are null
        // forever. A caller branching on them silently sees "unpriced".
        ModelInfo.ModelPricing p = find(models("model_list_current.json"), "openai/gpt-4o-mini").pricing;

        assertNull(p.promptUsdPer1k);
        assertNull(p.completionUsdPer1k);
    }

    @Test
    @DisplayName("the retired shape still parses")
    void retiredShapeStillParses() throws IOException {
        // Kept declared, not deleted: a caller that still reads them keeps compiling,
        // and an old recorded response still round-trips.
        assertEquals("0.000150", models("model_list.json").get(0).pricing.promptUsdPer1k);
    }
}
