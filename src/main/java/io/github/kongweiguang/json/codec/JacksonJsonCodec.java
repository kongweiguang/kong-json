package io.github.kongweiguang.json.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.kongweiguang.json.exception.JsonException;

import java.io.IOException;
import java.util.Objects;

/**
 * 基于 Jackson 的 {@link JsonCodec} 默认实现。
 *
 * @author kongweiguang
 */
public final class JacksonJsonCodec implements JsonCodec {

    private final JsonMapper mapper;

    /**
     * 创建 Jackson JSON 编解码策略。
     *
     * @param mapper 用于序列化、解析和转换的 Jackson JsonMapper
     */
    public JacksonJsonCodec(JsonMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    }

    /**
     * 获取当前策略使用的 Jackson JsonMapper。
     *
     * @return Jackson JsonMapper
     */
    @Override
    public JsonMapper mapper() {
        return mapper;
    }

    /**
     * 将对象序列化为紧凑 JSON 字符串。
     *
     * @param value 待序列化的对象
     * @return 紧凑 JSON 字符串；当 value 为 null 时返回 null
     */
    @Override
    public String str(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw wrap("Failed to stringify value", e);
        }
    }

    /**
     * 将对象序列化为格式化 JSON 字符串。
     *
     * @param value 待序列化的对象
     * @return 格式化 JSON 字符串；当 value 为 null 时返回 null
     */
    @Override
    public String strPretty(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw wrap("Failed to stringify value", e);
        }
    }

    /**
     * 解析 JSON 字符串。
     *
     * @param json JSON 字符串
     * @return 解析后的 JSON 节点
     */
    @Override
    public JsonNode node(String json) {
        Objects.requireNonNull(json, "json must not be null");

        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw wrap("Failed to parse JSON", e);
        }
    }

    /**
     * 将对象转换为目标类型。
     *
     * @param value 源对象
     * @param type 目标类型
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T cvt(Object value, Class<T> type) {
        if (value == null || type == null) {
            return null;
        }

        if (type.equals(String.class) && value instanceof String str) {
            return (T) str;
        }

        try {
            if (value instanceof String str) {
                return mapper.readValue(str, type);
            }

            return mapper.convertValue(value, type);
        } catch (IOException | IllegalArgumentException e) {
            throw wrap("Failed to convert value to " + type.getName(), e);
        }
    }

    /**
     * 将对象转换为目标泛型类型。
     *
     * @param value 源对象
     * @param type 目标泛型类型引用
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    @Override
    public <T> T cvt(Object value, TypeReference<T> type) {
        if (value == null || type == null) {
            return null;
        }

        try {
            if (value instanceof String str) {
                return mapper.readValue(str, type);
            }

            return mapper.convertValue(value, type);
        } catch (IOException | IllegalArgumentException e) {
            throw wrap("Failed to convert value by type reference", e);
        }
    }

    /**
     * 将对象转换为指定的 Jackson JavaType。
     *
     * @param value 源对象
     * @param type 目标 JavaType
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    @Override
    public <T> T cvt(Object value, JavaType type) {
        if (value == null || type == null) {
            return null;
        }

        try {
            if (value instanceof String str) {
                return mapper.readValue(str, type);
            }

            return mapper.convertValue(value, type);
        } catch (IOException | IllegalArgumentException e) {
            throw wrap("Failed to convert value by JavaType", e);
        }
    }

    /**
     * 将对象转换为 JSON 节点；字符串会按 JSON 内容解析。
     *
     * @param value 源对象
     * @return JSON 节点；当 value 为 null 时返回 null
     */
    @Override
    public JsonNode node(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof JsonNode jsonNode) {
            return jsonNode;
        }

        if (value instanceof String str) {
            return node(str);
        }

        try {
            return mapper.valueToTree(value);
        } catch (IllegalArgumentException e) {
            throw wrap("Failed to convert value to JsonNode", e);
        }
    }

    /**
     * 创建 Jackson 参数化类型。
     *
     * @param parametrized 原始类型
     * @param parameterTypes 泛型参数类型
     * @return Jackson JavaType
     */
    @Override
    public JavaType javaType(Class<?> parametrized, Class<?>... parameterTypes) {
        Objects.requireNonNull(parametrized, "parametrized must not be null");
        Objects.requireNonNull(parameterTypes, "parameterTypes must not be null");
        return mapper.getTypeFactory().constructParametricType(parametrized, parameterTypes);
    }

    private static JsonException wrap(String message, Exception e) {
        return new JsonException(message, e);
    }
}
