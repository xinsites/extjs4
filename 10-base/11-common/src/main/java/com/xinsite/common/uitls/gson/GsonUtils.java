package com.xinsite.common.uitls.gson;

import com.google.gson.*;
import com.xinsite.common.uitls.collect.ListUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class GsonUtils {
    /**
     * 将对象转换成json字符串
     */
    public static String toJson(Object obj) {
        //解决new Gson().toJson(object) null值不进行转换问题
        //Gson gson = new GsonBuilder().serializeNulls().create();
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * 将json字符串转换成对象
     */
    public static <T> T getBean(Object json, Class<T> clazz) {
        return new Gson().fromJson(json.toString(), clazz);
    }

    /**
     * 将json字符串转换成List对象
     */
    public static <T> List<T> getList(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        return new Gson().fromJson(json, type);
    }

    /**
     * 将JsonArray转换成List对象
     */
    public static <T> List<T> getList(JsonArray array, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        String json = GsonUtils.toJson(array);
        return new Gson().fromJson(json, type);
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    }

    public static <T> ArrayList<T> gsonToList(String json, Class<T> cls) {
        if (StringUtils.isEmpty(json)) return new ArrayList<T>();
        try {
            Type type = new TypeToken<ArrayList<JsonObject>>() {
            }.getType();
            ArrayList<JsonObject> jsonObjs = new Gson().fromJson(json, type);// 反序列化出ArrayList<JsonObject>，
            ArrayList<T> listOfT = new ArrayList<T>();
            for (JsonObject jsonObj : jsonObjs) {
                listOfT.add(new Gson().fromJson(jsonObj, cls));
            }
            return listOfT;
        } catch (Exception ex) {
            return new ArrayList<T>();
        }
    }

    public static <T> List<Map<String, T>> getListMaps(JsonArray array) {
        List<Map<String, T>> list = null;
        String json = GsonUtils.toJson(array);
        Gson gson = new Gson();
        list = gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    public static <T> List<Map<String, T>> getListMaps(String json) {
        List<Map<String, T>> list = null;
        Gson gson = new Gson();
        list = gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    public static <T> Map<String, T> gsonToMaps(String json) {
        Map<String, T> map = null;
        Gson gson = new Gson();
        map = gson.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    /**
     * 将json字符串转换成List对象
     */
    public static <T> List<T> getList(String json, Type typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }

    /**
     * 将JsonArray转换成List对象
     */
    public static <T> List<T> getList(JsonArray array, Type typeOfT) {
        String json = GsonUtils.toJson(array);
        return new Gson().fromJson(json, typeOfT);
    }

    /**
     * 返回JsonArray某个JsonObject对象
     */
    public static JsonObject getObject(JsonArray array, int index) {
        if (array != null && index < array.size()) {
            return array.get(index).getAsJsonObject();
        }
        return new JsonObject();
    }

    /**
     * 返回JsonArray某行的key值
     */
    public static String getObjectValue(JsonArray array, int rowNo, String key) {
        JsonObject object = getObject(array, rowNo);
        return tryParse(object, key, "");
    }

    /**
     * 将json字符串转换成JsonObject对象
     */
    public static JsonObject getObject(String json) {
        return new Gson().fromJson(json, JsonObject.class);
    }

    /**
     * 将json字符串转换成JsonObject对象
     */
    public static Map getMap(String json) {
        return new Gson().fromJson(json, Map.class);
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public static <T> T tryParse(JsonObject object, String key, Class<T> clazz) {
        if (StringUtils.isEmpty(key)) return null;
        if (object.get(key) == null) return null;
        String value = object.get(key).getAsString();
        return ValueUtils.tryParse(value, clazz);
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public static <T> T tryParse(JsonObject object, String key, T defaultValue) {
        if (StringUtils.isEmpty(key)) return defaultValue;
        try {
            JsonElement element = object.get(key);
            String value = element.getAsString();
            return ValueUtils.tryParse(value, defaultValue);
        } catch (Exception e) {
            // silent
        }
        return defaultValue;
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public static String tryParse(JsonObject object, String key) {
        if (StringUtils.isEmpty(key)) return "";
        try {
            JsonElement element = object.get(key);
            String value = element.getAsString();
            return ValueUtils.tryParse(value, "");
        } catch (Exception e) {
            // silent
        }
        return "";
    }

    public static JsonObject getWhereJsonObject(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!StringUtils.isEmpty(field_id) && !StringUtils.isEmpty(field_value.toString())) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (GsonUtils.tryParse(jsonObject, field_id, "").equals(field_value.toString())) {
                    child.add(jsonObject);
                }
            }
        }
        if (child != null && child.size() > 0) {
            return child.get(0).getAsJsonObject();
        }
        return null;
    }

    public static JsonArray getWhereArray(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!StringUtils.isEmpty(field_id) && !StringUtils.isEmpty(field_value.toString())) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (GsonUtils.tryParse(jsonObject, field_id, "").equals(field_value.toString())) {
                    child.add(jsonObject);
                }
            }
        }
        return child;
    }

    public static JsonArray getNotWhereArray(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!StringUtils.isEmpty(field_id) && !StringUtils.isEmpty(field_value.toString())) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (!GsonUtils.tryParse(jsonObject, field_id, "").equals(field_value.toString())) {
                    child.add(jsonObject);
                }
            }
        }
        return child;
    }

    public static JsonArray getWhereArrayByIds(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!StringUtils.isEmpty(field_id) && !StringUtils.isEmpty(field_value.toString())) {
            List<String> values = Arrays.asList(field_value.toString().split(","));
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (values.contains(GsonUtils.tryParse(jsonObject, field_id, ""))) {
                    child.add(jsonObject);
                }
            }
        }
        return child;
    }

    public static List<String> getArrayFields(JsonArray array) {
        List<String> list = new ArrayList<String>();
        if (array != null && array.size() > 0) {
            JsonObject object = GsonUtils.getObject(array, 0);
            Set<String> set = object.keySet();
            list = new ArrayList<String>(set);
        }
        return list;
    }

    /**
     * 获取模板文件列表cloumns字符串
     */
    public static String getJsonByMap(Map ht) {
        StringBuilder json = new StringBuilder();
        for (Object key : ht.keySet()) {
            if (!StringUtils.isEmpty(json.toString()))
                json.append(",\n");
            json.append("\"").append(key).append("\"").append(":").append(ht.get(key));
        }
        return "{" + json.toString() + "\n}";

    }


}
