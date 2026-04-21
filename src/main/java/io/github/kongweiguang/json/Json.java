package io.github.kongweiguang.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.kongweiguang.json.builder.JsonArrayBuilder;
import io.github.kongweiguang.json.builder.JsonObjectBuilder;
import io.github.kongweiguang.json.codec.JacksonJsonCodec;
import io.github.kongweiguang.json.codec.JsonCodec;
import io.github.kongweiguang.json.codec.JsonMappers;
import io.github.kongweiguang.json.file.JsonFiles;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JSON 门面工具类，统一提供 JSON 构建、解析、序列化、类型转换和文件读取入口。
 *
 * @author kongweiguang
 */
public final class Json {

    private static final JsonMapper DEFAULT_MAPPER = JsonMappers.defaultMapper();
    private static volatile JsonCodec codec = new JacksonJsonCodec(DEFAULT_MAPPER);

    private Json() {
    }

    /**
     * 创建 JSON 对象构建器。
     *
     * @return JSON 对象构建器
     */
    public static JsonObjectBuilder object() {
        return JsonObjectBuilder.create();
    }

    /**
     * 创建 JSON 数组构建器。
     *
     * @return JSON 数组构建器
     */
    public static JsonArrayBuilder array() {
        return JsonArrayBuilder.create();
    }

    /**
     * 将对象序列化为紧凑 JSON 字符串。
     *
     * @param value 待序列化的对象
     * @return 紧凑 JSON 字符串；当 value 为 null 时返回 null
     */
    public static String str(Object value) {
        return codec().str(value);
    }

    /**
     * 将对象序列化为格式化 JSON 字符串。
     *
     * @param value 待序列化的对象
     * @return 格式化 JSON 字符串；当 value 为 null 时返回 null
     */
    public static String strPretty(Object value) {
        return codec().strPretty(value);
    }

    /**
     * 解析 JSON 字符串。
     *
     * @param json JSON 字符串
     * @return 解析后的 JSON 节点
     */
    public static JsonNode node(String json) {
        return codec().node(json);
    }

    /**
     * 将对象转换为目标类型。
     *
     * @param value 源对象
     * @param type 目标类型
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    public static <T> T cvt(Object value, Class<T> type) {
        return codec().cvt(value, type);
    }

    /**
     * 将对象转换为目标泛型类型。
     *
     * @param value 源对象
     * @param type 目标泛型类型引用
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    public static <T> T cvt(Object value, TypeReference<T> type) {
        return codec().cvt(value, type);
    }

    /**
     * 将对象转换为指定的 Jackson JavaType。
     *
     * @param value 源对象
     * @param type 目标 JavaType
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    public static <T> T cvt(Object value, JavaType type) {
        return codec().cvt(value, type);
    }

    /**
     * 创建 Jackson 参数化类型。
     *
     * @param parametrized 原始类型
     * @param parameterTypes 泛型参数类型
     * @return Jackson JavaType
     */
    public static JavaType javaType(Class<?> parametrized, Class<?>... parameterTypes) {
        return codec().javaType(parametrized, parameterTypes);
    }

    /**
     * 将对象转换为指定元素类型的列表。
     *
     * @param value 源对象
     * @param elementType 元素类型
     * @return 转换后的列表；当 value 为 null 时返回 null
     */
    public static <T> List<T> list(Object value, Class<T> elementType) {
        if (value == null) {
            return null;
        }

        return cvt(value, javaType(List.class, elementType));
    }

    /**
     * 将对象转换为指定泛型类型的列表。
     *
     * @param value 源对象
     * @param type 列表泛型类型引用
     * @return 转换后的列表；当 value 或 type 为 null 时返回 null
     */
    public static <T> List<T> list(Object value, TypeReference<List<T>> type) {
        return cvt(value, type);
    }

    /**
     * 将对象转换为指定键值类型的 Map。
     *
     * @param value 源对象
     * @param keyType 键类型
     * @param valueType 值类型
     * @return 转换后的 Map；当 value 为 null 时返回 null
     */
    public static <K, V> Map<K, V> map(Object value, Class<K> keyType, Class<V> valueType) {
        if (value == null) {
            return null;
        }

        return cvt(value, javaType(Map.class, keyType, valueType));
    }

    /**
     * 将对象转换为指定泛型类型的 Map。
     *
     * @param value 源对象
     * @param type Map 泛型类型引用
     * @return 转换后的 Map；当 value 或 type 为 null 时返回 null
     */
    public static <K, V> Map<K, V> map(Object value, TypeReference<Map<K, V>> type) {
        return cvt(value, type);
    }

    /**
     * 按 UTF-8 编码读取 JSON 文件。
     *
     * @param path 文件路径
     * @return 解析后的 JSON 节点
     */
    public static JsonNode read(Path path) {
        return read(path, StandardCharsets.UTF_8);
    }

    /**
     * 按指定编码读取 JSON 文件。
     *
     * @param path 文件路径
     * @param charset 文件编码
     * @return 解析后的 JSON 节点
     */
    public static JsonNode read(Path path, Charset charset) {
        return JsonFiles.read(path, charset);
    }

    /**
     * 按 UTF-8 编码读取 JSON Lines 文件。
     *
     * @param path 文件路径
     * @return 每行解析得到的 JSON 节点列表
     */
    public static List<JsonNode> readLines(Path path) {
        return readLines(path, StandardCharsets.UTF_8);
    }

    /**
     * 按指定编码读取 JSON Lines 文件，空行会被忽略。
     *
     * @param path 文件路径
     * @param charset 文件编码
     * @return 每行解析得到的 JSON 节点列表
     */
    public static List<JsonNode> readLines(Path path, Charset charset) {
        return JsonFiles.readLines(path, charset);
    }

    /**
     * 替换全局 JSON 编解码策略。
     *
     * @param jsonCodec JSON 编解码策略
     */
    public static void configure(JsonCodec jsonCodec) {
        codec = Objects.requireNonNull(jsonCodec, "jsonCodec must not be null");
    }

    /**
     * 使用 Jackson JsonMapper 替换全局 JSON 编解码策略。
     *
     * @param jsonMapper Jackson JsonMapper
     */
    public static void configure(JsonMapper jsonMapper) {
        configure(new JacksonJsonCodec(jsonMapper));
    }

    /**
     * 获取当前全局 JSON 编解码策略。
     *
     * @return 当前 JSON 编解码策略
     */
    public static JsonCodec codec() {
        return codec;
    }

    /**
     * 获取当前 JSON 编解码策略使用的 Jackson JsonMapper。
     *
     * @return 当前 Jackson JsonMapper
     */
    public static JsonMapper mapper() {
        return codec().mapper();
    }

    /**
     * 获取严格模式的默认 Jackson JsonMapper。
     *
     * @return 默认 Jackson JsonMapper
     */
    public static JsonMapper defaultMapper() {
        return DEFAULT_MAPPER;
    }

    /**
     * 创建启用常见宽松解析特性的 Jackson JsonMapper。
     *
     * @return 宽松模式 Jackson JsonMapper
     */
    public static JsonMapper lenientMapper() {
        return JsonMappers.lenientMapper();
    }
}
