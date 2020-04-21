package com.xinsite.core.bll.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.PerEnum;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.List;

/**
 * 权限管理需要的查询、补值
 * create by zhangxiaxin
 */
public class BLL_PowerInfo {

    /**
     * 获取权限类型
     */
    public static int getObjType(String tb_type) {
        return tb_type.equals("user") ? 1 : 2;
    }

    /**
     * 【权限管理】栏目树形目录
     */
    public static JsonArray getItemTree(String condition, int org_id, int tb_type, int tb_id, int self) throws Exception {
        condition = BLL_UserPower.addSuperItems(condition, "a");

        String str = "'' all_fun_ids,'' checkgroup,";  //"'[[1, \"是\"], [0, \"否\"]]' checkgroup,";
        String sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.iconCls,a.isused,a.isdataper,a.create_time,'' fun_ids,'' data_per,'' data_ids,%s\n" +
                " a.expanded expand,case when  (select count(1) from sys_menu b where b.isdel=0 and b.isused=1 and (b.org_id=0 or b.org_id=%d) and b.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                " from sys_menu a where a.isdel=0 and a.isused=1 %s order by item_sort";
        if (tb_id > 0 && tb_type > 0 && self > 0) {
            sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.iconCls,a.isused,a.isdataper,a.create_time,'' fun_ids,'' data_per,'' data_ids,%s\n" +
                    " a.expanded expand,case when  (select count(1) from sys_menu b where b.isdel=0 and b.isused=1" +
                    " and (b.org_id=0 or b.org_id=%d) and b.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                    " from sys_menu a,(select item_id from sys_power_menu where isdel=0 and tb_id=%d and tb_type=%d) b\n" +
                    " where a.item_id=b.item_id and a.isdel=0 and a.isused=1 %s order by a.item_sort";

            return DBFunction.executeJsonArray(String.format(sql, str, org_id, tb_id, tb_type, condition));
        } else {
            return DBFunction.executeJsonArray(String.format(sql, str, org_id, condition));
        }
    }

    /**
     * 【权限管理】栏目树形全部目录
     */
    public static JsonArray getAllItemTree(String condition, int tb_type, int tb_id, int self, List<DBParameter> ls) throws Exception {
        condition = BLL_UserPower.addSuperItems(condition, "a");

        String str = "'' all_fun_ids,'' checkgroup";
        String sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.iconCls,a.isused,a.isdataper,a.create_time,\n" +
                " '' fun_ids,'' data_per,'' data_ids,a.expanded expand,%s from sys_menu a where a.isdel=0 %s order by item_sort";
        if (tb_id > 0 && tb_type > 0 && self > 0) {
            sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.iconCls,a.isused,a.isdataper,a.create_time,\n" +
                    " '' fun_ids,'' data_per,'' data_ids,a.expanded expand,%s from sys_menu a,\n" +
                    " (select item_id from sys_power_menu where isdel=0 and tb_id=%d and tb_type=%d) b\n" +
                    " where a.item_id=b.item_id and a.isdel=0 %s order by a.item_sort";

            return DBFunction.executeJsonArray(String.format(sql, str, tb_id, tb_type, condition), DBParameter.getParameter(ls));
        } else {
            return DBFunction.executeJsonArray(String.format(sql, str, condition), DBParameter.getParameter(ls));
        }
    }

    /**
     * 获取对象权限栏目信息
     */
    public static int getPowerMenuId(int tb_type, int tb_id, int item_id) throws Exception {
        String sql = "select pm_id from sys_power_menu where tb_id={0} and tb_type={1} and item_id={2}";
        sql = StringUtils.format(sql, tb_id, tb_type, item_id);
        return NumberUtils.strToInt(DBFunction.executeScalar(sql));
    }

    /**
     * 获取对象权限栏目信息
     */
    public static JsonArray getMenuPermission(int tb_type, int tb_id) throws Exception {
        String sql = "select pm_id,item_id,isdel from sys_power_menu where tb_id={0} and tb_type={1} order by item_id";
        return DBFunction.executeJsonArray(StringUtils.format(sql, tb_id, tb_type));
    }

    /**
     * 获取对象权限信息
     */
    public static JsonArray getPermissionInfo(int tb_type, int tb_id) throws Exception {
        String sql = "select item_id,(select group_concat(fun_id separator ',') from sys_power_fun b1 where a1.pm_id=b1.pm_id) fun_ids,\n" +
                "data_per,data_ids from sys_power_menu a1 where a1.isdel=0 and a1.tb_id={0} and a1.tb_type={1} order by a1.item_id";
        return DBFunction.executeJsonArray(StringUtils.format(sql, tb_id, tb_type));
    }

    /**
     * 【剔除栏目】树形目录
     */
    public static JsonArray getRemoveItemTree(int user_id) throws Exception {
        String sql = "select a.item_id id,a.pid,a.item_name text,a.iconCls,b.del_item,a.expanded expand,\n" +
                "(select group_concat(fun_id separator ',') from sys_power_fun c where c.pm_id=b.pm_id) del_fun_ids \n" +
                "from sys_menu a,sys_power_menu b where a.item_id=b.item_id and a.isdel=0 and b.isdel=0 \n" +
                " and b.tb_type=%d and b.tb_id=%d order by a.item_sort";

        return DBFunction.executeJsonArray(String.format(sql, PerEnum.剔除权限.getIndex(), user_id));
    }

