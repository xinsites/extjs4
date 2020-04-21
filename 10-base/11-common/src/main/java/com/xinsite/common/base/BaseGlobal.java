package com.xinsite.common.base;


import com.beust.jcommander.internal.Maps;
import com.xinsite.common.uitls.io.PropertiesLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 全局配置基类
 */
public class BaseGlobal {
    /**
     * 当前对象实例
     */
    private static BaseGlobal global = null;

    /**
     * 属性文件加载对象
     */
    private PropertiesLoader loader = null;

    /**
     * 保存全局属性值
     */
    private Map<String, String> map = Maps.newHashMap();

    /**
     * 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
     */
    public static synchronized BaseGlobal getInstance() {
        if (global == null) {
            synchronized (BaseGlobal.class) {
                if (global == null)
                    global = new BaseGlobal();
            }
        }
        return global;
    }

    /**
     * 获取配置
     */
    public String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            if (loader != null) {
                value = loader.getProperty(key);
                map.put(key, value != null ? value : StringUtils.EMPTY);
            }
            return StringUtils.EMPTY;
        }
        return value;
    }


    public PropertiesLoader getLoader() {
        return loader;
    }

    public void setLoader(PropertiesLoader loader) {
        this.loader = loader;
    }
}
