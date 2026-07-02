package com.meshapi.sdk.types.models;

import java.util.List;

/**
 * Parameters for GET /v1/models/search.
 *
 * <p>All fields are optional. Build an instance with {@link #builder()}.
 */
public class ModelSearchParams {
    public final String q;
    public final Boolean free;
    public final Boolean discounted;
    public final List<String> inputModality;
    public final List<String> outputModality;
    public final List<String> brand;
    /** Sort field: {@code brand|name|id|context_length}. Default: {@code brand}. */
    public final String sort;
    /** Sort order: {@code asc|desc}. Default: {@code asc}. */
    public final String order;
    /** Max results (1–100). Default: 20. */
    public final Integer limit;
    /** Pagination offset (>=0). Default: 0. */
    public final Integer offset;

    private ModelSearchParams(Builder b) {
        this.q = b.q;
        this.free = b.free;
        this.discounted = b.discounted;
        this.inputModality = b.inputModality;
        this.outputModality = b.outputModality;
        this.brand = b.brand;
        this.sort = b.sort;
        this.order = b.order;
        this.limit = b.limit;
        this.offset = b.offset;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String q;
        private Boolean free;
        private Boolean discounted;
        private List<String> inputModality;
        private List<String> outputModality;
        private List<String> brand;
        private String sort;
        private String order;
        private Integer limit;
        private Integer offset;

        public Builder q(String q) { this.q = q; return this; }
        public Builder free(Boolean free) { this.free = free; return this; }
        public Builder discounted(Boolean discounted) { this.discounted = discounted; return this; }
        public Builder inputModality(List<String> inputModality) { this.inputModality = inputModality; return this; }
        public Builder outputModality(List<String> outputModality) { this.outputModality = outputModality; return this; }
        public Builder brand(List<String> brand) { this.brand = brand; return this; }
        public Builder sort(String sort) { this.sort = sort; return this; }
        public Builder order(String order) { this.order = order; return this; }
        public Builder limit(Integer limit) { this.limit = limit; return this; }
        public Builder offset(Integer offset) { this.offset = offset; return this; }

        public ModelSearchParams build() { return new ModelSearchParams(this); }
    }
}
