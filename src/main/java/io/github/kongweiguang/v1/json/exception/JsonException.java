package io.github.kongweiguang.v1.json.exception;

/**
 * kong-json 在解析、序列化、类型转换或文件读取失败时抛出的运行时异常。
 *
 * @author kongweiguang
 */
public class JsonException extends RuntimeException {

    /**
     * 创建带错误消息和根因的 JSON 异常。
     *
     * @param message 错误消息
     * @param cause 根因异常
     */
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建只包含根因的 JSON 异常。
     *
     * @param cause 根因异常
     */
    public JsonException(Throwable cause) {
        super(cause);
    }
}