    /**
     * 【剔除栏目】新增时栏目树目录
     */
    public static JsonArray selRemoveItemTree(String condition, int user_id) throws Exception {
        String sql = "select a.item_id id,a.pid,a.item_name text,a.iconCls, a.expanded expand," +
                " case when b.item_id is null then '' else 'disabled' end disabled,\n" +
                " case when  (select count(1) from sys_menu b where b.isdel=0 and b.isused=1 and b.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                " from sys_menu a left join sys_power_menu b on b.isdel=0 and a.item_id=b.item_id and b.tb_type=%d and b.tb_id=%d\n" +
                " where a.isdel=0 and a.isused=1 %s order by a.item_sort";

        return DBFunction.executeJsonArray(String.format(sql, PerEnum.剔除权限.getIndex(), user_id, condition));
    }

    /**
     * 设置栏目功能值
     */
    public static void setCheckGroup(JsonArray array) throws Exception {
        if (array == null || array.size() == 0) return;

        String item_ids = ArrayUtils.joinFieldsToRepeat(array, "id", 0);
        String sql = "select item_id,fun_id,name,serialcode from sys_menu_fun where isdel=0 and item_id in({0}) order by item_id,serialcode";
        JsonArray list_funs = DBFunction.executeJsonArray(StringUtils.format(sql, item_ids));

        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            StringBuilder sb = new StringBuilder();
            StringBuilder fun_all_ids = new StringBuilder();
            if (list_funs.size() > 0) {
                JsonArray funs = GsonUtils.getWhereArray(list_funs, "item_id", dr.get("id").getAsInt());
                for (int j = 0; j < funs.size(); j++) {
                    JsonObject r = GsonUtils.getObject(funs, j);
                    int fun_id = GsonUtils.tryParse(r, "fun_id", 0);
                    if (sb.length() != 0) sb.append(",");
                    sb.append(StringUtils.format("[{0}, '{1}']", fun_id, GsonUtils.tryParse(r, "name", "")));
                    if (fun_all_ids.length() != 0) fun_all_ids.append(",");
                    fun_all_ids.append(fun_id);
                }
            }
            dr.addProperty("checkgroup", "[" + sb.toString() + "]");
            dr.addProperty("all_fun_ids", fun_all_ids.toString());
        }
    }

    /**
     * 设置栏目功能值
     */
    public static void setCheckGroup(JsonArray array, int item_id) throws Exception {
        if (array == null || array.size() == 0) return;

        String sql = "select a1.item_name,a1.isfun,a1.isdataper,b1.fun_id,b1.name from sys_menu a1 left join sys_menu_fun b1 \n" +
                "on a1.item_id=b1.item_id and a1.isdel=0 and b1.isdel=0 where a1.item_id={0} \n" +
                "order by a1.item_id,b1.serialcode";
        JsonArray list_funs = DBFunction.executeJsonArray(StringUtils.format(sql, item_id));
        int isdataper = 0;
        int isfun = 0;
        String item_name = StringUtils.EMPTY;
        StringBuilder sb = new StringBuilder();
        String all_fun_ids = StringUtils.EMPTY;
        if (list_funs.size() > 0) {
            for (int j = 0; j < list_funs.size(); j++) {
                JsonObject r = GsonUtils.getObject(list_funs, j);
                int fun_id = GsonUtils.tryParse(r, "fun_id", 0);
                if (j == 0) {
                    isdataper = GsonUtils.tryParse(r, "isdataper", 0);
                    isfun = GsonUtils.tryParse(r, "isfun", 0);
                    item_name = GsonUtils.tryParse(r, "item_name", "");
                }
                if (sb.length() != 0) {
                    sb.append(",");
                    all_fun_ids += ",";
                }
                all_fun_ids += fun_id;
                sb.append(StringUtils.format("[{0}, '{1}']", fun_id, GsonUtils.tryParse(r, "name", "")));
            }
        }
        int super_role = Global.getInt("config.super_role");
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String funids = GsonUtils.tryParse(dr, "fun_ids", "");
            int role_id = GsonUtils.tryParse(dr, "role_id", 0);
            if (role_id == super_role) {
                if (StringUtils.isEmpty(all_fun_ids) || "0".equals(all_fun_ids)) {
                    dr.addProperty("checkgroup", StringUtils.format("[[{0}, '{1}']]", item_id, item_name));
                    dr.addProperty("fun_ids", item_id);
                } else {
                    dr.addProperty("checkgroup", "[" + sb.toString() + "]");
                    dr.addProperty("fun_ids", all_fun_ids);
                }
                dr.addProperty("data_per", -1);
            } else {
                if (StringUtils.isEmpty(funids) || isfun == 0) {
                    dr.addProperty("checkgroup", StringUtils.format("[[{0}, '{1}']]", item_id, item_name));
                    dr.addProperty("fun_ids", item_id);
                } else {
                    dr.addProperty("checkgroup", "[" + sb.toString() + "]");

                }
            }
            dr.addProperty("isdataper", isdataper == 1);
        }
    }

    /**
     * 按条件获取菜单栏目树
     */
    public static JsonArray getItemTree(String condition) throws Exception {
        String sql = "select a.item_id id,a.item_name text,a.item_type,a.iconCls,a.expanded expand,a.isdataper,\n" +
                " case when (select count(1) from sys_menu c where c.isdel=0 and c.isused=1 and c.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                " from sys_menu a where a.isdel=0 and a.isused=1 {0} order by a.item_sort";
        sql = StringUtils.format(sql, condition);
        return DBFunction.executeJsonArray(sql);
    }

}
