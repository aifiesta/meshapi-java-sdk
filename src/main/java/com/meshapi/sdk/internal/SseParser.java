package com.meshapi.sdk.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Parses a Server-Sent Events stream into ChatCompletionChunk objects.
 */
public class SseParser implements Iterator<ChatCompletionChunk>, AutoCloseable {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final BufferedReader reader;
    private final String requestId;
    private ChatCompletionChunk nextChunk = null;
    private boolean done = false;
    private MeshAPIError pendingError = null;

    public SseParser(InputStream inputStream) {
        this(inputStream, null);
    }

    public SseParser(InputStream inputStream, String requestId) {
        this.reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.requestId = requestId;
        advance();
    }

    /**
     * The {@code X-Request-Id} response header of the underlying SSE response,
     * or null when the header was absent. Available as soon as the stream is
     * opened (headers arrive before any chunk).
     */
    public String getRequestId() {
        return requestId;
    }

    @Override
    public boolean hasNext() {
        return !done && (nextChunk != null || pendingError != null);
    }

    @Override
    public ChatCompletionChunk next() {
        if (pendingError != null) {
            MeshAPIError err = pendingError;
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

    private void advance() {
        try {
            StringBuilder frameBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    String frame = frameBuilder.toString();
                    frameBuilder.setLength(0);
                    ParseResult result = parseFrame(frame);
                    if (result == null) {
                        continue;
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
            done = true;
        } catch (Exception e) {
            if (!done) {
                pendingError = MeshAPIError.streamInterrupted(e.getMessage());
            }
            done = true;
        }
    }

    public static final class ParseResult {
        public ChatCompletionChunk chunk;
        public MeshAPIError error;
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
            JsonNode errorNode = root.path("error");
            if (!errorNode.isMissingNode()) {
                String code = errorNode.path("code").asText("upstream_error");
                String msg = errorNode.path("message").asText("upstream error");
                ParseResult r = new ParseResult();
                r.error = MeshAPIError.fromStreamFrame(code, msg);
                return r;
            }

            ParseResult r = new ParseResult();
            r.chunk = MAPPER.treeToValue(root, ChatCompletionChunk.class);
            return r;
        } catch (Exception e) {
            return null;
        }
    }
}
