package io.github.kongweiguang.v1.json;

import java.util.Arrays;

/**
 * JSON 测试使用的人员模型。
 *
 * @author kongweiguang
 */
public class Person {

    private String name;
    private Integer age;
    private String[] hobby;

    /**
     * 获取姓名。
     *
     * @return 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名。
     *
     * @param name 姓名
     * @return 当前人员对象
     */
    public Person setName(String name) {
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
     * @return 当前人员对象
     */
    public Person setAge(Integer age) {
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
     * @return 当前人员对象
     */
    public Person setHobby(String[] hobby) {
        this.hobby = hobby;
        return this;
    }


    @Override
    public String toString() {
        return "Person{" +
               "name='" + name + '\'' +
               ", age=" + age +
               ", hobby=" + Arrays.toString(hobby) +
               '}';
    }
}
