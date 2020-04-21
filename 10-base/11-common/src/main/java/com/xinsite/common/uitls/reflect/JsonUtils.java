package com.xinsite.common.uitls.reflect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List toList(JSONArray jsonArray, Class clz)
            throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            BeanWrapperImpl bw = new BeanWrapperImpl(clz);
            JSONArray namesArray = jo.names();
            if (namesArray != null) {
                for (int j = 0; j < namesArray.length(); j++) {
                    String name = (String) namesArray.get(j);
                    Object value = jo.get(name);
                    try {
                        setProperty(bw, name, value);
                    } catch (Exception e) {
                    }
                }
                list.add(bw.getWrappedInstance());
            }
        }
        return list;
    }

    /**
     * 将集合转换成JSONArray
     */
    @SuppressWarnings("rawtypes")
    public static JSONArray toJsonArray(List list) throws JSONException {
        JSONArray jsonarray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            JSONObject objJson = toJsonObject(obj);
            jsonarray.put(i, objJson);
        }
        return jsonarray;
    }

    /**
     * 将javaBean转换成JSONObject
     *
     * @param bean javaBean
     * @return json对象
     */
    public static JSONObject toJsonObject(Object bean) {
        return new JSONObject(toMap(bean));
    }

    /**
     * 将Json字符串转为List
     *
     * @param jsonString 接受的json
     * @param clz        列表里的对象对应的类
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List toList(String jsonString, Class clz/*, String[] dataFormats*/)
            throws JSONException {

        JSONArray ja = new JSONArray(jsonString);

        List list = new ArrayList();

        for (int i = 0; i < ja.length(); i++) {

            JSONObject jo = ja.getJSONObject(i);
            BeanWrapperImpl bw = new BeanWrapperImpl(clz);
            JSONArray namesArray = jo.names();
            for (int j = 0; j < namesArray.length(); j++) {
                String name = (String) namesArray.get(j);
                Object value = jo.get(name);
                try {
                    setProperty(bw, name, value/*, dataFormats*/);
                } catch (Exception e) {
                }
            }
            list.add(bw.getWrappedInstance());
        }

        return list;
    }

    /**
     * 将Json字符串转为List
     *
     * @param jsonString 接受的json
     * @param clz        列表里的对象对应的类
     * @param fieldMap   对象的字段名-对应的类
     */
    @SuppressWarnings({"rawtypes", "unused", "unchecked"})
    public static List toList(String jsonString, Class clz, Map fieldMap)
            throws JSONException {

        JSONArray ja = new JSONArray(jsonString);

        List list = new ArrayList();

        for (int i = 0; i < ja.length(); i++) {

            JSONObject jo = ja.getJSONObject(i);
            BeanWrapperImpl bw = new BeanWrapperImpl(clz);
            JSONArray namesArray = jo.names();
            for (int j = 0; j < namesArray.length(); j++) {
                String name = (String) namesArray.get(j);
                Object value = jo.get(name);
                value.getClass();
                String sv = value.toString();

                try {
                    if (fieldMap != null && fieldMap.get(name) != null) {
                        Class subClz = (Class) fieldMap.get(name);

                        if (value instanceof JSONArray) {
                            Object o = toList((JSONArray) value, subClz);
                            setProperty(bw, name, o);
                        } else {
                            Object o = toObject(value.toString(), subClz, fieldMap);
                            setProperty(bw, name, o);
                        }

                    } else {

                        setProperty(bw, name, value/*, dataFormats*/);
                    }
                } catch (Exception e) {
                }
            }
            list.add(bw.getWrappedInstance());
        }

        return list;
    }

    /**
     * 将Json对象转为域对象
     *
     * @param jo  接受的json对象
     * @param clz 对象对应的类
     */
    @SuppressWarnings("rawtypes")
    public static Object toObject(JSONObject jo, Class clz/*, String[] dataFormats*/)
            throws JSONException {
        BeanWrapperImpl bw = new BeanWrapperImpl(clz);
        JSONArray namesArray = jo.names();

        for (int j = 0; j < namesArray.length(); j++) {
            String name = (String) namesArray.get(j);
            Object value = jo.get(name);
            try {
                setProperty(bw, name, value/*, dataFormats*/);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        return bw.getWrappedInstance();
    }

    /**
     * 将数组转为域对象,本方法仅供内部调用
     *
     * @param clz 对象对应的类
     */
    @SuppressWarnings({"rawtypes", "unused"})
    public static Object toObject(ArrayList ja, Class clz)
            throws JSONException {
        BeanWrapperImpl bw = new BeanWrapperImpl(clz);

        for (int j = 0; j < ja.size(); j++) {
            JSONObject jo = (JSONObject) ja.get(j);
        }

        return bw.getWrappedInstance();
    }

    /**
     * 将Json对象转为域对象
     *
     * @param clz      对象对应的类
     * @param fieldMap 对象的字段名-对应的类
     */
    @SuppressWarnings("rawtypes")
    public static Object toObject(String js, Class clz, Map fieldMap)
            throws JSONException {
        BeanWrapperImpl bw = new BeanWrapperImpl(clz);
        JSONObject jo = new JSONObject(js);
        JSONArray namesArray = jo.names();

        for (int j = 0; j < namesArray.length(); j++) {
            String name = (String) namesArray.get(j);
            Object value = jo.get(name);
            try {
                if (fieldMap != null && fieldMap.get(name) != null) {
                    Class subClz = (Class) fieldMap.get(name);

                    if (value instanceof JSONArray) {
                        Object o = toList((JSONArray) value, subClz);
                        setProperty(bw, name, o);
                    } else {
                        Object o = toObject(value.toString(), subClz, null);
                        setProperty(bw, name, o);
                    }
                } else {

                    setProperty(bw, name, value/*, dataFormats*/);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        return bw.getWrappedInstance();
    }

    /**
     * 将Json字符串转为域对象
     *
     * @param jsonString 接受的json字符串
     * @param clz        对象对应的类
     */
    @SuppressWarnings("rawtypes")
    public static Object toObject(String jsonString, Class clz) throws JSONException {
        JSONObject jo = new JSONObject(jsonString);

        BeanWrapperImpl bw = new BeanWrapperImpl(clz);
        JSONArray namesArray = jo.names();

        for (int j = 0; j < namesArray.length(); j++) {
            String name = (String) namesArray.get(j);
            Object value = jo.get(name);
            try {
                setProperty(bw, name, value/*, dataFormats*/);
            } catch (Exception e) {
            }
        }

        return bw.getWrappedInstance();
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


    /**
     * 将Json字符串转为对象
     *
     * @param jsonString    接受的json
     * @param clz           列表里的类
     * @param colletionName 类里的集合字段
     * @param colletionClz  类里的集合字段中的类
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List toList(String jsonString, Class clz, String colletionName, Class colletionClz) throws JSONException {

        JSONArray ja = new JSONArray(jsonString);

        List list = new ArrayList();

        for (int i = 0; i < ja.length(); i++) {

            JSONObject jo = ja.getJSONObject(i);
            BeanWrapperImpl bw = new BeanWrapperImpl(clz);
            JSONArray namesArray = jo.names();
            for (int j = 0; j < namesArray.length(); j++) {
                String name = (String) namesArray.get(j);
                Object value = jo.get(name);
                try {
                    if (name.equals(colletionName)) {//对应的名称是数组

                        List values = JsonUtils.toList(value.toString(), colletionClz);
                        JsonUtils.setProperty(bw, name, values);
                    } else {
                        JsonUtils.setProperty(bw, name, value);
                    }


                } catch (Exception e) {
                    //;/e.printStackTrace();
                }
            }
            list.add(bw.getWrappedInstance());
        }

        return list;
    }

    /**
     * 将jsonString转换成Map，只转换简单的name：value形式的对象，其余忽略
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map simpleObjToMap(String jsonString) throws JSONException {
        JSONObject jo = new JSONObject(jsonString);
        Map map = new HashMap();

        JSONArray namesArray = jo.names();

        for (int j = 0; j < namesArray.length(); j++) {
            String name = (String) namesArray.get(j);
            Object value = jo.get(name);
            map.put(name, value);
        }


        return map;
    }


    /**
     * 将javaBean转换成Map
     *
     * @param javaBean javaBean
     * @return Map对象
     */
    public static Map<String, String> toMap(Object javaBean) {
        Map<String, String> result = new HashMap<String, String>();
        Method[] methods = javaBean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);

                    Object value = method.invoke(javaBean, (Object[]) null);
                    result.put(field, null == value ? "" : value.toString());
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     * 将数组转为域对象,本方法仅供内部调用
     */
    public static String toJson(Object obj) {
        JSONObject jsonObj = new JSONObject(obj);
        return jsonObj.toString();
    }

}
