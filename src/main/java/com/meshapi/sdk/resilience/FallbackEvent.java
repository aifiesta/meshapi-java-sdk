package com.meshapi.sdk.resilience;

/** The chat model-fallback chain is advancing to the next model. */
public final class FallbackEvent implements ResilienceEvent {

    /** Model whose request just failed. */
    public final String fromModel;
    /** Model the chain is advancing to. */
    public final String toModel;
    /** 0-based index of {@code toModel} within the configured chain. */
    public final int chainIndex;
    /** Total number of models in the chain (primary excluded). */
    public final int chainLength;
    /** HTTP status of the failure that advanced the chain; {@code null} for a network error. */
    public final Integer status;
    /** Machine-readable error code of the failure, when available. */
    public final String errorCode;
    /** Gateway request id of the failed attempt, when a response was received. */
    public final String requestId;

    public FallbackEvent(String fromModel, String toModel, int chainIndex, int chainLength,
                         Integer status, String errorCode, String requestId) {
        this.fromModel = fromModel;
        this.toModel = toModel;
        this.chainIndex = chainIndex;
        this.chainLength = chainLength;
        this.status = status;
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    @Override
    public String type() { return "fallback"; }

    @Override
    public String toString() { return ResilienceEvents.format(this); }
}
