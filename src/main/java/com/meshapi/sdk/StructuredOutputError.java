package com.meshapi.sdk;

/**
 * Thrown by {@code chat.completions().parse(...)} when the model's response
 * cannot be decoded into the requested type.
 *
 * <p>The most common cause is that the model does not support structured outputs
 * ({@code response_format}): the gateway forwards the field, the provider ignores
 * it, and the model returns plain text instead of JSON. The underlying Jackson
 * error is available via {@link #getCause()}. A client-side error, so
 * {@link #getStatus()} is {@code 0} and {@link #getErrorCode()} is
 * {@code "structured_output_parse_error"}.
 */
public class StructuredOutputError extends MeshAPIError {

    public StructuredOutputError(String message, Throwable cause) {
        super(message, 0, "structured_output_parse_error", "", null, null);
        if (cause != null) {
            initCause(cause);
        }
    }
}
