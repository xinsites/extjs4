package com.xinsite.common.enums.system;

public enum LogTypeEnum {
    登录日志(1),
    访问日志(2),
    操作日志(3),
    异常日志(4);

    // 成员变量
    private int index;

    // 构造方法
    private LogTypeEnum(int index) {
        //this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
