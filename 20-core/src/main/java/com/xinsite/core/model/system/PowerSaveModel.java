package com.xinsite.core.model.system;

/**
 * 权限保存Model，页面传值
 * create by zhangxiaxin
 */
public class PowerSaveModel {
    public String check;   //是否选中
    public int item_id;    //栏目Id
    public int isdataper;  //是否数据权限
    public int dataPer;    //数据权限下拉列表值
    public String data_ids;//数据权限自定义部门值
    public String fun_ids; //栏目功能表(sys_menu_fun)fun_id集合
}
