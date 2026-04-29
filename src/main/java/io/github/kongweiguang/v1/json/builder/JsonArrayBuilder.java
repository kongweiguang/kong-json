package io.github.kongweiguang.v1.json.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.kongweiguang.v1.json.Json;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JSON 数组链式构建器，用于按顺序逐步组装 ArrayNode。
 *
 * @author kongweiguang
 */
public final class JsonArrayBuilder {

    private final ArrayNode node;

    private JsonArrayBuilder() {
        this(Json.mapper().createArrayNode());
    }

    private JsonArrayBuilder(ArrayNode node) {
        this.node = Objects.requireNonNull(node, "node must not be null");
    }

    static JsonArrayBuilder wrap(ArrayNode node) {
        return new JsonArrayBuilder(node);
    }

    /**
     * 创建 JSON 数组构建器。
     *
     * @return JSON 数组构建器
     */
    public static JsonArrayBuilder create() {
        return new JsonArrayBuilder();
    }

    /**
     * 追加数组元素，并尽量保留值的原生 JSON 类型。
     *
     * @param value 数组元素值
     * @return 当前构建器
     */
    public JsonArrayBuilder add(Object value) {
        JsonValueHelper.addValue(node, value);
        return this;
    }

    /**
     * 追加数组元素，并将元素值显式写为 JSON 字符串。
     *
     * @param value 数组元素值
     * @return 当前构建器
     */
    public JsonArrayBuilder addString(Object value) {
        JsonValueHelper.addString(node, value);
        return this;
    }

    /**
     * 追加嵌套 JSON 对象元素。
     *
     * @param consumer 嵌套对象构建回调
     * @return 当前构建器
     */
    public JsonArrayBuilder object(Consumer<JsonObjectBuilder> consumer) {
        Objects.requireNonNull(consumer, "consumer must not be null");
        JsonObjectBuilder object = JsonObjectBuilder.wrap(node.objectNode());
        consumer.accept(object);
        return add(object.toNode());
    }

    /**
     * 追加嵌套 JSON 数组元素。
     *
     * @param consumer 嵌套数组构建回调
     * @return 当前构建器
     */
    public JsonArrayBuilder array(Consumer<JsonArrayBuilder> consumer) {
        Objects.requireNonNull(consumer, "consumer must not be null");
        JsonArrayBuilder array = wrap(node.arrayNode());
        consumer.accept(array);
        return add(array.toNode());
    }

    /**
     * 批量追加集合中的所有元素。
     *
     * @param collection 待追加的元素集合
     * @return 当前构建器
     */
    public JsonArrayBuilder addAll(Collection<?> collection) {
        if (collection != null) {
            collection.forEach(this::add);
        }
        return this;
    }

    /**
     * 将当前数组序列化为紧凑 JSON 字符串。
     *
     * @return 紧凑 JSON 字符串
     */
    public String toJson() {
        return node.toString();
    }

    /**
     * 将当前数组序列化为格式化 JSON 字符串。
     *
     * @return 格式化 JSON 字符串
     */
    public String toPrettyJson() {
        return node.toPrettyString();
    }

    /**
     * 将当前数组转换为指定元素类型的列表。
     *
     * @param elementType 元素类型
     * @return 转换后的列表
     */
    public <T> List<T> toList(Class<T> elementType) {
        return Json.list(node, elementType);
    }

    /**
     * 获取当前构建器持有的可变 ArrayNode。
     *
     * @return 可变 ArrayNode
     */
    public ArrayNode toNode() {
        return node;
    }

    /**
     * 获取当前 ArrayNode 的深拷贝。
     *
     * @return 拷贝后的 ArrayNode
     */
    public ArrayNode copyNode() {
        return node.deepCopy();
    }
}
