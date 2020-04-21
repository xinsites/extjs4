package com.xinsite.common.uitls.lang;

import com.xinsite.common.uitls.gson.GsonUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Date;

public class ValueUtils {

    /**
     * 对象转换
     */
    @SuppressWarnings("unchecked")
    public static <T> T tryParse(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        T retObject = null;
        try {
            if (clazz == String.class) {
                retObject = (T) ObjectUtils.objToStr(value);
            } else if (clazz == Integer.class) {
                retObject = (T) ObjectUtils.toInteger(value);
            } else if (clazz == Long.class) {
                retObject = (T) ObjectUtils.toLong(value);
            } else if (clazz == Double.class) {
                retObject = (T) ObjectUtils.toDouble(value);
            } else if (clazz == Float.class) {
                retObject = (T) ObjectUtils.toFloat(value);
            } else if (clazz == Boolean.class) {
                if (value.toString().equals("1")) value = "true";
                retObject = (T) ObjectUtils.toBoolean(value);
            } else if (clazz == Date.class) {
                if (value instanceof String) {
                    retObject = (T) DateUtils.parseDate(value);
                } else {
                    retObject = (T) DateUtil.getJavaDate((Double) value);
                }
            } else {
                retObject = GsonUtils.getBean(value.toString(), clazz);
            }
        } catch (ClassCastException e) {
            // silent
        }
        return retObject;
    }

    /**
     * 对象转换，有默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T tryParse(Object value, T defaultValue) {
        T retObject = null;
        try {
            if (value != null && !StringUtils.isEmpty(value.toString())) {
                String str_value = value.toString();
                Class<T> clazz = (Class<T>) defaultValue.getClass();
                if (clazz == String.class) {
                    //str_value=str_value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    retObject = (T) ObjectUtils.objToStr(str_value);
                } else if (clazz == Integer.class) {
                    retObject = (T) ObjectUtils.toInteger(str_value);
                } else if (clazz == Long.class) {
                    retObject = (T) ObjectUtils.toLong(str_value);
                } else if (clazz == Double.class) {
                    retObject = (T) ObjectUtils.toDouble(str_value);
                } else if (clazz == Float.class) {
                    retObject = (T) ObjectUtils.toFloat(str_value);
                } else if (clazz == Boolean.class) {
                    if (str_value.equals("1")) str_value = "true";
                    retObject = (T) ObjectUtils.toBoolean(str_value);
                } else if (clazz == Date.class) {
                    if (value instanceof String) {
                        retObject = (T) DateUtils.parseDate(value);
                    } else {
                        retObject = (T) DateUtil.getJavaDate((Double) value);
                    }
                } else {
                    retObject = GsonUtils.getBean(value.toString(), clazz);
                }
            }
        } catch (ClassCastException e) {
            // silent
        }
        return retObject == null ? defaultValue : retObject;
    }

    /**
     * 对象转换，有默认值,如果是空或者给定的固定返回默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T tryParse(Object value, Object fixValue, T defaultValue) {
        T retObject = null;
        try {
            if (value != null && !StringUtils.isEmpty(value.toString())) {
                String str_value = value.toString();
                Class<T> clazz = (Class<T>) defaultValue.getClass();
                if (clazz == String.class) {
                    //str_value=str_value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    retObject = (T) ObjectUtils.objToStr(str_value);
                } else if (clazz == Integer.class) {
                    retObject = (T) ObjectUtils.toInteger(str_value);
                } else if (clazz == Long.class) {
                    retObject = (T) ObjectUtils.toLong(str_value);
                } else if (clazz == Double.class) {
                    retObject = (T) ObjectUtils.toDouble(str_value);
                } else if (clazz == Float.class) {
                    retObject = (T) ObjectUtils.toFloat(str_value);
                } else if (clazz == Boolean.class) {
                    if (str_value.equals("1")) str_value = "true";
                    retObject = (T) ObjectUtils.toBoolean(str_value);
                } else if (clazz == Date.class) {
                    if (value instanceof String) {
                        retObject = (T) DateUtils.parseDate(value);
                    } else {
                        retObject = (T) DateUtil.getJavaDate((Double) value);
                    }
                } else {
                    retObject = GsonUtils.getBean(value.toString(), clazz);
                }
            }
            if (value != null && fixValue != null) {
                if (value.toString().equals(fixValue.toString())) return defaultValue;
            }
        } catch (ClassCastException e) {
            // silent
        }
        return retObject == null ? defaultValue : retObject;
    }

}
