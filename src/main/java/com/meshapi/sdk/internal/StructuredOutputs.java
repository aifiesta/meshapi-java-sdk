package com.meshapi.sdk.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helpers for structured outputs: build a JSON schema from a class by reflection
 * (respecting Jackson's {@code @JsonProperty} / {@code @JsonIgnore}; enum values
 * are derived by serializing each constant through the configured mapper, so
 * {@code @JsonValue} and enum-related mapper settings are honored), and shared
 * parse/error utilities. No external dependency — Jackson (already a dep) does
 * the decoding; this reflects the request schema.
 *
 * <p><strong>Reflection scope:</strong> the schema is derived from a class's
 * declared <em>fields</em> and field-level {@code @JsonProperty} / {@code @JsonIgnore}
 * annotations only. It does not introspect getters, setters, or constructor
 * parameters, which Jackson also uses when (de)serializing. If your POJO exposes
 * its Jackson properties through getters/setters or {@code @JsonCreator}/constructor
 * annotations rather than fields, the generated schema may not match how Jackson
 * decodes the response — pass an explicit schema via
 * {@link com.meshapi.sdk.types.chat.StructuredParseOptions#schema(java.util.Map)}.
 */
public final class StructuredOutputs {

    private StructuredOutputs() {}

    /** Pointer users follow when a model turns out not to support structured outputs. */
    public static final String MODELS_URL = "https://app.meshapi.ai/org/<your-org-id>/models";

    // -----------------------------------------------------------------------
    // Reflection: Class -> JSON schema
    // -----------------------------------------------------------------------

    public static Map<String, Object> schemaForClass(Class<?> type, ObjectMapper mapper) {
        return schemaForClass(type, mapper, new HashSet<>());
    }

    private static Map<String, Object> schemaForClass(Class<?> c, ObjectMapper mapper, Set<Class<?>> seen) {
        if (c == String.class || c == char.class || c == Character.class) {
            return leaf("string");
        }
        if (c == boolean.class || c == Boolean.class) {
            return leaf("boolean");
        }
        if (c == int.class || c == Integer.class || c == long.class || c == Long.class
                || c == short.class || c == Short.class || c == byte.class || c == Byte.class
                || c == BigInteger.class) {
            return leaf("integer");
        }
        if (c == float.class || c == Float.class || c == double.class || c == Double.class
                || c == BigDecimal.class) {
            return leaf("number");
        }
        if (c.isEnum()) {
            // Serialize each constant through the configured mapper so the schema
            // matches whatever Jackson will actually accept back — @JsonProperty,
            // @JsonValue (including non-string codes), and mapper enum settings.
            List<Object> values = new ArrayList<>();
            boolean allText = true;
            boolean allIntegral = true;
            boolean allNumeric = true;
            for (Object k : c.getEnumConstants()) {
                JsonNode node = mapper.valueToTree(k);
                if (node.isTextual()) {
                    values.add(node.asText());
                    allIntegral = false;
                    allNumeric = false;
                } else if (node.isNumber()) {
                    values.add(node.numberValue());
                    allText = false;
                    if (!node.isIntegralNumber()) {
                        allIntegral = false;
                    }
                } else {
                    values.add(mapper.convertValue(node, Object.class));
                    allText = false;
                    allIntegral = false;
                    allNumeric = false;
                }
            }
            Map<String, Object> s = new LinkedHashMap<>();
            if (allText) {
                s.put("type", "string");
            } else if (allIntegral) {
                s.put("type", "integer");
            } else if (allNumeric) {
                s.put("type", "number");
            }
            s.put("enum", values);
            return s;
        }
        if (c.isArray()) {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("type", "array");
            s.put("items", schemaForClass(c.getComponentType(), mapper, seen));
            return s;
        }
        if (Collection.class.isAssignableFrom(c)) {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("type", "array");
            s.put("items", new LinkedHashMap<>()); // raw collection -> unconstrained items
            return s;
        }
        if (Map.class.isAssignableFrom(c) || c == Object.class) {
            return leaf("object");
        }
        // Temporal types: Jackson decodes these from strings (or numbers), not
        // objects — so a POJO/object schema would be wrong. Map them to string.
        if (java.util.Date.class.isAssignableFrom(c)
                || java.util.Calendar.class.isAssignableFrom(c)
                || java.time.temporal.Temporal.class.isAssignableFrom(c)) {
            return leaf("string");
        }
        // POJO
        if (seen.contains(c)) {
            return leaf("object"); // break recursive types
        }
        seen.add(c);
        try {
            return structSchema(c, mapper, seen);
        } finally {
            seen.remove(c);
        }
    }

    private static Map<String, Object> schemaForType(Type t, ObjectMapper mapper, Set<Class<?>> seen) {
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            Class<?> raw = (Class<?>) pt.getRawType();
            if (Collection.class.isAssignableFrom(raw)) {
                Map<String, Object> s = new LinkedHashMap<>();
                s.put("type", "array");
                s.put("items", schemaForType(pt.getActualTypeArguments()[0], mapper, seen));
                return s;
            }
            if (Map.class.isAssignableFrom(raw)) {
                Map<String, Object> s = new LinkedHashMap<>();
                s.put("type", "object");
                s.put("additionalProperties", schemaForType(pt.getActualTypeArguments()[1], mapper, seen));
                return s;
            }
            return schemaForClass(raw, mapper, seen);
        }
        if (t instanceof Class) {
            return schemaForClass((Class<?>) t, mapper, seen);
        }
        return new LinkedHashMap<>(); // wildcard / type variable -> any
    }

    private static Map<String, Object> structSchema(Class<?> c, ObjectMapper mapper, Set<Class<?>> seen) {
        Map<String, Object> props = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Field f : k.getDeclaredFields()) {
                int mod = f.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isTransient(mod) || f.isSynthetic()) {
                    continue;
                }
                if (f.getAnnotation(JsonIgnore.class) != null) {
                    continue;
                }
                JsonProperty jp = f.getAnnotation(JsonProperty.class);
                String name = (jp != null && !jp.value().isEmpty()) ? jp.value() : f.getName();
                if (props.containsKey(name)) {
                    continue; // subclass field already recorded
                }
                props.put(name, schemaForType(f.getGenericType(), mapper, seen));
                required.add(name);
            }
        }
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }
        schema.put("additionalProperties", false);
        return schema;
    }

    private static Map<String, Object> leaf(String type) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("type", type);
        return s;
    }

    // -----------------------------------------------------------------------
    // Parse / error helpers
    // -----------------------------------------------------------------------

    public static String extractContent(ChatCompletionResponse resp) {
        if (resp == null || resp.choices == null || resp.choices.isEmpty()) {
            return "";
        }
        ChatCompletionResponse.ResponseMessage m = resp.choices.get(0).message;
        return (m == null || m.content == null) ? "" : m.content;
    }

    /** True when decoding failed because the content wasn't JSON at all (prose / empty). */
    public static boolean isNotJson(JsonProcessingException e, String content) {
        return content == null || content.isBlank() || e instanceof JsonParseException;
    }

    public static String correctionPrompt(Throwable e) {
        return "Your previous response failed schema validation: " + e.getMessage()
                + ". Return ONLY a JSON object that matches the requested schema, with no prose, "
                + "markdown, or code fences.";
    }

    public static String errorMessage(String model, boolean notJson, Throwable cause) {
        String where = (model != null && !model.isEmpty()) ? " from model '" + model + "'" : "";
        if (notJson) {
            return "Could not parse a structured response" + where + ": the model returned text that is "
                    + "not valid JSON, which usually means it does not support structured outputs "
                    + "(response_format). Check the model's support on the Models page (" + MODELS_URL
                    + ") or the supports_structured_output flag from GET /v1/models, and prefer a model "
                    + "with first-class support (e.g. openai/* or google/gemini-*). Original error: "
                    + cause.getMessage();
        }
        return "Could not parse a structured response" + where + ": the response was valid JSON but did "
                + "not match the requested type. Retry with a higher maxRetries, or confirm the model "
                + "supports structured outputs on the Models page (" + MODELS_URL + "). Original error: "
                + cause.getMessage();
    }
}
