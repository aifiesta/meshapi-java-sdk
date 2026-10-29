package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter for usage queries.
 *
 * <p>Every field is optional; unset fields are omitted from the request.
 *
 * <pre>{@code
 * UsageFilter filter = UsageFilter.builder()
 *     .provider(List.of("openai"))
 *     .stream(true)
 *     .isByok(false)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageFilter {

    @JsonProperty("provider")
    private final List<String> provider;

    @JsonProperty("fault_owner")
    private final List<String> faultOwner;

    @JsonProperty("is_byok")
    private final Boolean isByok;

    @JsonProperty("stream")
    private final Boolean stream;

    @JsonProperty("traffic_type")
    private final List<String> trafficType;

    @JsonProperty("is_retry_attempt")
    private final List<Boolean> isRetryAttempt;

    private UsageFilter(Builder b) {
        this.provider = b.provider == null ? null : List.copyOf(b.provider);
        this.faultOwner = b.faultOwner == null ? null : List.copyOf(b.faultOwner);
        this.isByok = b.isByok;
        this.stream = b.stream;
        this.trafficType = b.trafficType == null ? null : List.copyOf(b.trafficType);
        this.isRetryAttempt = b.isRetryAttempt == null ? null : List.copyOf(b.isRetryAttempt);
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public List<String> getProvider() { return provider; }

    public List<String> getFaultOwner() { return faultOwner; }

    public Boolean getIsByok() { return isByok; }

    public Boolean getStream() { return stream; }

    public List<String> getTrafficType() { return trafficType; }

    public List<Boolean> getIsRetryAttempt() { return isRetryAttempt; }

    /** Builder for {@link UsageFilter}. */
    public static final class Builder {
        private List<String> provider;
        private List<String> faultOwner;
        private Boolean isByok;
        private Boolean stream;
        private List<String> trafficType;
        private List<Boolean> isRetryAttempt;

        private Builder() {}

        /** Restrict results to the given provider slugs. */
        public Builder provider(List<String> provider) {
            this.provider = provider == null ? null : new ArrayList<>(provider);
            return this;
        }

        /** Restrict results to the given fault owners. */
        public Builder faultOwner(List<String> faultOwner) {
            this.faultOwner = faultOwner == null ? null : new ArrayList<>(faultOwner);
            return this;
        }

        /** Restrict results to requests served on your own provider key ({@code true}) or not ({@code false}). */
        public Builder isByok(Boolean isByok) {
            this.isByok = isByok;
            return this;
        }

        /** Restrict results to streaming ({@code true}) or non-streaming ({@code false}) requests. */
        public Builder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        /** Restrict results to the given traffic types. When unset, every traffic type is included. */
        public Builder trafficType(List<String> trafficType) {
            this.trafficType = trafficType == null ? null : new ArrayList<>(trafficType);
            return this;
        }

        /**
         * Select which attempts to include. When unset, only the served attempt is returned;
         * pass {@code [true, false]} to include retry attempts as well.
         */
        public Builder isRetryAttempt(List<Boolean> isRetryAttempt) {
            this.isRetryAttempt = isRetryAttempt == null ? null : new ArrayList<>(isRetryAttempt);
            return this;
        }

        /** @return the built filter. */
        public UsageFilter build() {
            return new UsageFilter(this);
        }
    }
}
