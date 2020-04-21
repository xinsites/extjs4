//按栏目信息，计算单击某个栏目
function itemClickByItem(item, num) {
    var time_out = 50;
    if (item.item_type == "page") time_out = 300;
    setTimeout(function () {
        if (main_left_tree && item.action) {
            var tree = main_left_tree.getView();
            var record = getNewLeftTreeNode();
            record.set("id", item.id);
            record.raw.id = item.id;
            record.raw.text = item.text;
            record.raw.iconCls = item.iconCls;
            record.raw.item_type = item.item_type;
            record.raw.isdataper = item.isdataper;
            record.raw.action = item.action;
            try {
                if (item.item_type == "method")
                    eval(item.action);
                else if (item.item_type == "page")
                    addTabPanel(record, tree)
            } catch (e) {
                Ext.getBody().unmask();
                errorBoxShow("请确定：" + item.action + " 是否存在！");
            }
        }
    }, num * time_out);
}


//系统管理->用户管理
function itemClick_SysUser(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.user_manage";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record)) {
            addTabPanelByPanel(new createPanel_User(record), record);
        }
    });
}

//系统管理->机构管理
function itemClick_SysOrg(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.organize_manage";
    msgArray[1] = "javascript.menu.system.organize_adminer";
    msgArray[2] = "javascript.menu.permission.adminer";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_Organize(record), record);
    });
}

//系统管理->部门管理
function itemClick_SysDept(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.dept_manage";
    msgArray[1] = "javascript.menu.system.member_allocate";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_Dept(record), record);
    });
}

//系统管理->角色管理
function itemClick_SysRole(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.role_manage";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_Role(record), record);
    });
}

//系统管理->生成数据表
function itemClickGenTable(tree, record) {
    var msgArray = new Array();
    msgArray[msgArray.length] = "javascript.menu.system.gen_table_manage";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createGenTablePanel(record), record);
    });
}

//系统管理->生成数据字段
function itemClickGenField(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.gen_table_field";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createGenTableFieldPanel(record), record);
    });
}

//系统管理->参数配置
function itemClick_SysConfig(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.parameter_config";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_Config(record), record);
    });
}

//系统管理->栏目管理
function itemClick_SysItem(tree, record) {
    var array = new Array();
    array[array.length] = "javascript.menu.system.item_manage";
    array[array.length] = "javascript.menu.system.item_function";
    array[array.length] = "javascript.menu.system.item_search";
    Ext.require(array, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_Item(record), record);
    });
}

//系统管理->权限管理
function itemClick_SysPower(tree, record) {
    var array = new Array();
    array[array.length] = "javascript.menu.permission.manage";
    array[array.length] = "javascript.menu.system.item_search";
    Ext.require(array, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_Permission(record), record);
    });
}

//系统管理->编码管理
function itemClick_SysCode(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.code_manage";
    msgArray[1] = "javascript.menu.system.code_other";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_CodeManage(record), record);
    });
}

//系统管理->系统数据源
function itemClick_SysDataSource(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.system.data_source";
    msgArray[1] = "javascript.menu.system.code_other";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_DataSource(record), record);
    });
}

//系统首页->待办任务列表
function showWaitItem(item_id) {
    var record = getNewLeftTreeNode();
    record.set("id", item_id);
    record.raw.id = item_id;
    record.raw.text = "待办任务";
    record.raw.isdataper = "0";
    record.raw.item_type = "method";
    record.raw.action = "itemClick_WaitTask(tree,record)";
    var tree = main_left_tree.getView();
    itemClick_WaitTask(tree, record);
}

//我的事项->待办任务列表
function itemClick_WaitTask(tree, record) {
    var msgArray = new Array();
    msgArray[msgArray.length] = "javascript.navbar.wait_item";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanel_WaitItem(record), record);
    });
}

//系统首页->用户中心
function openUserCenter() {
    var msgArray = new Array();
    msgArray[0] = "javascript.navbar.user_center";
    Ext.require(msgArray, function () {
        if (!isActiveTab({raw: {id: "tab_usercenter"}})) {
            var record = getNewLeftTreeNode();
            record.set("id", "tab_usercenter");
            record.raw.id = "tab_usercenter";
            record.raw.text = "用户中心";
            record.raw.uploadfile_type = "*.doc,*.docx,*.pdf,*.ppt,*.txt";
            addTabPanelByTabId(new createPanel_UserCenter(record), "用户中心", "tab_usercenter");
        }
    });
}

//系统监控->在线用户
function itemClick_MonitorOnline(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.monitor.user_online";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanelUserOnline(record), record);
    });
}

//系统管理->系统日志
function itemClick_MonitorSysLog(tree, record) {
    var msgArray = new Array();
    msgArray[0] = "javascript.menu.monitor.system_log";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanelSystemLog(record), record);
    });
}

/*
* 自定义栏目点击定义
*/

//数据对象->单表_单元格对象
function itemObjClickSingleCellediting(tree, record) {
    var msgArray = new Array();
    msgArray[msgArray.length] = "javascript.menu.build_form.single_cellediting";
    Ext.require(msgArray, function () {
        if (!isActiveTab(record))
            addTabPanelByPanel(new createPanelSingleCellediting(record), record);
    });
}