package com.xinsite.core.model.design;

/**
 * 默认值实体类
 */
public class DefaultValueModel {
    public String value_tip; //默认值标识
    public String value; //默认值
    public String type; //默认值类型，object：对象；string :字符串
    public String text; //默认值说明

    public DefaultValueModel() {
    }

    public DefaultValueModel(String value_tip, String value) {
        this.value_tip = value_tip;
        this.value = value;
    }
}

