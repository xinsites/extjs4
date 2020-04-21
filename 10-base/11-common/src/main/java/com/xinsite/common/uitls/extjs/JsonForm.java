package com.xinsite.common.uitls.extjs;

import com.xinsite.common.uitls.gson.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Extjs,FormPanel组件Json格式字符串
 *
 * @author www.xinsite.vip
 * @version 2018-9-11
 */
public class JsonForm {
    /**
     * FormPanel组件Json格式字符串
     */
    public static String getFormJson(Object obj, String table_name) {
        StringBuilder sb = new StringBuilder(String.format("\"%s\"", table_name));
        sb.append(":{\"data\":");
        sb.append(GsonUtils.toJson(obj));
        sb.append("}");
        return sb.toString();
    }

    /**
     * FormPanel组件Json格式字符串
     */
    public static String getFormJson(JsonArray array, String table_name) {
        StringBuilder sb = new StringBuilder(String.format("\"%s\"", table_name));
        sb.append(":{\"data\":");
        if (array != null && array.size() > 0) {
            JsonObject object = GsonUtils.getObject(array, 0);
            sb.append(GsonUtils.toJson(object));
        } else {
            sb.append("{}");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * FormPanel组件Json格式字符串
     */
    public static String getFormJson(JsonObject object, String table_name) {
        StringBuilder sb = new StringBuilder(String.format("\"%s\"", table_name));
        sb.append(":{\"data\":");
        sb.append(GsonUtils.toJson(object));
        sb.append("}");
        return sb.toString();
    }
}
