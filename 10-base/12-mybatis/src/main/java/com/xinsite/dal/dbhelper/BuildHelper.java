package com.xinsite.dal.dbhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.dal.uitls.Utils_Gson;
import com.xinsite.dal.uitls.Utils_String;
import com.xinsite.mybatis.helper.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mybatis生成Helper
 */
public class BuildHelper {
    private static final Logger log = LoggerFactory.getLogger(BuildHelper.class);

    private static final String SQL = "select * from "; //数据库操作


    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames(String db_key, String db_name) {
        DataSource.setDataSource(db_key);
        List<String> table_names = new ArrayList<>();
        try {
            String sql = BuildHelper.getTablesSql(db_name);
            JsonArray array = DBFunction.executeJsonArray(sql);
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject dr_table = Utils_Gson.getObject(array, i);
                    String table_name = Utils_Gson.tryParse(dr_table, "table_name");
                    if (table_name.indexOf("view_") == 0) continue;
                    table_names.add(table_name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return table_names;
    }

    /**
     * 获取表中所有字段名称
     */
    public static List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName + " where 1=0";
        try {
            Connection conn = DruidUtils.getConnection();
            pStemt = conn.prepareStatement(tableSql);
            ResultSetMetaData rsmd = pStemt.getMetaData();  //结果集元数据
            int size = rsmd.getColumnCount(); //表列数
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (Exception e) {
            log.error("getColumnNames failure", e);
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pStemt);
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     */
    public static List<String> getColumnTypes(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            Connection conn = DruidUtils.getConnection();
            pStemt = conn.prepareStatement(tableSql);
            ResultSetMetaData rsmd = pStemt.getMetaData();  //结果集元数据
            int size = rsmd.getColumnCount();               //表列数
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (Exception e) {
            log.error("getColumnTypes failure", e);
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pStemt);
        }
        return columnTypes;
    }

    /**
     * 获取表结构的sql语句
     */
    public static String getStructureSql(String tableName) throws Exception {
        Connection conn = DruidUtils.getConnection();
        String url = conn.getMetaData().getURL().toLowerCase();
        if (url.indexOf("mysql") >= 0) {
            return "show full columns from " + tableName;
        } else if (url.indexOf("sqlserver") >= 0) {
            String sql = "select Field=a.name,\n" +
                    "        Type=b.name,\n" +
                    "        [Null]=case when a.isnullable=1 then 'YES'else 'NO' end,\n" +
                    "        [Key]=case when exists(select 1 from sysobjects where xtype='PK' and name in (\n" +
                    "                select name from sysindexes where indid in(\n" +
                    "                        select indid from sysindexkeys where id = a.id and item_id=a.item_id\n" +
                    "                ))) then 'PRI' else '' end,\n" +
                    "\t\tExtra=case when COLUMNPROPERTY( a.id,a.name,'IsIdentity')=1 then 'auto_increment'else '' end,\n" +
                    "        Comment=convert(varchar(50),ISNULL(g.[value], ''))\n" +
                    "from syscolumns a\n" +
                    "        left join systypes b on a.xtype=b.xusertype\n" +
                    "        inner join sysobjects d on (a.id=d.id  and d.xtype='U' and  d.name<>'dtproperties')\n" +
                    "        left join sys.extended_properties g  on (a.id=g.major_id AND a.item_id = g.minor_id )\n" +
                    "where d.name='%s'    \n" +
                    "order by d.name,a.colorder";
            return String.format(sql, tableName);
        } else {
            throw new RuntimeException("表结构的sql语句尚未配置！");
        }
    }

    /**
     * 获取所有表的sql语句
     */
    public static String getTablesSql(String db_name) throws Exception {
        Connection conn = DruidUtils.getConnection();
        String url = conn.getMetaData().getURL().toLowerCase();
        if (url.indexOf("mysql") >= 0) {
            return String.format("select table_name from information_schema.tables where table_schema='%s'", db_name);
        } else if (url.indexOf("sqlserver") >= 0) {
            String sql = "";
            return String.format(sql, db_name);
        } else {
            throw new RuntimeException("所有表的sql语句尚未配置！");
        }
    }

    /**
     * 获取表名的sql语句
     */
    public static String getTBCommentSql(String db, String tableName) throws Exception {
        Connection conn = DruidUtils.getConnection();
        String url = conn.getMetaData().getURL().toLowerCase();
        if (url.indexOf("mysql") >= 0) {
            String sql = "select table_name,table_comment from information_schema.tables where table_schema='%s' and TABLE_NAME='%s'";
            return String.format(sql, db, tableName);
        } else if (url.indexOf("sqlserver") >= 0) {
            String sql = "select distinct table_name=d.name,table_comment=convert(varchar(50),f.value) \n" +
                    "from syscolumns a\n" +
                    "\tLEFT JOIN systypes b on a.xusertype= b.xusertype\n" +
                    "\tINNER JOIN sysobjects d on a.id= d.id \n" +
                    "\tAND d.xtype= 'U' \n" +
                    "\tAND d.name<> 'dtproperties'\n" +
                    "\tLEFT JOIN sys.extended_properties f on d.id= f.major_id \n" +
                    "\tAND f.minor_id= 0\n" +
                    "where d.name='%s'";
            return String.format(sql, tableName);
        } else {
            throw new RuntimeException("表名的sql语句尚未配置！");
        }
    }

    /**
     * 获取表中字段的所有注释
     */
    public static List<String> getColumnComments(String tableName) {
        List<String> columnComments = new ArrayList<>();//列名注释集合
        PreparedStatement pStemt = null;
        ResultSet rset = null;
        try {
            Connection conn = DruidUtils.getConnection();
            pStemt = conn.prepareStatement(BuildHelper.getStructureSql(tableName));
            rset = pStemt.executeQuery();
            while (rset.next()) {
                columnComments.add(rset.getString("Comment"));
            }
        } catch (Exception e) {
            log.error("getColumnComments failure", e);
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pStemt);
            DruidUtils.closeRs(rset);
        }
        return columnComments;
    }

    /**
     * 获取表中主键字段的类型
     */
    public static String primaryKeyType(String tableName) {
        String data_type = "int";
        PreparedStatement pStemt = null;
        ResultSet rset = null;
        try {
            Connection conn = DruidUtils.getConnection();
            pStemt = conn.prepareStatement(BuildHelper.getStructureSql(tableName));
            rset = pStemt.executeQuery();
            while (rset.next()) {
                if (rset.getString("Key").equalsIgnoreCase("PRI")) {
                    data_type = rset.getString("Type");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("PrimaryKeyType failure", e);
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pStemt);
            DruidUtils.closeRs(rset);
        }
        if (data_type.toLowerCase().indexOf("varchar") == 0) data_type = "String";
        else data_type = "int";
        return data_type;
    }

    /**
     * 根据数据库连接和表名获取主键名
     *
     * @param table 数据库中的表名
     * @return 执行成功返回一个主键名的字符数组，否则返回null或抛出一个异常
     */
    public static String getPrimaryKey(String table) {
        PreparedStatement pStemt = null;
        ResultSet rset = null;
        List<String> Keys = new ArrayList<>();//列名注释集合
        try {
            Connection conn = DruidUtils.getConnection();
            pStemt = conn.prepareStatement(BuildHelper.getStructureSql(table));
            rset = pStemt.executeQuery();
            while (rset.next()) {
                if (rset.getString("Key").equalsIgnoreCase("PRI")) {
                    Keys.add(rset.getString("Field"));
                }
            }
        } catch (Exception e) {
            log.error("getPrimaryKey failure", e);
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pStemt);
            DruidUtils.closeRs(rset);
        }
        return Utils_String.joinAsList(Keys, ",");
    }

    /**
     * 返回数据库表的中文名称，没有返回“对象”
     *
     * @return 执行成功返回一个表的中文名称，否则返回“对象”
     */
    public static String getTableComment(String db_name, String table) {
        PreparedStatement pStemt = null;
        ResultSet rset = null;
        String table_explain = "对象";
        try {
            Connection conn = DruidUtils.getConnection();
            pStemt = conn.prepareStatement(BuildHelper.getTBCommentSql(db_name, table));
            rset = pStemt.executeQuery();
            while (rset.next()) {
                table_explain = rset.getString("table_comment");
                break;
            }

        } catch (Exception e) {
            log.error("getPrimaryKey failure", e);
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pStemt);
            DruidUtils.closeRs(rset);
        }
        return table_explain;
    }

