package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xinsite.common.bean.Editors;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonForm;
import com.xinsite.common.uitls.extjs.JsonGrid;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.BLL_Common;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.enums.FieldTagEnum;
import com.xinsite.core.model.design.ConfigTableModel;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.*;

/**
 * 历史操作信息存储
 * create by zhangxiaxin
 */
public class BLL_Store {

    /**
     * 新增历史记录信息
     */
    public static boolean addHistoryRecord(String store_data, int item_id, long idleaf, int create_uid, String opertype) throws Exception {
        if (item_id > 0 && idleaf > 0) {
            //if (!BLL_Menu.isHistoryItem(item_id)) return true; //不记录历史记录
            String sql = "select * from tb_info_share where item_id={0} and idleaf={1}";
            JsonArray array = DBFunction.executeJsonArray(StringUtils.format(sql, item_id, idleaf));
            if (array != null && array.size() > 0) {
                JsonObject object = GsonUtils.getObject(array, 0);
                String share_uuid = GsonUtils.tryParse(object, "share_uuid", "");
                int sub_table_num = GsonUtils.tryParse(object, "sub_table_num", 0);
                if (sub_table_num == 0) {  //头一次新增
                    sub_table_num = getSubTableNum();
                    List<DBParameter> where = new ArrayList<>();
                    where.add(new DBParameter("item_id", "=", item_id));
                    where.add(new DBParameter("idleaf", "=", idleaf));

                    Map link = new HashMap();
                    link.put("sub_table_num", sub_table_num);
                    DBFunction.updateByTbName(link, "tb_info_share", where);
                }

                if (create_uid == 0) create_uid = UserUtils.getUserId();
                String store_table = "tb_info_store" + sub_table_num;
                Map store = new HashMap();
                store.put("share_uuid", share_uuid);
                store.put("store_data", store_data);
                store.put("create_time", DateUtils.getDateTime());
                store.put("opertype", opertype);
                store.put("user_id", create_uid);

                int store_id = 0;
                if (GsonUtils.tryParse(object, "sub_table_num", 0) > 0) {
                    sql = "select * from tb_info_store%d where share_uuid='%s' order by store_id desc limit 0,1"; //当天修改的最后一条记录
                    array = DBFunction.executeJsonArray(String.format(sql, sub_table_num, share_uuid));
                    if (array != null && array.size() > 0) {
                        JsonObject obj = GsonUtils.getObject(array, 0);
                        Date create_time = DateUtils.parseDate(GsonUtils.tryParse(obj, "create_time", DateUtils.getDateTime()));
                        long Minutes = DateUtils.pastMinutes(create_time);
                        String oper_type = GsonUtils.tryParse(obj, "opertype", "");
                        if (Minutes < 2 && oper_type.equals("修改") && create_uid == GsonUtils.tryParse(obj, "user_id", 0)) {
                            store_id = GsonUtils.tryParse(obj, "store_id", 0); //同一个人2分钟内做的修改不新增历史记录
                            DBFunction.updateByTbName(store, store_table, "store_id=" + store_id);
                        }
                    }
                }
                if (store_id == 0) DBFunction.insertByTbName(store, store_table);
                BLL_Share.updateModRecords(share_uuid, sub_table_num);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取数据对象栏目最后修改的信息
     */
    public static String getStoreData(JsonArray fields, int item_id, long idleaf) throws Exception {
        StringBuilder json = new StringBuilder("");
        if (fields != null && fields.size() > 0) {
            String oid = GsonUtils.getObjectValue(fields, 0, "oid");
            JsonArray tables = BLL_Design.getDesignTable(NumberUtils.strToInt(oid));
            String primary_key = StringUtils.EMPTY;
            String delete_field = StringUtils.EMPTY;

            if (tables != null && tables.size() > 0) {
                json.append("{success:true");
                for (int i = 0; i < tables.size(); i++) {
                    JsonObject obj = GsonUtils.getObject(tables, i);
                    String table_key = GsonUtils.tryParse(obj, "table_key", "");
                    String table_name = GsonUtils.tryParse(obj, "table_name", "");
                    String relation = GsonUtils.tryParse(obj, "tb_relation", "");
                    String layout_type = GsonUtils.tryParse(obj, "layout_type", "");
                    if (relation.equals("主表") || relation.equals("1对1")) {
                        String extend_name = GsonUtils.tryParse(obj, "extend_name", "");
                        String sql = "select * from {0} where {1}={2}";
                        if (relation.equals("主表")) {
                            primary_key = BLL_Design.getFieldName(fields, FieldTagEnum.主键.getValue());
                            delete_field = BLL_Design.getFieldName(fields, FieldTagEnum.删除标识.getValue());
                        } else {
                            JsonArray array = BLL_Design.getTableFields(table_name);
                            primary_key = BLL_Design.getFieldName(array, FieldTagEnum.主键.getValue());
                            delete_field = BLL_Design.getFieldName(array, FieldTagEnum.删除标识.getValue());
                        }
                        if (StringUtils.isEmpty(primary_key)) return StringUtils.EMPTY;
                        if (!StringUtils.isEmpty(extend_name)
                                && layout_type.indexOf("introduce") == -1) {
                            sql = "select a1.*,a2.* from {0} a1 left join {1} a2 on a1.{2}=a2.{2} where a1.{2}={3}";
                            sql = StringUtils.format(sql, table_name, extend_name, primary_key, idleaf);
                            if (StringUtils.isNotEmpty(delete_field))
                                sql += String.format(" and a1.%s=0", delete_field);
                        } else {
                            sql = StringUtils.format(sql, table_name, primary_key, idleaf);
                            if (StringUtils.isNotEmpty(delete_field)) sql += String.format(" and %s=0", delete_field);
                        }
                        JsonArray into = DBFunction.executeJsonArray(sql);
                        json.append(",").append(JsonForm.getFormJson(into, table_key));
                    } else {
                        String sql = "select * from {0} where {1}={2}";
                        if (relation.equals("附件列表")) {
                            sql = " select * from tb_object_att where {0}={1} and item_id=" + item_id;
                            sql = StringUtils.format(sql, primary_key, idleaf);
                        } else {
                            JsonArray array = BLL_Design.getTableFields(table_name);
                            delete_field = BLL_Design.getFieldName(array, FieldTagEnum.删除标识.getValue());
                            sql = StringUtils.format(sql, table_name, primary_key, idleaf);
                            if (StringUtils.isNotEmpty(delete_field)) sql += String.format(" and %s=0", delete_field);
                        }

                        JsonArray into = DBFunction.executeJsonArray(sql);
                        json.append(",\"").append(table_key).append("\":").append(JsonGrid.getGridJson(into));
                    }
                }
                json.append("}");
            }
        }
        return json.toString();
    }

    /**
     * 获取历史记录表当前活动表号
     */
    public static int getSubTableNum() throws Exception {
        int table_num = 1;
        for (int i = 1; i < 10000; i++) {
            table_num = i;
            if (!BLL_Common.isExistTableName("tb_info_store" + i)) {
                break;
            } else {
                String sql = "select count(1) from tb_info_store{0}";
                long count = NumberUtils.strToLong(DBFunction.executeScalar(StringUtils.format(sql, i)));
                int subtable_records = SysConfigCache.getSubTableRecords();
                if (subtable_records == 0) subtable_records = 100;
                if (count < subtable_records * 10000) break;
            }
        }
        String table_name = "tb_info_store" + table_num;
        if (!BLL_Common.isExistTableName(table_name)) {
            //新增一个历史记录表
            String create_table = "create table `%s` (\n" +
                    "  `store_id` int(11) not null auto_increment,\n" +
                    "  `share_uuid` varchar(50) default null,\n" +
                    "  `store_data` longtext,\n" +
                    "  `create_time` datetime not null default current_timestamp,\n" +
                    "  `opertype` varchar(50) default null,\n" +
                    "  `user_id` int(11) default null,\n" +
                    "  primary key (`store_id`)\n" +
                    ") engine=innodb default charset=utf8;";
            DBFunction.executeNonQuery(String.format(create_table, table_name));
        }
        return table_num;
    }

    /**
     * 获取历史记录信息
     */
    public static Hashtable restoreInfoConfig(int store_id, String share_uuid, int sub_table_num) throws Exception {
        Hashtable ht = new Hashtable();
        String store_data = ""; //待恢复的数据
        String sql = "select * from tb_info_store{0} where store_id=" + store_id; //当天修改的最后一条记录
        JsonArray dt1 = DBFunction.executeJsonArray(StringUtils.format(sql, sub_table_num));
        if (dt1 != null && dt1.size() > 0) store_data = GsonUtils.getObjectValue(dt1, 0, "store_data");
        if (StringUtils.isEmpty(store_data)) return null;

        int tid = 0, idleaf = 0;
        sql = "select * from tb_info_share where share_uuid=@share_uuid";
        JsonArray dt2 = DBFunction.executeJsonArray(sql, new DBParameter("@share_uuid", "=", share_uuid));
        if (dt2 != null && dt2.size() > 0) {
            JsonObject object = GsonUtils.getObject(dt2, 0);
            tid = GsonUtils.tryParse(object, "tid", 0);
            idleaf = GsonUtils.tryParse(object, "idleaf", 0);
        }
        if (tid == 0 || idleaf == 0) return null;
        Hashtable build = BLL_Design.getDesignBuild(tid);
        ht.put("store_data", store_data);
        ht.put("layout_type", CommUtils.getFieldValue(build, "layout_type"));
        ht.put("config_tables", CommUtils.getFieldValue(build, "config_tables"));
        ht.put("table_count", CommUtils.getFieldValue(build, "table_count"));
        ht.put("object_name", CommUtils.getFieldValue(build, "object_name"));

        return ht;
    }

    public static String getValidTableKey(JsonObject jObject, String table_key) {
        JsonElement element = jObject.get(table_key);
        if (element != null) return table_key;
        table_key = StringUtils.replaceVal(table_key, "_", ".");
        element = jObject.get(table_key);
        if (element != null) return table_key;
        table_key = StringUtils.replaceVal(table_key, ".", "_");
        return table_key;
    }

    /**
     * 恢复记录到某个时间点
     */
    public static boolean restoreHistoryInfo(int create_uid, int store_id, String share_uuid, int sub_table_num) {
        DBFunction.startTransaction();
        try {
            String store_data = ""; //待恢复的数据
            String sql = "select * from tb_info_store{0} where store_id=" + store_id;
            JsonArray dt1 = DBFunction.executeJsonArray(StringUtils.format(sql, sub_table_num));
            if (dt1 != null && dt1.size() > 0) store_data = GsonUtils.getObjectValue(dt1, 0, "store_data");
            if (StringUtils.isEmpty(store_data)) return false;

            int tid = 0, item_id = 0, idleaf = 0;
            sql = "select * from tb_info_share a1 where a1.share_uuid=@share_uuid";
            JsonArray dt2 = DBFunction.executeJsonArray(sql, new DBParameter("@share_uuid", "=", share_uuid));
            if (dt2 != null && dt2.size() > 0) {
                JsonObject object = GsonUtils.getObject(dt2, 0);
                tid = GsonUtils.tryParse(object, "tid", 0);
                item_id = GsonUtils.tryParse(object, "item_id", 0);
                idleaf = GsonUtils.tryParse(object, "idleaf", 0);
            }
            if (tid == 0 || item_id == 0 || idleaf == 0) return false;

            JsonObject table = BLL_Design.getTableInfo(tid);
            if (table == null) return false;
            JsonObject object = BLL_Design.getObjectInfo(GsonUtils.tryParse(table, "oid", 0));
            if (object == null) return false;
            String config_tables = GsonUtils.tryParse(object, "config_tables", "");
            if (StringUtils.isEmpty(config_tables)) return false;
            JsonArray fields = BLL_Design.getTableFields(GsonUtils.tryParse(table, "table_name", ""));
            String title_field = BLL_Design.getFieldName(fields, FieldTagEnum.标题字段.getValue());
            String primary_key = BLL_Design.getFieldName(fields, FieldTagEnum.主键.getValue());
            if (StringUtils.isEmpty(primary_key)) return false;
            if (StringUtils.isEmpty(title_field)) return false;

            List<ConfigTableModel> config_tbs = GsonUtils.gsonToList(config_tables, ConfigTableModel.class);
            if (config_tbs.size() == 0) return false;

            String del_sql = "";
            JsonObject jObject = GsonUtils.getObject(store_data);
            for (ConfigTableModel en : config_tbs) {
                if (en.relation.equals("1对多")) {
                    del_sql = StringUtils.format("delete from {0} where {1}={2};", en.table_name, primary_key, idleaf);
                    DBFunction.executeNonQuery(del_sql); //删除原来的信息
                } else if (en.relation.equals("附件列表")) {
                    del_sql = StringUtils.format("delete from tb_object_att where item_id={0} and {1}={2};", item_id, primary_key, idleaf);
                    DBFunction.executeNonQuery(del_sql); //删除原来的信息
                }
            }
            if (idleaf > 0) {
                String title_value = "";
                for (ConfigTableModel en : config_tbs) {
                    if (en.relation.equals("主表")) {
                        String table_key = BLL_Store.getValidTableKey(jObject, en.table_key);
                        JsonObject obj = jObject.get(table_key).getAsJsonObject();
                        Map data = GsonUtils.getMap(obj.get("data").getAsJsonObject().toString());
                        Map ht = new HashMap();
                        Map text = new HashMap();
                        for (String field_name : (Set<String>) data.keySet()) {
                            if (field_name.equalsIgnoreCase(primary_key)) continue;
                            if (field_name.equalsIgnoreCase(title_field)) title_value = data.get(field_name).toString();
                            if (BLL_Design.isExistFieldName(fields, field_name, true)) {
                                text.put(field_name, data.get(field_name) != null ? data.get(field_name).toString() : null);
                            }
                            if (!text.containsKey(field_name)) {
                                if (BLL_Design.isExistFieldName(fields, field_name, false)) {
                                    ht.put(field_name, data.get(field_name) != null ? data.get(field_name).toString() : null);
                                }
                            }
//                            if (en.editors_text != null && en.editors_text.size() > 0) {
//                                for (Editors editor : en.editors_text) {
//                                    if (field_name.equalsIgnoreCase(editor.field_name)) {
//                                        text.put(field_name, data.get(field_name) != null ? data.get(field_name).toString() : null);
//                                        break;
//                                    }
//                                }
//                            }
//
//                            if (!text.containsKey(field_name)) {
//                                if (en.editors != null && en.editors.size() > 0) {
//                                    for (Editors editor : en.editors) {
//                                        if (field_name.equalsIgnoreCase(editor.field_name)) {
//                                            ht.put(field_name, data.get(field_name) != null ? data.get(field_name).toString() : null);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
                        }
                        DBFunction.updateByTbName(ht, en.table_name, primary_key + "=" + idleaf); //更新主表
                        if (text.size() > 0) {
                            DBFunction.updateByTbName(text, en.table_name + "_text", primary_key + "=" + idleaf); //更新扩展表
                        }
                    } else if (en.relation.equals("1对1")) {
                        String table_key = BLL_Store.getValidTableKey(jObject, en.table_key);
                        JsonArray child_fields = BLL_Design.getTableFields(en.table_name);
                        JsonObject obj = jObject.get(table_key).getAsJsonObject();
                        Map data = GsonUtils.getMap(obj.get("data").getAsJsonObject().toString());
                        Map ht = new HashMap();
                        for (String field_name : (Set<String>) data.keySet()) {
                            if (field_name.equalsIgnoreCase(primary_key)) continue;
                            if (BLL_Design.isExistFieldName(child_fields, field_name, false))
                                ht.put(field_name, data.get(field_name) != null ? data.get(field_name).toString() : null);
                        }
                        DBFunction.updateByTbName(ht, en.table_name, primary_key + "=" + idleaf); //更新1对1副表
                    } else if (en.relation.equals("1对多")) {
                        String table_key = BLL_Store.getValidTableKey(jObject, en.table_key);
                        String tb_simple_obj = jObject.get(table_key).toString();
                        JsonArray arrays = GsonUtils.getBean(tb_simple_obj, JsonArray.class);
                        JsonArray child_fields = BLL_Design.getTableFields(en.table_name);
                        String child_primary = BLL_Design.getFieldName(child_fields, FieldTagEnum.主键.getValue());
                        if (StringUtils.isEmpty(child_primary)) child_primary = "id";
                        for (int i = 0; i < arrays.size(); i++) {
                            JsonObject obj = GsonUtils.getObject(arrays, i);
                            Map data = GsonUtils.getMap(obj.toString());
                            Hashtable ht = new Hashtable();
                            for (Map.Entry entry : obj.entrySet()) {
                                String field_name = entry.getKey().toString();
                                if (field_name.equalsIgnoreCase(child_primary)) continue;
                                if (BLL_Design.isExistFieldName(child_fields, field_name, false))
                                    ht.put(field_name, data.get(field_name).toString());
                            }
                            DBFunction.insertByTbName(ht, en.table_name); //还原1对多副表
                        }
                    } else if (en.relation.equals("附件列表")) {
                        String table_key = BLL_Store.getValidTableKey(jObject, en.table_key);
                        String tb_simple_obj = jObject.get(table_key).toString();
                        JsonArray arrays = GsonUtils.getBean(tb_simple_obj, JsonArray.class);
                        JsonArray child_fields = BLL_Design.getTableFields(en.table_name);
                        for (int i = 0; i < arrays.size(); i++) {
                            JsonObject obj = GsonUtils.getObject(arrays, i);
                            Map data = GsonUtils.getMap(obj.toString());
                            Hashtable ht = new Hashtable();
                            for (Map.Entry entry : obj.entrySet()) {
                                String field_name = entry.getKey().toString();
                                if (field_name.equalsIgnoreCase("attach_id")) continue;
                                if (BLL_Design.isExistFieldName(child_fields, field_name, false))
                                    ht.put(field_name, data.get(field_name).toString());
                            }
                            DBFunction.insertByTbName(ht, en.table_name); //还原1对多附件列表
                        }
                    }

                    if (!StringUtils.isEmpty(title_value)) {  //修改公共表的标题
                        Hashtable link = new Hashtable();
                        link.put("title", title_value);
                        link.put("modify_time", DateUtils.getDateTime());
                        link.put("modify_uid", create_uid);

                        DBFunction.updateByTbName(link, "tb_info_share", new DBParameter("share_uuid", "=", share_uuid));
                    }
                }

                if (create_uid == 0) create_uid = UserUtils.getUserId();
                String table_name = "tb_info_store" + sub_table_num;
                Hashtable history = new Hashtable();
                history.put("share_uuid", share_uuid);
                history.put("store_data", store_data);
                history.put("create_time", DateUtils.getDateTime());
                history.put("opertype", "还原");
                history.put("user_id", create_uid);
                DBFunction.insertByTbName(history, table_name); //新增操作历史记录

                BLL_Share.updateModRecords(share_uuid, sub_table_num);
                DBFunction.commit();
            } else {
                DBFunction.rollback();
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        } finally {
            DBFunction.rollback();
        }
        return true;
    }

    /**
     * 获取infoid
     */
    public static String getShareUUID(int store_id, int sub_table_num) throws Exception {
        String table_name = "tb_info_store" + sub_table_num;
        String QuerySql = "select share_uuid from %s where store_id=" + store_id;
        String obj = DBFunction.executeScalar(String.format(QuerySql, table_name));
        return obj == null ? "" : obj;
    }

    /**
     * 删除历史记录
     */
    public static boolean deleteById(int store_id, int sub_table_num) throws Exception {
        DBFunction.startTransaction();
        try {
            String share_uuid = BLL_Store.getShareUUID(store_id, sub_table_num);
            String table_name = "tb_info_store" + sub_table_num;
            String strSql = StringUtils.format("delete from {0} where store_id={1}", table_name, store_id);
            DBFunction.executeNonQuery(strSql);

            BLL_Share.updateModRecords(share_uuid, sub_table_num);
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return true;
    }

    /**
     * 清空历史记录(只保留多少天的历史记录)
     */
    public static boolean clearHistoryRecords(int days) {
        DBFunction.startTransaction();
        try {
            if (days == 0) days = -1;
            String strSql = "delete from tb_info_store{0} where DATEDIFF(now(), create_time)>{1}";
            String updateSql = "update tb_info_share a1 set a1.modify_records=(select count(1) from tb_info_store{0} b1 where b1.share_uuid=a1.share_uuid) where a1.sub_table_num={0}";
            for (int i = 1; i < 10000; i++) {
                if (BLL_Common.isExistTableName("tb_info_store" + i)) {
                    DBFunction.executeNonQuery(StringUtils.format(strSql, i, days));
                    DBFunction.executeNonQuery(StringUtils.format(updateSql, i));
                } else {
                    break;
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
}

