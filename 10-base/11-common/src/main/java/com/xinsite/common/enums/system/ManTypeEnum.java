package com.xinsite.common.enums.system;

/**
 * 各种栏目成员分配人员类型_枚举
 */
public enum ManTypeEnum {
    领导(1),   //项目负责人
    负责人(2), //项目组长
    成员(3);

    // 成员变量
    private int index;

    // 构造方法
    private ManTypeEnum(int index) {
        //this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
