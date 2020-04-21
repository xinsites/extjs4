package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.collect.MapUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.system.BLL_Menu;
import com.xinsite.core.enums.FieldTagEnum;
import com.xinsite.core.enums.ObjectTypeEnum;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信息共享表
 * create by zhangxiaxin
 */
public class BLL_Share {

    /**
     * 信息介绍，根据栏目Id获取主键值
     */
    public static int getIdLeafByItemid(int item_id) {
        String sql = "select max(idleaf) from tb_info_share where item_id=@item_id";
        Object obj = DBFunction.executeScalar(sql, new DBParameter("@item_id", "=", item_id));
        return NumberUtils.strToInt(obj);
    }

    /**
     * 获取Map中指定字段的值
     */
    public static String getMapValue(Map<String, Object> tb, String title_field) {
        if (StringUtils.isEmpty(title_field)) return "";
        for (Map.Entry<String, Object> entry : tb.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(title_field))
                return entry.getValue() != null ? entry.getValue().toString() : null;
        }
        return "";
    }

    /**
     * 新增信息到共享设计表
     */
    public static void addInfoShare(Map<String, Object> tb, String table_name, int item_id, long idleaf) throws Exception {
        if (item_id > 0 && idleaf > 0) {
            boolean ishistory = BLL_Menu.isHistoryItem(item_id);
            boolean isrecycle = BLL_Menu.isRecycleItem(item_id);
            if (!ishistory && !isrecycle) return; //不记录历史记录&&不进回收站，不进入共同信息表

            JsonArray array = BLL_Design.getTableFields(table_name);
            String title_field = BLL_Design.getFieldName(array, FieldTagEnum.标题字段.getValue());
            if (StringUtils.isEmpty(title_field)) title_field = "title";

            if (array.size() > 0 && MapUtils.isExistHashtable(tb, title_field)) {
                String tid = GsonUtils.getObjectValue(array, 0, "tid");
                //新增之前删除，防止意外清除了设计表，而没有清除tb_info_share
                DBFunction.executeNonQuery(StringUtils.format("delete from tb_info_share where item_id={0} and idleaf={1}", item_id, idleaf));
                Map map = new HashMap();
                map.put("share_uuid", IdGenerate.buildUUID());  //e0a953c3ee6040eaa9fae2b667060e09
                map.put("item_id", item_id);
                map.put("idleaf", idleaf);
                map.put("create_time", DateUtils.getDateTime());
                map.put("title", BLL_Share.getMapValue(tb, title_field));
                map.put("tid", tid);

                int create_uid = 0;
                String creater_field = BLL_Design.getFieldName(array, FieldTagEnum.创建人字段.getValue());
                if (tb.containsKey(creater_field)) {
                    create_uid = NumberUtils.strToInt(tb.get(creater_field).toString());
                    map.put("create_uid", create_uid);
                } else {
                    map.put("create_uid", UserUtils.getUserId());
                }
                String org_id = BLL_Design.getFieldName(array, FieldTagEnum.机构号.getValue());
                if (tb.containsKey(org_id)) {
                    map.put("org_id", tb.get(org_id).toString());
                } else {
                    map.put("org_id", UserUtils.getOrgId());
                }

                DBFunction.insertByTbName(map, "tb_info_share");
                if (ishistory) {
                    String store_data = BLL_Store.getStoreData(array, item_id, idleaf);
                    BLL_Store.addHistoryRecord(store_data, item_id, idleaf, create_uid, "新增");
                }
            }
        }
    }

    /**
     * 修改信息到共享设计表
     */
    public static void modInfoShare(Map<String, Object> tb, String table_name, int item_id, long idleaf) throws Exception {
        if (item_id > 0 && idleaf > 0) {
            boolean ishistory = BLL_Menu.isHistoryItem(item_id);
            boolean isrecycle = BLL_Menu.isRecycleItem(item_id);
            if (!ishistory && !isrecycle) return; //不记录历史记录&&不进回收站，不进入共同信息表

            JsonArray array = BLL_Design.getTableFields(table_name);
            String title_field = BLL_Design.getFieldName(array, FieldTagEnum.标题字段.getValue());
            if (StringUtils.isEmpty(title_field)) title_field = "title";

            if (MapUtils.isExistHashtable(tb, title_field)) {  //记录公共表
                List<DBParameter> where = new ArrayList<>();
                where.add(new DBParameter("item_id", "=", item_id));
                where.add(new DBParameter("idleaf", "=", idleaf));

                Map link = new HashMap();
                link.put("title", BLL_Share.getMapValue(tb, title_field));
                link.put("modify_time", DateUtils.getDateTime());
                int modify_uid = 0;
                String modify_field = BLL_Design.getFieldName(array, FieldTagEnum.修改人字段.getValue());

                if (MapUtils.isExistHashtable(tb, modify_field)) {
                    link.put("modify_uid", BLL_Share.getMapValue(tb, modify_field));
                }
                DBFunction.updateByTbName(link, "tb_info_share", where);

                if (ishistory) {
                    String store_data = BLL_Store.getStoreData(array, item_id, idleaf);
                    boolean bool = BLL_Store.addHistoryRecord(store_data, item_id, idleaf, modify_uid, "修改");
                    if (!bool) {  //修改时发现这条信息没有到记录公共表tb_info_share
                        BLL_Share.addInfoShare(tb, table_name, item_id, idleaf);
                    }
                }
            }
        }
    }

    /**
     * 共享设计表标记删除(信息到回收站)
     */
    public static void deleteInfoShare(String table_name, int item_id, String ids) throws Exception {
        if (!BLL_Menu.isRecycleItem(item_id)) return; //不记录删除记录
        ids = StringUtils.joinAsFilter(ids);
        if (item_id > 0 && !StringUtils.isEmpty(ids)) {
            DBFunction.startTransaction();
            try {
                JsonArray array = BLL_Design.getTableFields(table_name);
                String primary_key = BLL_Design.getFieldName(array, FieldTagEnum.主键.getValue());
                if (StringUtils.isEmpty(primary_key)) return;
                String title_field = BLL_Design.getFieldName(array, FieldTagEnum.标题字段.getValue());
                if (StringUtils.isEmpty(title_field)) return;

                String strSql = "update tb_info_share set isdel=1,delete_time=now(),delete_uid={2} where item_id={0} and idleaf={1};";
                List<String> idleafs = StringUtils.stringToList(ids);
                for (String str : idleafs) {
                    long idleaf = NumberUtils.strToLong(str);
                    int count = DBFunction.executeNonQuery(StringUtils.format(strSql, item_id, idleaf, UserUtils.getUserId()));
                    if (count == 0) {
                        String title_value = BLL_Design.getTitleValue(table_name, title_field, primary_key, idleaf);
                        if (StringUtils.isEmpty(title_value)) continue;

                        String tid = GsonUtils.getObjectValue(array, 0, "tid");
                        Map map = new HashMap();
                        map.put("share_uuid", IdGenerate.buildUUID());  //e0a953c3ee6040eaa9fae2b667060e09
                        map.put("item_id", item_id);
                        map.put("idleaf", idleaf);
                        map.put("title", title_value);
                        map.put("isdel", 1);
                        map.put("create_time", DateUtils.getDateTime());
                        map.put("delete_time", DateUtils.getDateTime());
                        map.put("create_uid", UserUtils.getUserId());
                        map.put("delete_uid", UserUtils.getUserId());
                        map.put("org_id", UserUtils.getOrgId());
                        map.put("tid", tid);
                        DBFunction.insertByTbName(map, "tb_info_share");
                    }
                }

                DBFunction.commit();
            } catch (Exception ex) {
                LogError.write(LogEnum.Error, ex.toString());
            } finally {
                DBFunction.rollback();
            }
        }
    }

    /**
     * 回收站恢复删除
     */
    public static boolean restoreInfoShare(int item_id, long idleaf) throws Exception {
        int tid = BLL_Design.getTableId(item_id, idleaf);
        JsonObject table = BLL_Design.getTableInfo(tid);
        if (table != null) {
            String table_name = GsonUtils.tryParse(table, "table_name", "");
            JsonArray array = BLL_Design.getTableFields(table_name);
            String primary_key = BLL_Design.getFieldName(array, FieldTagEnum.主键.getValue());
            if (StringUtils.isEmpty(primary_key)) return false;

            String delete_field = BLL_Design.getFieldName(array, FieldTagEnum.删除标识.getValue());

            String strSql = StringUtils.format("update tb_info_share set isdel=0 where item_id={0} and idleaf={1};", item_id, idleaf);
            if (StringUtils.isNotEmpty(delete_field)) {
                strSql += StringUtils.format("update {0} set {1}=0 where {2}={3};", table_name, delete_field, primary_key, idleaf);
            }

            int oid = GsonUtils.tryParse(table, "oid", 0);
            if (oid > 0) {
                String object_type = BLL_Design.getObjectType(oid);
                if (object_type.equals(ObjectTypeEnum.流程对象.getValue())) {
                    strSql += StringUtils.format("update wd_task_status set isdel=0 where item_id={0} and idleaf={1};", item_id, idleaf);
                }
            }
            return DBFunction.executeNonQuery(strSql) > 0;
        }
        return false;
    }

    /**
     * 彻底删除设计表共同信息
     */
    public static boolean clearInfoShare(String ids) {
        DBFunction.startTransaction();
        try {
            String[] arr = ids.split(",");
            String strSql = "delete from tb_info_share where item_id={0} and idleaf={1}";
            for (String str : arr) {
                String[] strs = str.split("_");
                if (strs.length == 2) {
                    int item_id = NumberUtils.strToInt(strs[0]);
                    long idleaf = NumberUtils.strToInt(strs[1]);
                    int tid = BLL_Design.getTableId(item_id, idleaf);
                    JsonObject table = BLL_Design.getTableInfo(tid);
                    if (table != null) {
                        int oid = GsonUtils.tryParse(table, "oid", 0);
                        JsonArray array = BLL_Design.getDesignTable(oid);
                        if (array != null && array.size() > 0) {
                            DBFunction.executeNonQuery(StringUtils.format(strSql, item_id, idleaf));  //删除公共关系表
                            for (int i = 0; i < array.size(); i++) {
                                JsonObject dr = GsonUtils.getObject(array, i);
                                String tb_relation = GsonUtils.tryParse(dr, "tb_relation", "");
                                String table_name = GsonUtils.tryParse(dr, "table_name", "");
                                String extend_name = GsonUtils.tryParse(dr, "extend_name", "");
                                if (tb_relation.equals("附件列表")) {
                                    DBFunction.executeNonQuery(StringUtils.format("delete from tb_object_att where item_id={0} and idleaf={1}", item_id, idleaf));
                                } else {
                                    JsonArray fields = BLL_Design.getTableFields(table_name);
                                    String primary_key = BLL_Design.getFieldName(fields, FieldTagEnum.主键.getValue());
                                    if (StringUtils.isNotEmpty(primary_key)) {
                                        DBFunction.executeNonQuery(StringUtils.format("delete from {0} where {1}={2}", table_name, primary_key, idleaf));
                                        if (StringUtils.isNotEmpty(extend_name)) { //有扩展表
                                            DBFunction.executeNonQuery(StringUtils.format("delete from {0} where {1}={2}", extend_name, primary_key, idleaf));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return true;
    }

    /**
     * 彻底删除设计表共同信息(只保留多少天的删除数据)
     */
    public static boolean clearInfoShare(int days) {
        DBFunction.startTransaction();
        try {
            if (days == 0) days = -1;
            JsonArray array = DBFunction.executeJsonArray(StringUtils.format("select distinct tid,item_id from tb_info_share where isdel=1 and DATEDIFF(now(), create_time)>{0}", days));
            String strSql = "delete from tb_info_share where isdel=1 and DATEDIFF(now(), create_time)>{0}";
            DBFunction.executeNonQuery(StringUtils.format(strSql, days)); //删除公共关系表
            for (int i = 0; i < array.size(); i++) {
                JsonObject r = GsonUtils.getObject(array, i);
                int tid = GsonUtils.tryParse(r, "tid", 0);
                int item_id = GsonUtils.tryParse(r, "item_id", 0);
                if (tid > 0) {
                    JsonObject table = BLL_Design.getTableInfo(tid);
                    if (table != null) {
                        int oid = GsonUtils.tryParse(table, "oid", 0);
                        String main_table = GsonUtils.tryParse(table, "table_name", "");
                        if (StringUtils.isEmpty(main_table)) continue;

                        String extend_name = GsonUtils.tryParse(table, "extend_name", "");
                        JsonArray fields = BLL_Design.getTableFields(main_table);
                        String primary_key = BLL_Design.getFieldName(fields, FieldTagEnum.主键.getValue());
                        String delete_field = BLL_Design.getFieldName(fields, FieldTagEnum.删除标识.getValue());
                        String item_field = BLL_Design.getFieldName(fields, FieldTagEnum.栏目号.getValue());
                        String create_field = BLL_Design.getFieldName(fields, FieldTagEnum.创建时间.getValue());
                        if (StringUtils.isEmpty(primary_key)) continue;
                        if (StringUtils.isEmpty(delete_field)) continue;
                        if (StringUtils.isEmpty(item_field)) continue;
                        if (StringUtils.isEmpty(create_field)) continue;

                        String ids_sql = "select %s from %s where %s=1 and %s=%d and DATEDIFF(now(), %s)>%d)";
                        ids_sql = String.format(ids_sql, primary_key, main_table, delete_field, item_field, item_id, create_field, days);

                        JsonArray design = BLL_Design.getDesignTable(oid);
                        if (design != null && design.size() > 0) {
                            for (int j = 0; j < design.size(); j++) {
                                JsonObject dr = GsonUtils.getObject(array, j);
                                String tb_relation = GsonUtils.tryParse(dr, "tb_relation", "");
                                String table_name = GsonUtils.tryParse(dr, "table_name", "");

                                if (tb_relation.equals("附件列表")) {
                                    DBFunction.executeNonQuery(StringUtils.format("delete from tb_object_att where item_id={0} and idleaf in({1})", item_id, ids_sql));
                                } else if (!tb_relation.equals("主表")) {
                                    DBFunction.executeNonQuery(StringUtils.format("delete from {0} where {1} in({2})", table_name, primary_key, ids_sql));
                                } else if (tb_relation.equals("主表")) {
                                    if (!StringUtils.isEmpty(extend_name)) {
                                        DBFunction.executeNonQuery(StringUtils.format("delete from {0} where {1} in({2})", extend_name, primary_key, ids_sql));
                                    }
                                }
                            }
                            String delete_sql = "delete from %s where %s=1 and %s=%d and DATEDIFF(now(), %s)>%d";
                            DBFunction.executeNonQuery(String.format(delete_sql, main_table, delete_field, item_field, item_id, create_field, days));
                        }
                    }
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }

        return true;
    }

    /**
     * 更新共享信息修改记录次数
     */
    public static void updateModRecords(String share_uuid, int sub_table_num) throws Exception {
        String table_name = "tb_info_store" + sub_table_num;
        String sql = String.format("select count(1) from %s where share_uuid=@share_uuid", table_name);
        int modify_records = NumberUtils.strToInt(DBFunction.executeScalar(sql, new DBParameter("@share_uuid", "=", share_uuid)));

        Map map = new HashMap();
        map.put("modify_time", DateUtils.getDateTime());
        map.put("modify_records", modify_records);
        DBFunction.updateByTbName(map, "tb_info_share", new DBParameter("@share_uuid", "=", share_uuid));
    }

}


