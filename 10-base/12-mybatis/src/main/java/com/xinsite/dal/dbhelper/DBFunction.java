package com.xinsite.dal.dbhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.datasource.DataSourceHolder;
import com.xinsite.dal.uitls.Utils_Gson;
import com.xinsite.dal.uitls.Utils_String;
import com.xinsite.dal.uitls.Utils_Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DBFunction {

    /**
     * 获取表的主键最大值
     */
    public static long executeScalar(String tableName, String primaryKey) throws Exception {
        String sql = String.format("select max(%s) id from %s", primaryKey, tableName);
        return Utils_Value.tryParse(DruidUtils.executeScalar(sql), 0L);
    }

    /**
     * 获取相关表字段名称
     */
    public static String getFieldNameById(String field, String table, String primaryId, int id) {
        if (id < 1) return Utils_String.EMPTY;
        String QuerySql = "select %s from %s where %s=" + id;
        String obj = DBFunction.executeScalar(String.format(QuerySql, field, table, primaryId));
        return obj == null ? "" : obj;
    }

    /**
     * 获取表的主键最大值
     */
    public static long getMaxId(String tableName, String primaryKey) throws Exception {
        String sql = String.format("select max(%s) id from %s", primaryKey, tableName);
        return Utils_Value.tryParse(DruidUtils.executeScalar(sql), 0L);
    }

    /**
     * 获取表的主键最大值
     */
    public static String getSqlCount(String QuerySql, DBParameter... params) throws Exception {
        return DruidUtils.executeScalar(QuerySql, params);
    }

    /**
     * 获取表的记录数
     */
    public static long getTableCount(String QuerySql, DBParameter... params) {
        try {
            return Utils_Value.tryParse(DruidUtils.executeScalar(QuerySql, params), 0L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取表值
     */
    public static String executeScalar(String QuerySql, DBParameter... params) {
        try {
            return DruidUtils.executeScalar(QuerySql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 该记录是否存在
     *
     * @param where:查询条件根据DBParameter拼接
     */
    public static boolean isExistsByTable(String tableName, List<DBParameter> where) throws Exception {
        if (where == null || where.size() == 0) return false;
        String sql = String.format("select count(1) from %s where 1=1%s", tableName, DBParameter.getSqlWhere(where));
        String count = DruidUtils.executeScalar(sql, DBParameter.getParameter(where));
        return Utils_Value.tryParse(count, 0L) > 0;
    }

    /**
     * 该记录是否存在
     *
     * @param where:已经拼接好，可以带参数（拼接可以and、or混排）
     * @param params:查询参数（操作符作废，已经在where中）
     */
    public static boolean isExistsByTable(String tableName, String where, DBParameter... params) throws Exception {
        if (Utils_String.isEmpty(where)) where = "1=1";
        String sql = String.format("select count(1) from %s where %s", tableName, where);
        String count = DruidUtils.executeScalar(sql, params);
        return Utils_Value.tryParse(count, 0L) > 0;
    }

    /**
     * 该记录是否存在
     *
     * @param params:查询条件根据DBParameter拼接
     */
    public static boolean isExistsByTable(String tableName, DBParameter... params) throws Exception {
        String sql = String.format("select count(1) from %s where 1=1%s", tableName, DBParameter.getSqlWhere(params));
        String count = DruidUtils.executeScalar(sql, params);
        return Utils_Value.tryParse(count, 0L) > 0;
    }

    /**
     * 根据表名，新增数据(主键自增，并返回新增的主键值)
     */
    public static <T> T insertTable(Map<String, Object> params, String tableName, T id) throws Exception {
        if (params == null || params.size() == 0) return Utils_Value.tryParse(0, id);
        String fields = "";
        String values = "";
        for (String key : params.keySet()) {
            if (!fields.equals("")) {
                fields += ",";
                values += ",";
            }
            fields += key;
            values += "@" + key;
        }

        String strSql = String.format("insert into %s (%s) values(%s);", tableName, fields, values);
        return DruidUtils.insertTable(strSql, id, DBParameter.getParameter(params));
    }

    /**
     * 根据表名，新增数据(主键自增，并返回新增的主键值)
     */
    public static int insertByTbName(Map<String, Object> params, String tableName) throws Exception {
        return DBFunction.insertTable(params, tableName, 0);
    }

    /**
     * 根据表名及主键值，删除数据
     */
    public static boolean deleteByTbName(String tableName, String id, Object idVal) throws Exception {
        String strSql = String.format("delete from %s where %s=@%s", tableName, id, id);
        return DruidUtils.executeNonQuery(strSql, new DBParameter("@" + id, idVal)) > 0;
    }

    /**
     * 根据表名及主键值，删除数据
     */
    public static boolean deleteByTbName(String tableName, DBParameter... where) throws Exception {
        if (where == null || where.length == 0) return false;
        String strSql = String.format("delete from %s where 1=1%s", tableName, DBParameter.getSqlWhere(where));
        return DruidUtils.executeNonQuery(strSql, where) > 0;
    }

    /**
     * 根据表名及主键值，删除数据
     */
    public static boolean deleteByTbNameIds(String TbName, String id, String idsVal) throws Exception {
        idsVal = Utils_String.joinAsFilter(idsVal);
        String strSql = String.format("delete from %s where %s in(%s)", TbName, id, idsVal);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 根据表名及主键值，删除数据
     */
    public static int executeNonQuery(String sql, DBParameter... params) throws Exception {
        return DruidUtils.executeNonQuery(sql, params);
    }

    /**
     * 根据表名，修改数据
     */
    public static boolean updateByTbName(Map<String, Object> params, String tableName, String where) throws Exception {
        if (params == null || params.size() == 0) return false;
        String sets = "";
        for (String key : params.keySet()) {
            if (!sets.equals("")) {
                sets += ",";
            }
            sets += String.format("%s=@%s", key, key);
        }
        String strSql = String.format("update %s set %s where %s", tableName, sets, where);
        int count = DruidUtils.executeNonQuery(strSql, DBParameter.getParameter(params));
        return count > 0;
    }

    /**
     * 根据表名，修改数据
     */
    public static boolean updateByTbName(Map<String, Object> params, String tableName, DBParameter... where) throws Exception {
        return updateByTbName(params, tableName, Arrays.asList(where));
    }

    /**
     * 根据表名，修改数据
     */
    public static boolean updateByTbName(Map<String, Object> params, String tableName, List<DBParameter> where) throws Exception {
        if (params == null || params.size() == 0) return false;
        String sets = "";
        for (String key : params.keySet()) {
            String field = key.replace("[", "").replace("]", "");
            if (!sets.equals("")) {
                sets += ",";
            }
            sets += String.format("%s=@%s", field, field);
        }
        //where加前缀是避免与params参数冲突
        String strSql = String.format("update %s set %s where 1=1%s", tableName, sets, DBParameter.getSqlWhere(where, "w_"));
        if (where != null) {
            for (DBParameter parameter : where) {
                params.put("w_" + parameter.getKey().replace("@", ""), parameter.getValue());
            }
        }
        int count = DruidUtils.executeNonQuery(strSql, DBParameter.getParameter(params));
        if (where != null) {
            for (DBParameter parameter : where) {
                params.remove("w_" + parameter.getKey().replace("@", ""));
            }
        }
        return count > 0;
    }

    /**
     * 根据Sql语句(sql的where没有参数)，获取转换成JsonArray数组
     */
    public static JsonArray executeJsonArray(String sql, DBParameter... where) throws Exception {
        return DBFunction.executeJsonArray(sql, "", where);
    }

    /**
     * 根据Sql语句(sql的where没有参数)，获取转换成JsonArray数组
     */
    public static JsonArray executeJsonArray(String sql, String columns, DBParameter... where) throws Exception {
        if (sql.toLowerCase().indexOf("where") > 0 && sql.toLowerCase().indexOf("order by") == -1)
            sql += DBParameter.getSqlWhere(sql, where);
        else if (where.length > 0 && sql.toLowerCase().indexOf("order by") == -1)
            sql += " where 1=1" + DBParameter.getSqlWhere(sql, where);

        return DruidUtils.executeJsonArray(sql, columns, where);
    }

    /**
     * 根据Sql语句(sql的where没有参数)，获取转换成ListMaps数组
     */
    public static List<Map<String, Object>> executeListMaps(String sql, DBParameter... where) throws Exception {
        return DBFunction.executeListMaps(sql, "", where);
    }

    /**
     * 根据Sql语句(sql的where没有参数)，获取转换成JsonArray数组
     */
    public static List<Map<String, Object>> executeListMaps(String sql, String columns, DBParameter... where) throws Exception {
        if (sql.toLowerCase().indexOf("where") > 0)
            sql += DBParameter.getSqlWhere(sql, where);
        else
            sql += " where 1=1" + DBParameter.getSqlWhere(sql, where);

        return DruidUtils.executeListMaps(sql, columns, where);
    }

    /**
     * 该数据库表名是否存在
     */
    public static boolean isExistTableName(String TableName) {
        String sql = Utils_String.format("show tables like '{0}'", TableName);  //不区分大小写
        Object obj = DBFunction.executeScalar(sql);
        if (!Utils_String.isEmpty(obj.toString())) return true;
        return false;
    }

    /**
     * 该数据库表中字段是否存在
     */
    public static boolean isExistFieldName(String table_name, String FieldName) {
        try {
            String sql = BuildHelper.getStructureSql(table_name);
            JsonArray array = DBFunction.executeJsonArray(sql);
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject dr = Utils_Gson.getObject(array, i);
                    String Field = Utils_Gson.tryParse(dr, "Field", "");
                    if (Field.equalsIgnoreCase(FieldName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前线程上的连接开启事务
     */
    public static void startTransaction(String... data_source) {
        DruidUtils.startTransaction(data_source);
    }

    /**
     * 提交事务
     */
    public static void commit() {
        DruidUtils.commit();
    }

    /**
     * 回滚事务
     */
    public static void rollback() {
        DruidUtils.rollback();
    }

    /**
     * 关闭连接
     */
    public static void close() {
        DruidUtils.close();
    }


}
