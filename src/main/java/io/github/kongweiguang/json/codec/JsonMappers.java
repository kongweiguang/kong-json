package io.github.kongweiguang.json.codec;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.USE_STD_BEAN_NAMING;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

/**
 * Jackson JsonMapper 工厂，集中维护 kong-json 默认的序列化和解析配置。
 *
 * @author kongweiguang
 */
public final class JsonMappers {

    private JsonMappers() {
    }

    /**
     * 创建严格模式的默认 JsonMapper。
     *
     * @return 严格模式 JsonMapper
     */
    public static JsonMapper defaultMapper() {
        return baseMapperBuilder().build();
    }

    /**
     * 创建宽松模式 JsonMapper，允许常见非标准 JSON 输入。
     *
     * @return 宽松模式 JsonMapper
     */
    public static JsonMapper lenientMapper() {
        return baseMapperBuilder()
                .configure(ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
                .configure(ALLOW_SINGLE_QUOTES.mappedFeature(), true)
                .configure(ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true)
                .configure(ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
                .build();
    }

    /**
     * 创建基础 JsonMapper 构建器，统一注册通用 Jackson 配置。
     *
     * @return 基础 JsonMapper 构建器
     */
    private static JsonMapper.Builder baseMapperBuilder() {
        return JsonMapper.builder()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(FAIL_ON_EMPTY_BEANS, false)
                .serializationInclusion(NON_NULL)
                .propertyNamingStrategy(LOWER_CAMEL_CASE)
                .enable(USE_STD_BEAN_NAMING)
                .addModule(new JavaTimeModule());
    }
}
