package com.xinsite.core.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.DataPerEnum;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.model.TreeStoreModel;

import java.util.*;

public class CommUtils {

    public static String getFirstItems(String queryfield) {
        List<String> list = StringUtils.stringToList(queryfield);
        if (list.size() > 1) return list.get(0);
        return queryfield;
    }

    public static String getFieldValue(Hashtable ht, String key) {
        if (ht != null && ht.containsKey(key)) {
            return ht.get(key).toString();
        }
        return "";
    }

    public static String getFieldValue(Map ht, String key) {
        if (ht != null && ht.containsKey(key)) {
            return ht.get(key).toString();
        }
        return "";
    }

    public static boolean columnsExists(JsonArray array, String field_name) {
        List<String> fields = GsonUtils.getArrayFields(array);
        return fields.contains(field_name);
    }

    public static String getColumnsName(JsonArray array, String field_name) {
        List<String> fields = GsonUtils.getArrayFields(array);
        for (String field : fields) {
            if (field.equalsIgnoreCase(field_name)) return field;
        }
        return "";
    }

    /**
     * 导出Excel 本地下拉列表设置文本值
     */
    public static void setExcelCodeText(JsonArray array, String field_name, String store_datas) {
        if (!StringUtils.isEmpty(store_datas)) {
            field_name = CommUtils.getColumnsName(array, field_name);
            if (!CommUtils.columnsExists(array, field_name)) return;
            List<String[]> codes = GsonUtils.getList(store_datas, String[].class);
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                String value = GsonUtils.tryParse(dr, field_name, "");
                if (StringUtils.isEmpty(value)) continue;
                List<String> list = Arrays.asList(value.split(","));
                StringBuilder sb = new StringBuilder();
                for (String id : list) {
                    for (String[] strs : codes) {
                        if (strs.length >= 2) {
                            if (strs[0].equalsIgnoreCase(id)) {
                                if (sb.length() != 0) sb.append(",");
                                sb.append(strs[1]);
                            }
                        }
                    }
                }
                dr.addProperty(field_name, sb.toString());
            }
        }
    }

    /**
     * 导出Excel 本地下拉列表复选框设置文本值
     */
    public static void setExcelCheckBoxText(JsonArray array, String field_name, String store_datas) {
        if (!StringUtils.isEmpty(store_datas)) {
            field_name = CommUtils.getColumnsName(array, field_name);
            if (!CommUtils.columnsExists(array, field_name)) return;
            List<String[]> codes = GsonUtils.getList(store_datas, String[].class);
            codes.add(new String[]{"true", "是"}); //行编辑复选框默认值是true，false
            codes.add(new String[]{"false", "否"});
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                String value = GsonUtils.tryParse(dr, field_name, "");
                if (StringUtils.isEmpty(value)) continue;
                List<String> list = Arrays.asList(value.split(","));
                StringBuilder sb = new StringBuilder();
                for (String id : list) {
                    for (String[] strs : codes) {
                        if (strs.length >= 2) {
                            if (strs[0].equalsIgnoreCase(id)) {
                                if (sb.length() != 0) sb.append(",");
                                sb.append(strs[1]);
                            }
                        }
                    }
                }
                dr.addProperty(field_name, sb.toString());
            }
        }
    }

    /**
     * 导出Excel 本地下拉树获取文本值
     */
    public static void setExcelTreeCodeText(JsonArray array, String field_name, String store_datas) {
        if (!StringUtils.isEmpty(store_datas)) {
            field_name = CommUtils.getColumnsName(array, field_name);
            if (!CommUtils.columnsExists(array, field_name)) return;
            List<TreeStoreModel> stores = GsonUtils.getList(store_datas, TreeStoreModel.class);
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                String value = GsonUtils.tryParse(dr, field_name, "");
                if (StringUtils.isEmpty(value)) continue;
                List<String> list = Arrays.asList(value.split(","));
                StringBuilder sb = new StringBuilder();
                CommUtils.getTreeCodeText(stores, list, sb);
                dr.addProperty(field_name, sb.toString());
            }
        }
    }

    /**
     * 导出Excel 动态下拉树获取文本值
     */
    public static void getTreeCodeText(List<TreeStoreModel> stores, List<String> values, StringBuilder sb) {
        if (stores != null) {
            for (TreeStoreModel store : stores) {
                for (String id : values) {
                    if (store.id.equalsIgnoreCase(id)) {
                        if (sb.length() != 0) sb.append(",");
                        sb.append(store.text);
                        values.remove(id);
                        break;
                    }
                }
                if (values.size() > 0)
                    getTreeCodeText(store.children, values, sb);
            }
        }
    }

    public static boolean enumDataPerContains(int data_per) {
        for (DataPerEnum typeEnum : DataPerEnum.values()) {
            if (typeEnum.getIndex() == data_per) {
                return true;
            }
        }
        return false;
    }

    public static String getFieldTextByArray(JsonArray array, String primarykey, String textfield, String value) {
        String texts = StringUtils.EMPTY;
        List<Integer> list = StringUtils.splitToList(value);
        for (Integer id : list) {
            JsonObject object = GsonUtils.getWhereJsonObject(array, primarykey, id);
            if (object != null) {
                if (!StringUtils.isEmpty(texts)) texts += ",";
                texts += GsonUtils.tryParse(object, textfield, "");
            }
        }
        return texts;
    }

    public static String getEditorForm(String editor_form) {
        editor_form = editor_form.replace("\\r", "\n").replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
        return editor_form;
    }


    /**
     * 设置某列表字段部分为密码状态
     */
    public static void setFieldPassWord(JsonArray array, String field_name) {
        if (Global.getBoolean("config.field_password")) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject json = GsonUtils.getObject(array, i);
                String field_value = GsonUtils.tryParse(json, field_name, "");
                json.remove(field_name);
                json.addProperty(field_name, StringUtils.replaceIndexByChar(field_value, '*', 2, 5));
            }
        }
    }
}
