package io.github.kongweiguang.json.file;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kongweiguang.json.Json;
import io.github.kongweiguang.json.exception.JsonException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * JSON 文件读取工具，负责把文件内容交给统一的 JSON 解析入口处理。
 *
 * @author kongweiguang
 */
public final class JsonFiles {

    private JsonFiles() {
    }

    /**
     * 按指定编码读取并解析单个 JSON 文件。
     *
     * @param path 文件路径
     * @param charset 文件编码
     * @return 解析后的 JSON 节点
     */
    public static JsonNode read(Path path, Charset charset) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(charset, "charset must not be null");

        try {
            return Json.node(Files.readString(path, charset));
        } catch (IOException e) {
            throw wrap("Failed to read JSON file: " + path, e);
        }
    }

    /**
     * 按指定编码读取 JSON Lines 文件，空行会被跳过。
     *
     * @param path 文件路径
     * @param charset 文件编码
     * @return 每个非空行解析得到的 JSON 节点列表
     */
    public static List<JsonNode> readLines(Path path, Charset charset) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(charset, "charset must not be null");

        try {
            return Files.readAllLines(path, charset)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .map(Json::node)
                    .toList();
        } catch (JsonException e) {
            throw wrap("Failed to read JSON Lines file: " + path, e);
        } catch (IOException e) {
            throw wrap("Failed to read JSON Lines file: " + path, e);
        }
    }

    private static JsonException wrap(String message, Exception e) {
        return new JsonException(message, e);
    }
}
