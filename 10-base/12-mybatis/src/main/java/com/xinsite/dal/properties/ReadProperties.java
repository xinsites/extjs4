package com.xinsite.dal.properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.xinsite.dal.datasource.DataSourceHolder;
import com.xinsite.dal.uitls.Utils_Props;
import com.xinsite.dal.uitls.Utils_Value;
import com.xinsite.dal.uitls.Utils_Yml;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 以文件形式读取数据源，通过Application启动时
 */
public class ReadProperties {

    private static Map<String, DruidDataSource> map = new HashMap<>();

    public static Connection getConnection() throws Exception {
        DruidDataSource source = getDataSource();

        if (source == null) throw new RuntimeException("获取数据源连接池失败！");
        return source.getConnection();
    }

    public static DruidDataSource getDataSource() {
        String db_key = DataSourceHolder.getDBKey();
        if (!map.containsKey(db_key)) addDataSource(db_key);

        DruidDataSource source = map.get(db_key);
        if (!source.isEnable()) source = map.get(DataSourceHolder.getMasterKey());
        return source;
    }

    public static Properties getProperties() {
        Properties conf = Utils_Yml.getYmlByFileName("config/application-druid.yml"); //默认配置
        if (conf == null) conf = Utils_Yml.getYmlByFileName("druid.yml");  //第二配置
        if (conf == null) conf = Utils_Props.loadProps("druid.properties"); //第三配置
        return conf;
    }

    public static void addDataSource(String db_key) {
        if (map.containsKey(db_key)) return;
        Properties conf = ReadProperties.getProperties();
        if (conf != null) {
            try {
                DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
                String prex_key = String.format("spring.datasource.druid.%s.", db_key);
                //可以不配置，阿里的数据库连接池会通过url自动搜寻
                //dataSource.setDriverClassName(conf.getProperty(prex_key + "driverClassName"));
                String enabled_str = conf.getProperty(prex_key + "enabled");
                if (StringUtils.isEmpty(enabled_str)) enabled_str = "true";
                dataSource.setEnable(Utils_Value.toBoolean(enabled_str));

                dataSource.setUrl(conf.getProperty(prex_key + "url"));
                dataSource.setUsername(conf.getProperty(prex_key + "username"));
                dataSource.setPassword(conf.getProperty(prex_key + "password"));

                ReadProperties.setDataSource(dataSource, conf);
                map.put(db_key, dataSource);
            } catch (Exception e) {
                System.out.println("新增数据库创建连接池失败！");
            }
        }
    }

    public static void setDataSource(DruidDataSource dataSource, Properties conf) {
        DruidProperties properties = DruidProperties.getInstance();
        dataSource.setDbType(conf.getProperty("spring.datasource.type"));
        //dataSource.setDriverClassName(conf.getProperty("spring.datasource.driverClassName"));
        if (!properties.isLoad()) {
            properties.setInitialSize(conf.getProperty("spring.datasource.druid.initialSize"));
            properties.setMinIdle(conf.getProperty("spring.datasource.druid.minIdle"));
            properties.setMaxActive(conf.getProperty("spring.datasource.druid.maxActive"));
            properties.setMaxWait(conf.getProperty("spring.datasource.druid.maxWait"));
            properties.setTimeBetweenEvictionRunsMillis(conf.getProperty("spring.datasource.druid.timeBetweenEvictionRunsMillis"));
            properties.setMinEvictableIdleTimeMillis(conf.getProperty("spring.datasource.druid.minEvictableIdleTimeMillis"));
            properties.setMaxEvictableIdleTimeMillis(conf.getProperty("spring.datasource.druid.maxEvictableIdleTimeMillis"));
            properties.setValidationQuery(conf.getProperty("spring.datasource.druid.validationQuery"));
            properties.setTestWhileIdle(conf.getProperty("spring.datasource.druid.testWhileIdle"));
            properties.setTestOnBorrow(conf.getProperty("spring.datasource.druid.testOnBorrow"));
            properties.setTestOnReturn(conf.getProperty("spring.datasource.druid.testOnReturn"));
        }
        properties.dataSource(dataSource);
    }


//    public static void addDataBaseIn(DataBase db) {
//        Properties prop = new Properties();
////        if ("Mysql".equalsIgnoreCase(db.getDb_key())) {
////            prop.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
////            prop.setProperty("validationQuery", "select 1 from dual");
////            prop.setProperty("connectionProperties", "useUnicode=true;characterEncoding=UTF8;serverTimezone=UTC;useSSL=false");
////        } else if ("Oracle".equalsIgnoreCase(db.getType())) {
////            prop.setProperty("driverClassName", "oracle.jdbc.driver.OracleDriver");
////            prop.setProperty("validationQuery", "select 1 from dual");
////        } else if ("Hive".equalsIgnoreCase(db.getType())) {
////            prop.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
////            prop.setProperty("validationQuery", "select 1");
////        } else {
////            throw new RuntimeException("连接池目前只支持Mysql、Oracle、Hive三种数据库类型！");
////        }
//        prop.setProperty("url", db.getUrl());
//        prop.setProperty("username", db.getUserName());
//        prop.setProperty("password", db.getPasswd());
//        prop.setProperty("initialSize", db.getInit() + "");
//        prop.setProperty("maxActive", db.getMaxActive() + "");
//        prop.setProperty("minIdle", db.getMinIdle() + "");
//        prop.setProperty("maxWait", "60000");
//        prop.setProperty("filters", "stat");
//        prop.setProperty("timeBetweenEvictionRunsMillis", "35000");
//        prop.setProperty("minEvictableIdleTimeMillis", "30000");
//        prop.setProperty("testWhileIdle", "true");
//        prop.setProperty("testOnBorrow", "false");
//        prop.setProperty("testOnReturn", "false");
//        prop.setProperty("poolPreparedStatements", "false");
//        prop.setProperty("maxPoolPreparedStatementPerConnectionSize", "200");
////        prop.setProperty("removeAbandoned", "true");
//
//    }
}