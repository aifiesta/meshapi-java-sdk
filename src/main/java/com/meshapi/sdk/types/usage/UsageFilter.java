package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Filter for usage event queries.
 *
 * <p>Every field is optional; unset fields are omitted from the request.
 *
 * <pre>{@code
 * UsageFilter filter = UsageFilter.builder()
 *     .provider(List.of("openai"))
 *     .isByok(false)
 *     .stream(true)
 *     .isRetryAttempt(List.of(true, false))
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageFilter {

    private final List<String> provider;
    private final List<String> faultOwner;
    private final Boolean isByok;
    private final Boolean stream;
    private final List<String> trafficType;
    private final List<Boolean> isRetryAttempt;

    private UsageFilter(Builder b) {
        this.provider = b.provider;
        this.faultOwner = b.faultOwner;
        this.isByok = b.isByok;
        this.stream = b.stream;
        this.trafficType = b.trafficType;
        this.isRetryAttempt = b.isRetryAttempt;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("provider")
    public List<String> getProvider() { return provider; }

    @JsonProperty("fault_owner")
    public List<String> getFaultOwner() { return faultOwner; }

    @JsonProperty("is_byok")
    public Boolean getIsByok() { return isByok; }

    @JsonProperty("stream")
    public Boolean getStream() { return stream; }

    @JsonProperty("traffic_type")
    public List<String> getTrafficType() { return trafficType; }

    @JsonProperty("is_retry_attempt")
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

        /** Only include events served by these providers. */
        public Builder provider(List<String> provider) { this.provider = provider; return this; }

        /** Only include events whose failure is attributed to these owners. */
        public Builder faultOwner(List<String> faultOwner) { this.faultOwner = faultOwner; return this; }

        /** Only include bring-your-own-key events ({@code true}) or platform-key events ({@code false}). */
        public Builder isByok(Boolean isByok) { this.isByok = isByok; return this; }

        /** Only include streaming ({@code true}) or non-streaming ({@code false}) events. */
        public Builder stream(Boolean stream) { this.stream = stream; return this; }

        /** Only include events of these traffic types. When unset, every traffic type is included. */
        public Builder trafficType(List<String> trafficType) { this.trafficType = trafficType; return this; }

        /**
         * Select by retry attempt. When unset, only the served attempt is included. Pass
         * {@code [true, false]} to include retry attempts as well.
         */
        public Builder isRetryAttempt(List<Boolean> isRetryAttempt) { this.isRetryAttempt = isRetryAttempt; return this; }

        public UsageFilter build() {
            return new UsageFilter(this);
        }
    }
}
