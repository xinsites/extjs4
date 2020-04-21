package com.xinsite.common.enums.system;

/**
 * 特殊栏目_枚举
 */
public enum ItemEnum {
    待办任务(5),
    用户管理(12),
    权限管理(21),
    发起事项(45),
    经办事项(46);

    // 成员变量
    private int item_id;

    // 构造方法
    private ItemEnum(int item_id) {
        this.item_id = item_id;
    }

    public int getId() {
        return item_id;
    }

}
