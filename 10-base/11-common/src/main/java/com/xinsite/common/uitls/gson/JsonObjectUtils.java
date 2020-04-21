package com.xinsite.common.uitls.gson;

import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.google.gson.JsonObject;

public class JsonObjectUtils {
    private JsonObject object;

    public JsonObjectUtils(JsonObject object) {
        this.object = object;
    }

    public void setObject(JsonObject object) {
        this.object = object;
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public <T> T tryParse(String key, Class<T> clazz) {
        if (StringUtils.isEmpty(key)) return null;
        if (object.get(key) == null) return null;
        String value = this.object.get(key).getAsString();
        return ValueUtils.tryParse(value, clazz);
    }

    /**
     * 获取对象值，没有返回默认值
     */
    public <T> T tryParse(String key, T defaultValue) {
        if (StringUtils.isEmpty(key)) return defaultValue;
        if (object.get(key) == null) return defaultValue;
        String value = this.object.get(key).getAsString();
        return ValueUtils.tryParse(value, defaultValue);
    }
}
