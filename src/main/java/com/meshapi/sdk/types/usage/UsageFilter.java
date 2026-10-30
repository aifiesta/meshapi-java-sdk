package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter for usage event queries.
 *
 * <p>Since v1.0.372 the filter can narrow by {@code provider}, {@code fault_owner},
 * {@code is_byok}, {@code stream}, {@code traffic_type} and {@code is_retry_attempt}.
 * Every field is optional; omitted fields are not sent.
 *
 * <pre>{@code
 * UsageFilter filter = UsageFilter.builder()
 *     .provider("openai")
 *     .stream(true)
 *     .isRetryAttempt(true, false)
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
        this.provider = b.provider;
        this.faultOwner = b.faultOwner;
        this.isByok = b.isByok;
        this.stream = b.stream;
        this.trafficType = b.trafficType;
        this.isRetryAttempt = b.isRetryAttempt;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** @return provider slugs to include, or {@code null}. */
    public List<String> getProvider() { return provider; }

    /** @return fault owners to include, or {@code null}. */
    public List<String> getFaultOwner() { return faultOwner; }

    /** @return whether to include only BYOK ({@code true}) or only non-BYOK traffic, or {@code null}. */
    public Boolean getIsByok() { return isByok; }

    /** @return whether to include only streamed ({@code true}) or only non-streamed requests, or {@code null}. */
    public Boolean getStream() { return stream; }

    /** @return traffic types to include, or {@code null}. */
    public List<String> getTrafficType() { return trafficType; }

    /** @return retry-attempt values to include, or {@code null} for the served attempt only. */
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

        /** Filters by one or more provider slugs. */
        public Builder provider(String... provider) {
            this.provider = new ArrayList<>(Arrays.asList(provider));
            return this;
        }

        /** Filters by one or more fault owners. */
        public Builder faultOwner(String... faultOwner) {
            this.faultOwner = new ArrayList<>(Arrays.asList(faultOwner));
            return this;
        }

        /** Filters by whether the request used your own provider key. */
        public Builder isByok(Boolean isByok) {
            this.isByok = isByok;
            return this;
        }

        /** Filters by whether the request was streamed. */
        public Builder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        /** Filters by one or more traffic types. */
        public Builder trafficType(String... trafficType) {
            this.trafficType = new ArrayList<>(Arrays.asList(trafficType));
            return this;
        }

        /**
         * Filters by retry attempt. Omit to get the served attempt only; pass
         * {@code true, false} to include retry attempts as well.
         */
        public Builder isRetryAttempt(Boolean... isRetryAttempt) {
            this.isRetryAttempt = new ArrayList<>(Arrays.asList(isRetryAttempt));
            return this;
        }

        /** @return the built filter. */
        public UsageFilter build() {
            return new UsageFilter(this);
        }
    }
}
