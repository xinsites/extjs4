package com.xinsite.core.enums;

/**
 * 登录帐号控制失败类型
 */
public enum KickoutEnum {
    重复登录(1),
    Session丢失(2),
    重新登录失败(3);

    // 成员变量
    private int code;

    // 构造方法
    private KickoutEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
