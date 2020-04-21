package com.xinsite.dal.uitls;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class Utils_Gson {
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
    public static <T> T getBean(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
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
        String json = Utils_Gson.toJson(array);
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
        if (Utils_String.isEmpty(json)) return new ArrayList<T>();
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
        String json = Utils_Gson.toJson(array);
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
        String json = Utils_Gson.toJson(array);
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
        if (Utils_String.isEmpty(key)) return null;
        if (object.get(key) == null) return null;
        String value = object.get(key).getAsString();
        return Utils_Value.tryParse(value, clazz);
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public static <T> T tryParse(JsonObject object, String key, T defaultValue) {
        if (Utils_String.isEmpty(key)) return defaultValue;
        try {
            JsonElement element = object.get(key);
            String value = element.getAsString();
            return Utils_Value.tryParse(value, defaultValue);
        } catch (Exception e) {
            // silent
        }
        return defaultValue;
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public static String tryParse(JsonObject object, String key) {
        if (Utils_String.isEmpty(key)) return "";
        try {
            JsonElement element = object.get(key);
            String value = element.getAsString();
            return Utils_Value.tryParse(value, "");
        } catch (Exception e) {
            // silent
        }
        return "";
    }

    public static JsonObject getWhereJsonObject(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!Utils_String.isEmpty(field_id) && !Utils_String.isEmpty(field_value.toString())) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (Utils_Gson.tryParse(jsonObject, field_id, "").equals(field_value.toString())) {
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
        if (!Utils_String.isEmpty(field_id) && !Utils_String.isEmpty(field_value.toString())) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (Utils_Gson.tryParse(jsonObject, field_id, "").equals(field_value.toString())) {
                    child.add(jsonObject);
                }
            }
        }
        return child;
    }

    public static JsonArray getNotWhereArray(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!Utils_String.isEmpty(field_id) && !Utils_String.isEmpty(field_value.toString())) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (!Utils_Gson.tryParse(jsonObject, field_id, "").equals(field_value.toString())) {
                    child.add(jsonObject);
                }
            }
        }
        return child;
    }

    public static JsonArray getWhereArrayByIds(JsonArray array, String field_id, Object field_value) {
        JsonArray child = new JsonArray();
        if (!Utils_String.isEmpty(field_id) && !Utils_String.isEmpty(field_value.toString())) {
            List<String> values = Arrays.asList(field_value.toString().split(","));
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (values.contains(Utils_Gson.tryParse(jsonObject, field_id, ""))) {
                    child.add(jsonObject);
                }
            }
        }
        return child;
    }

    public static List<String> getArrayFields(JsonArray array) {
        List<String> list = new ArrayList<String>();
        if (array != null && array.size() > 0) {
            JsonObject object = Utils_Gson.getObject(array, 0);
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
            if (!Utils_String.isEmpty(json.toString()))
                json.append(",\n");
            json.append("\"").append(key).append("\"").append(":").append(ht.get(key));
        }
        return "{" + json.toString() + "\n}";

    }

    @SuppressWarnings("rawtypes")
    public static void setProperty(BeanWrapper beanWrapper, String propName, Object value) {
        // 得到.所在的位置
        int indexOfDot = propName.indexOf(".");
        // 没有.的情况
        if (indexOfDot < 0) {
            if (beanWrapper.getPropertyType(propName).equals(
                    java.util.Date.class)) {
                try {
                    value = Long.parseLong((String) value.toString());
                    value = new java.util.Date((Long) value);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            if (value instanceof JSONArray) {
                //do nothingList sobjs=new ArrayList();
            } else {
                beanWrapper.setPropertyValue(propName, value);
            }

            return;
        }

        // 以下是有.的情况
        String nestedProperty = propName.substring(0, indexOfDot);
        Class nestedClass = beanWrapper.getPropertyType(nestedProperty);

        // 新建一个子对象并设值
        BeanWrapper nestedBeanWrapper = new BeanWrapperImpl(nestedClass);
        Object nestedObject = beanWrapper.getPropertyValue(nestedProperty);
        if (nestedObject == null) {
            beanWrapper.setPropertyValue(nestedProperty, nestedBeanWrapper
                    .getWrappedInstance());
        } else {
            nestedBeanWrapper = new BeanWrapperImpl(nestedObject);
        }
        String subProperty = propName.substring(indexOfDot + 1);
        setProperty(nestedBeanWrapper, subProperty, value/*, dataFormats*/);
    }
}
