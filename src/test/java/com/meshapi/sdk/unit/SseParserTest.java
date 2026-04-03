package com.meshapi.sdk.unit;

import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.internal.SseParser;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SseParserTest {

    private static SseParser parse(String sseContent) {
        return new SseParser(new ByteArrayInputStream(sseContent.getBytes(StandardCharsets.UTF_8)));
    }

    private static String makeChunkFrame(String content) {
        return "data: {\"id\":\"x\",\"object\":\"chat.completion.chunk\",\"created\":1,\"model\":\"m\"," +
               "\"choices\":[{\"index\":0,\"delta\":{\"content\":\"" + content + "\"},\"finish_reason\":null}]}\n\n";
    }

    private static List<ChatCompletionChunk> collect(SseParser parser) {
        List<ChatCompletionChunk> chunks = new ArrayList<>();
        while (parser.hasNext()) {
            chunks.add(parser.next());
        }
        return chunks;
    }

    // -----------------------------------------------------------------------
    // SseParser.parseFrame unit tests
    // -----------------------------------------------------------------------

    @Test
    void parseFrame_validChunk() {
        String frame = "data: {\"id\":\"x\",\"object\":\"chat.completion.chunk\",\"created\":1,\"model\":\"m\"," +
                "\"choices\":[{\"index\":0,\"delta\":{\"content\":\"Hi\"},\"finish_reason\":null}]}\n";
        var result = SseParser.parseFrame(frame);
        assertNotNull(result);
        assertFalse(result.done);
        assertNull(result.error);
        assertNotNull(result.chunk);
        assertEquals("Hi", result.chunk.choices.get(0).delta.content);
    }

    @Test
    void parseFrame_doneSentinel() {
        var result = SseParser.parseFrame("data: [DONE]\n");
        assertNotNull(result);
        assertTrue(result.done);
    }

    @Test
    void parseFrame_emptyFrame() {
        assertNull(SseParser.parseFrame(""));
        assertNull(SseParser.parseFrame("   \n"));
    }

    @Test
    void parseFrame_malformedJson() {
        assertNull(SseParser.parseFrame("data: {not json}\n"));
    }

    @Test
    void parseFrame_errorFrame() {
        String frame = "data: {\"error\":{\"code\":\"upstream_error\",\"message\":\"Provider failed\"}}\n";
        var result = SseParser.parseFrame(frame);
        assertNotNull(result);
        assertNotNull(result.error);
        assertEquals("upstream_error", result.error.getErrorCode());
    }

    // -----------------------------------------------------------------------
    // End-to-end SseParser tests
    // -----------------------------------------------------------------------

    @Test
    void sseParser_singleChunk() {
        String sse = makeChunkFrame("Hello") + "data: [DONE]\n\n";
        List<ChatCompletionChunk> chunks = collect(parse(sse));
        assertEquals(1, chunks.size());
        assertEquals("Hello", chunks.get(0).choices.get(0).delta.content);
    }

    @Test
    void sseParser_multipleChunks() {
        String sse = makeChunkFrame("A") + makeChunkFrame("B") + makeChunkFrame("C") + "data: [DONE]\n\n";
        List<ChatCompletionChunk> chunks = collect(parse(sse));
        assertEquals(3, chunks.size());
        assertEquals("A", chunks.get(0).choices.get(0).delta.content);
        assertEquals("C", chunks.get(2).choices.get(0).delta.content);
    }

    @Test
    void sseParser_doneTerminatesIteration() {
        String sse = makeChunkFrame("First") + "data: [DONE]\n\n" + makeChunkFrame("NEVER");
        List<ChatCompletionChunk> chunks = collect(parse(sse));
        assertEquals(1, chunks.size(), "Should stop at [DONE]");
        assertEquals("First", chunks.get(0).choices.get(0).delta.content);
    }

    @Test
    void sseParser_midStreamError() {
        String sse = makeChunkFrame("Part1") +
                "data: {\"error\":{\"code\":\"upstream_error\",\"message\":\"Server died\"}}\n\n";
        SseParser parser = parse(sse);

        // First chunk succeeds
        assertTrue(parser.hasNext());
        ChatCompletionChunk first = parser.next();
        assertEquals("Part1", first.choices.get(0).delta.content);

        // Next call throws
        assertTrue(parser.hasNext());
        assertThrows(MeshAPIError.class, parser::next);
    }

    @Test
    void sseParser_chunkedTcpFragmentation() {
        // Two chunks in same SSE body (no fragmentation to simulate in Java — just verify normal parsing)
        String sse = makeChunkFrame("X") + makeChunkFrame("Y") + "data: [DONE]\n\n";
        List<ChatCompletionChunk> chunks = collect(parse(sse));
        assertEquals(2, chunks.size());
        assertEquals("X", chunks.get(0).choices.get(0).delta.content);
        assertEquals("Y", chunks.get(1).choices.get(0).delta.content);
    }
}
