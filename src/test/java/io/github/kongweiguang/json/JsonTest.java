package io.github.kongweiguang.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.kongweiguang.json.builder.JsonArrayBuilder;
import io.github.kongweiguang.json.builder.JsonObjectBuilder;
import io.github.kongweiguang.json.codec.JsonCodec;
import io.github.kongweiguang.json.exception.JsonException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    private static final JsonMapper DEFAULT_MAPPER = Json.mapper();

    private final User user = new User().setAge(1).setName("kong").setHobby(new String[]{"j", "n"});

    @AfterEach
    void restoreMapper() {
        Json.configure(DEFAULT_MAPPER);
    }

    @Test
    void objectBuilderWritesNativeJsonTypes() {
        JsonObjectBuilder jsonObject = Json.object()
                .put("name", "kong")
                .put("age", 1)
                .put("active", true)
                .put("salary", new BigDecimal("88.50"))
                .put("nullable", null)
                .put("profile", user)
                .array("tags", array -> array.add("java").add(8))
                .putString("literalNumber", 1);

        JsonNode node = jsonObject.toNode();

        assertEquals("kong", node.get("name").asText());
        assertTrue(node.get("age").isInt());
        assertEquals(1, node.get("age").asInt());
        assertTrue(node.get("active").isBoolean());
        assertTrue(node.get("active").asBoolean());
        assertTrue(node.get("salary").isBigDecimal());
        assertTrue(node.get("nullable").isNull());
        assertTrue(node.get("profile").isObject());
        assertEquals("kong", node.get("profile").get("name").asText());
        assertTrue(node.get("tags").isArray());
        assertEquals(8, node.get("tags").get(1).asInt());
        assertEquals("1", node.get("literalNumber").asText());
    }

    @Test
    void arrayBuilderWritesNativeJsonTypes() {
        JsonArrayBuilder jsonArray = Json.array()
                .add(1)
                .add(true)
                .add(null)
                .add(user)
                .array(array -> array.add("nested").add(false))
                .addString(66);

        JsonNode node = jsonArray.toNode();

        assertTrue(node.isArray());
        assertTrue(node.get(0).isInt());
        assertTrue(node.get(1).isBoolean());
        assertTrue(node.get(2).isNull());
        assertTrue(node.get(3).isObject());
        assertEquals("kong", node.get(3).get("name").asText());
        assertTrue(node.get(4).isArray());
        assertFalse(node.get(4).get(1).asBoolean());
        assertEquals("66", node.get(5).asText());
    }

    @Test
    void buildersSupportNestedObjectsCollectionsAndCopies() {
        JsonObjectBuilder jsonObject = Json.object()
                .putAll(Map.of(
                        "count", 2,
                        "enabled", true,
                        "items", List.of("a", "b")
                ))
                .object("user", object -> object.put("name", "kong"));

        JsonArrayBuilder jsonArray = Json.array()
                .addAll(List.of(1, true, Map.of("name", "kong")))
                .object(object -> object.put("nested", true));

        JsonNode copiedObject = jsonObject.copyNode();
        JsonNode copiedArray = jsonArray.copyNode();

        jsonObject.toNode().put("count", 3);
        jsonArray.toNode().add("changed");

        assertEquals(2, copiedObject.get("count").asInt());
        assertEquals(3, jsonObject.toNode().get("count").asInt());
        assertEquals(4, copiedArray.size());
        assertEquals(5, jsonArray.toNode().size());
        assertEquals("b", jsonObject.toNode().get("items").get(1).asText());
        assertEquals("kong", jsonArray.toNode().get(2).get("name").asText());
    }

    @Test
    void stringifySerializesStringsAsJsonStrings() {
        assertEquals("\"kong\"", Json.str("kong"));
        assertEquals("{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}", Json.str(user));
        assertTrue(Json.strPretty(user).contains(System.lineSeparator()));
    }

    @Test
    void parseUsesStrictJsonByDefaultAndLenientMapperIsOptIn() {
        assertEquals("kong", Json.node("{\"name\":\"kong\"}").get("name").asText());
        assertThrows(JsonException.class, () -> Json.node("{name:'kong'}"));

        Json.configure(Json.lenientMapper());

        assertEquals("kong", Json.node("{name:'kong'}").get("name").asText());
    }

    @Test
    void conversionApisSupportStringNodeAndPojoInputs() {
        String json = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";

        User fromString = Json.cvt(json, User.class);
        JsonNode node = Json.codec().node(fromString);
        Person fromNode = Json.cvt(node, Person.class);

        assertEquals("kong", fromString.getName());
        assertEquals(1, fromString.getAge());
        assertArrayEquals(new String[]{"j", "n"}, fromString.getHobby());
        assertEquals("kong", fromNode.getName());
        assertArrayEquals(new String[]{"j", "n"}, fromNode.getHobby());
        assertEquals(fromString.getName(), Json.cvt("kong", String.class));
    }

    @Test
    void listAndMapConversionsPreserveGenericTypes() {
        String usersJson = "[{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}]";
        String mapJson = "{\"name\":[\"j\",\"n\"],\"age\":[\"1\"],\"hobby\":[\"j\",\"n\"]}";

        List<User> users = Json.list(usersJson, User.class);
        List<Person> persons = Json.list(users, Person.class);
        Map<String, List<String>> map = Json.map(mapJson, new TypeReference<Map<String, List<String>>>() {
        });

        assertEquals(1, users.size());
        assertEquals("kong", users.getFirst().getName());
        assertEquals("kong", persons.getFirst().getName());
        assertEquals(List.of("j", "n"), map.get("name"));
        assertEquals(List.of("1"), map.get("age"));
    }

    @Test
    void readAndReadLinesUseConsistentParsing(@TempDir Path tempDir) throws IOException {
        Path jsonFile = tempDir.resolve("sample.json");
        Files.writeString(jsonFile, "{\"name\":\"kong\",\"age\":1}", StandardCharsets.UTF_8);

        Path gbkFile = tempDir.resolve("sample-gbk.json");
        Files.writeString(gbkFile, "{\"name\":\"孔\"}", Charset.forName("GBK"));

        Path jsonlFile = tempDir.resolve("sample.jsonl");
        Files.writeString(jsonlFile, "{\"id\":1}\n\n{\"id\":2}", StandardCharsets.UTF_8);

        JsonNode jsonNode = Json.read(jsonFile);
        JsonNode gbkNode = Json.read(gbkFile, Charset.forName("GBK"));
        List<JsonNode> jsonl = Json.readLines(jsonlFile);

        assertEquals("kong", jsonNode.get("name").asText());
        assertEquals("孔", gbkNode.get("name").asText());
        assertEquals(2, jsonl.size());
        assertEquals(1, jsonl.getFirst().get("id").asInt());
        assertEquals(2, jsonl.get(1).get("id").asInt());
    }

    @Test
    void readApisWrapIoAndParseFailures(@TempDir Path tempDir) throws IOException {
        Path missing = tempDir.resolve("missing.json");
        Path invalid = tempDir.resolve("invalid.jsonl");
        Files.writeString(invalid, "{\"id\":1}\n{bad}", StandardCharsets.UTF_8);

        assertThrows(JsonException.class, () -> Json.read(missing));
        assertThrows(JsonException.class, () -> Json.readLines(missing));
        assertThrows(JsonException.class, () -> Json.readLines(invalid));
    }

    @Test
    void customMapperRequiresNonNullAndCanBeReplaced() {
        assertThrows(NullPointerException.class, () -> Json.configure((JsonMapper) null));
        assertThrows(NullPointerException.class, () -> Json.configure((JsonCodec) null));

        JsonMapper customMapper = JsonMapper.builder().build();
        Json.configure(customMapper);

        assertEquals(customMapper, Json.mapper());
        assertNotNull(Json.object().toNode());
        assertNotNull(Json.array().toNode());
    }

    @Test
    void conversionApisHandleNullInputsConsistently() {
        assertNull(Json.str(null));
        assertNull(Json.strPretty(null));
        assertNull(Json.cvt(null, User.class));
        assertNull(Json.cvt(null, new TypeReference<List<User>>() {
        }));
        assertNull(Json.cvt(null, Json.javaType(List.class, User.class)));
        assertNull(Json.codec().node((Object) null));
        assertNull(Json.list(null, User.class));
        assertNull(Json.map(null, String.class, Object.class));
    }

    @Test
    void invalidJsonIsWrappedInJsonException() {
        JsonException exception = assertThrows(JsonException.class, () -> Json.cvt("{bad json}", User.class));
        assertInstanceOf(Exception.class, exception.getCause());
    }
}
