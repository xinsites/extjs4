package com.xinsite.common.bean;

public class Editors {
    public String field_name;    //数据库字段名
    public String field_explain; //数据库中文字段名
    public String data_type;     //数据库字段类型
    public String xtype;        //网页字段输入框类型
    public boolean isNull = true;     //是否允许空，true:可以空值
    public String default_value = "";  //默认值

    public Editors() {
    }

    public Editors(String field_name, String field_explain, String data_type, String xtype, int field_empty, String default_value) {
        this.field_name = field_name;
        this.field_explain = field_explain;
        this.data_type = data_type;
        this.xtype = xtype;
        this.isNull = true;
        if (field_empty == 0) this.isNull = false;
        this.default_value = default_value;
    }
}

