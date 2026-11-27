package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/web/search}.
 *
 * <p>Set {@code includeImages} to receive image results in {@link WebSearchResponse#images}.
 * {@code includeImageDescriptions} is only accepted together with {@code includeImages};
 * sending descriptions without images is rejected with a 422.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class WebSearchRequest {

    @JsonProperty("query")
    private final String query;

    @JsonProperty("search_depth")
    private final String searchDepth;

    @JsonProperty("include_images")
    private final Boolean includeImages;

    @JsonProperty("include_image_descriptions")
    private final Boolean includeImageDescriptions;

    private WebSearchRequest(Builder b) {
        this.query = b.query;
        this.searchDepth = b.searchDepth;
        this.includeImages = b.includeImages;
        this.includeImageDescriptions = b.includeImageDescriptions;
    }

    /** @return a new builder */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the search query */
    public String getQuery() {
        return query;
    }

    /** @return the search depth, or {@code null} for the server default */
    public String getSearchDepth() {
        return searchDepth;
    }

    /** @return whether image results are requested, or {@code null} if unset */
    public Boolean getIncludeImages() {
        return includeImages;
    }

    /** @return whether image descriptions are requested, or {@code null} if unset */
    public Boolean getIncludeImageDescriptions() {
        return includeImageDescriptions;
    }

    /** Builder for {@link WebSearchRequest}. */
    public static final class Builder {
        private String query;
        private String searchDepth;
        private Boolean includeImages;
        private Boolean includeImageDescriptions;

        private Builder() {
        }

        /**
         * @param query the search query (required)
         * @return this builder
         */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * @param searchDepth the search depth, for example {@code "basic"}, {@code "fast"} or {@code "ultra-fast"}
         * @return this builder
         */
        public Builder searchDepth(String searchDepth) {
            this.searchDepth = searchDepth;
            return this;
        }

        /**
         * @param includeImages {@code true} to return image results alongside the web results
         * @return this builder
         */
        public Builder includeImages(Boolean includeImages) {
            this.includeImages = includeImages;
            return this;
        }

        /**
         * @param includeImageDescriptions {@code true} to add a description to each image result;
         *     requires {@code includeImages(true)}
         * @return this builder
         */
        public Builder includeImageDescriptions(Boolean includeImageDescriptions) {
            this.includeImageDescriptions = includeImageDescriptions;
            return this;
        }

        /**
         * @return the request
         * @throws IllegalStateException if {@code query} is missing
         */
        public WebSearchRequest build() {
            if (query == null || query.isBlank()) {
                throw new IllegalStateException("query is required");
            }
            return new WebSearchRequest(this);
        }
    }
}
