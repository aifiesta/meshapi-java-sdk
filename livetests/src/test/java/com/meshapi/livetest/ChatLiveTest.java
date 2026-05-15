package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.templates.CreateTemplateRequest;
import com.meshapi.sdk.types.templates.TemplateSummary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatLiveTest extends LiveTestBase {

    @Test
    void nonStreaming_basic() {
        MeshAPI client = newClient();
        ChatCompletionResponse resp = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.user("Reply with the single word: pong"))
                        .maxTokens(10)
                        .build());

        assertNotNull(resp.id);
        assertFalse(resp.choices.isEmpty());
        assertNotNull(resp.choices.get(0).message);
        assertNotNull(resp.choices.get(0).message.content);
        System.out.printf("[PASS] chat.create → id=%s model=%s content=%s%n",
                resp.id, resp.model, resp.choices.get(0).message.content);
    }

    @Test
    void nonStreaming_multiTurn() {
        MeshAPI client = newClient();
        ChatCompletionResponse resp = client.chat().completions().create(
                ChatCompletionRequest.builder()
                        .model(MODEL)
                        .addMessage(ChatMessage.system("You are a concise assistant. One sentence only."))
                        .addMessage(ChatMessage.user("What is the capital of France?"))
                        .maxTokens(20)
                        .build());

        assertNotNull(resp.choices.get(0).message.content);
        String content = resp.choices.get(0).message.content;
        System.out.printf("[PASS] chat multi-turn → %s%n", content);
    }

    @Test
    void nonStreaming_withTemplate() {
        MeshAPI client = newClient();
        String name = uniqueName("java-chat-tpl");
        TemplateSummary tmpl = client.templates().create(
                CreateTemplateRequest.builder()
                        .name(name)
                        .system("You are a helpful assistant who always responds in exactly 3 words.")
                        .model(MODEL)
                        .build());
        try {
            ChatCompletionResponse resp = client.chat().completions().create(
                    ChatCompletionRequest.builder()
                            .template(tmpl.name)
                            .addMessage(ChatMessage.user("Greet me."))
                            .maxTokens(15)
                            .build());
            assertNotNull(resp.id);
            System.out.printf("[PASS] chat with template=%s → id=%s%n", tmpl.name, resp.id);
        } finally {
            try { client.templates().delete(tmpl.id); } catch (Exception ignored) {}
        }
    }
}
