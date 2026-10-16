package ai.meshapi.contract;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Prints the SDK's typed surface as JSON for the shared contract drift gate. */
public final class DumpSurface {

    private static final String PKG = "com.meshapi.sdk.types.";
    private static final String API_VERSION_HEADER = "X-Mesh-Version";

    // SDK type name -> spec component name, where the two differ.
    private static final Map<String, String> SPEC_NAMES = Map.of(
            "ChatMessage", "Message",
            "ModelInfo", "ModelOut",
            "RagFileStatus", "FileStatusResponse",
            "RagFileListResponse", "FileListResponse",
            "Voice", "VoiceInfo",
            "TranscriptionRequest", "Body_create_transcription",
            "AudioTranslationRequest", "Body_create_translation",
            "TranscriptionTranslateRequest", "Body_create_transcription_translate");

    private static final Pattern SIGNATURE = Pattern.compile(
            "^ {4}(?:public |private |protected )?(?:static )?(?:<[^>]+> )?[\\w<>\\[\\],.? ]+? (\\w+)\\(([^)]*)\\)\\s*(?:throws [^{]+)?\\{",
            Pattern.MULTILINE);
    private static final Pattern HTTP_CALL = Pattern.compile(
            "http\\.(get|post|patch|put|delete|stream|streamJson|getBytes|postBytes|postMultipart)\\(\\s*\"((?:[^\"\\\\]|\\\\.)*)\"(?:\\s*\\+\\s*(\\w+))?");
    private static final Pattern QUERY_KEY = Pattern.compile("\"(\\w+)=\"|append\\(\"(\\w+)\"|\\.put\\(\"(\\w+)\"");

    private DumpSurface() {}

    public static void main(String[] args) throws IOException {
        Path root = Path.of(args.length > 0 ? args[0] : ".");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(build(root)));
    }

    public static ObjectNode build(Path root) throws IOException {
        Map<String, Set<String>> models = modelFields(root.resolve("target/classes"));
        ObjectMapper om = new ObjectMapper();
        ObjectNode out = om.createObjectNode();
        out.put("schema_version", "1");
        out.put("spec_version", Files.readString(root.resolve(".meshapi-spec-version")).trim());
        ArrayNode fields = out.putArray("model_fields");
        Set<String> rows = new TreeSet<>();
        models.forEach((owner, names) -> names.forEach(f -> rows.add(owner + "\u0000" + f)));
        for (String row : rows) {
            String[] parts = row.split("\u0000");
            fields.addArray().add(parts[0]).add(parts[1]);
        }
        ArrayNode params = out.putArray("params");
        params(root.resolve("src/main/java/com/meshapi/sdk/resources"), models).forEach(params::add);
        return out;
    }

    static Map<String, Set<String>> modelFields(Path classesDir) throws IOException {
        Map<String, Set<String>> out = new TreeMap<>();
        Path base = classesDir.resolve(PKG.replace('.', '/'));
        List<Path> files;
        try (Stream<Path> s = Files.walk(base)) {
            files = s.filter(p -> p.toString().endsWith(".class")).toList();
        }
        for (Path file : files) {
            String rel = base.relativize(file).toString();
            String binary = PKG + rel.substring(0, rel.length() - ".class".length()).replace('/', '.');
            String simple = binary.substring(Math.max(binary.lastIndexOf('.'), binary.lastIndexOf('$')) + 1);
            if (simple.equals("Builder") || simple.isEmpty() || Character.isDigit(simple.charAt(0))) {
                continue;
            }
            Class<?> cls;
            try {
                cls = Class.forName(binary, false, DumpSurface.class.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
            Set<String> names = jsonNames(cls);
            out.computeIfAbsent(simple, k -> new TreeSet<>()).addAll(names);
            String spec = SPEC_NAMES.get(simple);
            if (spec != null) {
                out.computeIfAbsent(spec, k -> new TreeSet<>()).addAll(names);
            }
        }
        return out;
    }

    private static Set<String> jsonNames(Class<?> cls) {
        Set<String> names = new TreeSet<>();
        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())
                        || f.isSynthetic() || f.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }
                addName(names, f.getName(), f.getAnnotation(JsonProperty.class), f.getAnnotation(JsonAlias.class));
            }
            for (Method m : c.getDeclaredMethods()) {
                JsonProperty jp = m.getAnnotation(JsonProperty.class);
                if (jp != null && !jp.value().isEmpty()) {
                    names.add(jp.value());
                }
            }
        }
        return names;
    }

    private static void addName(Set<String> names, String javaName, JsonProperty jp, JsonAlias alias) {
        names.add(jp != null && !jp.value().isEmpty() ? jp.value() : javaName);
        if (alias != null) {
            names.addAll(List.of(alias.value()));
        }
    }

    static Set<String> params(Path resourcesDir, Map<String, Set<String>> models) throws IOException {
        Set<String> found = new TreeSet<>();
        List<Path> files;
        try (Stream<Path> s = Files.list(resourcesDir)) {
            files = s.filter(p -> p.toString().endsWith(".java")).toList();
        }
        for (Path file : files) {
            String src = Files.readString(file);
            List<int[]> sigs = new ArrayList<>();
            Matcher sig = SIGNATURE.matcher(src);
            while (sig.find()) {
                sigs.add(new int[] {sig.start(), sig.end()});
            }
            Matcher call = HTTP_CALL.matcher(src);
            while (call.find()) {
                int owner = -1;
                for (int i = 0; i < sigs.size() && sigs.get(i)[0] < call.start(); i++) {
                    owner = i;
                }
                if (owner < 0) {
                    continue;
                }
                int bodyEnd = owner + 1 < sigs.size() ? sigs.get(owner + 1)[0] : src.length();
                String header = src.substring(sigs.get(owner)[0], sigs.get(owner)[1]);
                Matcher hm = SIGNATURE.matcher(header);
                hm.find();
                String body = src.substring(sigs.get(owner)[1], bodyEnd);

                String verb = call.group(1);
                String method = switch (verb) {
                    case "getBytes" -> "GET";
                    case "postBytes", "postMultipart", "stream", "streamJson" -> "POST";
                    default -> verb.toUpperCase();
                };
                String path = call.group(2) + (call.group(3) != null ? "{" + call.group(3) + "}" : "");
                String scope = method + " " + path;

                Set<String> names = new TreeSet<>();
                names.add(API_VERSION_HEADER);
                if (call.group(3) != null) {
                    names.add(call.group(3));
                }
                for (String p : hm.group(2).split(",")) {
                    String[] tv = p.trim().split("\\s+");
                    if (tv.length < 2) {
                        continue;
                    }
                    String type = tv[tv.length - 2].replaceAll("<.*", "").replaceAll("\\[\\]", "");
                    Set<String> modelNames = models.get(type.substring(type.lastIndexOf('.') + 1));
                    if (modelNames != null) {
                        names.addAll(modelNames);
                    } else {
                        names.add(tv[tv.length - 1]);
                    }
                }
                Matcher q = QUERY_KEY.matcher(body);
                while (q.find()) {
                    for (int g = 1; g <= 3; g++) {
                        if (q.group(g) != null) {
                            names.add(q.group(g));
                        }
                    }
                }
                names.forEach(n -> found.add(scope + ":" + n));
            }
        }
        return found;
    }
}
