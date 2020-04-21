package com.xinsite.core.bll.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.PerEnum;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.model.system.PowerSearchModel;
import com.xinsite.core.bll.system.BLL_User;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.dal.uitls.Utils_Context;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 根据用户获取各类菜单栏目、功能权限
 * create by zhangxiaxin
 */
public class BLL_UserPower {

    public static String addSuperItems(String condition, String alias) {
        if (!StringUtils.isEmpty(alias) && alias.indexOf(".") == -1) {
            alias = alias + ".";
        }
        if (!UserUtils.isSuperAdminer()) {
            String super_items = Global.getConfig("config.super_items"); //只有超级管理员能看到的栏目
            if (!StringUtils.isEmpty(super_items) && !super_items.equals("0"))
                condition += StringUtils.format(" and {0}item_id not in({1})", alias, super_items);
        }
        return condition;
    }

    /**
     * 获取该用户剔除的菜单栏目ids
     */
    public static String getUserRemoveItemIds(int user_id) {
        String sql = "select item_id from sys_power_menu where tb_type=%d and tb_id=%d and del_item=1 and isdel=0";
        JsonArray array = null;
        try {
            array = DBFunction.executeJsonArray(String.format(sql, PerEnum.剔除权限.getIndex(), user_id));
            List<Integer> list = ArrayUtils.listByField(array, "item_id", 0);
            return StringUtils.joinAsList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取用户缓存剔除的菜单栏目ids
     */
    public static String getUserCacheRemoveItemIds(int user_id) {
        UserCacheService userCacheService = Utils_Context.getBean(UserCacheService.class);
        return userCacheService.getCacheRemoveItemIds(user_id);
    }

    /**
     * sql加剔除栏目条件
     */
    public static String addRemoveItemIds(String sql, String del_item_ids, String... alias_name) {
        String alias = StringUtils.EMPTY;
        if (alias_name.length > 0) alias = alias_name[0];
        if (!StringUtils.isEmpty(alias) && alias.indexOf(".") == -1) {
            alias = alias + ".";
        }
        if (!StringUtils.isEmpty(del_item_ids)) {
            if (del_item_ids.indexOf(",") > 0)
                sql += StringUtils.format(" and {0}item_id not in({1})", alias, del_item_ids);
            else sql += StringUtils.format(" and {0}item_id !={1}", alias, del_item_ids);
        }
        return sql;
    }

    /**
     * sql加栏目条件
     */
    public static String addItemIds(String sql, String item_ids, String... alias_name) {
        String alias = StringUtils.EMPTY;
        if (alias_name.length > 0) alias = alias_name[0];
        if (!StringUtils.isEmpty(alias) && alias.indexOf(".") == -1) {
            alias = alias + ".";
        }
        if (!StringUtils.isEmpty(item_ids)) {
            if (item_ids.indexOf(",") > 0) sql += StringUtils.format(" and {0}item_id in({1})", alias, item_ids);
            else sql += StringUtils.format(" and {0}item_id={1}", alias, item_ids);
        }
        return sql;
    }

    /**
     * 获取该用户实际菜单栏目的sql
     */
    public static String getUserItemIdSql(int user_id, PowerSearchModel powerModel) throws Exception {
        String user_sql = StringUtils.EMPTY;
        String role_sql = StringUtils.EMPTY;
        String del_item_ids = StringUtils.EMPTY; //用户剔除的栏目ids
        int role_id = 0; //用户角色
        if (powerModel.del_per == 1) {
            del_item_ids = BLL_UserPower.getUserCacheRemoveItemIds(user_id);
        }
        if (powerModel.user_per == 1) {
            user_sql = "select item_id from sys_power_menu where tb_type=%d and tb_id=%d and isdel=0";
            user_sql = String.format(user_sql, PerEnum.用户权限.getIndex(), user_id);
            if (powerModel.del_per == 1) user_sql = BLL_UserPower.addRemoveItemIds(user_sql, del_item_ids);
        }
        if (powerModel.role_per == 1) {
            role_id = BLL_User.getFieldByUser(user_id, "role_id", 0); //用户角色
            if (role_id > 0) {
                role_sql = "select item_id from sys_power_menu where tb_type=%d and tb_id=%d and isdel=0";
                role_sql = String.format(role_sql, PerEnum.角色权限.getIndex(), role_id);
                if (powerModel.del_per == 1) role_sql = BLL_UserPower.addRemoveItemIds(role_sql, del_item_ids);
            }
        }
        if (powerModel.user_per == 1 && powerModel.role_per == 1) {
            return user_sql + " union " + role_sql;
        } else if ((powerModel.user_per + powerModel.role_per) > 0) {
            if (powerModel.user_per == 1) return user_sql;
            if (powerModel.role_per == 1) return role_sql;
        } else if (powerModel.del_per == 1 && !StringUtils.isEmpty(del_item_ids)) {
            return del_item_ids;
        }
        return "0";
    }

    /**
     * 登录用户实际菜单栏目的sql
     */
    public static String getUserItemIdSql(LoginUser loginUser) throws Exception {
        String del_item_ids = BLL_UserPower.getUserCacheRemoveItemIds(loginUser.getUserId()); //用户剔除的栏目ids
        String item_id_sql = "select item_id from sys_power_menu where tb_type=%d and tb_id=%d and isdel=0";
        item_id_sql = String.format(item_id_sql, PerEnum.用户权限.getIndex(), loginUser.getUserId());
        item_id_sql = BLL_UserPower.addRemoveItemIds(item_id_sql, del_item_ids);

        if (loginUser.getRoleId() > 0) {
            item_id_sql += " union select item_id from sys_power_menu where tb_type=%d and tb_id=%d and isdel=0";
            item_id_sql = String.format(item_id_sql, PerEnum.角色权限.getIndex(), loginUser.getRoleId());
            item_id_sql = BLL_UserPower.addRemoveItemIds(item_id_sql, del_item_ids);
        }
        return item_id_sql;
    }

    /**
     * 【用户管理】栏目权限树形目录
     *
     * @param self 只包含配置栏目
     */
    public static JsonArray getUserPerItemTree(String condition, int user_id, int self, PowerSearchModel powerModel) throws Exception {
        condition = BLL_UserPower.addSuperItems(condition, "a");
        String str = "'' all_fun_ids,'' checkgroup,";  //"'[[1, \"是\"], [0, \"否\"]]' checkgroup,";
        String sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.iconCls,a.isused,a.isdataper,a.create_time,'' fun_ids,'' data_per,'' data_ids,%s\n" +
                "        a.expanded expand,case when (select count(1) from sys_menu b where b.isdel=0 and b.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                "        from sys_menu a where a.isdel=0 and a.isused=1 %s order by a.item_sort";
        //加角色权限，并且是超级管理员
        if (powerModel.role_per == 1 && BLL_User.isSuperAdminer(user_id)) {
            return DBFunction.executeJsonArray(String.format(sql, str, condition));
        } else {
            if (self > 0) {
                String item_sql = BLL_UserPower.getUserItemIdSql(user_id, powerModel);
                if (item_sql.indexOf("select") >= 0) {
                    sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.iconCls,a.isused,a.isdataper,a.create_time,'' fun_ids,'' data_per,'' data_ids,%s\n" +
                            " a.expanded expand,case when  (select count(1) from sys_menu c where c.isdel=0 and c.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                            " from sys_menu a,(%s) b where a.item_id=b.item_id and a.isdel=0 and a.isused=1 %s order by a.item_sort";
                    return DBFunction.executeJsonArray(String.format(sql, str, item_sql, condition));
                } else {
                    if (item_sql.indexOf(",") > 0) condition += String.format(" and a.item_id not in(%s)", item_sql);
                    else condition += " and a.item_id=" + item_sql;
                }
            }
            return DBFunction.executeJsonArray(String.format(sql, str, condition));
        }
    }

    /**
     * 【用户管理】获取用户权限（用户权限+角色权限+剔除权限）
     */
    public static JsonArray getUserPermission(int user_id, PowerSearchModel powerModel) throws Exception {
        String sql = "select a1.item_id,(select group_concat(distinct fun_id separator ',') from sys_power_fun fun where (fun.pm_id=u.pm_id or fun.pm_id=r.pm_id) \n" +
                " and not exists(select 1 from sys_power_fun df where df.pm_id=d.pm_id and df.fun_id=fun.fun_id)) fun_ids, \n" +
                "case when u.data_per>0 then u.data_per else r.data_per end data_per, \n" +
                " case when u.data_per>0 then u.data_ids else r.data_ids end data_ids \n" +
                " from (select item_id from  sys_power_menu where tb_type=@tb_type1 and tb_id=@user_id and isdel=0 union \n" +
                " select item_id from  sys_power_menu where tb_type=@tb_type2 and tb_id=@role_id and isdel=0) a1 \n" +
                "left join sys_power_menu u on a1.item_id=u.item_id and u.isdel=0 and u.tb_type=@tb_type1 and u.tb_id=@user_id\n" +
                "left join sys_power_menu r on a1.item_id=r.item_id and r.isdel=0 and r.tb_type=@tb_type2 and r.tb_id=@role_id\n" +
                "left join sys_power_menu d on a1.item_id=d.item_id and d.isdel=0 and d.tb_type=@tb_type3 and d.tb_id=@user_id3\n" +
                "where not exists(select 1 from sys_power_menu c1 where a1.item_id=c1.item_id and c1.tb_type=@tb_type3 and c1.tb_id=@user_id3 and c1.del_item=1)";

        int role_id = 0;
        int user_id3 = user_id;
        if (powerModel.role_per == 1) role_id = BLL_User.getFieldByUser(user_id, "role_id", 0); //用户角色
        if (powerModel.user_per == 0) user_id = 0; //不包括用户权限
        if (powerModel.del_per == 0) {            //不包括剔除权限
            user_id3 = 0;
            sql = "select a1.item_id,(select group_concat(distinct fun_id separator ',') from sys_power_fun fun where " +
                    "fun.pm_id=u.pm_id or fun.pm_id=r.pm_id) fun_ids, \n" +
                    "case when u.data_per>0 then u.data_per else r.data_per end data_per, \n" +
                    " case when u.data_per>0 then u.data_ids else r.data_ids end data_ids \n" +
                    " from (select item_id from  sys_power_menu where tb_type=@tb_type1 and tb_id=@user_id and isdel=0 union \n" +
                    " select item_id from  sys_power_menu where tb_type=@tb_type2 and tb_id=@role_id and isdel=0) a1 \n" +
                    "left join sys_power_menu u on a1.item_id=u.item_id and u.isdel=0 and u.tb_type=@tb_type1 and u.tb_id=@user_id\n" +
                    "left join sys_power_menu r on a1.item_id=r.item_id and r.isdel=0 and r.tb_type=@tb_type2 and r.tb_id=@role_id";
        } else if (powerModel.role_per == 0 && powerModel.user_per == 0 && powerModel.del_per == 1) {
            sql = "select * from (select a1.item_id,(select group_concat(distinct fun_id separator ',') from sys_power_fun fun where  \n" +
                    "fun.pm_id=a1.pm_id ) fun_ids, case when a1.data_per>0 then a1.data_per else a1.data_per end data_per, \n" +
                    " case when a1.data_per>0 then a1.data_ids else a1.data_ids end data_ids,a1.del_item  \n" +
                    " from sys_power_menu a1 where a1.isdel=0 and a1.tb_type=@tb_type3 and a1.tb_id=@user_id3 " +
                    ") aa where aa.del_item=1 or aa.fun_ids is not null";
        }

        return DBFunction.executeJsonArray(sql,
                new DBParameter("@tb_type1", PerEnum.用户权限.getIndex()),
                new DBParameter("@tb_type2", PerEnum.角色权限.getIndex()),
                new DBParameter("@tb_type3", PerEnum.剔除权限.getIndex()),
                new DBParameter("@user_id", user_id),
                new DBParameter("@user_id3", user_id3),
                new DBParameter("@role_id", role_id));
    }

    /**
     * 登录用户左边菜单栏目
     */
    public static JsonArray getLoginItems(String condition, LoginUser loginUser) throws Exception {
        String sql = null;
        if (loginUser.isSuperAdminer()) {
            sql = "select a.item_id id,a.item_name text,a.item_method action,a.item_type,a.iconCls,a.expanded expand,a.isdataper,\n" +
                    " case when (select count(1) from sys_menu c where c.isdel=0 and c.isused=1 and c.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                    " from sys_menu a where a.isdel=0 and a.isused=1 {0} order by a.item_sort";
            sql = StringUtils.format(sql, condition);
            return DBFunction.executeJsonArray(sql);
        } else {
            sql = "select a.item_id id,a.item_name text,a.item_method action,a.item_type,a.iconCls,a.expanded expand,a.isdataper,\n" +
                    " case when (select count(1) from sys_menu c where c.isdel=0 and c.isused=1 and c.pid=a.item_id and c.pid=b.item_id)>0 then 'false' else 'true' end leaf\n" +
                    " from sys_menu a,({0}) b where a.item_id=b.item_id and a.isdel=0 and a.isused=1 {1} order by a.item_sort";
            String item_id_sql = BLL_UserPower.getUserItemIdSql(loginUser);
            condition = BLL_UserPower.addSuperItems(condition, "a");

            sql = StringUtils.format(sql, item_id_sql, condition);
            return DBFunction.executeJsonArray(sql);
        }
    }

    /**
     * 登录用户栏目功能信息（用户权限+角色权限+剔除权限）
     */
    public static JsonArray getLoginItemPower(int item_id, LoginUser loginUser) throws Exception {
        return BLL_UserPower.getLoginItemPower(item_id + "", loginUser);
    }

    /**
     * 登录用户栏目功能信息（用户权限+角色权限+剔除权限）
     */
    public static JsonArray getLoginItemPower(String item_ids, LoginUser loginUser) throws Exception {
        String sql = "select b1.item_id,group_concat(distinct a1.itemid separator ',') all_item_ids,\n" +
                "(select group_concat(distinct c1.itemid separator ',')  from sys_menu_fun c1,sys_power_fun d1\n" +
                " where c1.isdel=0 and c1.fun_id=d1.fun_id and (d1.pm_id=u.pm_id or d1.pm_id=r.pm_id)\n" +
                " and not exists(select 1 from sys_power_fun df where df.pm_id=d.pm_id and df.fun_id=c1.fun_id)) item_ids, \n" +
                "case when u.data_per>0 then u.data_per else r.data_per end data_per, \n" +
                "case when u.data_per>0 then u.data_ids else r.data_ids end data_ids \n" +
                "from sys_menu b1 left join sys_menu_fun a1 on a1.item_id=b1.item_id and a1.isdel=0 \n" +
                "left join sys_power_menu u on b1.item_id=u.item_id and u.isdel=0 and u.tb_type=@tb_type1 and u.tb_id=@user_id %s\n" +
                "left join sys_power_menu r on b1.item_id=r.item_id and r.isdel=0 and r.tb_type=@tb_type2 and r.tb_id=@role_id %s\n" +
                "left join sys_power_menu d on b1.item_id=d.item_id and d.isdel=0 and d.tb_type=@tb_type3 and d.tb_id=@user_id\n" +
                "where (b1.item_id=u.item_id or b1.item_id=r.item_id) %s group by b1.item_id";

        if (UserUtils.isSuperAdminer()) {
            sql = "select a1.item_id,group_concat(distinct b1.itemid separator ',') item_all_ids,\n" +
                    "group_concat(distinct b1.itemid separator ',') item_ids,4 data_per,'' data_ids\n" +
                    "from sys_menu a1 left join sys_menu_fun b1 on a1.item_id=b1.item_id and b1.isdel=0\n" +
                    " where a1.isdel=0 and a1.isused=1 %s group by a1.item_id";
            String where = BLL_UserPower.addItemIds("", item_ids, "a1");
            return DBFunction.executeJsonArray(String.format(sql, where));
        }
        String del_item_ids = BLL_UserPower.getUserCacheRemoveItemIds(loginUser.getUserId());
        String u_del_ids = BLL_UserPower.addRemoveItemIds("", del_item_ids, "u");
        String r_del_ids = BLL_UserPower.addRemoveItemIds("", del_item_ids, "r");
        sql = String.format(sql, u_del_ids, r_del_ids, BLL_UserPower.addItemIds("", item_ids, "b1"));

        return DBFunction.executeJsonArray(sql,
                new DBParameter("@tb_type1", PerEnum.用户权限.getIndex()),
                new DBParameter("@tb_type2", PerEnum.角色权限.getIndex()),
                new DBParameter("@tb_type3", PerEnum.剔除权限.getIndex()),
                new DBParameter("@user_id", loginUser.getUserId()),
                new DBParameter("@role_id", loginUser.getRoleId()));
    }

    /**
     * 计算各申请的删除撤销权限
     */
    public static void setTaskPermission(JsonArray array) throws Exception {
        if (array == null || array.size() == 0) return;
        if (UserUtils.isSuperAdminer()) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                dr.addProperty("btn_del", "true");
                dr.addProperty("btn_canc", "true");
            }
        } else {
            List<Integer> list = ArrayUtils.listByField(array, "item_id", 0);
            list = StringUtils.listToRepeat(list);
            JsonArray pers = BLL_UserPower.getLoginItemPower(StringUtils.joinAsList(list), UserUtils.getLoginUser());
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                int item_id = GsonUtils.tryParse(dr, "item_id", 0);
                JsonArray drs = GsonUtils.getWhereArray(pers, "item_id", item_id);
                if (drs.size() > 0) {
                    String item_ids = GsonUtils.getObjectValue(drs, 0, "item_ids");
                    if (item_ids.indexOf("btn_del") >= 0) dr.addProperty("btn_del", "true");
                    if (item_ids.indexOf("btn_canc") >= 0) dr.addProperty("btn_canc", "true");
                }
            }
        }
    }

    /**
     * 获取用户角色控制器权限值
     */
    public static Set<String> getUserRolePermissions(int role_id) {
        String QuerySql = "select role_per_value from sys_role where role_id=" + role_id;
        String per_value = DBFunction.executeScalar(QuerySql);
        Set<String> permsSet = new HashSet<>();
        if (StringUtils.isNotEmpty(per_value)) permsSet.add(per_value);
        return permsSet;
    }

    /**
     * 获取用户栏目功能控制器权限值
     */
    public static Set<String> getUserFunPermissions(LoginUser loginUser) {
        Set<String> permsSet = new HashSet<>();
        String querySql = "select distinct a1.per_value\n" +
                "from view_menu_fun a1,view_power_fun b1 where a1.item_id=b1.item_id and a1.fun_id=b1.fun_id\n" +
                "and ((b1.tb_type=@tb_type1 and b1.tb_id=@user_id) or (b1.tb_type=@tb_type2 and b1.tb_id=@role_id)) %s \n" +
                "and not exists(select 1 from view_power_fun c2 where c2.tb_type=@tb_type3 and c2.tb_id=@user_id and c2.fun_id=b1.fun_id)\n" +
                "union select distinct a1.grid_per_value per_value\n" +
                "from view_menu_fun a1 left join view_power_fun b1 on a1.item_id=b1.item_id and a1.fun_id=b1.fun_id\n" +
                "and ((b1.tb_type=@tb_type1 and b1.tb_id=@user_id) or (b1.tb_type=@tb_type2 and b1.tb_id=@role_id)) \n" +
                "where a1.grid_per_value is not null %s ";

        String del_item_ids = BLL_UserPower.getUserCacheRemoveItemIds(loginUser.getUserId());
        del_item_ids = BLL_UserPower.addRemoveItemIds("", del_item_ids, "a1");
        querySql = String.format(querySql, del_item_ids, del_item_ids);
        try {
            JsonArray array = DBFunction.executeJsonArray(querySql,
                    new DBParameter("@tb_type1", PerEnum.用户权限.getIndex()),
                    new DBParameter("@tb_type2", PerEnum.角色权限.getIndex()),
                    new DBParameter("@tb_type3", PerEnum.剔除权限.getIndex()),
                    new DBParameter("@user_id", loginUser.getUserId()),
                    new DBParameter("@role_id", loginUser.getRoleId()));
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject dr = GsonUtils.getObject(array, i);
                    String per_value = GsonUtils.tryParse(dr, "per_value", "");
                    if (StringUtils.isNotEmpty(per_value)) permsSet.add(per_value);
//                    BLL_UserPower.addPermissions(permsSet, GsonUtils.tryParse(dr, "per_value", ""));
//                    BLL_UserPower.addPermissions(permsSet, GsonUtils.tryParse(dr, "grid_per_value", ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        permsSet.add("xxxxxxxxxxxxxx");
        return permsSet;
    }

    /**
     * 添加一个控制器权限值
     */
    public static void addPermissions(Set<String> permsSet, String per_value) {
        if (StringUtils.isNotEmpty(per_value)) {
            if (!permsSet.contains(per_value)) permsSet.add(per_value);
        }
    }
}
