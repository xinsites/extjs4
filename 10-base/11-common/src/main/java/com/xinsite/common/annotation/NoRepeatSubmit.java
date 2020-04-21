package com.xinsite.common.annotation;

import java.lang.annotation.*;

/**
 * 防止重复提交标记注解
 */
@Target(ElementType.METHOD) // 作用到方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface NoRepeatSubmit {

    /**
     * 获取信息或者新增时，保存Token，@NoRepeatSubmit(token=true)
     */
    boolean token() default false;

    /**
     * 保存信息时，移除Token，@NoRepeatSubmit(submit=true)
     */
    boolean submit() default false;

    /**
     * 以提交参数验证是否重复提交，@NoRepeatSubmit(params=true)
     */
    boolean params() default false;

    /**
     * 设置请求锁定时间，默认5秒
     */
    int lockTime() default 10;
}