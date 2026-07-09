package com.meshapi.sdk.resilience;

/**
 * The GATEWAY retried or fell back server-side while serving this request —
 * parsed from the {@code X-Mesh-Routing-*} response headers (present when the
 * API key's {@code routing_policy} is active). {@code fallback == true} means a
 * different provider than the primary served the request.
 */
public final class GatewayRoutingEvent implements ResilienceEvent {

    /** Request path, e.g. {@code /v1/chat/completions}. */
    public final String path;
    /** Number of attempts the gateway made server-side (1 = no retry). */
    public final int attempts;
    /** Whether a different provider than the primary served the request. */
    public final boolean fallback;
    /** Provider that ultimately served the request, when reported. */
    public final String servedProvider;
    /** Gateway request id, when reported. */
    public final String requestId;

    public GatewayRoutingEvent(String path, int attempts, boolean fallback,
                               String servedProvider, String requestId) {
        this.path = path;
        this.attempts = attempts;
        this.fallback = fallback;
        this.servedProvider = servedProvider;
        this.requestId = requestId;
    }

    @Override
    public String type() { return "gateway-routing"; }

    @Override
    public String toString() { return ResilienceEvents.format(this); }
}
