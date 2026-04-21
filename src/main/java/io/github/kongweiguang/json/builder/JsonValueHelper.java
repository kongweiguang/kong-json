package io.github.kongweiguang.json.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.kongweiguang.json.Json;

/**
 * JSON 构建器的值转换工具，负责把 Java 值写入 Jackson 节点。
 *
 * @author kongweiguang
 */
final class JsonValueHelper {

    private JsonValueHelper() {
    }

    /**
     * 将 Java 值转换为 JSON 节点，并尽量保留原生 JSON 类型。
     *
     * @param value Java 值
     * @return JSON 节点
     */
    static JsonNode valueToNode(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        }

        if (value instanceof JsonNode jsonNode) {
            return jsonNode;
        }

        if (value instanceof JsonObjectBuilder jsonObjectBuilder) {
            return jsonObjectBuilder.toNode();
        }

        if (value instanceof JsonArrayBuilder jsonArrayBuilder) {
            return jsonArrayBuilder.toNode();
        }

        if (value instanceof String text) {
            return TextNode.valueOf(text);
        }

        // 复杂对象交给当前 codec 处理，保证构建器与 Json.cvt 使用同一套转换策略。
        return Json.codec().node(value);
    }

    /**
     * 将 Java 值转换为 JSON 字符串节点。
     *
     * @param value Java 值
     * @return JSON 字符串节点；当 value 为 null 时返回 JSON null 节点
     */
    static JsonNode stringToNode(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        }

        return TextNode.valueOf(String.valueOf(value));
    }

    /**
     * 向对象节点写入字段，并保留字段值的原生 JSON 类型。
     *
     * @param node 对象节点
     * @param fieldName 字段名
     * @param value 字段值
     */
    static void putValue(ObjectNode node, String fieldName, Object value) {
        node.set(fieldName, valueToNode(value));
    }

    /**
     * 向对象节点写入字符串字段。
     *
     * @param node 对象节点
     * @param fieldName 字段名
     * @param value 字段值
     */
    static void putString(ObjectNode node, String fieldName, Object value) {
        node.set(fieldName, stringToNode(value));
    }

    /**
     * 向数组节点追加元素，并保留元素值的原生 JSON 类型。
     *
     * @param node 数组节点
     * @param value 元素值
     */
    static void addValue(ArrayNode node, Object value) {
        node.add(valueToNode(value));
    }

    /**
     * 向数组节点追加字符串元素。
     *
     * @param node 数组节点
     * @param value 元素值
     */
    static void addString(ArrayNode node, Object value) {
        node.add(stringToNode(value));
    }
}
