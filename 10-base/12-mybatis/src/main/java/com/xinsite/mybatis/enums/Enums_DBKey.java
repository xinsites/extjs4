package com.xinsite.mybatis.enums;

/**
 * 多重数据源类型, 数据源名称只能是小写字母
 */
public enum Enums_DBKey {
    /**
     * 主库:mysql
     */
    master,

    /**
     * 从库_01:mysql
     */
    viceone,

    /**
     * 从库_03:sqlserver
     */
    sqlserver,
    /**
     * 从库_04:oracle
     */
    oracle,
}
