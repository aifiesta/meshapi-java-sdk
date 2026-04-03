package com.routersvc.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelInfo {
    @JsonProperty("id") public String id;
    @JsonProperty("name") public String name;
    @JsonProperty("context_length") public Integer contextLength;
    @JsonProperty("is_free") public boolean isFree;
    @JsonProperty("pricing") public ModelPricing pricing;
    @JsonProperty("description") public String description;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelPricing {
        @JsonProperty("prompt_usd_per_1k") public String promptUsdPer1k;
        @JsonProperty("completion_usd_per_1k") public String completionUsdPer1k;
        @JsonProperty("image_usd_per_image") public String imageUsdPerImage;
        @JsonProperty("discount_pct") public String discountPct;
        @JsonProperty("prompt_usd_per_1k_discounted") public String promptUsdPer1kDiscounted;
        @JsonProperty("completion_usd_per_1k_discounted") public String completionUsdPer1kDiscounted;
    }
}
