//页面初始化加载入口
Ext.onReady(function () {
    Ext.BLANK_IMAGE_URL = "./images/s.gif";
    Ext.QuickTips.init();       //提示初始化
    initInfo();
    initLoadFileFn();
    // isUserLogoff();
    getOnlineStatic();
});

function getOnlineStatic() {
    if (t_onlinestatic) clearInterval(t_onlinestatic);
    Ext.Ajax.request({
        method: "POST", url: "main/reminds",
        params: {},
        success: function (response, options) {
            var resp = Ext.JSON.decode(response.responseText);
            if (resp.success) {
                $("#lblTaskCount").html(resp.waititem_count);
                $("#lblMsgCount").html(resp.unread_count);
            }
            t_onlinestatic = setTimeout(getOnlineStatic, 30 * 1000);
        },
        failure: function (response, options) {
            if (t_onlinestatic) clearInterval(t_onlinestatic);
        }
    });
}

//检查用户登录
function isUserLogoff() {
    $.ajax({
        type: "post", cache: false, dataType: "json",
        url: "anon/islogoff",
        success: function (r) {
            if (r.success)
                returnLoginPage(r.msg);
            else
                setTimeout(isUserLogoff, 30 * 1000);
        }
    });
}


//获取用户设置的固定标签
function getFixedTabPanel() {
    Ext.Ajax.request({
        method: "POST", url: path_url.info.util.fixtab_get,
        params: {},
        success: function (response, options) {
            var resp = Ext.JSON.decode(response.responseText);
            if (resp.success) {
                var fixed_tabs = resp.tabs;
                for (var idx = 0; idx < fixed_tabs.length; idx++) {
                    fixed_item["Id_" + fixed_tabs[idx].id] = true;
                    itemClickByItem(fixed_tabs[idx], idx);
                }
            }
        },
        failure: function (response, options) {
        }
    });
}

//用户固定、取消栏目标签
function setFixedTabPanel(item) {
    Ext.Ajax.request({
        method: "POST", url: path_url.info.util.fixtab_save,
        params: {item_id: item.item_id, fixed: item.closable},
        success: function (response, options) {
            var resp = Ext.JSON.decode(response.responseText);
            if (resp.success) {
                var id = 'tab-' + (item.id.split('-')[1] - 1 + 2) + "-closeEl";
                fixed_item["Id_" + item.item_id] = item.closable;
                item.closable = !item.closable;
                if (item.closable) $("#" + id).show();
                else $("#" + id).hide();
            } else {
                if (resp.msg) Ext.alert.msg('提示', resp.msg);
                else Ext.alert.msg('提示', "操作失败!");
            }
        },
        failure: function (response, options) {
            Ext.alert.msg('提示', "操作失败!");
        }
    });
}

//刷新面板页面
function refreshFixedTabPanel(tab) {
    if (tab.items.length == 1) {
        var main_panel = tab.items.items[0];
        if (main_panel && typeof main_panel.tab_change == "function") {
            main_panel.tab_change();
            return;
        }
    } else {
        if (typeof tab.tab_change == "function") {
            tab.tab_change();
            return;
        }
    }
    var grid = tab.queryById('main_grid');
    if (grid && grid.store) {
        grid.store.sorters.clear();
        if (grid.xtype == "gridpanel" || grid.xtype == "grid") //分页列表
            grid.store.load();
        else if (grid.xtype == "treepanel") //树形列表
            refreshTreeNode(grid);
        else if (grid.xtype == "cascadetree") //级联树形列表
            refreshTreeNode(grid);
    } else if (grid && typeof grid.data_load == "function") {
        grid.data_load();
    }

    for (var i = 1; i <= 3; i++) {
        var grid1 = tab.queryById('main_grid' + i);
        if (grid1 && grid1.store) {
            if (grid1.xtype == "gridpanel") //分页列表
                grid1.store.load();
            else if (grid1.xtype == "treepanel") //树形列表
                refreshTreeNode(grid1);
            else if (grid1.xtype == "cascadetree") //级联树形列表
                refreshTreeNode(grid1);
        }
        else if (grid1 && typeof grid1.data_load == "function") grid1.data_load();
    }
}

