package com.xinsite.dal.uitls;

import java.util.Date;

import org.apache.poi.ss.usermodel.DateUtil;

public class Utils_Value {

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object value) {
        return tryParse(value, 0);
    }

    /**
     * 转换为Boolean类型 'true', 'on', 'yes' or '1'
     */
    public static Boolean toBoolean(final Object value) {
        return tryParse(value, false);
    }

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
                retObject = (T) Utils_Object.objToStr(value);
            } else if (clazz == Integer.class) {
                retObject = (T) Utils_Object.toInteger(value);
            } else if (clazz == Long.class) {
                retObject = (T) Utils_Object.toLong(value);
            } else if (clazz == Double.class) {
                retObject = (T) Utils_Object.toDouble(value);
            } else if (clazz == Float.class) {
                retObject = (T) Utils_Object.toFloat(value);
            } else if (clazz == Boolean.class) {
                if (value.toString().equals("1")) value = "true";
                retObject = (T) Utils_Object.toBoolean(value);
            } else if (clazz == Date.class) {
                if (value instanceof String) {
                    retObject = (T) Utils_Date.parseDate(value);
                } else {
                    retObject = (T) DateUtil.getJavaDate((Double) value);
                }
            } else {
                retObject = Utils_Gson.getBean(value.toString(), clazz);
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
            if (value != null && !Utils_String.isEmpty(value.toString())) {
                String str_value = value.toString();
                Class<T> clazz = (Class<T>) defaultValue.getClass();
                if (clazz == String.class) {
                    //str_value=str_value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    retObject = (T) Utils_Object.objToStr(str_value);
                } else if (clazz == Integer.class) {
                    retObject = (T) Utils_Object.toInteger(str_value);
                } else if (clazz == Long.class) {
                    retObject = (T) Utils_Object.toLong(str_value);
                } else if (clazz == Double.class) {
                    retObject = (T) Utils_Object.toDouble(str_value);
                } else if (clazz == Float.class) {
                    retObject = (T) Utils_Object.toFloat(str_value);
                } else if (clazz == Boolean.class) {
                    if (str_value.equals("1")) str_value = "true";
                    retObject = (T) Utils_Object.toBoolean(str_value);
                } else if (clazz == Date.class) {
                    if (value instanceof String) {
                        retObject = (T) Utils_Date.parseDate(value);
                    } else {
                        retObject = (T) DateUtil.getJavaDate((Double) value);
                    }
                } else {
                    retObject = Utils_Gson.getBean(value.toString(), clazz);
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
            if (value != null && !Utils_String.isEmpty(value.toString())) {
                String str_value = value.toString();
                Class<T> clazz = (Class<T>) defaultValue.getClass();
                if (clazz == String.class) {
                    //str_value=str_value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    retObject = (T) Utils_Object.objToStr(str_value);
                } else if (clazz == Integer.class) {
                    retObject = (T) Utils_Object.toInteger(str_value);
                } else if (clazz == Long.class) {
                    retObject = (T) Utils_Object.toLong(str_value);
                } else if (clazz == Double.class) {
                    retObject = (T) Utils_Object.toDouble(str_value);
                } else if (clazz == Float.class) {
                    retObject = (T) Utils_Object.toFloat(str_value);
                } else if (clazz == Boolean.class) {
                    if (str_value.equals("1")) str_value = "true";
                    retObject = (T) Utils_Object.toBoolean(str_value);
                } else if (clazz == Date.class) {
                    if (value instanceof String) {
                        retObject = (T) Utils_Date.parseDate(value);
                    } else {
                        retObject = (T) DateUtil.getJavaDate((Double) value);
                    }
                } else {
                    retObject = Utils_Gson.getBean(value.toString(), clazz);
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
