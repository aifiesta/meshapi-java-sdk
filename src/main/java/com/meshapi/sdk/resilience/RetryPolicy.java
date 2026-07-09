package com.meshapi.sdk.resilience;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Transport-level retry policy. Unset fields keep the defaults:
 * 3 retries on 429/502/503/504 with exponential backoff (500 ms base, 30 s
 * max, ±20% jitter), honouring {@code Retry-After}, and no network-error
 * retry. Streaming requests are never retried.
 *
 * <pre>{@code
 * MeshAPI client = MeshAPI.builder()
 *     .baseUrl("...").token("...")
 *     .retry(RetryPolicy.builder()
 *         .maxRetries(5)
 *         .backoffBaseMs(250)
 *         .retryOnStatus(429, 503)
 *         .build())
 *     .build();
 * }</pre>
 */
public final class RetryPolicy {

    /** Default HTTP status codes that trigger a retry of the same request. */
    public static final Set<Integer> DEFAULT_RETRY_STATUS_CODES = Set.of(429, 502, 503, 504);

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_BACKOFF_BASE_MS = 500;
    private static final long DEFAULT_BACKOFF_MAX_MS = 30_000;

    private final Integer maxRetries;
    private final Set<Integer> retryOnStatus;
    private final Long backoffBaseMs;
    private final Long backoffMaxMs;
    private final Boolean respectRetryAfter;
    private final Boolean retryOnNetworkError;

    private RetryPolicy(Integer maxRetries, Set<Integer> retryOnStatus,
                        Long backoffBaseMs, Long backoffMaxMs,
                        Boolean respectRetryAfter, Boolean retryOnNetworkError) {
        this.maxRetries = maxRetries;
        this.retryOnStatus = retryOnStatus;
        this.backoffBaseMs = backoffBaseMs;
        this.backoffMaxMs = backoffMaxMs;
        this.respectRetryAfter = respectRetryAfter;
        this.retryOnNetworkError = retryOnNetworkError;
    }

    /** Maximum number of retries after the initial attempt (default 3). */
    public int maxRetries() { return maxRetries != null ? maxRetries : DEFAULT_MAX_RETRIES; }

    /** HTTP status codes that trigger a retry of the same request (default 429/502/503/504). */
    public Set<Integer> retryOnStatus() {
        return retryOnStatus != null ? retryOnStatus : DEFAULT_RETRY_STATUS_CODES;
    }

    /** Base delay for exponential backoff — doubles per attempt, ±20% jitter (default 500 ms). */
    public long backoffBaseMs() { return backoffBaseMs != null ? backoffBaseMs : DEFAULT_BACKOFF_BASE_MS; }

    /** Upper bound on a single backoff delay (default 30 000 ms). */
    public long backoffMaxMs() { return backoffMaxMs != null ? backoffMaxMs : DEFAULT_BACKOFF_MAX_MS; }

    /** Whether to honour the server's {@code Retry-After} response header (default true). */
    public boolean respectRetryAfter() { return respectRetryAfter == null || respectRetryAfter; }

    /**
     * Whether to also retry when the request fails before any response arrives
     * (DNS failure, connection refused/reset). Off by default: a network error
     * is ambiguous — the request may have reached the server, and POST bodies
     * are not idempotent. Timeouts/interrupts are never retried.
     */
    public boolean retryOnNetworkError() { return retryOnNetworkError != null && retryOnNetworkError; }

    /**
     * Resolve a policy against the deprecated top-level {@code maxRetries}
     * builder option: {@code retry.maxRetries} wins when explicitly set,
     * otherwise the legacy value applies, otherwise the default (3).
     */
    public static RetryPolicy resolve(RetryPolicy policy, Integer legacyMaxRetries) {
        RetryPolicy base = policy != null ? policy : builder().build();
        if (base.maxRetries != null || legacyMaxRetries == null) {
            return base;
        }
        return new RetryPolicy(legacyMaxRetries, base.retryOnStatus, base.backoffBaseMs,
                base.backoffMaxMs, base.respectRetryAfter, base.retryOnNetworkError);
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Integer maxRetries;
        private Set<Integer> retryOnStatus;
        private Long backoffBaseMs;
        private Long backoffMaxMs;
        private Boolean respectRetryAfter;
        private Boolean retryOnNetworkError;

        /** Maximum number of retries after the initial attempt (default 3, use 0 to disable). */
        public Builder maxRetries(int maxRetries) { this.maxRetries = maxRetries; return this; }

        /** HTTP status codes that trigger a retry (default 429, 502, 503, 504). */
        public Builder retryOnStatus(int... statuses) {
            Set<Integer> set = new LinkedHashSet<>();
            Arrays.stream(statuses).forEach(set::add);
            this.retryOnStatus = Set.copyOf(set);
            return this;
        }

        /** HTTP status codes that trigger a retry (default 429, 502, 503, 504). */
        public Builder retryOnStatus(Collection<Integer> statuses) {
            this.retryOnStatus = Set.copyOf(statuses);
            return this;
        }

        /** Base delay for exponential backoff (default 500 ms). */
        public Builder backoffBaseMs(long backoffBaseMs) { this.backoffBaseMs = backoffBaseMs; return this; }

        /** Upper bound on a single backoff delay (default 30 000 ms). */
        public Builder backoffMaxMs(long backoffMaxMs) { this.backoffMaxMs = backoffMaxMs; return this; }

        /** Honour the server's {@code Retry-After} response header when present (default true). */
        public Builder respectRetryAfter(boolean respectRetryAfter) {
            this.respectRetryAfter = respectRetryAfter;
            return this;
        }

        /** Opt in to retrying pre-response network errors (default false). */
        public Builder retryOnNetworkError(boolean retryOnNetworkError) {
            this.retryOnNetworkError = retryOnNetworkError;
            return this;
        }

        public RetryPolicy build() {
            return new RetryPolicy(maxRetries, retryOnStatus, backoffBaseMs, backoffMaxMs,
                    respectRetryAfter, retryOnNetworkError);
        }
    }
}