//获取该栏目是否能关闭
function isTabClosabled(item_id) {
    var closable = true;
    Object.keys(fixed_item).forEach(function (key) {
        if ("Id_" + item_id == key) {
            closable = !fixed_item[key];
            return;
        }
    });
    return closable;
}

function initInfo() {
    if (!$.cookie("menuStyle")) $.cookie("menuStyle", "accordionMenu");
    if (!$.cookie("selectedCss")) $.cookie("selectedCss", "neptune");
    if (!$.cookie("monetaryText")) $.cookie("monetaryText", "");
    if (!$.cookie("monetaryUnit")) $.cookie("monetaryUnit", "1");
    if (!$.cookie("unitText")) $.cookie("unitText", "元");
    Ext.monetaryText = $.cookie("monetaryText");
    Ext.monetaryUnit = $.cookie("monetaryUnit");
    Ext.unitText = $.cookie("unitText");
}

function reLocation() {
    location.search = location.search;
}

function bodylayout(item_attr) {
    ///=================================================头部
    var north = Ext.create('Ext.panel.Panel', {
        region: "north", height: 58,
        id: "body_north",
        //        bodyStyle: "background: #fff;",
        border: false, layout: "border",
        items: [{layout: "fit", region: "center", xtype: "box", contentEl: "top_left"},
            {layout: "fit", region: "east", xtype: "box", contentEl: "top_right"}
        ]
    });
    var leftWidth = Math.floor(document.documentElement.clientWidth * 0.18);
    if (leftWidth > 260) leftWidth = 260;
    if (getCookie("leftWidth")) leftWidth = Math.floor(getCookie("leftWidth"));

    ///=================================================左边
    var west = Ext.create('Ext.panel.Panel', {
        title: '菜单栏目', id: "body_west",
        width: leftWidth, maxWidth: 460,
        border: true, region: "west",
        split: true, collapsible: true,
        collapsed: false, cls: 'ExtCls3',
        cmargins: '0 2', // overflow: 'auto', frame: true,
        layout: 'fit', //collapseMode: 'mini',
        tools: [{
            type: 'gear',
            callback: function (panel, tool) {
                var regionMenu = panel.regionMenu || (panel.regionMenu =
                    Ext.widget({
                        xtype: 'menu',
                        items: [{
                            text: '菜单样式',
                            menu: [{
                                text: '智能树菜单',
                                group: 'menustyle',
                                disabled: true,
                                xtype: 'menucheckitem',
                                handler: function () {

                                }
                            }, {
                                text: '智能树菜单(默认折叠)',
                                group: 'menustyle',
                                disabled: true,
                                xtype: 'menucheckitem',
                                handler: function () {

                                }
                            }, {
                                text: '树菜单',
                                group: 'menustyle', xtype: 'menucheckitem',
                                checked: $.cookie("menuStyle") == "treeMenu",
                                handler: function () {
                                    $.cookie("menuStyle", "treeMenu");
                                    reLocation();
                                }
                            }, {
                                text: '手风琴+树菜单',
                                group: 'menustyle', xtype: 'menucheckitem',
                                checked: $.cookie("menuStyle") == "accordionMenu",
                                handler: function () {
                                    $.cookie("menuStyle", "accordionMenu");
                                    reLocation();
                                }
                            }]
                        }, '-', {
                            text: '隐藏头部工具栏',
                            checked: getCookie("hide_header_tool") == "true",
                            checkHandler: function (item, checked) {
                                north.setVisible(!checked);
                                setCookie("hide_header_tool", checked);
                                //Ext.alert.msg('信息提示', '你点击的"{0}"菜单变成{1}', item.text, checked ? 'checked' : 'unchecked');
                            }
                        }, {
                            text: '隐藏底部状态栏',
                            checked: getCookie("hide_footer_tool") != "true",
                            checkHandler: function (item, checked) {
                                south.setVisible(!checked);
                                setCookie("hide_footer_tool", !checked);
                                //Ext.alert.msg('信息提示', '你点击的"{0}"菜单变成{1}', item.text, checked ? 'checked' : 'unchecked');
                            }
                        }]
                    }));
                regionMenu.showBy(tool.el);
            }
        }],
        listeners: {
            "resize": function (panel, width, height, oldWidth, oldHeight, eOpts) {
                $.cookie("leftWidth", width);
            }
        },
        items: [getLeftPanel(item_attr, leftWidth)] //
    });
    ///=================================================中部 
    var center = Ext.create('Ext.panel.Panel', {
        region: "center",
        border: true,
        layout: "fit",
        cls: 'ExtCls2',
        items: [initRightTabPanel()]
    });
    ///=================================================底部      
    var south = Ext.create('Ext.panel.Panel', {
        id: "body_south", height: 20,
        layout: "border", region: "south", border: false,
        items: [
            {layout: "fit", region: "west", width: 100, xtype: "box", contentEl: "bottom_left"},
            {layout: "fit", region: "center", xtype: "box", contentEl: "bottom_center"},
            {layout: "fit", region: "east", width: 200, xtype: "box", contentEl: "online_peoples"}
        ]
    });
    north.setVisible(getCookie("hide_header_tool") != "true");
    south.setVisible(getCookie("hide_footer_tool") == "true");
    var vp = Ext.create('Ext.Viewport', {
        id: 'masterViewport',
        layout: "border",
        monitorResize: true,
        items: [center, north, west, south]
    });
}

