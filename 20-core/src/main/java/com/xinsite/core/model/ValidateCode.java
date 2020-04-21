package com.xinsite.core.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 有时效的验证码
 * create by zhangxiaxin
 */
@Data
public class ValidateCode {
    private String code;

    private LocalDateTime expireTime;

    public ValidateCode(String code) {
        this.code = code;
        this.expireTime = null;
    }

    public ValidateCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public boolean isExpried() {
        if (expireTime == null) return false;
        return LocalDateTime.now().isAfter(expireTime);
    }

    public String getCode() {
        return code;
    }
}
