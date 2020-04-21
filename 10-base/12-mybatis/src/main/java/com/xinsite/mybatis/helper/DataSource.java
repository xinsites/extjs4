package com.xinsite.mybatis.helper;

import com.xinsite.dal.datasource.DataSourceHolder;
import com.xinsite.mybatis.enums.Enums_DBKey;

public class DataSource {
    /**
     * 设置数据源
     */
    public static void setDataSource(Enums_DBKey dbKey) {
        DataSourceHolder.setDataSourceType(dbKey.toString());
    }

    /**
     * 设置数据源
     */
    public static void setDataSource(String dbKey) {
        DataSourceHolder.setDataSourceType(dbKey);
    }

    /**
     * 清空数据源，自动变成主数据源
     */
    public static void clearDataSource() {
        DataSourceHolder.clearDataSourceType();
    }

}
