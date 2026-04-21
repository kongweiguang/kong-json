package io.github.kongweiguang.json.codec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * JSON 编解码策略接口，定义序列化、解析、类型转换和节点转换能力。
 *
 * @author kongweiguang
 */
public interface JsonCodec {

    /**
     * 获取当前策略使用的 Jackson JsonMapper。
     *
     * @return Jackson JsonMapper
     */
    JsonMapper mapper();

    /**
     * 将对象序列化为紧凑 JSON 字符串。
     *
     * @param value 待序列化的对象
     * @return 紧凑 JSON 字符串；当 value 为 null 时返回 null
     */
    String str(Object value);

    /**
     * 将对象序列化为格式化 JSON 字符串。
     *
     * @param value 待序列化的对象
     * @return 格式化 JSON 字符串；当 value 为 null 时返回 null
     */
    String pretty(Object value);

    /**
     * 解析 JSON 字符串。
     *
     * @param json JSON 字符串
     * @return 解析后的 JSON 节点
     */
    JsonNode node(String json);

    /**
     * 将对象转换为目标类型。
     *
     * @param value 源对象
     * @param type 目标类型
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    <T> T cvt(Object value, Class<T> type);

    /**
     * 将对象转换为目标泛型类型。
     *
     * @param value 源对象
     * @param type 目标泛型类型引用
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    <T> T cvt(Object value, TypeReference<T> type);

    /**
     * 将对象转换为指定的 Jackson JavaType。
     *
     * @param value 源对象
     * @param type 目标 JavaType
     * @return 转换后的对象；当 value 或 type 为 null 时返回 null
     */
    <T> T cvt(Object value, JavaType type);

    /**
     * 将对象转换为 JSON 节点；字符串会按 JSON 内容解析。
     *
     * @param value 源对象
     * @return JSON 节点；当 value 为 null 时返回 null
     */
    JsonNode node(Object value);

    /**
     * 创建 Jackson 参数化类型。
     *
     * @param parametrized 原始类型
     * @param parameterTypes 泛型参数类型
     * @return Jackson JavaType
     */
    JavaType javaType(Class<?> parametrized, Class<?>... parameterTypes);
}
