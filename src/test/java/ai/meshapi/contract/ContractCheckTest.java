package ai.meshapi.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractCheckTest {

    private static final Path ROOT = Path.of(".").toAbsolutePath().normalize();
    private static final ObjectMapper OM = new ObjectMapper();
    private static ObjectNode surface;

    @BeforeAll
    static void dump() throws IOException {
        surface = DumpSurface.build(ROOT);
    }

    private static boolean hasField(JsonNode root, String model, String field) {
        for (JsonNode row : root.get("model_fields")) {
            if (row.get(0).asText().equals(model) && row.get(1).asText().equals(field)) {
                return true;
            }
        }
        return false;
    }

    private static int gate(Path actual, Path baseline) throws Exception {
        List<String> cmd = new ArrayList<>(List.of(
                "python3", "-m", "meshapi_contract.gate",
                "--spec", ROOT.resolve("contract/openapi.public.json").toString(),
                "--actual", actual.toString(),
                "--spec-version", Files.readString(ROOT.resolve(".meshapi-spec-version")).trim(),
                "--baseline", baseline.toString()));
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        p.getInputStream().readAllBytes();
        assertTrue(p.waitFor(60, TimeUnit.SECONDS));
        return p.exitValue();
    }

    private static void assumeGateInstalled() throws Exception {
        Process p = new ProcessBuilder("python3", "-c", "import meshapi_contract").redirectErrorStream(true).start();
        p.getInputStream().readAllBytes();
        boolean installed = p.waitFor(30, TimeUnit.SECONDS) && p.exitValue() == 0;
        if (System.getenv("CI") != null) {
            assertTrue(installed, "meshapi_contract must be installed in CI");
        }
        Assumptions.assumeTrue(installed, "meshapi_contract is not installed");
    }

    @Test
    void dumpContainsKnownRequestFieldsUnderSpecNames() {
        assertEquals("1", surface.get("schema_version").asText());
        assertTrue(hasField(surface, "ChatCompletionRequest", "max_tokens"));
        assertTrue(hasField(surface, "ChatCompletionRequest", "messages"));
        assertTrue(hasField(surface, "Message", "role"));
        assertTrue(hasField(surface, "ModelOut", "id"));
        assertTrue(surface.get("params").toString().contains("POST /v1/chat/completions:X-Mesh-Version"));
    }

    @Test
    void cleanSurfaceWithCheckedInBaselinePasses(@TempDir Path tmp) throws Exception {
        assumeGateInstalled();
        Path actual = tmp.resolve("actual.json");
        OM.writeValue(actual.toFile(), surface);
        assertEquals(0, gate(actual, ROOT.resolve("contract-baseline.json")));
    }

    @Test
    void fieldStrippedFromSurfaceFailsTheGate(@TempDir Path tmp) throws Exception {
        assumeGateInstalled();
        ObjectNode mutated = surface.deepCopy();
        ArrayNode kept = OM.createArrayNode();
        for (JsonNode row : mutated.get("model_fields")) {
            if (!(row.get(0).asText().equals("ChatCompletionRequest") && row.get(1).asText().equals("model"))) {
                kept.add(row);
            }
        }
        mutated.set("model_fields", kept);
        Path actual = tmp.resolve("actual.json");
        OM.writeValue(actual.toFile(), mutated);
        assertEquals(1, gate(actual, ROOT.resolve("contract-baseline.json")));
    }

    @Test
    void rowMissingFromBaselineFailsTheGate(@TempDir Path tmp) throws Exception {
        assumeGateInstalled();
        Path actual = tmp.resolve("actual.json");
        OM.writeValue(actual.toFile(), surface);
        ObjectNode baseline = (ObjectNode) OM.readTree(ROOT.resolve("contract-baseline.json").toFile());
        ((ArrayNode) baseline.get("allow")).remove(0);
        Path trimmed = tmp.resolve("baseline.json");
        OM.writeValue(trimmed.toFile(), baseline);
        assertEquals(1, gate(actual, trimmed));
    }
}
