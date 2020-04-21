package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

/**
 * 高级查询相关
 * create by zhangxiaxin
 */
public class BLL_Search {

    /**
     * 根据table_key获取数据库列表
     */
    public static JsonArray getSearchTable(String table_key) throws Exception {
        String sql = "select b.tid id,b.table_explain name from tb_gen_table b\n" +
                "        where b.table_key=@table_key order by b.pid,b.serialcode";
        return DBFunction.executeJsonArray(sql, new DBParameter("@table_key", "=", table_key));
    }

    /**
     * 根据oid获取数据库列表
     */
    public static JsonArray getSearchTable(int oid) throws Exception {
        String sql = "select b.tid id,b.table_explain name from tb_gen_table b\n" +
                "        where b.oid=@oid order by b.pid,b.serialcode";
        return DBFunction.executeJsonArray(sql, new DBParameter("@oid", "=", oid));
    }

    /**
     * 获取指定输入框类型的查询操作符
     */
    public static JsonArray getSearchOperator(String xtype) throws Exception {
        String sql = "select operator id,remark name from view_gen_operator where field_type=@field_type order by serialcode";
        return DBFunction.executeJsonArray(sql, new DBParameter("@field_type", "=", xtype));
    }

    /**
     * 获取所有的输入框类型
     */
    public static JsonArray getSearchOperator() throws Exception {
        String sql = "select concat_ws('_',field_type,value_type) field_type,'' arrays from view_gen_operator group by field_type,value_type order by 1";
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 获取所有输入框类型的查询操作符
     */
    public static JsonArray getSearchOperatorAll() throws Exception {
        String sql = "select concat_ws('_',field_type,value_type) field_type,operator id,remark name,serialcode from view_gen_operator order by 1,serialcode";
        return DBFunction.executeJsonArray(sql);
    }
}
