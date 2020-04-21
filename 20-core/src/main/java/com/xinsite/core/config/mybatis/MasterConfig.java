package com.xinsite.core.config.mybatis;

import com.xinsite.core.config.DruidConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 将master DataSource映射相应的mapper interface 类
 */
@Configuration
@MapperScan(basePackages = "com.xinsite.mybatis.datasource.master", sqlSessionFactoryRef = "master_Session"
        , nameGenerator = DruidConfig.SpringBeanNameGenerator.class)
public class MasterConfig {
    @Autowired
    @Qualifier("master")
    private DataSource master;

    @Bean
    public SqlSessionFactory master_Session() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(master);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappings/master/*.xml"));
        // 将mapper interface相应的mapper.xml 文件映射
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate masterSqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(master_Session());
    }
}
