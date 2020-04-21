package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.model.search.SearchDataModel;
import com.xinsite.core.model.search.SearchModel;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.List;
import java.util.Map;

/**
 * 系统管理：菜单栏目
 * create by zhangxiaxin
 */
public class BLL_Menu {

    /**
     * 拼接栏目查询条件
     */
    public static String getItemSearchWhere(List<SearchModel> searchs, List<DBParameter> ls) throws Exception {
        StringBuilder condition = new StringBuilder();
        if (searchs != null && searchs.size() > 0) {
            for (SearchModel search : searchs) {
                for (SearchDataModel field : search.datas) {
                    switch (field.field) {
                        case "item_name":
                            condition.append(" and a.item_name like @item_name");
                            ls.add(new DBParameter("@item_name", "like", "%" + field.value + "%"));
                            break;
                        case "item_pid":
                            int item_id = ValueUtils.tryParse(field.value, 0);
                            if (item_id > 0) {
                                String item_ids = TreeUtils.getTreeChildNodes(item_id, "sys_menu", "item_id", "pid");
                                if (!StringUtils.isEmpty(item_ids)) {
                                    condition.append(String.format(" and a.item_id in(%s)", item_ids));
                                }
                            }
                            break;
                        case "per_value":
                            condition.append(" and a.per_value like @per_value");
                            ls.add(new DBParameter("@per_value", "like", "%" + field.value + "%"));
                            break;
                        case "is_not_used":
                            condition.append(" and a.isused=0");
                            break;
                        case "isdataper":
                            condition.append(" and a.isdataper=" + field.value);
                            break;
                        case "isfun":
                            condition.append(" and a.isfun=" + field.value);
                            break;
                    }
                }
            }
        }
        return condition.toString();
    }

    /**
     * 根据条件获取栏目
     */
    public static JsonArray getItemTree(String condition, List<DBParameter> ls, boolean add_leaf) throws Exception {
        String sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.item_type,a.per_value,a.iconCls,a.isused,a.isdataper,a.ishistory,a.isfun,a.create_time,b.fun_names,a.expanded expand," +
                "case when  (select count(1) from sys_menu b where b.isdel=0 and (b.org_id=0 or b.org_id={1}) and b.pid=a.item_id)>0 then 'false' else 'true' end leaf " +
                "from sys_menu a left join view_item_fun b on a.item_id=b.item_id where a.isdel=0 {0} order by item_sort";

        condition = BLL_UserPower.addSuperItems(condition, "a");
        if (!add_leaf) {
            sql = "select a.item_id id,a.pid,a.item_name text,a.item_method,a.item_type,a.per_value,a.iconCls,a.isused,a.isdataper,a.ishistory,a.isfun,a.create_time,b.fun_names,a.expanded expand" +
                    " from sys_menu a left join view_item_fun b on a.item_id=b.item_id where a.isdel=0 {0} order by item_sort";
        }
        sql = StringUtils.format(sql, condition, UserUtils.getOrgId());
        return DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
    }

