package com.meshapi.sdk.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPIError;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JsonSseParser<T> implements Iterator<T>, AutoCloseable {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final BufferedReader reader;
    private final Class<T> valueType;
    private T nextChunk = null;
    private boolean done = false;
    private MeshAPIError pendingError = null;

    public JsonSseParser(InputStream inputStream, Class<T> valueType) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.valueType = valueType;
        advance();
    }

    @Override
    public boolean hasNext() {
        return !done && (nextChunk != null || pendingError != null);
    }

    @Override
    public T next() {
        if (pendingError != null) {
            MeshAPIError err = pendingError;
            pendingError = null;
            done = true;
            throw err;
        }
        if (!hasNext()) {
            throw new NoSuchElementException("SSE stream exhausted");
        }
        T current = nextChunk;
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
                    T parsed = parseFrame(frameBuilder.toString(), valueType);
                    frameBuilder.setLength(0);
                    if (parsed != null) {
                        nextChunk = parsed;
                        return;
                    }
                    continue;
                }
                frameBuilder.append(line).append('\n');
            }
            done = true;
        } catch (MeshAPIError e) {
            pendingError = e;
        } catch (Exception e) {
            pendingError = MeshAPIError.streamInterrupted(e.getMessage());
        }
    }

    public static <T> T parseFrame(String frame, Class<T> valueType) {
        String dataLine = null;
        for (String line : frame.split("\n")) {
            if (line.startsWith("data: ")) {
                dataLine = line.substring("data: ".length()).strip();
            }
        }
        if (dataLine == null || dataLine.isEmpty() || "[DONE]".equals(dataLine)) {
            return null;
        }
        try {
            JsonNode root = MAPPER.readTree(dataLine);
            JsonNode errorNode = root.path("error");
            if (!errorNode.isMissingNode()) {
                String code = errorNode.path("code").asText("upstream_error");
                String msg = errorNode.path("message").asText("upstream error");
                throw MeshAPIError.fromStreamFrame(code, msg);
            }
            return MAPPER.treeToValue(root, valueType);
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }
}
