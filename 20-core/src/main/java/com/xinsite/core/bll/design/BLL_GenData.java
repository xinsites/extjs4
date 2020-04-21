package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 生成表数据导入
 * create by zhangxiaxin
 */
public class BLL_GenData {

    public static JsonArray getObjectTree(String where) throws Exception {
        String sql = "select oid id,pid,object_name text,serialcode,object_type,object_key,expanded expand from tb_gen_object a1 where 1=1{0} order by a1.pid,a1.serialcode";
        return DBFunction.executeJsonArray(StringUtils.format(sql, where));
    }

    public static JsonArray getFieldXTypeCombo() throws Exception {
        String sql = "select field_value id,flag_1 name from tb_extend_info where table_name='input_xtype' order by serialcode";
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 保存字段排序
     */
    public static void saveFieldSort(String sortVal, String Field) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update tb_gen_field set {2}={0} where fid={1};";
        String[] Items = sortVal.split(";");
        for (String item : Items) {
            String[] arr = item.split(":");
            if (arr.length == 2)
                sb.append(StringUtils.format(sql, arr[1], arr[0], Field));
        }
        DBFunction.executeNonQuery(sb.toString());
    }

    /**
     * 导入数据
     */
    public static void importData(Map map) throws Exception {
        JsonArray objects = GsonUtils.getBean(map.get("object"), JsonArray.class);
        JsonArray tables = GsonUtils.getBean(map.get("table"), JsonArray.class);
        JsonArray fields = GsonUtils.getBean(map.get("field"), JsonArray.class);

        BLL_GenData.importObject(objects);
        BLL_GenData.importTable(tables);
        BLL_GenData.importField(fields);
    }

    /**
     * 该主键字段是否存在
     */
    public static boolean isExistData(int id, String table_name, String primary_key) {
        String sql = "select count(1) from %s where %s=%d";
        Object obj = DBFunction.executeScalar(String.format(sql, table_name, primary_key, id));
        return NumberUtils.strToInt(obj) > 0;
    }

    /**
     * 该object_key是否存在
     */
    public static boolean isExistObject(int oid, String object_key) {
        String sql = "select count(1) from tb_gen_object where oid!=@oid and object_key=@object_key";
        Object obj = DBFunction.executeScalar(sql, new DBParameter("@oid", oid), new DBParameter("@object_key", object_key));
        return NumberUtils.strToInt(obj) > 0;
    }

    /**
     * 该table_key是否存在
     */
    public static boolean isExistTable(int tid, String table_key) {
        String sql = "select count(1) from tb_gen_table where tid!=@tid and table_key=@table_key";
        Object obj = DBFunction.executeScalar(sql, new DBParameter("@tid", tid), new DBParameter("@table_key", table_key));
        return NumberUtils.strToInt(obj) > 0;
    }

    /**
     * 删除对象数据
     */
    public static void deleteObject(int oid) throws Exception {
        String sql = "delete from tb_gen_field where tid in(select tid from tb_gen_table where oid in({0}));\n" +
                "                    delete from tb_gen_table where oid in({0});\n" +
                "                    delete from tb_gen_object where oid in({0});";
        DBFunction.executeNonQuery(StringUtils.format(sql, oid));
    }

    /**
     * 导入对象数据
     */
    public static void importObject(JsonArray array) throws Exception {
        if (array != null) {
            if (array.size() == 0) throw new AppException("缺少数据对象数据！");
            for (int i = 0; i < array.size(); i++) {
                JsonObject json = GsonUtils.getObject(array, i);
                int oid = GsonUtils.tryParse(json, "oid", 0);
                String object_key = GsonUtils.tryParse(json, "object_key", "");
                object_key = StringUtils.replaceVal(object_key, ".", "_");
//                if (BLL_GenData.isExistObject(oid, object_key)) {
//                    throw new AppException(String.format("数据对象【%s】已存在！", object_key));
//                }
                Map map = new HashMap();
                map.put("pid", GsonUtils.tryParse(json, "pid", 0));
                map.put("object_type", GsonUtils.tryParse(json, "object_type", ""));
                map.put("object_name", GsonUtils.tryParse(json, "object_name", ""));
                map.put("object_key", object_key);  //已无用
                map.put("main_table", GsonUtils.tryParse(json, "main_table", ""));
                map.put("is_attgrid", GsonUtils.tryParse(json, "is_attgrid", 0));
                map.put("layout_type", GsonUtils.tryParse(json, "layout_type", ""));
                map.put("item_method", GsonUtils.tryParse(json, "create_method", ""));
                map.put("config_tables", GsonUtils.tryParse(json, "config_tables", ""));
                map.put("expanded", GsonUtils.tryParse(json, "expanded", ""));
                map.put("serialcode", GsonUtils.tryParse(json, "serialcode", 0));

                if (BLL_GenData.isExistData(oid, "tb_gen_object", "oid")) {
                    DBFunction.updateByTbName(map, "tb_gen_object", "oid=" + oid);
                } else {
                    map.put("oid", oid);
                    map.put("create_time", DateUtils.getDateTime());
                    map.put("create_uid", UserUtils.getUserId());
                    DBFunction.insertByTbName(map, "tb_gen_object");
                }
            }
        }
    }

