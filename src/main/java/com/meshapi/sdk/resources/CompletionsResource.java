package com.meshapi.sdk.resources;

import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.resilience.FallbackConfig;
import com.meshapi.sdk.resilience.FallbackEvent;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CompletionsResource {

    private final HttpClient http;

    public CompletionsResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Sends a non-streaming chat completion request, with the client-side
     * model-fallback chain: try the primary model; on a transient failure
     * (default 502/503/504, after the transport's own retries) re-issue
     * against each chain model in order. The chain comes from the per-call
     * {@code fallbackModels} (never sent to the server) or, when unset, the
     * client's {@code FallbackConfig}. Terminal errors (auth, validation,
     * billing, rate limit) never advance the chain. The gateway's server-side
     * routing (per-key {@code routing_policy}) runs within each attempt and is
     * reported separately via {@code GatewayRoutingEvent}s.
     */
    public ChatCompletionResponse create(ChatCompletionRequest params) {
        params.setStream(false);

        FallbackConfig config = http.fallback();
        List<String> requested = params.getFallbackModels() != null
                ? params.getFallbackModels()
                : config != null && config.models() != null ? config.models() : List.of();
        String originalModel = params.getModel();
        List<String> chain = new ArrayList<>();
        for (String m : requested) {
            if (m != null && !m.equals(originalModel)) {
                chain.add(m);
            }
        }
        Set<Integer> onStatus = config != null
                ? config.onStatus()
                : FallbackConfig.DEFAULT_FALLBACK_STATUS_CODES;

        RuntimeException lastError = null;
        // `model` may be unset (the key's default_model applies server-side) —
        // label it for fallback events; the chain always names explicit models.
        String fromModel = originalModel != null ? originalModel : "(key default)";
        try {
            for (int index = 0; index <= chain.size(); index++) {
                String model = index == 0 ? originalModel : chain.get(index - 1);
                if (index > 0) {
                    MeshAPIError err = lastError instanceof MeshAPIError me ? me : null;
                    http.emit(new FallbackEvent(
                            fromModel,
                            model,
                            index - 1,
                            chain.size(),
                            err != null ? err.getStatus() : null,
                            err != null ? err.getErrorCode() : null,
                            err != null && !err.getRequestId().isEmpty() ? err.getRequestId() : null));
                }
                try {
                    params.setModel(model);
                    return http.post("/v1/chat/completions", params, ChatCompletionResponse.class);
                } catch (RuntimeException err) {
                    lastError = err;
                    fromModel = model != null ? model : fromModel;
                    if (chain.isEmpty() || !isFallbackEligible(err, onStatus)) {
                        throw err;
                    }
                }
            }
            throw lastError;
        } finally {
            // Leave the caller's request object as they built it.
            params.setModel(originalModel);
        }
    }

    /**
     * A failure is worth trying on another model when it is transient
     * (default 502/503/504 — a provider/gateway path problem, not this
     * request) or a pre-response network error. Timeouts and interrupts always
     * propagate; terminal API errors (4xx auth/validation/billing) never
     * advance the chain — they would fail identically on every model.
     */
    private static boolean isFallbackEligible(RuntimeException err, Set<Integer> onStatus) {
        if (err instanceof MeshAPIError e) {
            return onStatus.contains(e.getStatus());
        }
        for (Throwable t = err; t != null; t = t.getCause()) {
            if (t instanceof HttpTimeoutException || t instanceof InterruptedException) {
                return false;
            }
            if (t instanceof IOException) {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens a streaming chat completion.
     *
     * <p>Returns an {@link Iterator} of SSE chunks. Iterate until {@link Iterator#hasNext()}
     * returns false. A mid-stream error causes {@link com.meshapi.sdk.MeshAPIError}
     * to be thrown from {@link Iterator#next()}.
     *
     * <p><strong>Streams do not retry</strong> and are never fallback-chained
     * (a partially consumed stream cannot be transparently restarted). Catch
     * the error and restart a new {@link #stream} call if reconnection is needed.
     */
    public Iterator<ChatCompletionChunk> stream(ChatCompletionRequest params) {
        params.setStream(true);
        return http.stream("/v1/chat/completions", params);
    }
}
