package com.xinsite.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.xinsite.dal.datasource.DynamicDataSource;
import com.xinsite.mybatis.enums.Enums_DBKey;
import com.xinsite.dal.properties.DruidProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.servlet.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * druid 配置多数据源
 * 扫描包(Mybatis对应的mapper包)
 */
@Configuration
public class DruidConfig {
    @Autowired
    private DruidProperties properties;

    @Bean(name = "master")
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return properties.dataSource(dataSource);
    }

    @Bean(name = "viceone")
    @ConfigurationProperties("spring.datasource.druid.viceone")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.viceone", name = "enabled", havingValue = "true")
    public DataSource vice01DataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return properties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.sqlserver")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.sqlserver", name = "enabled", havingValue = "true")
    public DataSource vice02DataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return properties.dataSource(dataSource);
    }

    @Primary
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(Enums_DBKey.master.name(), masterDataSource());
        targetDataSources.put(Enums_DBKey.viceone.name(), vice01DataSource());
        targetDataSources.put(Enums_DBKey.sqlserver.name(), vice02DataSource());
        return new DynamicDataSource(masterDataSource(), targetDataSources);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dynamicDataSource());
    }

    //注解创建bean时,自定义bean名称，解决Mybatis多数据源出现的相同mapper名称
    public static class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            return definition.getBeanClassName();
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties) {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter() {
            @Override
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }

            @Override
            public void destroy() {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
