package com.xinsite.dal.datasource;

import com.xinsite.dal.uitls.Utils_Props;
import com.xinsite.dal.uitls.Utils_Yml;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换处理
 */
public class DataSourceHolder {
    public static final Logger log = LoggerFactory.getLogger(DataSourceHolder.class);

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXT_IDEN = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void setDataSourceType(String dsType) {
        //log.info("切换到{}数据源", dsType);
        CONTEXT_IDEN.set(dsType);
    }

    /**
     * 获得数据源的变量
     */
    public static String getDataSourceType() {
        return CONTEXT_IDEN.get();
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSourceType() {
        //log.info("清空数据源，切换到主数据库");
        CONTEXT_IDEN.remove();
    }

    /**
     * 获得数据源的变量
     */
    public static String getDBKey() {
        String db_key = DataSourceHolder.getDataSourceType();
        if (StringUtils.isEmpty(db_key)) db_key = DataSourceHolder.getMasterKey();
        return db_key;
    }

    /**
     * 获得主数据源的变量
     */
    public static String getMasterKey() {
        String master_nama = Utils_Yml.getValue("config.master_database");
        if (StringUtils.isEmpty(master_nama)) master_nama = "master";
        return master_nama;
    }
}
