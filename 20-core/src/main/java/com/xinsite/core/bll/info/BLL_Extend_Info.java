package com.xinsite.core.bll.info;

import com.google.gson.JsonArray;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BLL_Extend_Info {

    /**
     * 保存信息
     */
    public static void saveInfo(Map ht, String table_name, int table_id) throws Exception {
        ht.put("table_name", table_name);
        ht.put("table_id", table_id);
        ht.put("create_time", DateUtils.getDateTime());
        DBFunction.insertByTbName(ht, "tb_extend_info");
    }

    /**
     * 获取特殊字段扩展记录值
     */
    public static <T> T getFieldValue(String table_name, int table_id, String field_extend, T defaultValue) {
        String sql = "select field_value from tb_extend_info where table_name=@table_name and field_extend=@field_extend and table_id=@table_id";
        Object obj = DBFunction.executeScalar(sql,
                new DBParameter("@table_name", "=", table_name)
                , new DBParameter("@field_extend", "=", field_extend)
                , new DBParameter("@table_id", "=", table_id));
        String value = obj == null ? "" : obj.toString();
        if (StringUtils.isEmpty(value)) return defaultValue;
        try {
            return ValueUtils.tryParse(value, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 获取特殊字段扩展记录值
     */
    public static <T> T getFieldValue(List<DBParameter> where, T defaultValue) {
        String sql = "select field_value from tb_extend_info where 1=1" + DBParameter.getSqlWhere(where);
        Object obj = DBFunction.executeScalar(sql, DBParameter.getParameter(where));
        String value = obj == null ? "" : obj.toString();
        if (StringUtils.isEmpty(value)) return defaultValue;
        try {
            return ValueUtils.tryParse(value, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 扩展记录值是否存在
     */
    public static boolean isExist(String table_name, int table_id, String field_extend, Object field_value) {
        String sql = "select count(1) from tb_extend_info where table_name=@table_name " +
                "and field_extend=@field_extend and table_id=@table_id and field_value=@field_value";
        List<DBParameter> where = new ArrayList<>();
        where.add(new DBParameter("@table_name", "=", table_name));
        where.add(new DBParameter("@table_id", "=", table_id));
        where.add(new DBParameter("@field_extend", "=", field_extend));
        where.add(new DBParameter("@field_value", "=", field_value));
        return NumberUtils.strToInt(DBFunction.executeScalar(sql, DBParameter.getParameter(where))) > 0;
    }

    /**
     * 扩展记录值是否存在
     */
    public static boolean isExist(List<DBParameter> where) {
        String sql = "select count(1) from tb_extend_info where 1=1" + DBParameter.getSqlWhere(where);
        return NumberUtils.strToInt(DBFunction.executeScalar(sql, DBParameter.getParameter(where))) > 0;
    }

    /**
     * 获取登录用户设置的固定标签
     */
    public static JsonArray getExtendValues(String table_name, int table_id, String field_extend, String... order_by) throws Exception {
        String sql = "select field_value,create_time from tb_extend_info where table_name=@table_name and field_extend=@field_extend and table_id=@table_id ";
        List<DBParameter> where = new ArrayList<>();
        where.add(new DBParameter("@table_name", "=", table_name));
        where.add(new DBParameter("@table_id", "=", table_id));
        where.add(new DBParameter("@field_extend", "=", field_extend));
        if (order_by.length > 0) {
            if (order_by[0].toLowerCase().indexOf("order by") >= 0)
                sql += order_by[0];
            else sql += "order by " + order_by[0];
        }
        return DBFunction.executeJsonArray(sql, DBParameter.getParameter(where));
    }

    /**
     * 获取登录用户设置的固定标签
     */
    public static <T> List<T> getListValues(String table_name, int table_id, String field_extend, T defaultValue, String... order_by) throws Exception {
        String sql = "select field_value,create_time from tb_extend_info where table_name=@table_name and field_extend=@field_extend and table_id=@table_id ";
        List<DBParameter> where = new ArrayList<>();
        where.add(new DBParameter("@table_name", "=", table_name));
        where.add(new DBParameter("@table_id", "=", table_id));
        where.add(new DBParameter("@field_extend", "=", field_extend));
        if (order_by.length > 0) {
            if (order_by[0].toLowerCase().indexOf("order by") >= 0)
                sql += order_by[0];
            else sql += "order by " + order_by[0];
        }
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(where));

        return ArrayUtils.listByField(array, "field_value", defaultValue);
    }

    /**
     * 删除记录
     */
    public static boolean delete(String table_name, int table_id, String field_extend, Object field_value) throws Exception {
        List<DBParameter> where = new ArrayList<>();
        where.add(new DBParameter("@table_name", "=", table_name));
        where.add(new DBParameter("@table_id", "=", table_id));
        where.add(new DBParameter("@field_extend", "=", field_extend));
        where.add(new DBParameter("@field_value", "=", field_value));
        return DBFunction.deleteByTbName("tb_extend_info", DBParameter.getParameter(where));
    }

    /**
     * 删除记录
     */
    public static boolean delete(String table_name, int table_id) throws Exception {
        List<DBParameter> where = new ArrayList<>();
        where.add(new DBParameter("@table_name", "=", table_name));
        where.add(new DBParameter("@table_id", "=", table_id));
        return DBFunction.deleteByTbName("tb_extend_info", DBParameter.getParameter(where));
    }

}
