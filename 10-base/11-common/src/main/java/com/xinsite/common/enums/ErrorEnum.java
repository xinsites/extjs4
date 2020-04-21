package com.xinsite.common.enums;

/**
 * web请求返回的code
 */
public enum ErrorEnum {
    Session超时(1000),
    重复登录(1001),
    查询表未找到(1002),
    导出超出(1003),
    权限不足(1004),
    空指针错误(1099);

    // 成员变量
    private int code;

    // 构造方法
    private ErrorEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
