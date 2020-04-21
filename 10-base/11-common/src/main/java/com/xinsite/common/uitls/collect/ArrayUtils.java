package com.xinsite.common.uitls.collect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    /**
     * 返回JsonArray某一字段排序后的JsonArray
     */
    public static JsonArray arrayOrderBy(JsonArray array, String orderBy, String fieldType) {
        if (array != null && array.size() > 1) {
            List<JsonObject> list = new ArrayList();
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObj = (JsonObject) array.get(i);
                list.add(jsonObj);
            }
            list = ListUtils.listOrderBy(list, orderBy, fieldType);
            JsonArray child = new JsonArray();
            for (int i = 0; i < list.size(); i++) {
                child.add(list.get(i));
            }
            return child;
        }
        return array;
    }

    /**
     * 返回JsonArray某一字段列表值
     */
    public static <T> List<T> listByField(JsonArray array, String field, T defaultValue) {
        List<T> list = new ArrayList<T>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObj = GsonUtils.getObject(array, i);
                list.add(GsonUtils.tryParse(jsonObj, field, defaultValue));
            }
        }
        return list;
    }

    /**
     * 返回JsonArray某一字段列表值，是否转换成小写
     */
    public static List<String> listByField(JsonArray array, String field, String defaultValue, boolean isLower) {
        List<String> list = new ArrayList<String>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObj = GsonUtils.getObject(array, i);
                if (isLower)
                    list.add(GsonUtils.tryParse(jsonObj, field, defaultValue).toLowerCase());
                else
                    list.add(GsonUtils.tryParse(jsonObj, field, defaultValue).toUpperCase());
            }
        }
        return list;
    }


    /**
     * 合并JsonArray某一字段列表值
     */
    public static <T> String joinFields(JsonArray array, String field, T defaultValue) {
        List<T> list = new ArrayList<T>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObj = GsonUtils.getObject(array, i);
                list.add(GsonUtils.tryParse(jsonObj, field, defaultValue));
            }
        }
        return StringUtils.joinAsList(list);
    }

    /**
     * 合并去重JsonArray某一字段列表值
     */
    public static <T> String joinFieldsToRepeat(JsonArray array, String field, T defaultValue) {
        List<T> list = new ArrayList<T>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObj = GsonUtils.getObject(array, i);
                T val = GsonUtils.tryParse(jsonObj, field, defaultValue);
                if (!list.contains(val)) list.add(val);
            }
        }
        return StringUtils.joinAsList(list);
    }

}
