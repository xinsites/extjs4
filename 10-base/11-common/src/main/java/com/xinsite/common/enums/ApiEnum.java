package com.xinsite.common.enums;

/**
 * API接口请求返回的code
 * 0：请求成功返回值
 * 1000-1999：参数错误
 * 2000-2999：用户错误
 * 3000-3999：接口异常
 * 4000-4999：业务错误
 */
public enum ApiEnum {
    信息成功返回(0),
    通用错误(1),
    参数签名错误(1000),
    参数不足(1001),
    用户已停用(2000),
    服务器异常(3000),
    金额不足(4000);

    // 成员变量
    private int code;

    // 构造方法
    private ApiEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
