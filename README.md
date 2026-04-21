# kong-json 快速入门

`kong-json` 是一个基于 Jackson 的 JSON 工具模块，提供统一的 JSON 构建、解析、序列化、类型转换和文件读取入口。

## 安装

当前模块坐标：

```xml
<dependency>
    <groupId>io.github.kongweiguang</groupId>
    <artifactId>kong-json</artifactId>
    <version>0.6</version>
</dependency>
```

主要入口类：

```java
import io.github.kongweiguang.json.Json;
```

后续示例会用到下面两个简单模型：

```java
public class User {
    private String name;
    private Integer age;
    private String[] hobby;

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String[] getHobby() {
        return hobby;
    }

    public User setHobby(String[] hobby) {
        this.hobby = hobby;
        return this;
    }
}
```

```java
public class Person {
    private String name;
    private Integer age;
    private String[] hobby;

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Person setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String[] getHobby() {
        return hobby;
    }

    public Person setHobby(String[] hobby) {
        this.hobby = hobby;
        return this;
    }
}
```

## 1. 构建 JSON 对象

`Json.object()` 会创建一个链式对象构建器。`put` 会尽量保留原生 JSON 类型，`putString` 会把值强制写成字符串。

```java
import com.fasterxml.jackson.databind.JsonNode;
import io.github.kongweiguang.json.Json;

import java.math.BigDecimal;

JsonNode node = Json.object()
        .put("name", "kong")
        .put("age", 1)
        .put("active", true)
        .put("salary", new BigDecimal("88.50"))
        .put("nullable", null)
        .array("tags", tags -> tags.add("java").add(8))
        .putString("literalNumber", 1)
        .toNode();

System.out.println(node.get("age").isInt());          // true
System.out.println(node.get("literalNumber").asText()); // 1
```

也可以直接输出 JSON 字符串：

```java
String json = Json.object()
        .put("name", "kong")
        .put("age", 1)
        .toJson();

// {"name":"kong","age":1}
```

## 2. 构建 JSON 数组

`Json.array()` 用于构建数组，支持普通值、对象、嵌套数组和批量追加。

```java
JsonNode array = Json.array()
        .add(1)
        .add(true)
        .add(null)
        .array(items -> items.add("nested").add(false))
        .object(object -> object.put("name", "kong"))
        .addString(66)
        .toNode();

System.out.println(array.get(0).isInt());    // true
System.out.println(array.get(5).asText());   // 66
```

## 3. 序列化和格式化输出

`Json.str` 输出紧凑 JSON，`Json.strPretty` 输出格式化 JSON。

```java
User user = new User()
        .setName("kong")
        .setAge(1)
        .setHobby(new String[]{"j", "n"});

String json = Json.str(user);
// {"name":"kong","age":1,"hobby":["j","n"]}

String pretty = Json.strPretty(user);
System.out.println(pretty);
```

注意：传入普通字符串时，会按照 JSON 字符串规则序列化：

```java
Json.str("kong"); // "\"kong\""
```

## 4. 解析 JSON 字符串

默认解析器使用严格 JSON 语法。

```java
JsonNode node = Json.node("{\"name\":\"kong\"}");
System.out.println(node.get("name").asText()); // kong
```

下面这种非标准 JSON 默认会抛出 `JsonException`：

```java
Json.node("{name:'kong'}");
```

如果需要兼容常见宽松写法，可以显式切换为宽松解析器：

```java
Json.configure(Json.lenientMapper());

JsonNode node = Json.node("{name:'kong'}");
System.out.println(node.get("name").asText()); // kong
```

## 5. 类型转换

`Json.cvt` 可以把 JSON 字符串、`JsonNode` 或普通对象转换成目标 Java 类型。

```java
String json = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";

User user = Json.cvt(json, User.class);
JsonNode node = Json.node(json);
Person person = Json.cvt(node, Person.class);

System.out.println(user.getName());   // kong
System.out.println(person.getName()); // kong
```

泛型类型可以使用 `TypeReference`：

```java
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

Map<String, List<String>> map = Json.map(
        "{\"name\":[\"j\",\"n\"],\"age\":[\"1\"]}",
        new TypeReference<Map<String, List<String>>>() {
        }
);

System.out.println(map.get("name")); // [j, n]
```

列表转换有快捷方法：

```java
List<User> users = Json.list(
        "[{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}]",
        User.class
);
```

## 6. 读取 JSON 文件

默认按 UTF-8 读取：

```java
import java.nio.file.Path;

JsonNode node = Json.read(Path.of("sample.json"));
```

指定文件编码：

```java
import java.nio.charset.Charset;

JsonNode node = Json.read(Path.of("sample-gbk.json"), Charset.forName("GBK"));
```

读取 JSON Lines 文件时，每个非空行都会被解析成一个 `JsonNode`：

```java
List<JsonNode> lines = Json.readLines(Path.of("sample.jsonl"));
```

文件读取失败或 JSON 解析失败时，会统一包装为 `JsonException`。

## 7. 空值行为

常用转换方法对空输入保持一致：

```java
Json.str(null);                       // null
Json.strPretty(null);                 // null
Json.cvt(null, User.class);           // null
Json.list(null, User.class);          // null
Json.map(null, String.class, Object.class); // null
```

## 8. 运行测试

在项目根目录执行：

```bash
mvn test
```

测试用例位于：

```text
src/test/java/io/github/kongweiguang/json/JsonTest.java
```
