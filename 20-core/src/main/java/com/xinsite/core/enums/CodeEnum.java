package com.xinsite.core.enums;

/**
 * 特殊编码类型_枚举
 */
public enum CodeEnum {
    任务执行人员(18),
    流程任务状态(22),
    审批任务状态(23),
    执行填写方式(20),
    回退种类(19),
    性别(1);

    // 成员变量
    private int id;

    // 构造方法
    private CodeEnum(int id) {
        //this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
