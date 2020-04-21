package com.xinsite.common.enums.system;

/**
 * 是否可用返回的code
 */
public enum EnableEnum {
    停用(0),
    启用(1);

    // 成员变量
    private int code;

    // 构造方法
    private EnableEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
