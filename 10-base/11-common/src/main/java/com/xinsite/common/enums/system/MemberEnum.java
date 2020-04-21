package com.xinsite.common.enums.system;

/**
 * 成员类型类型
 */
public enum MemberEnum {
    项目组("sys_project"),  //man_type,1:负责人;2:组长;3:成员
    部门管理("sys_dept"),   //man_type,1:部门领导;2:部门负责人
    用户领导("sys_user"),   //man_type,1:领导;3:领导下属成员
    流程角色("wd_role");    //man_type,3:流程角色

    // 成员变量
    private String value;

    // 构造方法
    private MemberEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
