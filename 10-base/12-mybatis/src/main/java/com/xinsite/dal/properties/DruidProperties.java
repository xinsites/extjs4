package com.xinsite.dal.properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.xinsite.dal.uitls.Utils_Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * druid 配置属性
 */
@Configuration
public class DruidProperties {
    private static DruidProperties single = null;

    @Value("${spring.datasource.druid.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.druid.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.druid.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.druid.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.maxEvictableIdleTimeMillis}")
    private int maxEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.testOnReturn}")
    private boolean testOnReturn;

    public DruidDataSource dataSource(DruidDataSource datasource) {
        /** 配置初始化大小、最小、最大 */
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);

        /** 配置获取连接等待超时的时间 */
        datasource.setMaxWait(maxWait);

        /** 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        /** 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);

        /**
         * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
         */
        datasource.setValidationQuery(validationQuery);
        /** 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
        datasource.setTestWhileIdle(testWhileIdle);
        /** 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnBorrow(testOnBorrow);
        /** 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnReturn(testOnReturn);
        return datasource;
    }

    /**
     * 获取实例
     */
    public static DruidProperties getInstance() {
        if (single == null) {
            synchronized (DruidProperties.class) {
                if (single == null) {
                    single = new DruidProperties();
                }
            }
        }
        return single;
    }

    public void setInitialSize(String initialSize) {
        this.initialSize = Utils_Value.toInteger(initialSize);
    }

    public void setMinIdle(String minIdle) {
        this.minIdle = Utils_Value.toInteger(minIdle);
    }

    public void setMaxActive(String maxActive) {
        this.maxActive = Utils_Value.toInteger(maxActive);
    }

    public void setMaxWait(String maxWait) {
        this.maxWait = Utils_Value.toInteger(maxWait);
    }

    public void setTimeBetweenEvictionRunsMillis(String timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = Utils_Value.toInteger(timeBetweenEvictionRunsMillis);
    }

    public void setMinEvictableIdleTimeMillis(String minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = Utils_Value.toInteger(minEvictableIdleTimeMillis);
    }

    public void setMaxEvictableIdleTimeMillis(String maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = Utils_Value.toInteger(maxEvictableIdleTimeMillis);
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setTestWhileIdle(String testWhileIdle) {
        if (StringUtils.isEmpty(testWhileIdle))
            this.testWhileIdle = true;
        else
            this.testWhileIdle = Utils_Value.toBoolean(testWhileIdle);
    }

    public void setTestOnBorrow(String testOnBorrow) {
        if (StringUtils.isEmpty(testOnBorrow))
            this.testOnBorrow = false;
        else
            this.testOnBorrow = Utils_Value.toBoolean(testOnBorrow);
    }

    public void setTestOnReturn(String testOnReturn) {
        if (StringUtils.isEmpty(testOnReturn))
            this.testOnReturn = false;
        else
            this.testOnReturn = Utils_Value.toBoolean(testOnReturn);
    }

    public boolean isLoad() {
        return initialSize > 0;
    }

    public int getInitialSize() {
        if (initialSize == 0) return 3;
        return initialSize;
    }

    public int getMinIdle() {
        if (minIdle == 0) return 10;
        return minIdle;
    }

    public int getMaxActive() {
        if (maxActive == 0) return 20;
        return maxActive;
    }

    public int getMaxWait() {
        if (maxWait == 0) return 60000;
        return maxWait;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        if (timeBetweenEvictionRunsMillis == 0) return 60000;
        return timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        if (minEvictableIdleTimeMillis == 0) return 300000;
        return minEvictableIdleTimeMillis;
    }

    public int getMaxEvictableIdleTimeMillis() {
        if (maxEvictableIdleTimeMillis == 0) return 900000;
        return maxEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        if (StringUtils.isEmpty(validationQuery))
            return "select 1 from dual";
        return validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }
}
