package com.xinsite.core.bll.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.PerEnum;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.common.enums.system.ExtendEnum;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLL_Item_Fixed {
    //private static String table_name = ExtendEnum.固定标签.getValue();

    /**
     * 保存信息
     */
    public static boolean saveInfo(int user_id, int item_id, String fixed) throws Exception {
        String table_name = ExtendEnum.固定标签.getValue();
        if (fixed.equalsIgnoreCase("true")) {
            if (!BLL_Extend_Info.isExist(table_name, user_id, "item_id", item_id)) {
                Map ht = new HashMap();
                ht.put("field_extend", "item_id");
                ht.put("field_value", item_id);
                ht.put("flag_1", "固定标签");
                BLL_Extend_Info.saveInfo(ht, table_name, user_id);
            }
        } else {
            BLL_Extend_Info.delete(table_name, user_id, "item_id", item_id);
        }
        return true;
    }

    /**
     * 获取登录用户设置的固定标签
     */
    public static JsonArray getSetFixedTab() throws Exception {
        JsonArray array = BLL_Extend_Info.getExtendValues(ExtendEnum.固定标签.getValue(), UserUtils.getUserId(), "item_id", "create_time");
        List<Integer> list = ArrayUtils.listByField(array, "field_value", 0);
        if (list.size() == 0) return array;

        List<Integer> bars = SysConfigCache.getRemindItems();
        String bars_item_ids = "0";
        if (bars.size() > 0) {
            for (int item_id : bars) {
                if (list.contains(item_id)) bars_item_ids += "," + item_id;
            }
        }

        String sql_1 = "select a1.item_id id,a1.item_name text,a1.item_method action,a1.item_type,\n" +
                " a1.iconCls,a1.isdataper from sys_menu a1 where a1.item_id in({0})";
        //sql_1 = StringUtils.format(sql_1, bars_item_ids);  //右上角导航提醒栏目，不需要权限

        String query_sql = "select a1.item_id id,a1.item_name text,a1.item_method action,a1.item_type,a1.iconCls,a1.isdataper from sys_menu a1,\n" +
                "                    (select item_id from sys_power_menu where isdel=0 and tb_type={0} and tb_id={1} union select item_id from sys_power_menu where isdel=0 and tb_type={2} and tb_id={3}) b1\n" +
                "                    where a1.item_id=b1.item_id and a1.isdel=0 and a1.isused=1{4}";

        String where = StringUtils.format("  and a1.item_id in({0})", StringUtils.joinAsList(list));
        String del_item_ids = BLL_UserPower.getUserCacheRemoveItemIds(UserUtils.getUserId());
        if (StringUtils.isNotEmpty(del_item_ids))
            where += StringUtils.format(" and a1.item_id not in({0})", del_item_ids);
        query_sql = StringUtils.format(query_sql, PerEnum.用户权限.getIndex(), UserUtils.getUserId(), PerEnum.角色权限.getIndex(), UserUtils.getRoleId(), where);

        if (UserUtils.isSuperAdminer()) query_sql = StringUtils.format(sql_1, StringUtils.joinAsList(list));
        else if (!bars_item_ids.equals("0")) query_sql += " union " + StringUtils.format(sql_1, bars_item_ids);

        JsonArray array_fixed = DBFunction.executeJsonArray(query_sql);
        if (array_fixed != null && array_fixed.size() > 1) {
            for (int i = 0; i < array_fixed.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array_fixed, i);
                int item_id = GsonUtils.tryParse(dr, "id", 0);
                JsonArray drs = GsonUtils.getWhereArrayByIds(array, "field_value", item_id + "");
                if (drs.size() > 0) {
                    String create_time = GsonUtils.getObjectValue(drs, 0, "create_time");
                    dr.addProperty("create_time", create_time);
                }
            }
            array_fixed = ArrayUtils.arrayOrderBy(array_fixed, "create_time asc", "string");
        }

        return array_fixed;
    }

    /**
     * 该用户设置的固定标签数
     */
    public static List<Integer> getFixedTabs() throws Exception {
        List<Integer> list_ids = BLL_Extend_Info.getListValues(ExtendEnum.固定标签.getValue(), UserUtils.getUserId(), "item_id", 0, "create_time");
        if (UserUtils.isSuperAdminer()) {
            return StringUtils.listToRepeat(list_ids);  //超级管理员
        }

        if (list_ids.size() == 0) return list_ids;
        String item_ids = StringUtils.joinAsList(list_ids);

        int user_id = UserUtils.getUserId();
        int role_id = UserUtils.getRoleId();
        String sql = "select a1.item_id from sys_menu a1, \n" +
                "(select item_id from sys_power_menu where isdel=0 and tb_type={0} and tb_id={1} union select item_id from sys_power_menu where isdel=0 and tb_type={2} and tb_id={3}) b1\n" +
                " where a1.item_id=b1.item_id and a1.isdel=0 and a1.isused=1{4} ";

        String where = StringUtils.format(" and a1.item_id in({0})", item_ids);
        String del_item_ids = BLL_UserPower.getUserCacheRemoveItemIds(UserUtils.getUserId());
        if (StringUtils.isNotEmpty(del_item_ids))
            where += StringUtils.format(" and a1.item_id not in({0})", del_item_ids);

        sql = StringUtils.format(sql, PerEnum.用户权限.getIndex(), user_id, PerEnum.角色权限.getIndex(), role_id, where);

        JsonArray array = DBFunction.executeJsonArray(sql);
        List<Integer> list = ArrayUtils.listByField(array, "item_id", 0);
        List<Integer> itemids = StringUtils.splitToList(item_ids);
        for (int item_id : SysConfigCache.getRemindItems()) {
            if (itemids.contains(item_id)) list.add(item_id); //导航提醒栏目(右上角提醒栏目)
        }
        return StringUtils.listToRepeat(list);
    }

    /**
     * 删除不在权限内的固定标签数
     */
    public static void deleteFixedTabs(List<Integer> list) throws Exception {
        if (list.size() > 0) {
            String delete_sql = "delete from tb_extend_info where table_name=@table_name " +
                    "and field_extend=@field_extend and table_id=@table_id and field_value not in('{0}')";
            List<DBParameter> where = new ArrayList<>();
            where.add(new DBParameter("@table_name", "=", ExtendEnum.固定标签.getValue()));
            where.add(new DBParameter("@table_id", "=", UserUtils.getUserId()));
            where.add(new DBParameter("@field_extend", "=", "item_id"));

            DBFunction.executeNonQuery(StringUtils.format(delete_sql, StringUtils.joinAsList(list, "','")), DBParameter.getParameter(where));
        }
    }
}

