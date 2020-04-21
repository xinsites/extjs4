package com.xinsite.dal.uitls;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 对象操作工具类, 继承org.apache.commons.lang3.ObjectUtils类
 *
 * @author zhangxiaxin
 * @version 2018-07-15
 */
public class Utils_Object extends org.apache.commons.lang3.ObjectUtils {

    /**
     * 转换为Double类型
     */
    public static Double toDouble(final Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return NumberUtils.toDouble(StringUtils.trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(final Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(final Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(final Object val) {
        return toLong(val).intValue();
    }

    /**
     * 转换为Boolean类型 'true', 'on', 'y', 't', 'yes' or '1' (case insensitive) will return true. Otherwise, false is returned.
     */
    public static Boolean toBoolean(final Object val) {
        if (val == null) {
            return false;
        }
        return BooleanUtils.toBoolean(val.toString()) || "1".equals(val.toString());
    }

    /**
     * 转换为字符串
     *
     * @param obj
     * @return
     */
    public static String objToStr(final Object obj) {
        return objToStr(obj, StringUtils.EMPTY);
    }

    /**
     * 如果对象为空，则使用defaultVal值
     *
     * @param obj
     * @param defaultVal
     * @return
     */
    public static String objToStr(final Object obj, final String defaultVal) {
        return obj == null ? defaultVal : obj.toString();
    }



}
