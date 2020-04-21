package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.enums.system.ItemEnum;
import com.xinsite.core.model.design.ConfigTableModel;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 设计表相关
 * create by zhangxiaxin
 */
public class BLL_Design {

    /**
     * 获取菜单栏目对应的主表名称
     */
    public static String getMainTableName(int item_id) {
        String querySql = "select max(main_table) from tb_gen_object where item_method in(select item_method from sys_menu where item_id={0})";
        Object obj = DBFunction.executeScalar(StringUtils.format(querySql, item_id));
        return obj == null ? "" : obj.toString();
    }

    /**
     * 获取对象类型
     */
    public static String getObjectType(int oid) {
        String querySql = "select object_type from tb_gen_object where oid=@oid";
        Object obj = DBFunction.executeScalar(querySql, new DBParameter("@oid", oid));
        return obj == null ? "" : obj.toString();
    }

    /**
     * 获取数据对象Id
     */
    public static int getObjectId(String table_key) {
        String querySql = "select max(oid) from tb_gen_table where table_key=@table_key";
        Object obj = DBFunction.executeScalar(querySql, new DBParameter("@table_key", table_key));
        return NumberUtils.strToInt(obj);
    }

    /**
     * 获取菜单栏目对应的对象Id
     */
    public static int getObjectId(int item_id) {
        String querySql = "select max(oid) from tb_gen_object where item_method in(select item_method from sys_menu where item_id={0})";
        Object obj = DBFunction.executeScalar(StringUtils.format(querySql, item_id));
        return NumberUtils.strToInt(obj);
    }

    /**
     * 获取数据库表Id
     */
    public static int getTableId(int oid) {
        String querySql = "select max(tid) tid from tb_gen_table where oid=@oid and tb_relation='主表'";
        Object obj = DBFunction.executeScalar(querySql, new DBParameter("@oid", oid));
        return NumberUtils.strToInt(obj);
    }

    /**
     * 根据共享信息表item_id、idleaf获取tid
     */
    public static int getTableId(int item_id, long idleaf) {
        String querySql = "select max(tid) tid from tb_info_share where item_id=@item_id and idleaf=@idleaf";
        Object obj = DBFunction.executeScalar(querySql, new DBParameter("@item_id", item_id), new DBParameter("@idleaf", idleaf));
        return NumberUtils.strToInt(obj);
    }

    /**
     * 获取数据库表名
     */
    public static String getTableName(String table_key) {
        String querySql = "select max(table_name) table_name from tb_gen_table where table_key=@table_key";
        Object obj = DBFunction.executeScalar(querySql, new DBParameter("@table_key", table_key));
        return obj == null ? "" : obj.toString();
    }

    /**
     * 获取数据对象
     */
    public static JsonObject getObjectInfo(int oid) throws Exception {
        String querySql = "select * from tb_gen_object where oid=@oid";
        JsonArray array = DBFunction.executeJsonArray(querySql, new DBParameter("@oid", oid));
        if (array.size() > 0) return GsonUtils.getObject(array, 0);
        return null;
    }

    public static String getObjectField(int oid, String field) throws Exception {
        String sql = "select {0} from tb_gen_object where oid={1}";
        Object obj = DBFunction.executeScalar(StringUtils.format(sql, field, oid));
        return obj == null ? "" : obj.toString();
    }

    /**
     * 获取数据库表名
     */
    public static JsonObject getTableInfo(int tid) throws Exception {
        String querySql = "select * from tb_gen_table where tid=@tid";
        JsonArray array = DBFunction.executeJsonArray(querySql, new DBParameter("@tid", tid));
        if (array.size() > 0) return GsonUtils.getObject(array, 0);
        return null;
    }

    /**
     * 获取数据库表特定字段
     */
    public static String getFieldName(int tid, String field_tag, String default_) {
        String querySql = "select field_name from tb_gen_field where field_tag=@field_tag and tid=" + tid;
        Object obj = DBFunction.executeScalar(querySql, new DBParameter("@field_tag", field_tag));
        String field_name = obj == null ? "" : obj.toString();
        if (StringUtils.isEmpty(field_name)) field_name = default_;
        return field_name;
    }

    /**
     * 获取该栏目的所有表单
     */
    public static JsonArray getDesignTable(int oid) throws Exception {
        String querySql = "select tid id,pid,table_explain text,table_name,tb_relation,extend_name,table_key,layout_type from tb_gen_table where oid={0}";
        return DBFunction.executeJsonArray(StringUtils.format(querySql, oid));
    }

    /**
     * 获取指定的设计表字段
     */
    public static JsonArray getDesignField(List<Integer> list) throws Exception {
        String querySql = "select a1.fid,b1.table_explain,a1.field_explain,c1.xtype_name from tb_gen_field a1,tb_gen_table b1,view_gen_xtype c1 \n" +
                "where a1.fid in(%s) and a1.tid=b1.tid and a1.xtype=c1.xtype";
        return DBFunction.executeJsonArray(String.format(querySql, StringUtils.joinAsList(list)));
    }

    /**
     * 获取数据表所有字段
     */
    public static JsonArray getTableFields(String table_name) throws Exception {
        String querySql = "select a1.oid,a1.tid,b1.fid,b1.field_name,b1.field_tag,b1.extend_suf from tb_gen_table a1,tb_gen_field b1 " +
                "where a1.tid=b1.tid and a1.table_name=@table_name";

        List<DBParameter> parms = new ArrayList<>();
        parms.add(new DBParameter("@table_name", table_name));
        return DBFunction.executeJsonArray(querySql, DBParameter.getParameter(parms));
    }

