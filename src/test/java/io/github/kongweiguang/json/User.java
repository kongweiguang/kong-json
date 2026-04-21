package io.github.kongweiguang.json;

import java.util.Arrays;

/**
 * JSON 测试使用的用户模型。
 *
 * @author kongweiguang
 */
public class User {

    private String name;
    private Integer age;
    private String[] hobby;

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户名。
     *
     * @param name 用户名
     * @return 当前用户对象
     */
    public User setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取年龄。
     *
     * @return 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄。
     *
     * @param age 年龄
     * @return 当前用户对象
     */
    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    /**
     * 获取兴趣列表。
     *
     * @return 兴趣列表
     */
    public String[] getHobby() {
        return hobby;
    }

    /**
     * 设置兴趣列表。
     *
     * @param hobby 兴趣列表
     * @return 当前用户对象
     */
    public User setHobby(String[] hobby) {
        this.hobby = hobby;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
               "name='" + name + '\'' +
               ", age=" + age +
               ", hobby=" + Arrays.toString(hobby) +
               '}';
    }
}
