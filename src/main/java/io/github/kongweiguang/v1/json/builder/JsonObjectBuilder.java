package io.github.kongweiguang.v1.json.builder;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kongweiguang.v1.json.Json;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JSON 对象链式构建器，用于按字段逐步组装 ObjectNode。
 *
 * @author kongweiguang
 */
public final class JsonObjectBuilder {

    private final ObjectNode node;

    private JsonObjectBuilder() {
        this(Json.mapper().createObjectNode());
    }

    private JsonObjectBuilder(ObjectNode node) {
        this.node = Objects.requireNonNull(node, "node must not be null");
    }

    static JsonObjectBuilder wrap(ObjectNode node) {
        return new JsonObjectBuilder(node);
    }

    /**
     * 创建 JSON 对象构建器。
     *
     * @return JSON 对象构建器
     */
    public static JsonObjectBuilder create() {
        return new JsonObjectBuilder();
    }

    /**
     * 添加或替换字段，并尽量保留值的原生 JSON 类型。
     *
     * @param key 字段名
     * @param value 字段值
     * @return 当前构建器
     */
    public JsonObjectBuilder put(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        JsonValueHelper.putValue(node, key, value);
        return this;
    }

    /**
     * 添加或替换字段，并将字段值显式写为 JSON 字符串。
     *
     * @param key 字段名
     * @param value 字段值
     * @return 当前构建器
     */
    public JsonObjectBuilder putString(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        JsonValueHelper.putString(node, key, value);
        return this;
    }

    /**
     * 添加嵌套 JSON 对象字段。
     *
     * @param key 字段名
     * @param consumer 嵌套对象构建回调
     * @return 当前构建器
     */
    public JsonObjectBuilder object(String key, Consumer<JsonObjectBuilder> consumer) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");
        consumer.accept(wrap(node.putObject(key)));
        return this;
    }

    /**
     * 添加嵌套 JSON 数组字段。
     *
     * @param key 字段名
     * @param consumer 嵌套数组构建回调
     * @return 当前构建器
     */
    public JsonObjectBuilder array(String key, Consumer<JsonArrayBuilder> consumer) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");
        consumer.accept(JsonArrayBuilder.wrap(node.putArray(key)));
        return this;
    }

    /**
     * 批量添加 Map 中的所有字段。
     *
     * @param map 待添加的字段集合
     * @return 当前构建器
     */
    public JsonObjectBuilder putAll(Map<String, ?> map) {
        if (map != null) {
            map.forEach(this::put);
        }
        return this;
    }

    /**
     * 将当前对象序列化为紧凑 JSON 字符串。
     *
     * @return 紧凑 JSON 字符串
     */
    public String toJson() {
        return node.toString();
    }

    /**
     * 将当前对象序列化为格式化 JSON 字符串。
     *
     * @return 格式化 JSON 字符串
     */
    public String toPrettyJson() {
        return node.toPrettyString();
    }

    /**
     * 将当前对象转换为指定键值类型的 Map。
     *
     * @param keyType 键类型
     * @param valueType 值类型
     * @return 转换后的 Map
     */
    public <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType) {
        return Json.map(node, keyType, valueType);
    }

    /**
     * 获取当前构建器持有的可变 ObjectNode。
     *
     * @return 可变 ObjectNode
     */
    public ObjectNode toNode() {
        return node;
    }

    /**
     * 获取当前 ObjectNode 的深拷贝。
     *
     * @return 拷贝后的 ObjectNode
     */
    public ObjectNode copyNode() {
        return node.deepCopy();
    }
}
