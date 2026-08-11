package com.meshapi.sdk.types.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a model entry from the MeshAPI models endpoints.
 * Maps to the {@code ModelOut} schema in the OpenAPI spec.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelInfo {
    // -----------------------------------------------------------------------
    // Required fields
    // -----------------------------------------------------------------------
    @JsonProperty("id") public String id;
    @JsonProperty("name") public String name;
    @JsonProperty("context_length") public Integer contextLength;
    @JsonProperty("is_free") public boolean isFree;
    @JsonProperty("pricing") public ModelPricing pricing;
    @JsonProperty("supports_thinking") public boolean supportsThinking;
    @JsonProperty("supports_completions_api") public boolean supportsCompletionsApi;
    @JsonProperty("supports_responses_api") public boolean supportsResponsesApi;
    @JsonProperty("model_type") public String modelType;
    @JsonProperty("input_modalities") public List<String> inputModalities;
    @JsonProperty("output_modalities") public List<String> outputModalities;

    // -----------------------------------------------------------------------
    // Optional fields
    // -----------------------------------------------------------------------
    @JsonProperty("description") public String description;
    @JsonProperty("brand") public String brand;
    @JsonProperty("provider") public String provider;
    @JsonProperty("supports_realtime") public Boolean supportsRealtime;
    @JsonProperty("supports_embeddings") public Boolean supportsEmbeddings;
    @JsonProperty("supports_tools") public Boolean supportsTools;
    @JsonProperty("supports_structured_output") public Boolean supportsStructuredOutput;
    @JsonProperty("supports_system_prompt") public Boolean supportsSystemPrompt;
    @JsonProperty("supports_batching") public Boolean supportsBatching;
    @JsonProperty("supports_background_response") public Boolean supportsBackgroundResponse;
    @JsonProperty("supports_video_generation") public Boolean supportsVideoGeneration;
    @JsonProperty("supports_image_edit") public Boolean supportsImageEdit;
    @JsonProperty("supports_image_inpaint") public Boolean supportsImageInpaint;
    @JsonProperty("supports_image_outpaint") public Boolean supportsImageOutpaint;
    @JsonProperty("supports_image_mix") public Boolean supportsImageMix;
    @JsonProperty("supports_image_reframe") public Boolean supportsImageReframe;
    @JsonProperty("supports_image_upscale") public Boolean supportsImageUpscale;
    @JsonProperty("supports_image_remove_background") public Boolean supportsImageRemoveBackground;
    @JsonProperty("supports_image_reference") public Boolean supportsImageReference;
    @JsonProperty("context_window") public Integer contextWindow;
    @JsonProperty("standard_context_threshold") public Integer standardContextThreshold;
    @JsonProperty("realtime_session_max_tokens") public Integer realtimeSessionMaxTokens;
    @JsonProperty("realtime_max_concurrent_per_owner") public Integer realtimeMaxConcurrentPerOwner;
    @JsonProperty("is_composite") public Boolean isComposite;
    @JsonProperty("composite_models") public List<String> compositeModels;

    // -----------------------------------------------------------------------
    // Nested ModelPricing
    // -----------------------------------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelPricing {
        // Retired on the wire. The gateway stopped returning these in v1.0.135 and has
        // no reference to them left, so they are always null. Kept declared because they
        // are part of this SDK's published surface — removing the fields would fail to
        // compile for callers that read them, where they now simply read null. Use the
        // per-1M or per-unit fields below instead. (Their previous "Required" comment
        // was wrong.)
        @JsonProperty("prompt_usd_per_1k") public String promptUsdPer1k;
        @JsonProperty("completion_usd_per_1k") public String completionUsdPer1k;

        // Optional pricing fields — all are Strings per the spec
        /** The unit the per-unit rates are quoted in: per_1m_tokens, per_second, … */
        @JsonProperty("pricing_unit") public String pricingUnit;
        @JsonProperty("prompt_usd_per_1m") public String promptUsdPer1m;
        @JsonProperty("completion_usd_per_1m") public String completionUsdPer1m;
        /**
         * The raw rate in this row's own {@link #pricingUnit}.
         *
         * <p>For token-priced rows this equals {@link #promptUsdPer1m}. For everything
         * else — per-second video, per-image, per-1k-chars, per-hour — the per-1M fields
         * are null <b>by design</b> and this is the only place the price exists. Read it
         * together with {@code pricingUnit}, which is what makes the bare number a price.
         */
        @JsonProperty("input_usd_per_unit") public String inputUsdPerUnit;
        /** Output side of {@link #inputUsdPerUnit}. */
        @JsonProperty("output_usd_per_unit") public String outputUsdPerUnit;
        @JsonProperty("image_output_usd_per_image") public String imageOutputUsdPerImage;
        @JsonProperty("request_usd") public String requestUsd;
        @JsonProperty("long_context_input_usd_per_1m") public String longContextInputUsdPer1m;
        @JsonProperty("long_context_output_usd_per_1m") public String longContextOutputUsdPer1m;
        @JsonProperty("cache_read_input_usd_per_1m") public String cacheReadInputUsdPer1m;
        @JsonProperty("cache_write_input_usd_per_1m") public String cacheWriteInputUsdPer1m;
        @JsonProperty("cache_read_audio_input_usd_per_1m") public String cacheReadAudioInputUsdPer1m;
        @JsonProperty("long_context_cache_read_input_usd_per_1m") public String longContextCacheReadInputUsdPer1m;
        @JsonProperty("long_context_cache_write_input_usd_per_1m") public String longContextCacheWriteInputUsdPer1m;
        @JsonProperty("batch_input_usd_per_1m") public String batchInputUsdPer1m;
        @JsonProperty("batch_output_usd_per_1m") public String batchOutputUsdPer1m;
        @JsonProperty("training_usd_per_1m") public String trainingUsdPer1m;
        @JsonProperty("fine_tuned_input_usd_per_1m") public String fineTunedInputUsdPer1m;
        @JsonProperty("fine_tuned_output_usd_per_1m") public String fineTunedOutputUsdPer1m;
        @JsonProperty("audio_input_usd_per_1m") public String audioInputUsdPer1m;
        @JsonProperty("audio_output_usd_per_1m") public String audioOutputUsdPer1m;
        @JsonProperty("transcription_usd_per_1m") public String transcriptionUsdPer1m;
        @JsonProperty("cached_audio_input_usd_per_1m") public String cachedAudioInputUsdPer1m;
        @JsonProperty("cached_text_input_usd_per_1m") public String cachedTextInputUsdPer1m;
        @JsonProperty("cache_hit_usd_per_1m") public String cacheHitUsdPer1m;
        @JsonProperty("output_with_audio_usd_per_1m") public String outputWithAudioUsdPer1m;
        @JsonProperty("output_with_video_usd_per_1m") public String outputWithVideoUsdPer1m;
        @JsonProperty("image_input_usd_per_image") public String imageInputUsdPerImage;
        @JsonProperty("image_output_size") public String imageOutputSize;
        @JsonProperty("effective_date") public String effectiveDate;
        @JsonProperty("deprecated_date") public String deprecatedDate;
        @JsonProperty("notes") public String notes;
        @JsonProperty("source_url") public String sourceUrl;
        @JsonProperty("discount_pct") public String discountPct;
    }
}
