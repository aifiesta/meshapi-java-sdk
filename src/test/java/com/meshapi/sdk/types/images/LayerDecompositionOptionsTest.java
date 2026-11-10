package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayerDecompositionOptionsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesSnakeCaseFields() throws Exception {
        LayerDecompositionOptions options = LayerDecompositionOptions.builder()
                .maxImages(10)
                .textRecognition(true)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(options));

        assertEquals(10, json.get("max_images").asInt());
        assertTrue(json.get("text_recognition").asBoolean());
        assertEquals(2, json.size());
    }

    @Test
    void omitsUnsetFields() throws Exception {
        LayerDecompositionOptions options = LayerDecompositionOptions.builder()
                .maxImages(3)
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(options));

        assertTrue(json.has("max_images"));
        assertFalse(json.has("text_recognition"));
    }

    @Test
    void emptyOptionsSerializeToEmptyObject() throws Exception {
        String json = mapper.writeValueAsString(LayerDecompositionOptions.builder().build());
        assertEquals("{}", json);
    }

    @Test
    void roundTripsThroughJson() throws Exception {
        LayerDecompositionOptions original = LayerDecompositionOptions.builder()
                .maxImages(5)
                .textRecognition(false)
                .build();

        LayerDecompositionOptions parsed = mapper.readValue(
                mapper.writeValueAsString(original), LayerDecompositionOptions.class);

        assertEquals(original, parsed);
        assertEquals(5, parsed.getMaxImages());
        assertFalse(parsed.getTextRecognition());
    }

    @Test
    void gettersReturnNullWhenUnset() {
        LayerDecompositionOptions options = LayerDecompositionOptions.builder().build();
        assertNull(options.getMaxImages());
        assertNull(options.getTextRecognition());
    }

    @Test
    void rejectsNonPositiveMaxImages() {
        assertThrows(IllegalArgumentException.class,
                () -> LayerDecompositionOptions.builder().maxImages(0));
    }
}
