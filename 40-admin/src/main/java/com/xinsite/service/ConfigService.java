package com.xinsite.service;

import com.xinsite.common.uitls.Global;
import org.springframework.stereotype.Service;

/**
 * html调用 thymeleaf 实现参数管理
 */
@Service("Config")
public class ConfigService {
    /**
     * 根据键名查询参数配置信息
     *
     * @param config_key 参数名称
     * @return 参数键值
     */
    public String getKey(String config_key) {
        return Global.getConfig(config_key);
    }
}
