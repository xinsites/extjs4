package com.xinsite.core.bll;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.response.ReturnMap;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.bll.system.BLL_DataSource;
import com.xinsite.core.bll.system.BLL_Dept;
import com.xinsite.core.bll.system.BLL_User;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

/**
 * 公共通用方法
 * create by zhangxiaxin
 */
public class BLL_Common {
    /**
     * 该数据库表名是否存在
     */
    public static boolean isExistTableName(String TableName) {
        String sql = StringUtils.format("show tables like '{0}'", TableName);  //不区分大小写
        Object obj = DBFunction.executeScalar(sql);
        if (!StringUtils.isEmpty(obj.toString())) return true;
        return false;
    }

    /**
     * 根据Id获取树形目集合
     */
    public static JsonArray getTreeByIds(String tablename, String id, String pid, String ids) throws Exception {
        String sql = StringUtils.format("select {0},{1} from {2} where {0} in({3})", id, pid, tablename, ids);
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 根据父Id获取树形目集合
     */
    public static JsonArray getTreeByPids(String tablename, String id, String pid, String pids) throws Exception {
        String sql = StringUtils.format("select {0},{1} from {2} where {1} in({3})", id, pid, tablename, pids);
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 该数据库表中字段是否存在
     */
    public static boolean isExistFieldName(String TableName, String field_name) {
        JsonArray dt = null;
        try {
            dt = DBFunction.executeJsonArray("desc " + TableName);
            if (dt != null && dt.size() > 0) {
                for (int i = 0; i < dt.size(); i++) {
                    JsonObject dr = GsonUtils.getObject(dt, i);
                    String Field = GsonUtils.tryParse(dr, "Field", "");
                    if (Field.equalsIgnoreCase(field_name)) {
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
     * 获取表单单行信息
     */
    public static String getRequestFormJson(String sql, String table_name) throws Exception {
        JsonArray array = DBFunction.executeJsonArray(sql);
        ReturnMap returnMap = new ReturnMap();
        return returnMap.getFormJson(array, table_name);
    }

    /**
     * 查询列表中 动态下拉列表获取文本值
     */
    public static void setGridListCodeText(JsonArray array, String field_name, String data_key, String xtype) throws Exception {
        if (array == null || array.size() == 0) return;
        if (StringUtils.isEmpty(data_key)) return;
        if (!CommUtils.columnsExists(array, field_name)) return;
        String sql = "select a1.id,a1.text,a1.value from sys_code a1,sys_codetype b1" +
                " where a1.codetype_id=b1.id and data_key=@data_key order by a1.serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql, new DBParameter("@data_key", data_key));

        String add_field = field_name + "_text";
        String id = "value";
        if (xtype.equalsIgnoreCase("treepicker")) id = "id";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String value = StringUtils.sqlFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(value)) continue;
            JsonArray drs = GsonUtils.getWhereArrayByIds(code, id, value);

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(r.get("text").getAsString());
            }
            if (StringUtils.isEmpty(sb.toString()))
                dr.addProperty(add_field, value);
            else
                dr.addProperty(add_field, sb.toString());
        }
    }

    /**
     * 查询列表中 动态下拉列表获取文本值
     */
    public static void setGridCodeText(JsonArray array, String sql_showfields, int oid) throws Exception {
        JsonArray dt = BLL_Design.getColumnsCodeValue(oid);
        if (dt != null && dt.size() > 0) {
            for (int i = 0; i < dt.size(); i++) {
                JsonObject dr = GsonUtils.getObject(dt, i);
                String fieldname = GsonUtils.tryParse(dr, "field_name", "");
                String data_key = GsonUtils.tryParse(dr, "data_key", "");
                if (sql_showfields.indexOf(fieldname) >= 0 && !StringUtils.isEmpty(data_key)) {
                    String store_type = GsonUtils.tryParse(dr, "store_type", "");
                    String xtype = GsonUtils.tryParse(dr, "xtype", "");
                    if (!StringUtils.isEmpty(store_type) && data_key.indexOf("ds.") == 0) { //可以用系统数据源
                        BLL_DataSource.setGridListDataText(array, fieldname, data_key);
                    } else {
                        BLL_Common.setGridListCodeText(array, fieldname, data_key, xtype);
                    }
                }
            }
        }
    }

    /**
     * 导出Excel设置列表成中文，下拉框存值改成中文
     */
    public static void setExcelCodeText(JsonArray array, String sql_showfields, int oid) throws Exception {
        if (array == null || array.size() == 0) return;
        JsonArray dt = BLL_Design.getColumnsCodeValue(oid);
        if (dt != null && dt.size() > 0) {
            for (int i = 0; i < dt.size(); i++) {
                try {
                    JsonObject dr = GsonUtils.getObject(dt, i);
                    String fieldname = GsonUtils.tryParse(dr, "field_name", "");
                    fieldname = CommUtils.getColumnsName(array, fieldname);
                    if (StringUtils.isEmpty(fieldname)) continue;
                    if (sql_showfields.indexOf(fieldname) >= 0) {
                        String data_key = GsonUtils.tryParse(dr, "data_key", "");
                        String store_datas = GsonUtils.tryParse(dr, "store_datas", "");
                        String xtype = GsonUtils.tryParse(dr, "xtype", "");
                        if (!StringUtils.isEmpty(data_key)) {
                            String store_type = GsonUtils.tryParse(dr, "store_type", "");
                            if (!StringUtils.isEmpty(store_type) && data_key.indexOf("ds.") == 0) { //可以用系统数据源
                                BLL_DataSource.setExcelDataText(array, fieldname, data_key);
                            } else {
                                BLL_Common.setExcelCodeText(array, fieldname, data_key, xtype); //下拉框选择值时替换文本
                            }
                        } else if (!StringUtils.isEmpty(store_datas)) {
                            if (xtype.equalsIgnoreCase("treepicker"))
                                CommUtils.setExcelTreeCodeText(array, fieldname, store_datas);
                            else if (xtype.equalsIgnoreCase("checkbox"))
                                CommUtils.setExcelCheckBoxText(array, fieldname, store_datas);
                            else
                                CommUtils.setExcelCodeText(array, fieldname, store_datas);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        JsonObject dr = GsonUtils.getObject(array, 0);
        for (String columnname : dr.keySet()) {
            String fieldname = columnname.toLowerCase();
            if (fieldname.indexOf("create_uid") >= 0) {
                BLL_User.setExcelCodeText(array, "create_uid");
            } else if (fieldname.indexOf("modify_uid") >= 0) {
                BLL_User.setExcelCodeText(array, "modify_uid");
            } else if (fieldname.indexOf("dept_id") >= 0) {
                BLL_Dept.setExcelCodeText(array, "dept_id");
            }
        }
    }

    /**
     * 导出Excel 动态下拉列表获取文本值
     */
    public static void setExcelCodeText(JsonArray array, String field_name, String data_key, String xtype) throws Exception {
        if (array == null || array.size() == 0) return;
        if (StringUtils.isEmpty(data_key)) return;
        field_name = CommUtils.getColumnsName(array, field_name);
        if (!CommUtils.columnsExists(array, field_name)) return;

        String sql = "select a1.id,a1.text,a1.value from sys_code a1,sys_codetype b1" +
                " where a1.codetype_id=b1.id and data_key=@data_key order by a1.serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql, new DBParameter("@data_key", data_key));

        String id = "value";
        if (xtype.equalsIgnoreCase("treepicker")) id = "id";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String value = StringUtils.sqlFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(value)) continue;
            JsonArray drs = GsonUtils.getWhereArrayByIds(code, id, value);

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "text", ""));
            }
            if (StringUtils.isEmpty(sb.toString()))
                dr.addProperty(field_name, value);
            else
                dr.addProperty(field_name, sb.toString());
        }
    }

    /**
     * 根据父结点函数添加父结点条件
     */
    public static String getPidFunctionWhere(String function_name, int id, String id_field, String pid_field) {
        Object obj = DBFunction.executeScalar(String.format("select %s(%d)", function_name, id));
        String pids = StringUtils.EMPTY;
        if (obj != null) {
            pids = StringUtils.joinAsFilter(obj.toString());
        }
        if (StringUtils.isNotEmpty(pids)) {
            return String.format(" and (%s=%d or FIND_IN_SET(%s, '%s'))", id_field, id, pid_field, pids);
        }
        return String.format(" and %s=%d", id_field, id);
    }

}