    /**
     * 根据条件获取栏目
     */
    public static JsonArray getAllItems() throws Exception {
        String sql = "select item_id id,item_name text,pid,disabled,iconCls from sys_menu b where isdel=0 and isused=1 order by item_sort";
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 根据条件获取栏目
     */
    public static JsonArray getItems(int item_id) throws Exception {
        String sql = "select a.item_id id,item_name text,item_method action,item_type,iconCls,expanded expand,a.isdataper,\n" +
                "            case when  (select count(1) from sys_menu c where c.isdel=0 and c.pid=a.item_id)>0 then 'false' else 'true' end leaf\n" +
                "                from sys_menu a where a.isdel=0 and a.item_id={0} and a.isused=1 order by a.item_sort";

        return DBFunction.executeJsonArray(StringUtils.format(sql, item_id));
    }

    /**
     * 根据条件获取栏目
     */
    public static JsonArray getGridTreeItem(String Condition) throws Exception {
        String sql = "select 0 pid,item_name text,iconCls,item_sort,a.item_id id,'true' leaf\n" +
                "        from sys_menu a where isdel=0 and isused=1 {0} order by item_sort";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition));
    }

    /**
     * 获取排序号
     */
    public static int getSerialCode(int pid) {
        String str = "ifnull(max(item_sort),0)+1";
        String strSql = StringUtils.format("select {0} from sys_menu where pid={1}", str, pid);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 保存栏目表排序
     */
    public static boolean saveColumnsSort(List<Map> list) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_menu set pid={0},item_sort={1} where item_id={2};";
        for (Map ht : list) {
            sb.append(StringUtils.format(sql, ht.get("pid"), ht.get("index"), ht.get("id")));
        }
        return DBFunction.executeNonQuery(sb.toString()) > 0;
    }

    /**
     * 删除列表
     */
    public static boolean deleteByIds(String id, int idsVal) throws Exception {
        String strSql = StringUtils.format("update sys_menu set isdel=1 where {0} in({1})", id, idsVal);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 编辑栏目信息，栏目管理
     */
    public static int saveItemTableInfo(Map<String, Object> ht, int item_id) {
        DBFunction.startTransaction();
        try {
            if (item_id == 0) {  //新增
                item_id = DBFunction.insertByTbName(ht, "sys_menu");
            } else {
                if (!DBFunction.updateByTbName(ht, "sys_menu", "item_id=" + item_id))
                    item_id = 0;
            }

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            //LogError.Write(LogType.Error, ex.ToString());
            return 0;
        }
        return item_id;
    }

    /**
     * 获取栏目名
     */
    public static String getItemName(int item_id) {
        String querySql = "select item_name from sys_menu where item_id=" + item_id;
        Object obj = DBFunction.executeScalar(querySql);
        return obj == null ? "" : obj.toString();
    }

    /**
     * 根据条件获取栏目
     */
    public static JsonArray getItem(int item_id) throws Exception {
        String querySql = "select * from sys_menu where item_id=" + item_id;
        return DBFunction.executeJsonArray(querySql);
    }

    /**
     * 是否是历史记录栏目
     */
    public static boolean isHistoryItem(int item_id) {
        String querySql = "select ishistory from sys_menu where item_id=" + item_id;
        return NumberUtils.strToInt(DBFunction.executeScalar(querySql)) == 1;
    }

    /**
     * 是否是回收站栏目
     */
    public static boolean isRecycleItem(int item_id) {
        String querySql = "select isrecycle from sys_menu where item_id=" + item_id;
        return NumberUtils.strToInt(DBFunction.executeScalar(querySql)) == 1;
    }

    /**
     * 据栏目Id获取栏目数据权限
     */
    public static int getDataPermissionByItemId(int item_id) {
        String querySql = "select isdataper from sys_menu where item_id=" + item_id;
        Object obj = DBFunction.executeScalar(querySql);
        return NumberUtils.strToInt(obj);
    }

    /**
     * 获取栏目某个字段值
     */
    public static <T> T getFieldByMenu(int item_id, String field, T defaultValue) {
        if (item_id == 0) return defaultValue;
        String querySql = "select %s from sys_menu where item_id=" + item_id;
        String str = DBFunction.executeScalar(String.format(querySql, field));
        return ValueUtils.tryParse(str, defaultValue);
    }

    /**
     * 控制器权限值是否存在
     */
    public static boolean isExistPerValue(int item_id, String per_value) throws Exception {
        if (StringUtils.isEmpty(per_value)) return false;
        String sql = "select item_id,per_value from sys_menu where per_value=@per_value or item_id=" + item_id;
        JsonArray array = DBFunction.executeJsonArray(sql, new DBParameter("@per_value", "=", per_value));
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (GsonUtils.tryParse(dr, "per_value", "").equalsIgnoreCase(per_value)) {
                if (item_id == 0)
                    return true;
                else if (item_id != dr.get("item_id").getAsInt())
                    return true;
            }
        }
        return false;
    }

}
