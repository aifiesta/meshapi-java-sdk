package com.meshapi.sdk.types.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for the Auto Router selection endpoint.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the
 * serialised JSON.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterSelectRequest {

    /**
     * Name of the Auto Router weight profile to apply to this selection, for example
     * {@code "cost_first"}. When omitted, the profile configured for the key, template or
     * organisation applies, and otherwise the global default.
     *
     * @since 1.0.383
     */
    @JsonProperty("weight_profile")
    private final String weightProfile;

    private RouterSelectRequest(Builder builder) {
        this.weightProfile = builder.weightProfile;
    }

    /**
     * Returns a new builder.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the requested weight profile.
     *
     * @return the weight profile name, or {@code null} if unset
     */
    public String getWeightProfile() {
        return weightProfile;
    }

    /** Builder for {@link RouterSelectRequest}. */
    public static final class Builder {
        private String weightProfile;

        private Builder() {
        }

        /**
         * Sets the Auto Router weight profile to use for this selection.
         *
         * @param weightProfile the profile name, for example {@code "cost_first"}
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
