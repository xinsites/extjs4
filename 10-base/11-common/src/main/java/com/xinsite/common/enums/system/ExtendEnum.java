package com.xinsite.common.enums.system;

/**
 * 扩展记录特殊表
 */
public enum ExtendEnum {
    固定标签("fixed_tab"),           //table_id：sys_user
    字段查询符("design_operator"),   //table_id：自增
    字段输入框("input_xtype"),       //table_id：自增
    文件类型("uploadfile_type"),     //table_id：sys_menu
    流程栏目("flow_menu");           //table_id：sys_menu

    // 成员变量
    private String value;

    // 构造方法
    private ExtendEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
