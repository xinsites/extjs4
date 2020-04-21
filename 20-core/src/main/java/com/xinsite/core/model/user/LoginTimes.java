package com.xinsite.core.model.user;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户登录错误次数记录，配合Ehcache缓存使用
 * create by zhangxiaxin
 */
public class LoginTimes {
    private AtomicInteger errorCount;  //错误次数
    private Date lastLoginTime;        //最后一次登录时间

    public LoginTimes() {
        errorCount = new AtomicInteger(0);
        lastLoginTime = new Date();
    }

    public AtomicInteger getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(AtomicInteger errorCount) {
        this.errorCount = errorCount;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
