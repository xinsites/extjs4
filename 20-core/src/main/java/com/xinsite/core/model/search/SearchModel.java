package com.xinsite.core.model.search;

import java.util.List;

/**
 * 高级查询表级条件，页面传值
 * 多表关联查询时，有可能都有字段查询条件
 * create by zhangxiaxin
 */
public class SearchModel {
    public String tableKey;         //数据表table_key
    public String tableName;        //数据库表名，根据table_key查询时获取
    public String alias;            //查询表的别名
    public List<SearchDataModel> datas;  //该表查询字段条件
}


