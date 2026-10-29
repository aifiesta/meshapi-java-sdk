package com.meshapi.sdk.types.usage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Filter for usage event queries.
 *
 * <p>Build with {@link #builder()}. Unset fields are omitted from the request.
 *
 * <pre>{@code
 * UsageFilter filter = UsageFilter.builder()
 *     .provider(List.of("openai"))
 *     .isByok(false)
 *     .stream(true)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageFilter {

    @JsonProperty("provider")
    private final Object provider;

    @JsonProperty("fault_owner")
    private final Object faultOwner;

    @JsonProperty("is_byok")
    private final Boolean isByok;

    @JsonProperty("stream")
    private final Boolean stream;

    @JsonProperty("traffic_type")
    private final Object trafficType;

    @JsonProperty("is_retry_attempt")
    private final Object isRetryAttempt;

    private UsageFilter(Builder b) {
        this.provider = b.provider;
        this.faultOwner = b.faultOwner;
        this.isByok = b.isByok;
        this.stream = b.stream;
        this.trafficType = b.trafficType;
        this.isRetryAttempt = b.isRetryAttempt;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    public Object getProvider() { return provider; }
    public Object getFaultOwner() { return faultOwner; }
    public Boolean getIsByok() { return isByok; }
    public Boolean getStream() { return stream; }
    public Object getTrafficType() { return trafficType; }
    public Object getIsRetryAttempt() { return isRetryAttempt; }

    /** Builder for {@link UsageFilter}. */
    public static final class Builder {
        private Object provider;
        private Object faultOwner;
        private Boolean isByok;
        private Boolean stream;
        private Object trafficType;
        private Object isRetryAttempt;

        private Builder() {}

        /** Filters to events served by the given provider slug(s). */
        public Builder provider(List<String> provider) { this.provider = provider; return this; }

        /** Filters to events attributed to the given fault owner(s). */
        public Builder faultOwner(List<String> faultOwner) { this.faultOwner = faultOwner; return this; }

        /** Filters to events served on your own provider key ({@code true}) or not ({@code false}). */
        public Builder isByok(Boolean isByok) { this.isByok = isByok; return this; }

        /** Filters to streaming ({@code true}) or non-streaming ({@code false}) requests. */
        public Builder stream(Boolean stream) { this.stream = stream; return this; }

        /** Filters to events of the given traffic type(s). */
        public Builder trafficType(List<String> trafficType) { this.trafficType = trafficType; return this; }

        /**
         * Selects which attempts are included. Omitted returns the served attempt only;
         * {@code List.of(true, false)} includes retry attempts as well.
         */
        public Builder isRetryAttempt(List<Boolean> isRetryAttempt) { this.isRetryAttempt = isRetryAttempt; return this; }

        /** @return the built filter */
        public UsageFilter build() {
            return new UsageFilter(this);
        }
    }
}
