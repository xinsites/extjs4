package com.xinsite.common.enums.system;

public enum PerEnum {
    用户权限(1),
    角色权限(2),
    剔除权限(3);

    // 成员变量
    private int index;

    // 构造方法
    private PerEnum(int index) {
        //this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
