package com.meshapi.sdk.types.templates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTemplateRequest {
    @JsonProperty("name") private String name;
    @JsonProperty("description") private String description;
    @JsonProperty("system") private String system;
    @JsonProperty("messages") private List<Map<String, Object>> messages;
    @JsonProperty("model") private String model;
    @JsonProperty("params") private Map<String, Object> params;
    @JsonProperty("variables") private List<String> variables;

    private CreateTemplateRequest() {}

    public String getName() { return name; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final CreateTemplateRequest req = new CreateTemplateRequest();

        public Builder name(String name) { req.name = name; return this; }
        public Builder description(String desc) { req.description = desc; return this; }
        public Builder system(String system) { req.system = system; return this; }
        public Builder messages(List<Map<String, Object>> msgs) { req.messages = msgs; return this; }
        public Builder model(String model) { req.model = model; return this; }
        public Builder params(Map<String, Object> params) { req.params = params; return this; }
        public Builder variables(List<String> vars) { req.variables = vars; return this; }

        public CreateTemplateRequest build() { return req; }
    }
}
