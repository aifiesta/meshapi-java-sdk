package com.meshapi.sdk.resilience;

/**
 * Marker for resilience observability events: every transport retry
 * ({@link RetryEvent}), every chat model-fallback hop ({@link FallbackEvent}),
 * and every gateway-side routing outcome ({@link GatewayRoutingEvent}).
 *
 * <p>Receive them via {@code MeshAPI.builder().logger(...)} or render them as
 * readable lines with {@link ResilienceEvents#format(ResilienceEvent)}
 * (printed automatically to stderr with {@code debug(true)}).
 */
public interface ResilienceEvent {

    /** Event discriminator: {@code "retry"}, {@code "fallback"}, or {@code "gateway-routing"}. */
    String type();
}
