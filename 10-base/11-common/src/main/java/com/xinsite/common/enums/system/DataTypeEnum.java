package com.xinsite.common.enums.system;

/**
 * 数据源类型_枚举
 */
public enum DataTypeEnum {
    编码表("code"),
    系统数据源("datasource");

    // 成员变量
    private String value;

    // 构造方法
    private DataTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
