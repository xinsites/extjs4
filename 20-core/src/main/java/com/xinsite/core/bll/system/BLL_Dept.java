package com.xinsite.core.bll.system;

import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 系统管理：部门管理
 * create by zhangxiaxin
 */
public class BLL_Dept {
    /**
     * 获取排序号
     */
    public static int getSerialCode(int pid) {
        String str = "ifnull(max(serialcode),0)+1";
        String strSql = StringUtils.format("select {0} from sys_dept where pid={1}", str, pid);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 保存排序
     */
    public static boolean saveSort(List<Map> list) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_dept set pid={0},serialcode={1} where dept_id={2};";
        for (Map ht : list) {
            sb.append(StringUtils.format(sql, ht.get("pid"), ht.get("index"), ht.get("id")));
        }
        return DBFunction.executeNonQuery(sb.toString()) > 0;
    }

    /**
     * 删除列表
     */
    public static boolean deleteByIds(String id, int idsVal) throws Exception {
        String strSql = StringUtils.format("update sys_dept set isdel=1 where {0} in({1})", id, idsVal);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 保存信息
     */
    public static int saveInfo(Map<String, Object> ht, int dept_id, int pid) throws Exception {
        if (dept_id == 0) {
            ht.put("pid", pid);
            ht.put("isdel", 0);
            ht.put("create_time", DateUtils.getDateTime());
            ht.put("serialcode", getSerialCode(pid));
            dept_id = DBFunction.insertByTbName(ht, "sys_dept");
        } else {
            if (!DBFunction.updateByTbName(ht, "sys_dept", "dept_id=" + dept_id))
                dept_id = 0;
        }
        return dept_id;
    }

    /**
     * 查询列表中 列表获取文本值
     */
    public static void setGridDeptText(JsonArray array, String field_name) throws Exception {
        if (array == null || array.size() == 0) return;
        if (!GsonUtils.getArrayFields(array).contains(field_name)) return;
        String sql = "select dept_id,dept_name,serialcode from sys_dept order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql);

        String add_field = field_name + "_text";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "dept_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "dept_name", ""));
            }
            dr.addProperty(add_field, sb.toString());
        }
    }

    /**
     * 导出Excel 文本值
     */
    public static void setExcelCodeText(JsonArray array, String field_name) throws Exception {
        if (array == null || array.size() == 0) return;
        if (!GsonUtils.getArrayFields(array).contains(field_name)) return;
        String sql = "select dept_id,dept_name,serialcode from sys_dept order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql);

        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "dept_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "dept_name", ""));
            }
            dr.addProperty(field_name, sb.toString());
        }
    }

    /**
     * 根据部门ID，获取部门名称
     */
    public static String getDeptNames(String Ids) throws Exception {
        Ids = StringUtils.joinAsFilter(Ids);
        if (StringUtils.isEmpty(Ids)) return "";
        String sql = "select dept_id,dept_name from sys_dept where dept_id in({0}) order by serialcode";
        JsonArray array = DBFunction.executeJsonArray(StringUtils.format(sql, Ids));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (sb.length() != 0) sb.append(",");
            sb.append(GsonUtils.tryParse(dr, "dept_name", ""));
        }
        return sb.toString();
    }

    /**
     * 获取部门的上级部门Id
     */
    public static int getDeptParentId(int dept_id) throws Exception {
        String sql = "select pid from sys_dept where dept_id={0}";
        JsonArray dt = DBFunction.executeJsonArray(StringUtils.format(sql, dept_id));
        if (dt.size() > 0) {
            JsonObject dr = GsonUtils.getObject(dt, 0);
            return GsonUtils.tryParse(dr, "pid", 0);
        }
        return 0;
    }

    /**
     * 获取部门名称
     */
    public static String getDeptName(int dept_id) {
        String QuerySql = "select dept_name from sys_dept where dept_id=" + dept_id;
        String obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj;
    }
}







