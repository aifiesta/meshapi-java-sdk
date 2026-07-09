package com.meshapi.sdk.resilience;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Client-side model-fallback chain for non-streaming
 * {@code chat.completions.create}: when the primary model's request exhausts
 * its transport retries on a transient error, the SDK re-issues it against
 * each model in the chain until one succeeds.
 *
 * <p>Distinct from the {@code models} request param (a server-side,
 * provider-handled fallback list): this chain is driven by the SDK, so it
 * works regardless of provider and is visible in your logs hop by hop.
 *
 * <pre>{@code
 * MeshAPI client = MeshAPI.builder()
 *     .baseUrl("...").token("...")
 *     .fallback(FallbackConfig.builder()
 *         .models("anthropic/claude-sonnet-5", "mistral/mistral-large")
 *         .build())
 *     .build();
 * }</pre>
 */
public final class FallbackConfig {

    /** Default error statuses eligible for advancing to the next model. */
    public static final Set<Integer> DEFAULT_FALLBACK_STATUS_CODES = Set.of(502, 503, 504);

    private final List<String> models;
    private final Set<Integer> onStatus;

    private FallbackConfig(List<String> models, Set<Integer> onStatus) {
        this.models = models;
        this.onStatus = onStatus;
    }

    /** Ordered list of models to try when the primary model's request fails. */
    public List<String> models() { return models; }

    /**
     * Error statuses eligible for advancing to the next model (default
     * 502/503/504). Terminal errors (auth, validation, billing) never advance
     * the chain.
     */
    public Set<Integer> onStatus() {
        return onStatus != null ? onStatus : DEFAULT_FALLBACK_STATUS_CODES;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private List<String> models = List.of();
        private Set<Integer> onStatus;

        /** Ordered list of models to try when the primary model's request fails. */
        public Builder models(String... models) {
            this.models = List.of(models);
            return this;
        }

        /** Ordered list of models to try when the primary model's request fails. */
        public Builder models(List<String> models) {
            this.models = List.copyOf(models);
            return this;
        }

        /** Error statuses eligible for advancing to the next model (default 502, 503, 504). */
        public Builder onStatus(int... statuses) {
            Set<Integer> set = new LinkedHashSet<>();
            Arrays.stream(statuses).forEach(set::add);
            this.onStatus = Set.copyOf(set);
            return this;
        }

        /** Error statuses eligible for advancing to the next model (default 502, 503, 504). */
        public Builder onStatus(Collection<Integer> statuses) {
            this.onStatus = Set.copyOf(statuses);
            return this;
        }

        public FallbackConfig build() {
            return new FallbackConfig(models, onStatus);
        }
    }
}
