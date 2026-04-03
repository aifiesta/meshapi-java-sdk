package com.routersvc.sdk.types.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateSummary {
    @JsonProperty("id") public String id;
    @JsonProperty("name") public String name;
    @JsonProperty("owner") public String owner;
    @JsonProperty("description") public String description;
    @JsonProperty("system") public String system;
    @JsonProperty("messages") public List<Map<String, Object>> messages;
    @JsonProperty("model") public String model;
    @JsonProperty("params") public Map<String, Object> params;
    @JsonProperty("variables") public List<String> variables;
    @JsonProperty("created_at") public String createdAt;
    @JsonProperty("updated_at") public String updatedAt;
}
