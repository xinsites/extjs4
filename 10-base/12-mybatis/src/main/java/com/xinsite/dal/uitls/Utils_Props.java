package com.xinsite.dal.uitls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件工具类
 */
public final class Utils_Props {

    private final static Logger LOGGER = LoggerFactory.getLogger(Utils_Props.class);

    /**
     * 加载属性文件
     *
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                //throw new FileNotFoundException(fileName + " file is not found");
                System.out.println(fileName + " file is not found");
            }
            if (is != null) {
                props = new Properties();
                props.load(is);
            }
        } catch (IOException e) {
            LOGGER.error("load properties file failure", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("close inputstream failure", e);
                }
            }
        }
        return props;
    }

    /**
     * 获取字符型属性(默认值为空字符串)
     *
     * @param props
     * @param key
     * @return
     */
    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }

    /**
     * 获取字符串属性（可指定默认值）
     *
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Properties props, String key, String defaultValue) {
        if (props == null) return defaultValue;
        String value = defaultValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    public static int getInt(Properties props, String key, int defaultValue) {
        if (props == null) return defaultValue;
        int value = defaultValue;
        if (props.containsKey(key)) {
            value = Utils_Value.tryParse(props.getProperty(key), Integer.class);
        }
        return value;
    }

    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        if (props == null) return defaultValue;
        boolean value = defaultValue;
        if (props.containsKey(key)) {
            value = Utils_Value.tryParse(props.getProperty(key), Boolean.class);
        }
        return value;
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static String getString(String fileName, String key, String defaultValue) {
        Properties props = Utils_Props.loadProps(fileName);
        return Utils_Props.getString(props, key, defaultValue);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static int getInt(String fileName, String key, int defaultValue) {
        Properties props = Utils_Props.loadProps(fileName);
        return Utils_Props.getInt(props, key, defaultValue);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static String getString(String key, String defaultValue) {
        Properties props = Utils_Props.loadProps("config/application.properties");
        return Utils_Props.getString(props, key, defaultValue);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static String getString(String key) {
        Properties props = Utils_Props.loadProps("config/application.properties");
        return Utils_Props.getString(props, key, "");
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static int getInt(String key, int defaultValue) {
        Properties props = Utils_Props.loadProps("config/application.properties");
        return Utils_Props.getInt(props, key, defaultValue);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static int getInt(String key) {
        Properties props = Utils_Props.loadProps("config/application.properties");
        return Utils_Props.getInt(props, key, 0);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        Properties props = Utils_Props.loadProps("config/application.properties");
        return Utils_Props.getBoolean(props, key, defaultValue);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static boolean getBoolean(String key) {
        Properties props = Utils_Props.loadProps("config/application.properties");
        return Utils_Props.getBoolean(props, key, false);
    }
}
