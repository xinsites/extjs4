package com.xinsite.common.uitls.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 说明 不依赖servlet context获取Spring Application Contexts
 */

@Component
public class ContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext contex) throws BeansException {
        ContextUtils.context = contex;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(String beanId, Class<T> c) {
        if (context == null) return null;
        return (T) context.getBean(beanId, c);
    }

    /**
     * 获取对象
     *
     * @param <T>
     * @return T
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        if (context == null) return null;
        return context.getBean(requiredType);
    }
}

