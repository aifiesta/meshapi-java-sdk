package com.meshapi.sdk.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.StructuredOutputError;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.internal.StructuredOutputs;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.chat.StructuredParseOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompletionsResource {

    private final HttpClient http;

    public CompletionsResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Sends a non-streaming chat completion request.
     */
    public ChatCompletionResponse create(ChatCompletionRequest params) {
        params.setStream(false);
        return http.post("/v1/chat/completions", params, ChatCompletionResponse.class);
    }

    /**
     * Opens a streaming chat completion.
     *
     * <p>Returns an {@link Iterator} of SSE chunks. Iterate until {@link Iterator#hasNext()}
     * returns false. A mid-stream error causes {@link com.meshapi.sdk.MeshAPIError}
     * to be thrown from {@link Iterator#next()}.
     *
     * <p><strong>Streams do not retry.</strong> Catch the error and restart a new
     * {@link #stream} call if reconnection is needed.
     */
    public Iterator<ChatCompletionChunk> stream(ChatCompletionRequest params) {
        params.setStream(true);
        return http.stream("/v1/chat/completions", params);
    }

    /**
     * Structured (JSON-schema-constrained) completion. The JSON schema is derived
     * from {@code type} by reflection (define a class with Jackson fields and
     * {@code parse} builds the schema and the typed result). Non-streaming.
     *
     * @see #parse(ChatCompletionRequest, Class, StructuredParseOptions)
     */
    public <T> T parse(ChatCompletionRequest request, Class<T> type) {
        return parse(request, type, StructuredParseOptions.create());
    }

    /**
     * Structured completion with options. With {@link StructuredParseOptions#maxRetries(int)},
     * a reply that fails to decode is fed back to the model with the error appended,
     * up to that many times. Throws {@link StructuredOutputError} when it still can't
     * be decoded — most often because the model does not support structured outputs
     * (it returned plain text).
     *
     * <p>Note: Jackson does not enforce required fields — a JSON object missing a
     * field decodes to that field's default. Type mismatches and non-JSON prose are
     * caught (and drive retries / the error).
     */
    public <T> T parse(ChatCompletionRequest request, Class<T> type, StructuredParseOptions options) {
        ObjectMapper mapper = http.getObjectMapper();

        Map<String, Object> schema = options.getSchema() != null
                ? options.getSchema()
                : StructuredOutputs.schemaForClass(type);
        String name = options.getSchemaName() != null ? options.getSchemaName() : "response";

        Map<String, Object> jsonSchema = new LinkedHashMap<>();
        jsonSchema.put("name", name);
        jsonSchema.put("schema", schema);
        Map<String, Object> responseFormat = new LinkedHashMap<>();
        responseFormat.put("type", "json_schema");
        responseFormat.put("json_schema", jsonSchema);
        request.setResponseFormat(responseFormat);

        // Work on a copy so retries don't mutate the caller's message list.
        List<ChatMessage> messages = new ArrayList<>(request.getMessages());
        request.setMessages(messages);
        String model = request.getModel();

        int attempt = 0;
        while (true) {
            ChatCompletionResponse resp = create(request);
            String content = StructuredOutputs.extractContent(resp);
            try {
                return mapper.readValue(content, type);
            } catch (JsonProcessingException e) {
                boolean notJson = StructuredOutputs.isNotJson(e, content);
                if (attempt >= options.getMaxRetries()) {
                    throw new StructuredOutputError(
                            StructuredOutputs.errorMessage(model, notJson, e), e);
                }
                attempt++;
                messages.add(ChatMessage.assistant(content));
                messages.add(ChatMessage.user(StructuredOutputs.correctionPrompt(e)));
            }
        }
    }
}