    /**
     * 数据表字段转换成小写
     */
    public static void tableColumnsToLowercase(String tableName) throws Exception {
        String sql = BuildHelper.getStructureSql(tableName);
        JsonArray array = DBFunction.executeJsonArray(sql);
        if (array != null && array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr_field = Utils_Gson.getObject(array, i);
                String field = Utils_Gson.tryParse(dr_field, "Field");
                if (!Utils_String.isEmpty(field)) {
                    if (field.toLowerCase().equals(field)) continue;

                    BuildHelper.fieldChange(dr_field, tableName, field, field.toLowerCase());
                }
            }
        }
    }

    /**
     * 数据库所有表特定字段转换
     */
    public static void tableFieldChange(String db_name) throws Exception {
        String sql = BuildHelper.getTablesSql(db_name);
        JsonArray array = DBFunction.executeJsonArray(sql);
        if (array != null && array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr_table = Utils_Gson.getObject(array, i);
                String table_name = Utils_Gson.tryParse(dr_table, "table_name");
                if (table_name.indexOf("view_") == 0) continue;
                sql = BuildHelper.getStructureSql(table_name);
                JsonArray fields = DBFunction.executeJsonArray(sql);
                if (fields != null && fields.size() > 0) {
                    for (int j = 0; j < fields.size(); j++) {
                        JsonObject dr_field = Utils_Gson.getObject(fields, j);
                        String field = Utils_Gson.tryParse(dr_field, "Field");

                        if (!Utils_String.isEmpty(field)) {
//                            if (field.toLowerCase().equals(field)) continue;
//                            BuildHelper.fieldChange(dr_field, table_name, field, field.toLowerCase());

                            String change_field = Utils_String.EMPTY;
                            if (field.equalsIgnoreCase("positiontime")) change_field = "position_time";
                            else if (field.equalsIgnoreCase("createtime")) change_field = "create_time";
                            else if (field.equalsIgnoreCase("modifytime")) change_field = "modify_time";
                            else if (field.equalsIgnoreCase("createuid")) change_field = "create_uid";
                            else if (field.equalsIgnoreCase("modifyuid")) change_field = "modify_uid";
                            else if (field.equalsIgnoreCase("colid")) change_field = "item_id";
                            else if (field.equalsIgnoreCase("orgid")) change_field = "org_id";
                            else if (field.equalsIgnoreCase("deptid")) change_field = "dept_id";
                            else if (field.equalsIgnoreCase("parentid")) change_field = "pid";
                            else if (field.equalsIgnoreCase("itemid")) change_field = "item_id";
                            else if (field.equalsIgnoreCase("inputtime")) change_field = "modify_time";
                            else if (field.equalsIgnoreCase("deletetime")) change_field = "delete_time";
                            else if (field.equalsIgnoreCase("deleteuid")) change_field = "delete_uid";
                            else if (field.equalsIgnoreCase("applyuser")) change_field = "apply_uid";
                            else if (field.equalsIgnoreCase("applydept")) change_field = "apply_deptid";
                            else if (field.equalsIgnoreCase("applydate")) change_field = "apply_date";

                            if (!Utils_String.isEmpty(change_field)) {
                                BuildHelper.fieldChange(dr_field, table_name, field, change_field);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 设计配置表特定字段转换
     */
    public static void designFieldChange() throws Exception {
        String sql = "select fid,field_name,editor_form,dataindex from design_field";
        JsonArray array = DBFunction.executeJsonArray(sql);
        if (array != null && array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr_field = Utils_Gson.getObject(array, i);
                int fid = Utils_Gson.tryParse(dr_field, "fid", 0);
                String field = Utils_Gson.tryParse(dr_field, "field_name");
                String editor_form = Utils_Gson.tryParse(dr_field, "editor_form");
                if (!Utils_String.isEmpty(field)) {
                    String change_field = Utils_String.EMPTY;
                    if (field.equalsIgnoreCase("positiontime")) change_field = "position_time";
                    else if (field.equalsIgnoreCase("createtime")) change_field = "create_time";
                    else if (field.equalsIgnoreCase("modifytime")) change_field = "modify_time";
                    else if (field.equalsIgnoreCase("createuid")) change_field = "create_uid";
                    else if (field.equalsIgnoreCase("modifyuid")) change_field = "modify_uid";
                    else if (field.equalsIgnoreCase("colid")) change_field = "item_id";
                    else if (field.equalsIgnoreCase("orgid")) change_field = "org_id";
                    else if (field.equalsIgnoreCase("deptid")) change_field = "dept_id";
                    else if (field.equalsIgnoreCase("applyuser")) change_field = "apply_uid";
                    else if (field.equalsIgnoreCase("applydept")) change_field = "apply_deptid";
                    else if (field.equalsIgnoreCase("applydate")) change_field = "apply_date";

                    if (Utils_String.isEmpty(change_field)) {
                        if (field.toLowerCase().equals(field)) continue;
                        change_field = field.toLowerCase();
                    }
                    Map<String, Object> map = new HashMap();
                    map.put("field_name", change_field);
                    if (!Utils_String.isEmpty(editor_form)) {
                        map.put("editor_form", editor_form.replace(field, change_field));
                    }
                    map.put("dataindex", change_field);
                    DBFunction.updateByTbName(map, "design_field", "fid=" + fid);
                }
            }
        }
    }

    private static void fieldChange(JsonObject json, String table_name, String field, String change_field) throws Exception {
        String update_sql = "alter table %s change %s %s %s";

        String type = Utils_Gson.tryParse(json, "Type");
        String Comment = Utils_Gson.tryParse(json, "Comment");
        String Null = Utils_Gson.tryParse(json, "Null");
        String Key = Utils_Gson.tryParse(json, "Key");
        String Extra = Utils_Gson.tryParse(json, "Extra");
        String Default = Utils_Gson.tryParse(json, "Default");

        update_sql = String.format(update_sql, table_name, field, change_field, type);
        if (Null.equalsIgnoreCase("NO")) update_sql += " NOT NULL";
        if (Key.equals("PRI") && Extra.equalsIgnoreCase("auto_increment")) update_sql += " auto_increment";
        if (!Utils_String.isEmpty(Default)) {
            if (type.toLowerCase().indexOf("varchar") >= 0) {
                update_sql += String.format(" default '%s'", Default);
            } else {
                update_sql += " default " + Default;
            }
        }

        if (!Utils_String.isEmpty(Comment)) update_sql += String.format(" COMMENT '%s'", Comment);
        DBFunction.executeNonQuery(update_sql);
    }
}