function getNewLeftTreeNode() {
    Ext.define('model_left_tree', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: [{"name": "id", "type": "int", "text": "主键"},
            {"name": "pid", "type": "int", "text": "上级栏目"},
            {"name": "text", "type": "string", "text": "栏目名称"},
            {"name": "action", "type": "string", "text": "执行方法"}]
    });
    return Ext.create('model_left_tree', {'id': 0, 'pid': 0});
}

function getLeftTreeConfigPanel(root_id) {
    var bodyStyle = "background:#FFF;border-width:0px 0px 1px 0px;";
    if ($.cookie("selectedCss") == "access") bodyStyle = "background:#1F2933;border-width:0px 0px 1px 0px;";
    var leftWidth = Math.floor(document.documentElement.clientWidth * 0.18);
    if (leftWidth > 250) leftWidth = 250;
    var expand_node;
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        proxy: {
            type: 'ajax', url: path_url.info.util.item_tree,
            reader: {type: 'json', id: "id"},
            extraParams: {method: ''}
        },
        listeners: {
            'beforeexpand': function (node, eOpts) {
                //this.proxy.extraParams.node = node.raw.id;  
            },
            'load': function (store, record) {
                Ext.Array.each(record.childNodes, function (rec, index) {
                    if (rec.raw.expand == "true") {
                        if (!rec.get('leaf') && !rec.isExpanded()) {
                            setTimeout(function () {
                                treepanel.getView().expand(rec, false);
                            }, 50);
                        }
                    }
                });
            },
            "expand": function (node) {
            }
        }
    });
    //store.addListener('beforeload', function () { loadMarsk.show(); });
    var treepanel = Ext.create('Ext.tree.Panel', {
        xtype: "treepanel", bodyStyle: bodyStyle,
        autoScroll: true, rootVisible: false, border: false,
        fields: ["text", "action", "iconCls"],
        useArrows: false,  //是否用三角箭头
        height: 100, width: leftWidth,
        singleClickExpand: true, //用单击文本展开,默认为双击
        store: store, id: "left_tree_store_" + root_id,
        root: {
            id: root_id, text: '根结点', iconCls: 'icon_home', expanded: true
        },
        viewConfig: {
            getRowClass: function () {
                return 'tree_panel_row_height18';
            },
            loadMask: true,
            loadingText: "请稍候，数据加载中..."
        },
        listeners: {
            'render': function (tree, eOpts) {
                if (main_left_tree == null) {
                    main_left_tree = tree;
                    getFixedTabPanel();
                }
            },
            "itemclick": function (treeview, record, item, index, e) {
                treeview.toggleOnDblClick = false; //取消双击展开折叠菜单行为
                var leaf = record.get('leaf');
                if (!record.get('leaf')) {
                    var expand = record.get('expanded')
                    if (record.isExpanded()) {
                        treeview.collapse(record);
                    } else {
                        treeview.expand(record, false);
                    }
                }
                if (record.raw.action)
                    addTabPanel(record, treeview);
                else if (record.get('leaf')) {
                    Ext.alert.msg("提示信息", "该栏目没有对应的方法，请检查是不是新增对象!");
                }
            }
        }
    });
    return treepanel;
}

