package com.xinsite.core.model.search;

/**
 * 查询列表排序Model，页面传值或自定义
 * create by zhangxiaxin
 */
public class OrderModel {
    public String property;  //排序字段，如a1.serialcode、create_time
    public String direction; //升序、降序，如asc、desc

//    对应页面Ext,Store属性sorters
//    sorters: [{
//        property: 'a1.serialcode',
//                direction: 'desc'
//    }, {
//        property: 'a1.create_time',
//                direction: 'desc'
//    }]
}
