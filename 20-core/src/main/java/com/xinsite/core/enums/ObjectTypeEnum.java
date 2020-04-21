package com.xinsite.core.enums;

/**
 * 生成对象类型
 */
public enum ObjectTypeEnum {
    项目目录("list"),
    普通对象("form"),
    查询对象("find"),
    流程对象("flow"),
    审批对象("trial");

    // 成员变量
    private String value;

    // 构造方法
    private ObjectTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
