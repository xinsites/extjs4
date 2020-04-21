package com.xinsite.common.uitls;

import com.xinsite.common.enums.UnitEnum;
import com.xinsite.common.uitls.io.YmlUtils;
import com.xinsite.common.uitls.lang.ByteUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 全局配置类
 */
public class Global {
    private static final Logger log = LoggerFactory.getLogger(Global.class);

    private static String location = "config/application.yml";

    /**
     * 保存全局属性值
     */
    private static Properties props;


    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        if (props == null) {
            props = YmlUtils.getYmlByFileName(location);
        }
        String value = StringUtils.EMPTY;
        if (props != null) {
            try {
                value = props.getProperty(key);
            } catch (Exception e) {
                log.error("获取全局配置异常 {}", key);
            }
        }
        return value;
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static Boolean getBoolean(String key) {
        return ValueUtils.tryParse(getConfig(key), false);
    }

    /**
     * 获取字符串属性（可指定默认值）
     */
    public static int getInt(String key) {
        return ValueUtils.tryParse(getConfig(key), 0);
    }

    /**
     * 单个文件最大上传字节数
     */
    public static long getMaxFileSize() {
        String max_file_size = Global.getConfig("spring.servlet.multipart.max-file-size");
        return ByteUtils.getByteSize(max_file_size);
    }

    /**
     * 单个文件最大上传MB
     */
    public static int getMaxFileSizeMB() {
        String max_file_size = Global.getConfig("spring.servlet.multipart.max-file-size");
        return ByteUtils.getUnitByType(max_file_size, UnitEnum.MB);
    }
}
