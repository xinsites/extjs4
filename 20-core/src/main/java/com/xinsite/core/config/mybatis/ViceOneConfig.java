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

@Configuration
@MapperScan(basePackages = "com.xinsite.mybatis.datasource.viceone", sqlSessionFactoryRef = "viceone_Session"
        , nameGenerator = DruidConfig.SpringBeanNameGenerator.class)
public class ViceOneConfig {
    @Autowired
    @Qualifier("viceone")
    private DataSource viceone;

    @Bean
    public SqlSessionFactory viceone_Session() throws Exception{
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(viceone);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappings/viceone/*.xml"));
        // 将mapper interface相应的mapper.xml 文件映射
        return factoryBean.getObject();
    }


    @Bean
    public SqlSessionTemplate viceoneSqlSessionTemplate() throws Exception{
        return new SqlSessionTemplate(viceone_Session());
    }
}
