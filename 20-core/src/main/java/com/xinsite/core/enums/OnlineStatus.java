package com.xinsite.core.enums;

/**
 * 用户会话
 */
public enum OnlineStatus {
    /**
     * 用户状态
     */
    在线("on_line"), 离线("off_line");

    // 成员变量
    private String value;

    // 构造方法
    private OnlineStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
