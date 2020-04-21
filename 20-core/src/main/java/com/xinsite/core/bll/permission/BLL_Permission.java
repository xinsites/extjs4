package com.xinsite.core.bll.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.PerEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.core.model.system.PowerSaveModel;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理新增、修改、删除
 * create by zhangxiaxin
 */
public class BLL_Permission {

    /**
     * 保存栏目功能权限
     */
    public static void savePermissionFun(int pm_id, String funids) throws Exception {
        if (pm_id > 0) {
            String del_sql = "delete from sys_power_fun where pm_id=" + pm_id;
            DBFunction.executeNonQuery(del_sql);  //删除该对象所有功能
            List<Integer> fun_ids = StringUtils.splitToList(funids);
            for (int fun_id : fun_ids) {
                Map ht = new HashMap();
                ht.put("pm_id", pm_id);
                ht.put("fun_id", fun_id);
                DBFunction.insertByTbName(ht, "sys_power_fun");
            }
        }
    }

    /**
     * 删除栏目功能权限
     */
    public static void deletePermissionFun(int tb_type, int tb_id, int item_id) throws Exception {
        String del_sql = "delete from sys_power_fun where exists(select 1 from sys_power_menu b1 where " +
                " sys_power_fun.pm_id=b1.pm_id and b1.tb_id={0} and b1.tb_type={1} and b1.item_id={2})";
        DBFunction.executeNonQuery(StringUtils.format(del_sql, tb_id, tb_type, item_id));  //删除功能权限
    }

    /**
     * 删除栏目功能权限
     */
    public static void deletePermissionFun(int tb_type, int tb_id, String item_ids) throws Exception {
        String del_sql = "delete from sys_power_fun where exists(select 1 from sys_power_menu b1 where " +
                " sys_power_fun.pm_id=b1.pm_id and b1.tb_id={0} and b1.tb_type={1} and b1.item_id in({2}))";
        DBFunction.executeNonQuery(StringUtils.format(del_sql, tb_id, tb_type, item_ids));  //删除功能权限
    }

