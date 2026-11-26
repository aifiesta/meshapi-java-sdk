package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Request body for {@code POST /v1/web/search}.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
 *
 * <pre>{@code
 * WebSearchRequest req = WebSearchRequest.builder()
 *     .query("aurora borealis forecast")
 *     .includeImages(true)
 *     .includeImageDescriptions(true)
 *     .build();
 * }</pre>
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
        this.query = Objects.requireNonNull(b.query, "query is required");
        this.searchDepth = b.searchDepth;
        this.includeImages = b.includeImages;
        this.includeImageDescriptions = b.includeImageDescriptions;
    }

    /** Returns a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** The search query. */
    public String getQuery() {
        return query;
    }

    /** The search depth, for example {@code basic}, {@code fast} or {@code ultra-fast}. */
    public String getSearchDepth() {
        return searchDepth;
    }

    /** Whether image results are requested. */
    public Boolean getIncludeImages() {
        return includeImages;
    }

    /** Whether each returned image carries a description. */
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

        /** Sets the search query (required). */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /** Sets the search depth, for example {@code basic}, {@code fast} or {@code ultra-fast}. */
        public Builder searchDepth(String searchDepth) {
            this.searchDepth = searchDepth;
            return this;
        }

        /**
         * Requests image results, returned in {@link WebSearchResponse#images}.
         *
         * <p>The API answers 422 when images are requested together with a pinned search
         * engine that returns no images.
         */
        public Builder includeImages(Boolean includeImages) {
            this.includeImages = includeImages;
            return this;
        }

        /**
         * Requests a description for each returned image.
         *
         * <p>Requires {@link #includeImages(Boolean)} set to {@code true}; otherwise the API
         * answers 422.
         */
        public Builder includeImageDescriptions(Boolean includeImageDescriptions) {
            this.includeImageDescriptions = includeImageDescriptions;
            return this;
        }

        /** Builds the request. */
        public WebSearchRequest build() {
            return new WebSearchRequest(this);
        }
    }
}
