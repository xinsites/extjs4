package com.xinsite;

import com.xinsite.core.config.DruidConfig;
import com.xinsite.core.shiro.ExceResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * SpingBoot(Spring、SpringMVC) + Mybatis
 * 入口类，启动内置Tomcat服务器，初始化等。
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})  //禁用数据源默认自动配置
@ComponentScan(nameGenerator = DruidConfig.SpringBeanNameGenerator.class)
public class XinSiteApplication extends SpringBootServletInitializer {

    /**
     * 打包成war
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        //builder.properties("spring.config.name:app");
        return builder.sources(XinSiteApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(XinSiteApplication.class, args);
    }

    @Bean
    public ExceResolver myExceptionResolver() {
        return new ExceResolver();  // 注册统一异常处理bean
    }
}
