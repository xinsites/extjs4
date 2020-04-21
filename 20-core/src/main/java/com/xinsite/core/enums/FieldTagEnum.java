package com.xinsite.core.enums;

/**
 * 字段标识_枚举
 */
public enum FieldTagEnum {
    主键("primary_key"),
    外键("foreign_key"),
    父结点("parent_id"),
    标题字段("title"),
    排序字段("order"),
    创建时间("create_time"),
    创建人字段("create_uid"),
    修改时间("modify_time"),
    修改人字段("modify_uid"),
    栏目号("item_id"),
    机构号("org_id"),
    部门号("dept_id"),
    申请人("apply_uid"),
    申请部门("apply_deptid"),
    申请日期("apply_date"),
    删除标识("delete");

    // 成员变量
    private String value;

    // 构造方法
    private FieldTagEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

