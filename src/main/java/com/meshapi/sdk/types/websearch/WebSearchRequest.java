package com.meshapi.sdk.types.websearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /v1/web/search}.
 *
 * <p>Set {@link Builder#includeImages(Boolean)} to receive image results in
 * {@link WebSearchResponse#images}. {@link Builder#includeImageDescriptions(Boolean)}
 * requires {@code includeImages} to be {@code true}; the API rejects descriptions
 * without images.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class WebSearchRequest {

    private final String query;
    private final Boolean includeImages;
    private final Boolean includeImageDescriptions;

    private WebSearchRequest(Builder b) {
        this.query = b.query;
        this.includeImages = b.includeImages;
        this.includeImageDescriptions = b.includeImageDescriptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("query")
    public String getQuery() {
        return query;
    }

    /** Whether image results are requested. */
    @JsonProperty("include_images")
    public Boolean getIncludeImages() {
        return includeImages;
    }

    /** Whether each image result carries a description. */
    @JsonProperty("include_image_descriptions")
    public Boolean getIncludeImageDescriptions() {
        return includeImageDescriptions;
    }

    /** Builder for {@link WebSearchRequest}. */
    public static final class Builder {
        private String query;
        private Boolean includeImages;
        private Boolean includeImageDescriptions;

        private Builder() {
        }

        /** The search query. */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /** Request image results alongside the web results. */
        public Builder includeImages(Boolean includeImages) {
            this.includeImages = includeImages;
            return this;
        }

        /** Request a description for each image result. Requires {@code includeImages(true)}. */
        public Builder includeImageDescriptions(Boolean includeImageDescriptions) {
            this.includeImageDescriptions = includeImageDescriptions;
            return this;
        }

        public WebSearchRequest build() {
            if (query == null || query.isEmpty()) {
                throw new IllegalStateException("query is required");
            }
            return new WebSearchRequest(this);
        }
    }
}
