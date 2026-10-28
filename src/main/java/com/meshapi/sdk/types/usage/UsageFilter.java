package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Filter for usage event queries.
 *
 * <p>Narrow usage events, their count, the summary and the CSV export by provider, fault
 * owner, BYOK, streaming, traffic type and retry attempt. Fields left unset are not sent.
 *
 * <pre>{@code
 * UsageFilter filter = UsageFilter.builder()
 *     .provider("openai")
 *     .stream(true)
 *     .isByok(false)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageFilter {

    @JsonProperty("provider")
    private final String provider;

    @JsonProperty("fault_owner")
    private final String faultOwner;

    @JsonProperty("is_byok")
    private final Boolean isByok;

    @JsonProperty("stream")
    private final Boolean stream;

    @JsonProperty("traffic_type")
    private final String trafficType;

    @JsonProperty("is_retry_attempt")
    private final List<Boolean> isRetryAttempt;

    private UsageFilter(Builder b) {
        this.provider = b.provider;
        this.faultOwner = b.faultOwner;
        this.isByok = b.isByok;
        this.stream = b.stream;
        this.trafficType = b.trafficType;
        this.isRetryAttempt = b.isRetryAttempt == null ? null : List.copyOf(b.isRetryAttempt);
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getProvider() { return provider; }
    public String getFaultOwner() { return faultOwner; }
    public Boolean getIsByok() { return isByok; }
    public Boolean getStream() { return stream; }
    public String getTrafficType() { return trafficType; }
    public List<Boolean> getIsRetryAttempt() { return isRetryAttempt; }

    /** Builder for {@link UsageFilter}. */
    public static final class Builder {
        private String provider;
        private String faultOwner;
        private Boolean isByok;
        private Boolean stream;
        private String trafficType;
        private List<Boolean> isRetryAttempt;

        private Builder() {}

        /** Filter to events served by this provider slug. */
        public Builder provider(String provider) { this.provider = provider; return this; }

        /** Filter to events whose failure is attributed to this owner. */
        public Builder faultOwner(String faultOwner) { this.faultOwner = faultOwner; return this; }

        /** Filter to BYOK ({@code true}) or platform-credential ({@code false}) events. */
        public Builder isByok(Boolean isByok) { this.isByok = isByok; return this; }

        /** Filter to streaming ({@code true}) or non-streaming ({@code false}) events. */
        public Builder stream(Boolean stream) { this.stream = stream; return this; }

        /** Filter to one traffic type. When unset, every traffic type is included. */
        public Builder trafficType(String trafficType) { this.trafficType = trafficType; return this; }

        /**
         * Select which attempts to include. When unset, only the served attempt is returned;
         * pass {@code [true, false]} to include retry attempts as well.
         */
        public Builder isRetryAttempt(List<Boolean> isRetryAttempt) { this.isRetryAttempt = isRetryAttempt; return this; }

        /** @return the built filter. */
        public UsageFilter build() {
            return new UsageFilter(this);
        }
    }
}