    /**
     * 获取数据表所有字段
     */
    public static JsonArray getTableFields(int tid) throws Exception {
        String sql = "select field_name id,field_explain name,xtype from tb_gen_field where tid={0} and isdefine=1 order by serialcode";
        return DBFunction.executeJsonArray(StringUtils.format(sql, tid));
    }

    /**
     * 根据字段标记获取字段名称
     */
    public static String getFieldName(JsonArray array, String field_tag) {
        if (array.size() > 0) {
            JsonArray dt = GsonUtils.getWhereArray(array, "field_tag", field_tag);
            if (dt.size() > 0) {
                return GsonUtils.getObjectValue(dt, 0, "field_name");
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 该字段是否存在，extend：是否扩展表
     */
    public static boolean isExistFieldName(JsonArray array, String field, boolean extend) {
        boolean isexist = false;
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                String field_name = GsonUtils.tryParse(dr, "field_name", "");
                if (field_name.equalsIgnoreCase(field)) {
                    if (extend) {
                        String extend_suf = GsonUtils.tryParse(dr, "extend_suf", "");
                        if (StringUtils.isNotEmpty(extend_suf)) isexist = true;
                    } else {
                        isexist = true;
                    }
                    if (isexist) break;
                }
            }
        }
        return isexist;
    }

    /**
     * 获取表单标题
     */
    public static String getTitleValue(String table_name, String field_title, String primary_key, long idleaf) throws Exception {
        String querySql = StringUtils.format("select {0} from {1} where {2}={3}", field_title, table_name, primary_key, idleaf);
        Object obj = DBFunction.executeScalar(querySql);
        return obj == null ? "" : obj.toString();
    }

    public static Hashtable getDesignBuild(int tid) throws Exception {
        Hashtable ht = new Hashtable();
        JsonObject table = BLL_Design.getTableInfo(tid);
        if (table != null) {
            int oid = GsonUtils.tryParse(table, "oid", 0);
            if (oid <= 0) return ht;
            JsonObject object = BLL_Design.getObjectInfo(oid);
            JsonArray array = BLL_Design.getDesignTable(oid);
            if (object == null) return ht;
            if (array == null || array.size() == 0) return ht;

            ht.put("layout_type", GsonUtils.tryParse(object, "layout_type", ""));
            ht.put("config_tables", GsonUtils.tryParse(object, "config_tables", ""));
            ht.put("object_name", GsonUtils.tryParse(object, "object_name", ""));
            ht.put("table_count", array.size());
        }
        return ht;
    }

    /**
     * 获取该对象所有表的配置参数
     */
    public static List<ConfigTableModel> getConfigTables(int oid) throws Exception {
        JsonObject object = BLL_Design.getObjectInfo(oid);
        if (object != null) {
            String config_tables = GsonUtils.tryParse(object, "config_tables", "");
            if (!StringUtils.isEmpty(config_tables)) {
                try {
                    return GsonUtils.getList(config_tables, ConfigTableModel.class);
                } catch (Exception ex) {
                    LogError.write(LogEnum.Error, ex.toString());
                }
            }
        }
        return null;
    }

    /**
     * 获取只存值的列表
     */
    public static JsonArray getColumnsCodeValue(int oid) throws Exception {
        String querySql = "select a.save_value,a.field_name,a.xtype,a.save_value,a.data_key,a.store_datas,a.store_type\n" +
                " from tb_gen_field a,tb_gen_table b where a.tid=b.tid and b.oid={0} and a.save_value=1";
        return DBFunction.executeJsonArray(StringUtils.format(querySql, oid));
    }

    /**
     * 查询列表中 列表获取文本值
     */
    public static void setGridObjectNames(JsonArray dt, String field_name) throws Exception {
        if (dt == null) return;
        if (!GsonUtils.getArrayFields(dt).contains(field_name)) return;
        String querySql = "select oid,object_name,serialcode from tb_gen_object order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(querySql);

        String add_field = field_name + "_text";
        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "oid", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "object_name", ""));
            }
            dr.addProperty(add_field, sb.toString());
        }
    }

    /**
     * 根据流程列表栏目Id，获取所有申请流程栏目Id
     */
    public static String getFlowItemIds(int grid_item_id) {
        String querySql = "select item_id from view_flow_item where grid_item_id=" + grid_item_id;
        List<Integer> list = new ArrayList<Integer>();
        try {
            JsonArray array = DBFunction.executeJsonArray(querySql);
            list = ArrayUtils.listByField(array, "item_id", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.joinAsList(list);
    }

    /**
     * 根据申请流程栏目Id，获取所有流程列表栏目Id
     */
    public static String getFlowGridItemIds(int item_id) {
        String querySql = "select item_id from sys_menu where item_id={1} or item_id={2} or item_id={3} or " +
                "item_id in(select grid_item_id from view_flow_item where item_id={0}) ";
        List<Integer> list = new ArrayList<Integer>();
        try {
            JsonArray array = DBFunction.executeJsonArray(StringUtils.format(querySql, item_id, ItemEnum.发起事项.getId(), ItemEnum.经办事项.getId(), ItemEnum.待办任务.getId()));
            list = ArrayUtils.listByField(array, "item_id", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.joinAsList(list);
    }

    /**
     * 给定的表字段是否存在JsonArray列表中
     */
    public static int getFidByFieldName(JsonArray array, String table_name, String field_name) {
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String tableName = GsonUtils.tryParse(dr, "table_name", "");
            String fieldName = GsonUtils.tryParse(dr, "field_name", "");
            if (tableName.equalsIgnoreCase(table_name) && fieldName.equalsIgnoreCase(field_name))
                return GsonUtils.tryParse(dr, "fid", 0);
        }
        return 0;
    }

}
