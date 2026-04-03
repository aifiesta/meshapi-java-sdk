package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;

public class ChatResource {

    public final CompletionsResource completions;

    public ChatResource(HttpClient http) {
        this.completions = new CompletionsResource(http);
    }

    public CompletionsResource completions() { return completions; }
}
