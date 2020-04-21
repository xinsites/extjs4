package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.ExtendEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.*;

/**
 * 单位组织：角色管理
 * create by zhangxiaxin
 */
public class BLL_Role {
    /**
     * 获取排序号
     */
    public static int getSerialCode(String add_type) {
        String str = "ifnull(max(serialcode),0)+1";
        if (add_type.equals("last")) str = "ifnull(min(serialcode),0)-1";
        String strSql = StringUtils.format("select {0} from sys_role", str);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 角色树
     */
    public static JsonArray getRoleTree(int org_id) throws Exception {
        String sql = "select a1.role_id id,0 pid,a1.role_name text,a1.role_remark,a1.role_per_value,a1.role_state,a1.issys,a1.org_id, \n" +
                "a1.create_time,a1.serialcode from sys_role a1 where a1.isdel=0 and a1.org_id={0} order by a1.serialcode asc";
        return DBFunction.executeJsonArray(StringUtils.format(sql, org_id));
    }

    /**
     * 设置角色成员数
     */
    public static void setUserNum(JsonArray dt, String field_table_id) throws Exception {
        if (dt == null || dt.size() == 0) return;
        String sql = "select role_id,count(user_id) user_num from sys_user where isdel=0 and role_id>0 group by role_id";
        JsonArray array = DBFunction.executeJsonArray(sql);

        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            int id = GsonUtils.tryParse(dr, field_table_id, 0);
            JsonArray drs = GsonUtils.getWhereArray(array, "role_id", id);
            if (drs.size() > 0) {
                dr.addProperty("user_num", GsonUtils.getObjectValue(drs, 0, "user_num"));
            } else {
                dr.addProperty("user_num", 0);
            }
        }
    }

    /**
     * 设计表保存排序
     */
    public static void saveSort(String key, String sortVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_role set serialcode={0} where {2}={1};";
        String[] Items = sortVal.split(";");
        for (String item : Items) {
            String[] arr = item.split(":");
            if (arr.length == 2)
                sb.append(StringUtils.format(sql, NumberUtils.strToInt(arr[1]), NumberUtils.strToInt(arr[0]), key));
        }
        DBFunction.executeNonQuery(sb.toString());
    }

    /**
     * 删除列表
     */
    public static boolean deleteByIds(String id, String idsVal) throws Exception {
        String strSql = StringUtils.format("update sys_role set isdel=1 where {0} in({1})", id, idsVal);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 保存信息
     */
    public static int saveInfo(Map<String, Object> ht, int role_id) throws Exception {
        if (role_id == 0) {
            ht.put("isdel", 0);
            ht.put("create_time", DateUtils.getDateTime());
            ht.put("serialcode", getSerialCode(""));
            role_id = DBFunction.insertByTbName(ht, "sys_role");
        } else {
            if (!DBFunction.updateByTbName(ht, "sys_role", "role_id=" + role_id))
                role_id = 0;
        }
        return role_id;
    }

    /**
     * 查询列表中 列表获取文本值
     */
    public static void setGridRoleNames(JsonArray array, String field_name) throws Exception {
        if (array == null) return;
        if (!GsonUtils.getArrayFields(array).contains(field_name)) return;
        String sql = "select role_id,role_name,serialcode from sys_role order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql);

        String add_field = field_name + "_text";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "role_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "role_name", ""));
            }
            dr.addProperty(add_field, sb.toString());
        }
    }

    /**
     * 导出Excel 文本值
     */
    public static void setExcelCodeText(JsonArray dt, String field_name) throws Exception {
        if (dt == null) return;
        if (!GsonUtils.getArrayFields(dt).contains(field_name)) return;
        String sql = "select role_id,role_name,serialcode from sys_role";
        JsonArray code = DBFunction.executeJsonArray(sql);

        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "role_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "role_name", ""));
            }
            dr.addProperty(field_name, sb.toString());
        }
    }

    /**
     * 角色用户保存
     */
    public static void saveRoleUser(int role_id, String IdVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_user set role_id={0} where user_id={1};";
        String[] Items = IdVal.split(";");
        for (String item : Items) {
            String[] arr = item.split(":");
            if (arr.length == 2) {
                int user_id = NumberUtils.strToInt(arr[0]);
                if (arr[1].equalsIgnoreCase("true")) {
                    sb.append(StringUtils.format(sql, role_id, user_id));
                } else if (BLL_User.isExistUserRole(user_id, role_id)) {
                    sb.append(StringUtils.format(sql, "null", user_id));
                }
            }
        }
        DBFunction.executeNonQuery(sb.toString());
    }

    /**
     * 获取机构管理员角色Id
     */
    public static int getManagerRoleId(int org_id) throws Exception {
        String strSql = StringUtils.format("select min(role_id) role_id from sys_role where org_id={0} and issys=1 and isdel=0", org_id);
        int role_id = NumberUtils.strToInt(DBFunction.executeScalar(strSql));
        if (role_id == 0) { //该机构还没有管理员角色
            Hashtable ht = new Hashtable();
            ht.put("create_time", DateUtils.getDateTime());
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("org_id", org_id);
            ht.put("role_per_value", "100001");
            ht.put("role_name", "系统管理员");
            ht.put("serialcode", 1);
            ht.put("role_state", 1);
            ht.put("issys", 1);
            role_id = BLL_Role.saveInfo(ht, role_id);
        }
        return role_id;
    }

    /**
     * 获取角色名称
     */
    public static String getRoleName(int role_id) {
        String QuerySql = "select role_name from sys_role where role_id=" + role_id;
        String obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj;
    }

}








