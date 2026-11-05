package com.meshapi.sdk.types.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/router/select}.
 *
 * <p>Build instances with {@link #builder()}. Fields left unset are omitted from the JSON body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RouterSelectRequest {

    @JsonProperty("weight_profile")
    private final String weightProfile;

    private RouterSelectRequest(Builder builder) {
        this.weightProfile = builder.weightProfile;
    }

    /**
     * Returns the Auto Router weight profile requested for this selection.
     *
     * @return the profile name, or {@code null} to use the key, template, org or global default
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
         * {@code "balanced"}. An unrecognised name falls back to {@code balanced}.
         *
         * @param weightProfile the profile name, or {@code null} to use the default
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
