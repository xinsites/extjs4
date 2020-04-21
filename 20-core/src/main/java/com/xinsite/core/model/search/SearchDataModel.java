package com.xinsite.core.model.search;

/**
 * 高级查询表字段条件，页面传值
 * create by zhangxiaxin
 */
public class SearchDataModel {
    public String field;    //数据表字段名
    public String operator; //字段查询符，如=,like,!=,>=等等
    public String value;    //字段查询值
    public String value2;   //字段查询值，区间查询时，第二个值
    public String fieldType;//字段在页面输入框类型，如textfield、datefield
    public String valType;  //字段查询值类型，如string、date、int
}
