package com.meshapi.sdk.types.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/router/select}.
 *
 * <p>Build instances with {@link #builder()}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterSelectRequest {

    @JsonProperty("weight_profile")
    private final String weightProfile;

    private RouterSelectRequest(Builder builder) {
        this.weightProfile = builder.weightProfile;
    }

    /**
     * Returns the Auto Router weight profile for this selection.
     *
     * @return the profile name, for example {@code "cost_first"} or {@code "latency_first"},
     *     or {@code null} to use the key, template or organisation default
     */
    public String getWeightProfile() {
        return weightProfile;
    }

    /**
     * Creates a new builder.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link RouterSelectRequest}. */
    public static final class Builder {
        private String weightProfile;

        private Builder() {
        }

        /**
         * Sets the Auto Router weight profile, for example {@code "cost_first"} or
         * {@code "latency_first"}. When omitted, the profile resolved from the key, template
         * or organisation default applies.
         *
         * @param weightProfile the profile name
         * @return this builder
         */
        public Builder weightProfile(String weightProfile) {
            this.weightProfile = weightProfile;
            return this;
        }

        /**
         * Builds the request.
         *
         * @return a new {@link RouterSelectRequest}
         */
        public RouterSelectRequest build() {
            return new RouterSelectRequest(this);
        }
    }
}
