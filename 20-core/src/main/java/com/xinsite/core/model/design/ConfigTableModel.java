package com.xinsite.core.model.design;

import com.xinsite.common.bean.Editors;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置一个表进行增删改查需要的所有参数
 */
public class ConfigTableModel {
    public int tid;
    public String table_name;     //数据库表名，不带前缀
    public String table_key;      //数据库表名，除去前缀
    public String tb_full_name;   //数据库表全名(小写)

    public String table_explain;  //中文表名
    public String relation;       //与主表关系
    public String layout_type;    //表格操作类型
    public String itemid;         //各对象ItemId的值
    public String model_name;     //js生成的模板名称

    public String build_columns;                 //生成模板文件需要的columns，对于“主表”、“1对多”关系的需要配置

    public String tab_config;    //选项卡属性，只有“多表主表”是选项卡编辑时有
    public String win_config;    //窗口属性，只有“主表”是窗口编辑时有
    public String form_config;      //表单属性，只有“主表”、“1对多”关系，并且是窗口编辑、选择卡编辑时有
    public String form_items;        //表单Items ，只有“主表”、“1对多”关系，并且是窗口编辑、选择卡编辑时有
    public String addobject;         //添加表单默认值
    public String f_primary_key;     //主键字段,idleaf
    public String f_foreign_key;     //外键字段,idleaf
    public String f_parent_id;       //父结点字段,pid
    public String f_delete;          //删除字段,isdel
    public String f_create_time;     //创建时间,create_time
    public String f_modify_time;     //修改时间,create_time
    public String f_order;           //排序字段,serialcode
    public String f_title;           //标题字段,title
    public String f_keyword;         //标题关键字名称
    public String order_by;          //查询列表默认排序
    public String f_create_uid;      //创建人
    public String f_modify_uid;      //修改人
    public String f_dept_id;         //部门号
    public String f_item_id;         //栏目号
    public String f_org_id;          //机构号
    public String f_apply_uid;       //申请人

    public List<Editors> editors = new ArrayList<>();       //新增、修改表单时获取该字段值时用
    public List<Editors> editors_text = new ArrayList<>();  //主表有扩展的text字段时有

    public List<ConfigureModel> config_grids = new ArrayList<>();   //系统配置编辑Grid列表数据
}

