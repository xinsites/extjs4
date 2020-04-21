package com.xinsite.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 配置自动启动浏览器
 */

@Component
public class MyCommandRunner implements CommandLineRunner {
    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 首页地址
     */
    @Value("${shiro.user.loginUrl}")
    private String indexUrl;


    @Override
    public void run(String... args) {
        try {
            Runtime.getRuntime().exec("cmd   /c   start   http://localhost:" + port + contextPath + indexUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}