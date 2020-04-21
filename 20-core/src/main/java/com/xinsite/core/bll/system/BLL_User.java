package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.codec.Md5Utils;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单位组织：用户管理
 * create by zhangxiaxin
 */
public class BLL_User {

    /**
     * 获取排序号
     */
    public static int getSerialCode(String add_type) {
        String str = "ifnull(max(serialcode),0)+1";
        if (add_type.equals("last")) str = "ifnull(min(serialcode),0)-1";
        String strSql = StringUtils.format("select {0} from sys_user", str);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 设计表保存排序
     */
    public static void saveDesignTableSort(String key, String sortVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_user set serialcode={0} where {2}={1};";
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
    public static boolean deleteByIds(String id, String user_ids) throws Exception {
        String strSql = StringUtils.format("update sys_user set isdel=1 where {0} in({1})", id, user_ids);
        boolean bool = DBFunction.executeNonQuery(strSql) > 0;
        if (bool) {
            List<Integer> list = StringUtils.splitToList(user_ids);
            for (int user_id : list) {
                String error_msg = MessageUtils.message("user.name.delete");
                ShiroUtils.kickoutUser(user_id, error_msg);  //删除用户下线
            }
        }
        return bool;
    }

    /**
     * 登录名是否存在
     */
    public static boolean isExistLoginName(int user_id, String login_name) throws Exception {
        String sql = "select user_id,login_name from sys_user where login_name=@login_name or user_id=" + user_id;
        JsonArray array = DBFunction.executeJsonArray(sql, new DBParameter("@login_name", "=", login_name));
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (GsonUtils.tryParse(dr, "login_name", "").equalsIgnoreCase(login_name)) {
                if (user_id == 0) {
                    return true;
                } else if (user_id != dr.get("user_id").getAsInt()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 保存信息
     */
    public static int saveInfo(Map<String, Object> ht, int user_id) throws Exception {
        if (user_id == 0) {
            ht.put("isdel", 0);
            ht.put("create_time", DateUtils.getDateTime());
            ht.put("serialcode", getSerialCode("first"));
            user_id = DBFunction.insertByTbName(ht, "sys_user");
        } else {
            if (!DBFunction.updateByTbName(ht, "sys_user", "user_id=" + user_id))
                user_id = 0;
        }
        return user_id;
    }

    /**
     * 查询列表中 列表获取用户名
     */
    public static void setGridUserNames(JsonArray array, String field_name) throws Exception {
        if (array == null) return;
        if (!GsonUtils.getArrayFields(array).contains(field_name)) return;
        String sql = "select user_id,user_name,serialcode from sys_user order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql);

        String add_field = field_name + "_text";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "user_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "user_name", ""));
            }
            dr.addProperty(add_field, sb.toString());
        }
    }

    /**
     * 查询列表中 列表获取用户名、头像
     */
    public static void setGridNamePhoto(JsonArray dt, String field_task_userid, String field_trial_userid) throws Exception {
        if (dt == null) return;
        if (!GsonUtils.getArrayFields(dt).contains(field_task_userid)) return;
        if (!GsonUtils.getArrayFields(dt).contains(field_trial_userid)) return;
        String sql = "select user_id,user_name,head_photo,serialcode from sys_user";
        JsonArray code = DBFunction.executeJsonArray(sql);

        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            int task_userid = GsonUtils.tryParse(dr, field_task_userid, 0);
            int trial_userid = GsonUtils.tryParse(dr, field_trial_userid, 0);
            int user_id = task_userid;
            if (trial_userid > 0) user_id = trial_userid;

            JsonArray drs = GsonUtils.getWhereArray(code, "user_id", user_id);
            if (drs.size() == 1) {
                JsonObject r = GsonUtils.getObject(drs, 0);
                dr.addProperty("operator_user", GsonUtils.tryParse(r, "user_name", ""));
                dr.addProperty("head_photo", GsonUtils.tryParse(r, "head_photo", ""));
            }
        }
    }

    /**
     * 导出Excel列表，获取用户名
     */
    public static void setExcelCodeText(JsonArray dt, String field_name) throws Exception {
        if (dt == null) return;
        if (!GsonUtils.getArrayFields(dt).contains(field_name)) return;
        String sql = "select user_id,user_name,serialcode from sys_user order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql);

        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "user_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "user_name", ""));
            }
            dr.addProperty(field_name, sb.toString());
        }
    }

    /**
     * 根据用户ID，获取用户名称
     */
    public static String getUserNames(String Ids) throws Exception {
        Ids = StringUtils.joinAsFilter(Ids);
        if (StringUtils.isEmpty(Ids)) return "";
        String sql = "select user_id,user_name from sys_user where user_id in({0}) order by serialcode";
        JsonArray dt = DBFunction.executeJsonArray(StringUtils.format(sql, Ids));
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < dt.size(); j++) {
            JsonObject r = GsonUtils.getObject(dt, j);
            if (sb.length() != 0) sb.append(",");
            sb.append(GsonUtils.tryParse(r, "user_name", ""));
        }
        return sb.toString();
    }

    /**
     * 用户角色是否存在
     */
    public static boolean isExistUserRole(int user_id, int role_id) {
        String sql = "select count(1) from sys_user where user_id=@user_id and role_id=" + role_id;
        return NumberUtils.strToInt(DBFunction.executeScalar(sql, new DBParameter("@user_id", "=", user_id))) > 0;
    }

    public static JsonArray getDeptUserInfo(List<DBParameter> ls, String Condition) throws Exception {
        String sql = "select user_id id,0 pid,login_name,user_name,head_photo,a.role_id,a.dept_id,b.dept_name from sys_user a left join sys_dept b on a.dept_id=b.dept_id \n" +
                "                    where a.isdel=0 and a.user_state=1 {0} order by a.serialcode desc,a.create_time desc";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition), DBParameter.getParameter(ls));
    }

    /**
     * 根据用户ID，获取用户部门
     */
    public static int getUserDeptId(int user_id) throws Exception {
        String sql = "select dept_id from sys_user where user_id={0}";
        JsonArray dt = DBFunction.executeJsonArray(StringUtils.format(sql, user_id));
        if (dt.size() > 0) {
            JsonObject dr = GsonUtils.getObject(dt, 0);
            return GsonUtils.tryParse(dr, "dept_id", 0);
        }
        return 0;
    }

    /**
     * 根据用户ID，获取用户名称
     */
    public static String getUserName(int user_id) {
        String QuerySql = "select user_name from sys_user where user_id=" + user_id;
        String obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj;
    }

    /**
     * 获取用户某个字段值
     */
    public static <T> T getFieldByUser(int user_id, String field, T defaultValue) {
        String QuerySql = "select %s from sys_user where user_id=" + user_id;
        String str = DBFunction.executeScalar(String.format(QuerySql, field));
        return ValueUtils.tryParse(str, defaultValue);
    }

    /**
     * 该用户是否属于超级管理员
     */
    public static boolean isSuperAdminer(int user_id) {
        int super_role = Global.getInt("config.super_role");
        return BLL_User.isExistUserRole(user_id, super_role);
    }

    /**
     * 角色用户成员
     */
    public static List<Integer> getRoleUserIds(int role_id, int org_id) throws Exception {
        String sql = "select a1.user_id from sys_user a1 where a1.isdel=0 and a1.role_id=@role_id and a1.org_id=@org_id";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@role_id", "=", role_id));
        ls.add(new DBParameter("@org_id", "=", org_id));
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
        return ArrayUtils.listByField(array, "user_id", 0);
    }
}








