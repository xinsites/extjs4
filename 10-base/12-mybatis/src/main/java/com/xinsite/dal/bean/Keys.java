package com.xinsite.dal.bean;

public class Keys {
    public String key;
    public Object value;

    public Keys() {
    }

    public Keys(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public static Keys getKey(String key, Object value) {
        Keys k = new Keys();
        k.key = key;
        k.value = value;
        return k;
    }

    public String getValue() {
        if (value != null)
            return value.toString();
        return "";
    }
}