function getLeftPanel(item_attr) {
    var menuStyle = $.cookie("menuStyle"),
        selectedCss = $.cookie("selectedCss");
    return Ext.create('Ext.panel.Panel', {
        layout: menuStyle == "accordionMenu" ? 'accordion' : "fit",
        hideMode: 'offsets', activeItem: 0, border: false,
        margins: selectedCss == "neptune" && menuStyle == "accordionMenu" ? '-6 -5 -0 -5' : "", //只有neptune主题
        layoutConfig: {
            animate: true //使用动画效果  
        },
        //        layout: {
        //            type: 'accordion',
        //            multi: true,
        //            animate: true
        //        },
        listeners: {
            'render': function (view, eOpts) {
                if (menuStyle == "treeMenu")
                    view.add(Ext.widget('treepanel', getLeftTreeConfigPanel(0)));
                else if (menuStyle == "accordionMenu") {
                    var idx_expanded = 0;
                    $.each(item_attr, function (index) {
                        if (item_attr[index]["expand"] == "true") {
                            idx_expanded = index;
                            return false;
                        }
                    });
                    for (var idx = 0; idx < item_attr.length; idx++) {
                        view.add(Ext.widget('panel',
                            {
                                cls: 'accordion_item',
                                border: false,
                                title: item_attr[idx].text,
                                layout: "fit",
                                collapsed: idx == idx_expanded ? false : true, //默认展开
                                iconCls: item_attr[idx].iconCls,
                                items: [getLeftTreeConfigPanel(item_attr[idx].id)]
                            }
                        ));
                    }
                }
            }
        },
        items: []
    });
}

///动态加载Js文件
function initLoadFileFn() {
    jQuery.ajaxSetup({cache: true}); //客户端缓存，调用js时后面的类似"?_=13126578"的参数去掉
    jQuery.ajax({
        type: "post", cache: false,
        dataType: "json", async: true,
        url: "anon/loadfile?t=" + Math.random(),
        success: function (obj) {
            if (obj.state) {
                //$.includePath = HtglUrl;
                $.include(obj.cssfiles);
                $.include(obj.jsfiles);
                $.loadCompleted = loadLeftItemTree;
                $.isLoadCompleted(obj.jsfiles.length);
            } else {
                Ext.getBody().unmask();
                errorBoxShow('部分组件加载失败');
            }
        }
    });
}

function loadComplete() {
    setTimeout(function () {
        Ext.get('loading').remove();
        Ext.get('loading-mask').fadeOut({remove: true});
        Ext.getBody().unmask();
    }, 50);
}

function loadLeftItemTree() {
    Ext.Ajax.request({
        method: "POST", url: path_url.info.util.item_level,
        success: function (response, options) {
            //alert(response.responseText);
            var resp = Ext.JSON.decode(response.responseText);
            loadComplete();
            if (resp.success) {
                bodylayout(resp.config);
            } else {
                returnLoginPage('您暂无栏目权限!');
            }
        },
        failure: function (response, options) {
            loadComplete();
            ajaxFailure(response);
        }
    });
}

