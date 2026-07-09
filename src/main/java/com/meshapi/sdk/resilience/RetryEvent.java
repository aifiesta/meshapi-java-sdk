package com.meshapi.sdk.resilience;

/** The same request is being re-sent after a transient failure. */
public final class RetryEvent implements ResilienceEvent {

    /** HTTP method of the request being retried. */
    public final String method;
    /** Request path, e.g. {@code /v1/chat/completions}. */
    public final String path;
    /** 1-based attempt that just failed; the next send is {@code attempt + 1}. */
    public final int attempt;
    /** Maximum number of retries after the initial attempt. */
    public final int maxRetries;
    /** HTTP status that triggered the retry; {@code null} for a network error. */
    public final Integer status;
    /** Gateway request id of the failed attempt, when a response was received. */
    public final String requestId;
    /** Delay before the next attempt, in milliseconds. */
    public final double delayMs;
    /** Why the retry happened: {@code "status"} or {@code "network-error"}. */
    public final String reason;

    public RetryEvent(String method, String path, int attempt, int maxRetries,
                      Integer status, String requestId, double delayMs, String reason) {
        this.method = method;
        this.path = path;
        this.attempt = attempt;
        this.maxRetries = maxRetries;
        this.status = status;
        this.requestId = requestId;
        this.delayMs = delayMs;
        this.reason = reason;
    }

    @Override
    public String type() { return "retry"; }

    @Override
    public String toString() { return ResilienceEvents.format(this); }
}
