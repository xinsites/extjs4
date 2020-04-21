package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.Map;

public class BLL_Config {

    /**
     * 获取排序号
     */
    public static int getSerialCode(int org_id, int item_id) {
        String strSql = StringUtils.format("select ifnull(max(serialcode),0)+1 from sys_config where org_id={0} and item_id={1}", org_id, item_id);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 系统配置表
     */
    public static JsonArray getConfig(int org_id, int itemid) throws Exception {
        String sql = "select id,config_key,field_explain,config_editor,config_value," +
                "config_text,issys from sys_config where org_id={0} and item_id={1} order by serialcode";
        return DBFunction.executeJsonArray(StringUtils.format(sql, org_id, itemid));
    }

    /**
     * 获取系统参数配置变量值
     */
    public static String getConfigValue(String config_key, String defaultValue, int... itemIds) {
        if (StringUtils.isEmpty(config_key)) return defaultValue;
        int itemid = 0;
        if (itemIds.length > 0) itemid = itemIds[0];
        else itemid = Global.getInt("config.itemid");
        return getConfigValue(UserUtils.getOrgId(), itemid, config_key, defaultValue);
    }

    /**
     * 获取系统参数配置变量值
     */
    public static <T> T getConfigValue(int org_id, String config_key, T defaultValue) {
        int itemid = Global.getInt("config.itemid");
        String value = getConfigValue(org_id, itemid, config_key, defaultValue.toString());
        try {
            return ValueUtils.tryParse(value, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 获取系统参数配置变量值
     */
    public static String getConfigValue(int org_id, int itemid, String config_key, String defaultValue) {
        String sql = "select config_value from sys_config where org_id=" + org_id + " and item_id=@itemid and config_key=@config_key";
        Object obj = DBFunction.executeScalar(sql, new DBParameter("@config_key", "=", config_key), new DBParameter("@itemid", "=", itemid));
        String value = obj == null ? "" : obj.toString();
        if (StringUtils.isEmpty(value)) value = defaultValue;
        return value;
    }

    /**
     * 检查系统参数配置
     */
    public static void checkOrganizeConfig(int org_id) throws Exception {
        String sql = "select count(1) from sys_config where org_id=" + org_id;
        Object obj = DBFunction.executeScalar(sql);

        if (NumberUtils.strToInt(obj) == 0) {
            sql = "insert into sys_config (org_id,field_explain,config_key,config_value,config_text,config_editor,serialcode)\n" +
                    "            select {0},field_explain,config_key,config_value,config_text,config_editor,serialcode from sys_config where org_id=0";

            DBFunction.executeNonQuery(StringUtils.format(sql, org_id));
        }
    }

    /**
     * 修改系统参数配置
     */
    public static boolean setConfigValue(Map<String, Object> ht, int id, String config_key) throws Exception {
        if (ht.containsKey("config_value"))
            SysConfigCache.setConfigValue(config_key, ht.get("config_value").toString());
        return DBFunction.updateByTbName(ht, "sys_config", new DBParameter("Id", id));  //修改失败说明没记录
    }

    /**
     * 永久删除
     */
    public static boolean deleteById(int id) throws Exception {
        String strSql = StringUtils.format("delete from sys_config where issys=0 and id={0}", id);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 新增系统参数配置
     */
    public static int insert(Map<String, Object> ht) throws Exception {
        return DBFunction.insertByTbName(ht, "sys_config");
    }

    /**
     * config_key是否存在
     */
    public static boolean isExistKey(int org_id, int item_id, String config_key) {
        String sql = "select count(1) from sys_config where org_id={0} and item_id={1} and config_key=@config_key";
        return DBFunction.getTableCount(StringUtils.format(sql, org_id, item_id), new DBParameter("@config_key", "=", config_key)) > 0;
    }
}






