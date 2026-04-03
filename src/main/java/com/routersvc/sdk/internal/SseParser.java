package com.routersvc.sdk.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routersvc.sdk.RouterSvcApiError;
import com.routersvc.sdk.types.chat.ChatCompletionChunk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Parses a Server-Sent Events stream into ChatCompletionChunk objects.
 *
 * <p>The stream terminates on [DONE] or connection close. Mid-stream error
 * frames cause {@link RouterSvcApiError} to be thrown from {@link Iterator#next()}.
 *
 * <p>Streams are fail-fast: no reconnection is attempted on error.
 */
public class SseParser implements Iterator<ChatCompletionChunk>, AutoCloseable {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final BufferedReader reader;
    private ChatCompletionChunk nextChunk = null;
    private boolean done = false;
    private RouterSvcApiError pendingError = null;

    public SseParser(InputStream inputStream) {
        this.reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        advance();
    }

    @Override
    public boolean hasNext() {
        return !done && (nextChunk != null || pendingError != null);
    }

    @Override
    public ChatCompletionChunk next() {
        if (pendingError != null) {
            RouterSvcApiError err = pendingError;
            pendingError = null;
            done = true;
            throw err;
        }
        if (!hasNext()) {
            throw new NoSuchElementException("SSE stream exhausted");
        }
        ChatCompletionChunk current = nextChunk;
        nextChunk = null;
        advance();
        return current;
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }

    // -----------------------------------------------------------------------
    // Internal — read the next frame
    // -----------------------------------------------------------------------

    private void advance() {
        try {
            StringBuilder frameBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    // Blank line = end of SSE frame
                    String frame = frameBuilder.toString();
                    frameBuilder.setLength(0);
                    ParseResult result = parseFrame(frame);
                    if (result == null) {
                        continue; // empty/comment frame, keep reading
                    }
                    if (result.done) {
                        done = true;
                        return;
                    }
                    if (result.error != null) {
                        pendingError = result.error;
                        return;
                    }
                    if (result.chunk != null) {
                        nextChunk = result.chunk;
                        return;
                    }
                } else {
                    frameBuilder.append(line).append('\n');
                }
            }
            // Stream ended without [DONE]
            done = true;
        } catch (Exception e) {
            if (!done) {
                pendingError = RouterSvcApiError.streamInterrupted(e.getMessage());
            }
            done = true;
        }
    }

    // -----------------------------------------------------------------------
    // Frame parsing
    // -----------------------------------------------------------------------

    public static final class ParseResult {
        public ChatCompletionChunk chunk;
        public RouterSvcApiError error;
        public boolean done;
    }

    public static ParseResult parseFrame(String frame) {
        String dataLine = null;
        for (String line : frame.split("\n")) {
            if (line.startsWith("data: ")) {
                dataLine = line.substring("data: ".length()).strip();
            }
        }
        if (dataLine == null || dataLine.isEmpty()) {
            return null;
        }
        if ("[DONE]".equals(dataLine)) {
            ParseResult r = new ParseResult();
            r.done = true;
            return r;
        }

        try {
            JsonNode root = MAPPER.readTree(dataLine);

            // Error frame detection
            JsonNode errorNode = root.path("error");
            if (!errorNode.isMissingNode()) {
                String code = errorNode.path("code").asText("upstream_error");
                String msg = errorNode.path("message").asText("upstream error");
                ParseResult r = new ParseResult();
                r.error = RouterSvcApiError.fromStreamFrame(code, msg);
                return r;
            }

            ParseResult r = new ParseResult();
            r.chunk = MAPPER.treeToValue(root, ChatCompletionChunk.class);
            return r;
        } catch (Exception e) {
            // Malformed frame — skip silently
            return null;
        }
    }
}