    /**
     * 导入数据表数据
     */
    public static void importTable(JsonArray array) throws Exception {
        if (array != null) {
            if (array.size() == 0) throw new AppException("缺少数据表数据！");
            for (int i = 0; i < array.size(); i++) {
                JsonObject json = GsonUtils.getObject(array, i);
                int tid = GsonUtils.tryParse(json, "tid", 0);
                String table_key = GsonUtils.tryParse(json, "table_key", "");
                table_key = StringUtils.replaceVal(table_key, ".", "_");
                String tb_relation = GsonUtils.tryParse(json, "tb_relation", "");
                if (BLL_GenData.isExistTable(tid, table_key) && !"附件列表".equals(tb_relation)) {
                    throw new AppException(String.format("数据表【%s】已存在！", table_key));
                }
                Map map = new HashMap();
                map.put("pid", GsonUtils.tryParse(json, "pid", 0));
                map.put("oid", GsonUtils.tryParse(json, "oid", 0));
                map.put("table_key", table_key);
                map.put("table_name", GsonUtils.tryParse(json, "table_name", ""));
                map.put("table_explain", GsonUtils.tryParse(json, "table_explain", ""));
                map.put("extend_name", GsonUtils.tryParse(json, "extend_name", ""));
                map.put("table_type", GsonUtils.tryParse(json, "table_type", ""));
                map.put("tb_relation", GsonUtils.tryParse(json, "tb_relation", ""));
                map.put("layout_type", GsonUtils.tryParse(json, "layout_type", ""));
                map.put("serialcode", GsonUtils.tryParse(json, "serialcode", 0));

                if (BLL_GenData.isExistData(tid, "tb_gen_table", "tid")) {
                    DBFunction.updateByTbName(map, "tb_gen_table", "tid=" + tid);
                } else {
                    map.put("tid", tid);
                    map.put("create_time", DateUtils.getDateTime());
                    map.put("create_uid", UserUtils.getUserId());
                    DBFunction.insertByTbName(map, "tb_gen_table");
                }
            }
        }
    }

    /**
     * 导入数据表字段数据
     */
    public static void importField(JsonArray array) throws Exception {
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject json = GsonUtils.getObject(array, i);
                int fid = GsonUtils.tryParse(json, "fid", 0);

                Map map = new HashMap();
                map.put("tid", GsonUtils.tryParse(json, "tid", 0));
                map.put("extend_suf", GsonUtils.tryParse(json, "extend_suf", ""));
                map.put("field_name", GsonUtils.tryParse(json, "field_name", ""));
                map.put("data_type", GsonUtils.tryParse(json, "data_type", ""));
                map.put("xtype", GsonUtils.tryParse(json, "xtype", ""));
                map.put("field_tag", GsonUtils.tryParse(json, "field_tag", ""));
                map.put("field_explain", GsonUtils.tryParse(json, "field_explain", ""));
                map.put("serialcode", GsonUtils.tryParse(json, "serialcode", 0));
                map.put("issearchfield", GsonUtils.tryParse(json, "issearchfield", 0));
                map.put("iscolumns", GsonUtils.tryParse(json, "iscolumns", 0));
                map.put("isdefine", GsonUtils.tryParse(json, "isdefine", 0));
                map.put("save_value", GsonUtils.tryParse(json, "save_value", 0));
                map.put("data_key", GsonUtils.tryParse(json, "data_key", ""));
                map.put("store_datas", GsonUtils.tryParse(json, "store_datas", ""));
                map.put("store_type", GsonUtils.tryParse(json, "store_type", ""));
                map.put("field_type", GsonUtils.tryParse(json, "field_type", ""));
                map.put("editor_search", GsonUtils.tryParse(json, "editor_search", ""));
                map.put("default_value", GsonUtils.tryParse(json, "default_value", ""));
                map.put("build_type", GsonUtils.tryParse(json, "build_type", ""));
                map.put("is_form_input", GsonUtils.tryParse(json, "is_form_input", 0));

                if (BLL_GenData.isExistData(fid, "tb_gen_field", "fid")) {
                    DBFunction.updateByTbName(map, "tb_gen_field", "fid=" + fid);
                } else {
                    map.put("fid", fid);
                    map.put("create_time", DateUtils.getDateTime());
                    map.put("create_uid", UserUtils.getUserId());
                    DBFunction.insertByTbName(map, "tb_gen_field");
                }
            }
        }
    }

    /**
     * 重新保存查询editor_search值
     */
    public static boolean saveFieldEditorSearch(int fid, String editor_search) {
        DBFunction.startTransaction();
        try {
            Map ht = new HashMap();
            ht.put("editor_search", editor_search);
            if (StringUtils.isEmpty(editor_search)) {
                ht.put("xtype", "");
            } else {
                Pattern p = Pattern.compile("xtype: '(.+)',");
                Matcher m = p.matcher(editor_search);
                if (m.find()) {
                    ht.put("xtype", m.group(1));
                } else {
                    p = Pattern.compile("xtype:'(.+)',");
                    m = p.matcher(editor_search);
                    if (m.find()) ht.put("xtype", m.group(1));
                }
            }
            DBFunction.updateByTbName(ht, "tb_gen_field", "fid=" + fid);

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return true;
    }
}
