package com.xinsite.dal.uitls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

public class Utils_Yml {
    private static final Logger logger = LoggerFactory.getLogger(Utils_Yml.class);

    /**
     * 根据文件名获取yml的文件内容
     *
     * @return
     */
    public static Properties getYmlByFileName(String location) {
        Properties props = null;
        if (location == null) location = "config/application.yml";
        Resource resource = Utils_Resource.getResource(location);
        if (resource.exists()) {
            InputStreamReader in = null;
            try {
                in = new InputStreamReader(resource.getInputStream(), "UTF-8");
                Yaml yml = new Yaml();
                Object obj = yml.loadAs(in, Map.class);
                Map<String, Object> param = (Map<String, Object>) obj;
                props = new Properties();
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();

                    if (val instanceof Map) {
                        forEachYaml(props, key, (Map<String, Object>) val);
                    } else {
                        props.put(key, val.toString());
                    }
                }
            } catch (IOException ex) {
                logger.error("Load " + location + " failure. ", ex);
            } finally {
                if (in != null) Utils_IO.closeQuietly(in);
            }
        }
        return props;
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        Properties props = getYmlByFileName(null);
        if (props == null) return null;
        return props.getProperty(key);
    }

    /**
     * 遍历yml文件，获取map集合
     *
     * @param key_str
     * @param obj
     * @return
     */
    public static void forEachYaml(Properties props, String key_str, Map<String, Object> obj) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (val == null) continue;

            String str_new = "";
            if (Utils_String.isNotBlank(key_str)) {
                str_new = key_str + "." + key;
            } else {
                str_new = key;
            }
            if (val instanceof Map) {
                forEachYaml(props, str_new, (Map<String, Object>) val);
            } else {
                props.put(str_new, val.toString());
            }

        }
    }
}
