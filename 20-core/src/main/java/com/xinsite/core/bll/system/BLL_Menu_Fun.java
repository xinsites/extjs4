package com.xinsite.core.bll.system;

import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 系统管理：菜单栏目功能
 * create by zhangxiaxin
 */
public class BLL_Menu_Fun {

    /**
     * 获取排序号
     */
    public static int getSerialCode(int item_id) {
        String str = "ifnull(max(serialcode),0)+1";
        String strSql = StringUtils.format("select {0} from sys_menu_fun where item_id={1}", str, item_id);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 功能列表保存排序
     */
    public static void saveGridSort(String key, String sortVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_menu_fun set serialcode={0} where {2}={1};";
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
    public static boolean deleteByIds(String idsVal) throws Exception {
        String strSql = StringUtils.format("update sys_menu_fun set isdel=1 where fun_id in({0})", idsVal);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 恢复删除
     */
    public static boolean restoreById(int fun_id) throws Exception {
        String strSql = StringUtils.format("update sys_menu_fun set isdel=0 where fun_id={0}", fun_id);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 永久删除
     */
    public static boolean deleteById(int fun_id) throws Exception {
        String strSql = StringUtils.format("delete from sys_menu_fun where fun_id={0}", fun_id);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 功能列表新增/修改表单信息
     */
    public static int saveInfo(Map<String, Object> ht, int Id) throws Exception {
        if (Id == 0) {
            Id = DBFunction.insertByTbName(ht, "sys_menu_fun");
        } else {
            DBFunction.updateByTbName(ht, "sys_menu_fun", "fun_id=" + Id);
        }
        return Id;
    }

    /**
     * 控制器权限值是否存在
     */
    public static boolean isExistPerValue(int fun_id, String per_value) throws Exception {
        if (StringUtils.isEmpty(per_value)) return false;
        String sql = "select fun_id,per_value from sys_menu_fun where per_value=@per_value or fun_id=" + fun_id;
        JsonArray array = DBFunction.executeJsonArray(sql, new DBParameter("@per_value", "=", per_value));
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (GsonUtils.tryParse(dr, "per_value", "").equalsIgnoreCase(per_value)) {
                if (fun_id == 0)
                    return true;
                else if (fun_id != dr.get("fun_id").getAsInt())
                    return true;
            }
        }
        return false;
    }

    /**
     * ItemId是否存在（栏目中唯一）
     */
    public static boolean isExistItemId(int fun_id, int item_id, String itemid) throws Exception {
        String sql = "select fun_id,itemid from sys_menu_fun where item_id={0} and (itemid=@itemid or fun_id={1})";
        sql = StringUtils.format(sql, item_id, fun_id);
        JsonArray array = DBFunction.executeJsonArray(sql, new DBParameter("@itemid", "=", itemid));
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (GsonUtils.tryParse(dr, "itemid", "").equalsIgnoreCase(itemid)) {
                if (fun_id == 0)
                    return true;
                else if (fun_id != dr.get("fun_id").getAsInt())
                    return true;
            }
        }
        return false;
    }

    /**
     * 菜单栏目是否存在ItemId
     */
    public static boolean isExistItemId(int item_id, String itemid) {
        String sql = "select count(1) from sys_menu_fun where itemid=@itemid and isdel=0 and item_id=" + item_id;
        return NumberUtils.strToInt(DBFunction.executeScalar(sql, new DBParameter("@itemid", "=", itemid))) > 0;
    }

    /**
     * 栏目功能批量保存
     */
    public static boolean saveItemFunInfo(String item_ids, String pre_permission, String fun_ids, String type) {
        DBFunction.startTransaction();
        try {
            List<Integer> list = StringUtils.splitToList(item_ids);
            JsonArray dt = BLL_Menu_Fun.getGridByIds(fun_ids);
            for (int item_id : list) {
//                if ("重置".equals(type)) {
//                    String del_sql = "delete from sys_menu_fun where item_id=" + item_id;  //删除原有的配置
//                    DBFunction.executeNonQuery(del_sql);
//                }
                for (int i = 0; i < dt.size(); i++) {
                    JsonObject dr = GsonUtils.getObject(dt, i);
                    String per_value = GsonUtils.tryParse(dr, "per_value", "");
                    String itemid = GsonUtils.tryParse(dr, "itemid", "");
                    Hashtable ht = new Hashtable();
                    ht.put("name", GsonUtils.tryParse(dr, "name", ""));        //功能名称

                    if (!StringUtils.isEmpty(pre_permission)) {
                        per_value = pre_permission + ":" + per_value;
                    } else {
                        per_value = String.format("item:%d:%s", item_id, per_value);
                    }
                    ht.put("per_value", per_value);
                    if ("替换".equals(type)) {
                        if (!BLL_Menu_Fun.isExistItemId(item_id, itemid)) {
                            ht.put("item_id", item_id);
                            ht.put("itemid", itemid);
                            ht.put("serialcode", i + 1); //BLL_Menu_Fun.getSerialCode(item_id)
                            DBFunction.insertByTbName(ht, "sys_menu_fun");
                        } else {
                            Map<String, Object> ht_where = new HashMap();
                            ht_where.put("item_id", item_id);
                            ht_where.put("itemid", itemid);
                            ht.put("isdel", 0);
                            ht.put("itemid", itemid);
                            ht.put("serialcode", i + 1);
                            DBFunction.updateByTbName(ht, "sys_menu_fun", DBParameter.getParameter(ht_where));
                        }
                    }
//                    else if ("重置".equals(type) || "追加".equals(type)) {
//                        ht.put("item_id", item_id);
//                        ht.put("itemid", itemid);
//                        ht.put("serialcode", BLL_Menu_Fun.getSerialCode(item_id));
//                        DBFunction.insertByTbName(ht, "sys_menu_fun");
//                    }
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
     * 获取一个栏目功能实例
     */
    public static Hashtable getItemFun(int item_id, String name, String itemid, int serialcode) {
        Hashtable ht = new Hashtable();
        ht.put("item_id", item_id);    //栏目Id
        ht.put("name", name);        //功能名称
        ht.put("itemid", itemid);
        ht.put("serialcode", serialcode);
        return ht;
    }

    /**
     * 功能表树(全部数据)
     */
    public static JsonArray getTreeComboBox() throws Exception {
        String sql = "select fun_id id,0 pid,name text\n" +
                "        from sys_menu_fun a where a.isdel=0 and a.item_id=0 order by a.serialcode ";

        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 功能表树(全部数据)
     */
    public static JsonArray getGridByIds(String Ids) throws Exception {
        Ids = StringUtils.joinAsFilter(Ids);
        if (StringUtils.isEmpty(Ids)) Ids = "0";
        String sql = "select fun_id,name,itemid,per_value from sys_menu_fun a where a.item_id=0 and fun_id in({0}) order by a.serialcode";
        return DBFunction.executeJsonArray(StringUtils.format(sql, Ids));
    }

    /**
     * 获取重复的权限值
     */
    public static JsonArray getRepeatPerValue(String pre_per) throws Exception {
        String sql = "select per_value from sys_menu_fun group by per_value having count(per_value)>1";

        return DBFunction.executeJsonArray(sql);
    }
}
