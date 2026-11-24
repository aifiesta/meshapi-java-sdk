package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for a RAG search.
 *
 * <p>Use {@link #builder()} to construct an instance. Fields left unset are omitted
 * from the serialised JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SearchRequest {

    private final String mode;

    private SearchRequest(Builder builder) {
        this.mode = builder.mode;
    }

    /** @return the search mode, or {@code null} to use the server default. */
    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link SearchRequest}. */
    public static final class Builder {
        private String mode;

        private Builder() {
        }

        /**
         * Sets {@code mode}: the search mode to run. Leave unset to use the server default.
         *
         * @param mode search mode
         * @return this builder
         */
        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        /** @return the built request. */
        public SearchRequest build() {
            return new SearchRequest(this);
        }
    }
}
