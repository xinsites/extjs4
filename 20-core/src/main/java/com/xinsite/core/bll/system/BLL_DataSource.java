package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.DataTypeEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.BuildHelper;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.uitls.Utils_Gson;
import com.xinsite.mybatis.helper.DataSource;

import java.util.*;

/**
 * 字典管理：系统数据源
 * create by zhangxiaxin
 */
public class BLL_DataSource {
    /**
     * 根据条件获取数据源
     */
    public static JsonArray getDataSourceTree(String Condition) throws Exception {
        String sql = "select id,pid,data_name text,data_type,data_key,data_page,query_field,\n" +
                "        case when  (select count(1) from sys_datasource a where a.isdel=0 and a.Pid=b.Id)>0 then 'false' else 'true' end leaf\n" +
                "        from sys_datasource b where isdel=0 {0} order by serialcode";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition));
    }

    /**
     * 根据条件获取数据源
     */
    public static JsonArray getDataTypeCombo(String Condition) throws Exception {
        String sql = "select data_key id,data_name name\n" +
                "        from sys_datasource where isdel=0 {0} order by serialcode";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition));
    }

    /**
     * 根据条件获取数据源
     */
    public static JsonObject getDataSourceById(int id) throws Exception {
        String sql = "select * from sys_datasource where Id = {0}";
        JsonArray array = DBFunction.executeJsonArray(StringUtils.format(sql, id));
        if (array.size() > 0) return GsonUtils.getObject(array, 0);
        return null;
    }

    /**
     * 根据条件获取数据源
     */
    public static JsonObject getDataSourceByKey(String data_key) throws Exception {
        String sql = "select * from sys_datasource where data_key=@data_key";
        JsonArray array = DBFunction.executeJsonArray(sql, new DBParameter("@data_key", "=", data_key));
        if (array.size() > 0) return GsonUtils.getObject(array, 0);
        return null;
    }

    /**
     * 保存数据源排序
     */
    public static boolean saveDataSourceSort(List<Hashtable> list) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_datasource set Pid={0},serialcode={1} where Id={2};";
        for (Hashtable ht : list) {
            sb.append(StringUtils.format(sql, ht.get("pid"), ht.get("serialcode"), ht.get("id")));
        }
        return DBFunction.executeNonQuery(sb.toString()) > 0;
    }

    /**
     * 获取系统数据源文本值
     */
    public static String getSystemDataText(String data_key, String Ids) throws Exception {
        Ids = StringUtils.joinAsFilter(Ids);
        if (StringUtils.isEmpty(Ids)) return "";
        StringBuilder sb = new StringBuilder();
        JsonObject object = BLL_DataSource.getDataSourceByKey(data_key);
        if (object != null) {
            String table_name = GsonUtils.tryParse(object, "table_name");
            String primary_key = GsonUtils.tryParse(object, "primary_key");
            String textfield = CommUtils.getFirstItems(GsonUtils.tryParse(object, "query_field"));

            String sql = "select {0} from {1} where {2} in({3})";
            JsonArray array = null;
            try {
                array = DBFunction.executeJsonArray(StringUtils.format(sql, textfield, table_name, primary_key, Ids));
                for (int i = 0; i < array.size(); i++) {
                    JsonObject dr = GsonUtils.getObject(array, i);
                    if (sb.length() != 0) sb.append(",");
                    sb.append(dr.get(textfield).getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (array == null) return Ids;
        }
        return sb.toString();
    }

    /**
     * 查询列表中 系统数据源动态下拉列表获取文本值
     */
    public static void setGridListDataText(JsonArray array, String field_name, String data_key) throws Exception {
        if (array == null || array.size() == 0) return;
        if (StringUtils.isEmpty(data_key)) return;
        if (!CommUtils.columnsExists(array, field_name)) return;
        JsonObject object = BLL_DataSource.getDataSourceByKey(data_key);
        if (object != null) {
            String table_name = GsonUtils.tryParse(object, "table_name");
            String primary_key = GsonUtils.tryParse(object, "primary_key");
            String textfield = CommUtils.getFirstItems(GsonUtils.tryParse(object, "query_field"));
            String sql = "select {0},{1} from {2} where {0}";
            sql = StringUtils.format(sql, primary_key, textfield, table_name);
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                String value = GsonUtils.tryParse(dr, field_name, "");
                if (StringUtils.isEmpty(value)) continue;
                List<Integer> list = StringUtils.splitToList(value);
                for (Integer id : list) {
                    if (!ids.contains(id)) ids.add(id);
                }
            }

            if (ids.size() > 0) {
                sql += StringUtils.format(" in({0})", StringUtils.joinAsList(ids));
                JsonArray drs = DBFunction.executeJsonArray(sql);
                String add_field = field_name + "_text";
                for (int i = 0; i < array.size(); i++) {
                    JsonObject dr = GsonUtils.getObject(array, i);
                    String value = GsonUtils.tryParse(dr, field_name, "");
                    if (StringUtils.isEmpty(value)) continue;
                    try {
                        String text = CommUtils.getFieldTextByArray(drs, primary_key, textfield, value);
                        if (StringUtils.isEmpty(text))
                            dr.addProperty(add_field, value);
                        else
                            dr.addProperty(add_field, text);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 导出Excel 动态下拉列表获取文本值
     */
    public static void setExcelDataText(JsonArray array, String field_name, String data_key) throws Exception {
        if (array == null || array.size() == 0) return;
        if (StringUtils.isEmpty(data_key)) return;
        field_name = CommUtils.getColumnsName(array, field_name);
        if (!CommUtils.columnsExists(array, field_name)) return;
        JsonObject object = BLL_DataSource.getDataSourceByKey(data_key);
        if (object != null) {
            String table_name = GsonUtils.tryParse(object, "table_name");
            String primary_key = GsonUtils.tryParse(object, "primary_key");
            String textfield = CommUtils.getFirstItems(GsonUtils.tryParse(object, "query_field"));
            String sql = "select {0},{1} from {2} where 1=1";
            try {
                JsonArray code = DBFunction.executeJsonArray(StringUtils.format(sql, primary_key, textfield, table_name));
                if (code != null && code.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject dr = GsonUtils.getObject(array, i);
                        String value = StringUtils.sqlFilter(GsonUtils.tryParse(dr, field_name, ""));
                        if (StringUtils.isEmpty(value)) continue;

                        String text = CommUtils.getFieldTextByArray(code, primary_key, textfield, value);
                        if (StringUtils.isEmpty(text))
                            dr.addProperty(field_name, value);
                        else
                            dr.addProperty(field_name, text);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 流程定义管理系统数据源
     */
    public static JsonArray getArray(String data_key, String Condition, List<DBParameter> ls) throws Exception {
        JsonObject source = BLL_DataSource.getDataSourceByKey(data_key);
        if (source != null) {
            String query_sql = GsonUtils.tryParse(source, "query_sql");   //查询语句
            String primary_key = GsonUtils.tryParse(source, "primary_key");
            JsonArray show = BLL_DataShow.getDataShow(DataTypeEnum.系统数据源.getValue(), data_key);
            String not_ids = BLL_DataShow.getNotShowIds(show);
            if (!StringUtils.isEmpty(not_ids)) {
                Condition += String.format(" and a1.%s not in(%s)", primary_key, not_ids); //只包含可见数据源
            }

            query_sql = query_sql.replace("{org_id}", UserUtils.getOrgId() + "");
            query_sql = query_sql.replace("{where}", Condition);

            JsonArray array = DBFunction.executeJsonArray(query_sql, DBParameter.getParameter(ls));
            BLL_DataShow.setDisabled(array, show);
            return array;
        }
        return null;
    }

    /**
     * 获取数据库下的所有表名
     */
    public static JsonArray getTableNames(String db_key, String db_name) {
        List<String> tableNames = BuildHelper.getTableNames(db_key, db_name);
        JsonArray array = new JsonArray();
        for (String table_name : tableNames) {
            JsonObject object = new JsonObject();
            object.addProperty("id", table_name);
            object.addProperty("name", table_name);
            array.add(object);
        }
        return array;
    }

    /**
     * 获取MyBatis数据源
     */
    public static Map getMyBatisSource(String db_key, String db_name, String db_tables) {
        DataSource.setDataSource(db_key);
        Map<String, Object> map = new HashMap<>();
        String[] attr_tables = org.apache.commons.lang3.StringUtils.split(db_tables, ",");
        Map<String, String> gen_data = new HashMap<>();
        Map<String, String> tables = new HashMap<>();
        map.put("db_key", db_key);
        for (String table_name : attr_tables) {
            String table_explain = BuildHelper.getTableComment(db_name, table_name);  //表的中文名
            tables.put(table_name, table_explain);
            try {
                String sql = BuildHelper.getStructureSql(table_name);
                JsonArray array = DBFunction.executeJsonArray(sql);
                JsonArray fields = new JsonArray();
                if (array != null && array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject dr_field = Utils_Gson.getObject(array, i);
                        String data_type = Utils_Gson.tryParse(dr_field, "Type");
                        String Key = Utils_Gson.tryParse(dr_field, "Key");
                        data_type = org.apache.commons.lang3.StringUtils.substringBefore(data_type, "(");
                        JsonObject object = new JsonObject();
                        object.addProperty("field_name", Utils_Gson.tryParse(dr_field, "Field"));
                        object.addProperty("data_type", data_type.toLowerCase());
                        object.addProperty("field_tag", "PRI".equals(Key) ? "primary_key" : "");
                        object.addProperty("field_explain", Utils_Gson.tryParse(dr_field, "Comment"));
                        fields.add(object);
                    }
                }
                if (fields.size() > 0) {
                    gen_data.put(table_name, GsonUtils.toJson(fields));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        map.put("tables", tables);
        map.put("gen_data", gen_data);
        return map;
    }
}




