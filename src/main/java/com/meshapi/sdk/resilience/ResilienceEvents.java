package com.meshapi.sdk.resilience;

/** Renders {@link ResilienceEvent}s as single readable lines (used by {@code debug(true)}). */
public final class ResilienceEvents {

    private ResilienceEvents() {}

    /**
     * Render an event as a single readable line, e.g.
     * <pre>
     * retrying POST /v1/chat/completions (attempt 1/3 failed: 503, next in 512ms) [req_abc]
     * falling back openai/gpt-4o → anthropic/claude-sonnet-5 (1/2: 503 provider_not_available)
     * gateway served /v1/chat/completions via bedrock (2 attempts, provider fallback) [req_abc]
     * </pre>
     */
    public static String format(ResilienceEvent event) {
        if (event instanceof RetryEvent e) {
            String rid = ridSuffix(e.requestId);
            String why = "network-error".equals(e.reason) ? "network error" : String.valueOf(e.status);
            return "retrying " + e.method + " " + e.path
                    + " (attempt " + e.attempt + "/" + (e.maxRetries + 1) + " failed: " + why
                    + ", next in " + Math.round(e.delayMs) + "ms)" + rid;
        }
        if (event instanceof FallbackEvent e) {
            String rid = ridSuffix(e.requestId);
            StringBuilder why = new StringBuilder();
            if (e.status != null && e.status != 0) {
                why.append(e.status);
            }
            if (e.errorCode != null && !e.errorCode.isEmpty()) {
                if (why.length() > 0) why.append(' ');
                why.append(e.errorCode);
            }
            String reason = why.length() > 0 ? why.toString() : "network error";
            return "falling back " + e.fromModel + " → " + e.toModel
                    + " (" + (e.chainIndex + 1) + "/" + e.chainLength + ": " + reason + ")" + rid;
        }
        if (event instanceof GatewayRoutingEvent e) {
            String rid = ridSuffix(e.requestId);
            String served = e.servedProvider != null && !e.servedProvider.isEmpty()
                    ? " via " + e.servedProvider : "";
            String detail = e.fallback
                    ? e.attempts + " attempts, provider fallback"
                    : e.attempts + " attempts";
            return "gateway served " + e.path + served + " (" + detail + ")" + rid;
        }
        return event.toString();
    }

    private static String ridSuffix(String requestId) {
        return requestId != null && !requestId.isEmpty() ? " [" + requestId + "]" : "";
    }
}
