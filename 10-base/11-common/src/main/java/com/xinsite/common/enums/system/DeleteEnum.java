package com.xinsite.common.enums.system;

/**
 * 是否删除
 */
public enum DeleteEnum {
    未删除(0),
    已删除(1);

    // 成员变量
    private int code;

    // 构造方法
    private DeleteEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