    /**
     * 实时保存栏目、功能权限
     */
    public static boolean saveRealPermission(int tb_type, int tb_id, List<PowerSaveModel> list) {
        DBFunction.startTransaction();
        try {
            for (PowerSaveModel en : list) {
                if (en.check.equalsIgnoreCase("true")) { //新增或者修改
                    int pm_id = BLL_PowerInfo.getPowerMenuId(tb_type, tb_id, en.item_id);
                    if (pm_id > 0) { //修改
                        Map ht = new HashMap();
                        ht.put("isdel", 0);
                        ht.put("create_time", DateUtils.getDateTime());
                        DBFunction.updateByTbName(ht, "sys_power_menu", "pm_id=" + pm_id);
                        BLL_Permission.savePermissionFun(pm_id, en.fun_ids);
                    } else { //新增
                        Map ht = new HashMap();
                        ht.put("tb_type", tb_type);
                        ht.put("tb_id", tb_id);
                        ht.put("item_id", en.item_id);
                        ht.put("create_time", DateUtils.getDateTime());
                        pm_id = DBFunction.insertByTbName(ht, "sys_power_menu");
                        BLL_Permission.savePermissionFun(pm_id, en.fun_ids);
                    }
                } else { //删除
                    BLL_Permission.deletePermissionFun(tb_type, tb_id, en.item_id);
                    String del_sql = "update sys_power_menu set isdel=1,data_per=null,data_ids=null where tb_id={0} and tb_type={1} and item_id={2}";
                    DBFunction.executeNonQuery(StringUtils.format(del_sql, tb_id, tb_type, en.item_id));
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }

    /**
     * 按钮保存栏目、功能权限（批量保存）
     */
    public static boolean saveBtnPermission(int tb_type, int tb_id, List<PowerSaveModel> list, String item_ids) {
        DBFunction.startTransaction();
        try {
            item_ids = StringUtils.joinAsFilter(item_ids);
            if (!StringUtils.isEmpty(item_ids)) {
                BLL_Permission.deletePermissionFun(tb_type, tb_id, item_ids);

                String sql_del = "update sys_power_menu set isdel=1,data_per=null,data_ids=null where tb_id={0} and tb_type={1} and item_id in({2})";
                DBFunction.executeNonQuery(StringUtils.format(sql_del, tb_id, tb_type, item_ids));
            }
            JsonArray dt = BLL_PowerInfo.getMenuPermission(tb_type, tb_id);
            for (PowerSaveModel en : list) {
                if (en.check.equalsIgnoreCase("true")) { //新增
                    Map ht = new HashMap();
                    ht.put("tb_id", tb_id);
                    ht.put("tb_type", tb_type);
                    ht.put("item_id", en.item_id);
                    ht.put("isdel", 0);
                    ht.put("create_time", DateUtils.getDateTime());
                    JsonArray drs = GsonUtils.getWhereArray(dt, "item_id", en.item_id);
                    if (drs.size() > 0) {
                        JsonObject object = GsonUtils.getObject(drs, 0);
                        int pm_id = GsonUtils.tryParse(object, "pm_id", 0);
                        DBFunction.updateByTbName(ht, "sys_power_menu", "pm_id=" + pm_id);
                        BLL_Permission.savePermissionFun(pm_id, en.fun_ids);
                    } else {
                        ht.put("data_per", en.dataPer);
                        ht.put("data_ids", en.data_ids);
                        int pm_id = DBFunction.insertByTbName(ht, "sys_power_menu");
                        BLL_Permission.savePermissionFun(pm_id, en.fun_ids);
                    }
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }

    /**
     * 清空所属对象所有栏目权限、功能权限、数据权限
     */
    public static boolean clearAllPermission(int tb_type, int tb_id) {
        DBFunction.startTransaction();
        try {
            String del_sql = "delete from sys_power_fun where exists(select 1 from sys_power_menu b1 where " +
                    " sys_power_fun.pm_id=b1.pm_id and b1.tb_id={0} and b1.tb_type={1})";
            DBFunction.executeNonQuery(StringUtils.format(del_sql, tb_id, tb_type));  //删除功能权限

            del_sql = "update sys_power_menu set isdel=1,del_item=null,data_per=null,data_ids=null where tb_id={0} and tb_type={1}"; //删除栏目、数据权限
            DBFunction.executeNonQuery(StringUtils.format(del_sql, tb_id, tb_type));

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }

    /**
     * 清空所属对象指定栏目权限、功能权限、数据权限
     */
    public static boolean deletePermission(int tb_type, int tb_id, int item_id) {
        DBFunction.startTransaction();
        try {
            BLL_Permission.deletePermissionFun(tb_type, tb_id, item_id);

            String del_sql = "update sys_power_menu set isdel=1,del_item=null,data_per=null,data_ids=null where tb_id={0} and tb_type={1} and item_id={2}"; //删除栏目、数据权限
            DBFunction.executeNonQuery(StringUtils.format(del_sql, tb_id, tb_type, item_id));

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }

    /**
     * 保存数据权限
     */
    public static boolean saveDataPermission(int tb_type, int tb_id, int data_per, String data_ids, List<PowerSaveModel> list) {
        DBFunction.startTransaction();
        try {
            JsonArray dt = BLL_PowerInfo.getMenuPermission(tb_type, tb_id);
            for (PowerSaveModel en : list) {
                if (en.check.equalsIgnoreCase("true")) { //新增或者修改
                    Map ht = new HashMap();
                    ht.put("isdel", 0);
                    if (en.isdataper == 1) {
                        ht.put("data_per", data_per);
                        ht.put("data_ids", data_ids);
                    } else {
                        ht.put("data_per", 0);
                        ht.put("data_ids", "");
                    }
                    JsonArray drs = GsonUtils.getWhereArray(dt, "item_id", en.item_id);
                    if (drs.size() > 0) { //决定修改
                        JsonObject object = GsonUtils.getObject(drs, 0);
                        int pm_id = GsonUtils.tryParse(object, "pm_id", 0);
                        DBFunction.updateByTbName(ht, "sys_power_menu", "pm_id=" + pm_id);
                    } else { //决定新增
                        ht.put("tb_id", tb_id);
                        ht.put("tb_type", tb_type);
                        ht.put("item_id", en.item_id);
                        ht.put("create_time", DateUtils.getDateTime());
                        DBFunction.insertByTbName(ht, "sys_power_menu");
                    }
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }

    /**
     * 用户剔除权限新增
     */
    public static boolean saveRemovePermission(int user_id, String item_ids) {
        DBFunction.startTransaction();
        try {
            List<Integer> list = StringUtils.splitToList(item_ids);
            if (list.size() > 0) {
                BLL_Permission.deletePermissionFun(PerEnum.剔除权限.getIndex(), user_id, item_ids);
                JsonArray dt = BLL_PowerInfo.getMenuPermission(PerEnum.剔除权限.getIndex(), user_id);
                for (int item_id : list) {
                    JsonArray drs = GsonUtils.getWhereArray(dt, "item_id", item_id);
                    if (drs.size() == 0) { //新增
                        Map ht = new HashMap();
                        ht.put("tb_type", PerEnum.剔除权限.getIndex());
                        ht.put("tb_id", user_id);
                        ht.put("item_id", item_id);
                        ht.put("del_item", 0);
                        ht.put("create_time", DateUtils.getDateTime());
                        DBFunction.insertByTbName(ht, "sys_power_menu");
                    } else {
                        JsonObject object = GsonUtils.getObject(drs, 0);
                        int pm_id = GsonUtils.tryParse(object, "pm_id", 0);
                        DBFunction.executeNonQuery("update sys_power_menu set isdel=0,del_item=0 where pm_id=" + pm_id);
                    }
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }

    /**
     * 用户剔除权限单元格编辑
     */
    public static boolean editingRemovePermission(int user_id, int item_id, String field, String value) {
        DBFunction.startTransaction();
        try {
            if (field.equals("del_item")) {
                Map ht = new HashMap();
                ht.put(field, ValueUtils.tryParse(value, 0));
                DBFunction.updateByTbName(ht, "sys_power_menu",
                        new DBParameter("tb_type", "=", PerEnum.剔除权限.getIndex()),
                        new DBParameter("tb_id", "=", user_id),
                        new DBParameter("item_id", "=", item_id));
            } else if (field.equals("del_fun_ids")) {
                int pm_id = BLL_PowerInfo.getPowerMenuId(PerEnum.剔除权限.getIndex(), user_id, item_id);
                BLL_Permission.savePermissionFun(pm_id, value);
            }

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            return false;
        }
        return true;
    }
}
