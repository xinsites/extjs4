package com.xinsite.common.uitls.extjs;

import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * Extjs,TreePanel组件Json格式字符串
 *
 * @author www.xinsite.vip
 * @version 2018-9-11
 */
public class JsonTree {
    /**
     * 树形Json格式字符串,所有列加额外属性（逐层加载）
     */
    public static String getTreeJson(JsonArray array) {
        JsonObject object = GsonUtils.getObject(array, 0);
        Set<String> fields = object.keySet();
        String[] attributes = fields.toArray(new String[fields.size()]);
        return getTreeJson(array, "", attributes);
    }

    /**
     * 树形Json格式字符串,所有列加额外属性（逐层加载）
     */
    public static String getTreeJson(JsonArray array, String otherAttr) {
        JsonObject object = GsonUtils.getObject(array, 0);
        Set<String> fields = object.keySet();
        String[] attributes = fields.toArray(new String[fields.size()]);
        return getTreeJson(array, otherAttr, attributes);
    }

    /**
     * 树形Json格式字符串,指定列加额外属性（逐层加载）
     */
    public static String getTreeJson(JsonArray array, String otherAttr, String fields) {
        return JsonTree.getTreeJson(array, otherAttr, fields.split(",")); //（逐层加载）
    }

    /**
     * 树形Json格式字符串,指定列加额外属性（逐层加载）
     */
    public static String getTreeJson(JsonArray array, String otherAttr, String[] fields) {
        StringBuilder strResult = new StringBuilder("[");
        for (int i = 0; i < array.size(); i++) {
            JsonObject jsonObject = array.get(i).getAsJsonObject();
            if (strResult.length() != 1) strResult.append(",");
            strResult.append("{");

            for (int j = 0; j < fields.length; j++) {
                if (j != 0) strResult.append(",");
                if (fields[j].equals("leaf"))
                    strResult.append(String.format("%s:%s", fields[j], GsonUtils.tryParse(jsonObject, fields[j], true)));
                else {
                    String value = GsonUtils.tryParse(jsonObject, fields[j], "");
                    value = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    strResult.append(String.format("%s:\"%s\"", fields[j], value));
                }
            }
            if (!StringUtils.isEmpty(otherAttr)) strResult.append("," + otherAttr);
            strResult.append("}");
        }
        strResult.append("]");
        return strResult.toString();
    }

    /**
     *   Extjs树形组件(全部加载树)
     */
    public static String getTreeJsonByPid(JsonArray array, String pid_value) {
        JsonObject object = GsonUtils.getObject(array, 0);
        Set<String> sets = object.keySet();
        String[] fields = sets.toArray(new String[sets.size()]);
        return getTreeJson(array, "id", "pid", pid_value, "", "", fields);
    }

    /**
     *   Extjs树形组件(全部加载树)
     */
    public static String getTreeJsonByPid(JsonArray array, String field_id, String pid_value) {
        JsonObject object = GsonUtils.getObject(array, 0);
        Set<String> sets = object.keySet();
        String[] fields = sets.toArray(new String[sets.size()]);
        return getTreeJson(array, field_id, "pid", pid_value, "", "", fields);
    }

    /**
     *   Extjs树形组件(全部加载树)
     */
    public static String getTreeJsonByPid(JsonArray array, String pid_value, String parentAttr, String childAttr) {
        JsonObject object = GsonUtils.getObject(array, 0);
        Set<String> sets = object.keySet();
        String[] fields = sets.toArray(new String[sets.size()]);
        return getTreeJson(array, "id", "pid", pid_value, parentAttr, childAttr, fields);
    }

    /**
     *   Extjs树形组件(全部加载树)
     */
    public static String getTreeJsonByPid(JsonArray array, String field_pid, String pid_value, String[] fields) {
        return getTreeJson(array, "id", field_pid, pid_value, "", "", fields);
    }

    /**
     *   Extjs树形组件(全部加载树)
     */
    public static String getTreeJson(JsonArray array, String field_id, String field_pid, String pid_value, String parentAttr, String childAttr, String[] fields) {
        StringBuilder strResult = new StringBuilder("[");
        JsonArray child = GsonUtils.getWhereArray(array, field_pid, pid_value);
        for (int i = 0; i < child.size(); i++) {
            JsonObject jsonObject = child.get(i).getAsJsonObject();
            if (strResult.length() != 1) strResult.append(",");
            strResult.append("{");

            for (int j = 0; j < fields.length; j++) {
                if (j != 0) strResult.append(",");
                if (fields[j].equals("leaf"))
                    strResult.append(String.format("%s:%s", fields[j], GsonUtils.tryParse(jsonObject, fields[j], true)));
                else {
                    String value = GsonUtils.tryParse(jsonObject, fields[j], "");
                    value = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    strResult.append(String.format("%s:\"%s\"", fields[j], value));
                }
            }
            pid_value = GsonUtils.tryParse(jsonObject, field_id, "");
            JsonArray nodes = GsonUtils.getWhereArray(array, field_pid, pid_value);
            if (nodes.size() > 0) {
                strResult.append(",leaf:false");
                strResult.append(",children:" + getTreeJson(array, field_id, field_pid, pid_value, parentAttr, childAttr, fields));
                if (!StringUtils.isEmpty(parentAttr)) strResult.append("," + parentAttr);
            } else {
                strResult.append(",leaf:true");
                if (!StringUtils.isEmpty(childAttr)) strResult.append("," + childAttr);
            }
            strResult.append("}");
        }
        strResult.append("]");
        return strResult.toString();
    }

}
