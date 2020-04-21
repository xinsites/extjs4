package com.xinsite.common.enums.system;

public enum DataPerEnum {
    未设置(0),
    仅限本人(1),
    仅限本人及下属(2),
    所在部门(3),
    所在公司(4),
    自定义部门(5),
    自定义用户(6);

    // 成员变量
    private int index;

    // 构造方法
    private DataPerEnum(int index) {
        //this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
