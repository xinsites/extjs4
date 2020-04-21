//初始化rightTabPanel
function initRightTabPanel() {
    if (!rightTabPanel) {
        var hPer = 0.5, wPer = 0.6;
        var width = (document.documentElement.clientWidth - 220) * wPer;
        var height = (document.documentElement.clientHeight - 100) * hPer;
        rightTabPanel = Ext.create('Ext.tab.Panel', {
            id: "rightTabPanel", border: false, activeItem: 0,
            enableTabScroll: true,   //如果Tab过多会出现滚动条
            layoutOnTabChange: true,
            hideCollapseTool: true, autoDestroy: true,
            //plugins: { ptype: 'tabclosemenu' },
            plugins: Ext.create('Ext.ux.TabCloseMenu', {}),
            //            style: 'padding:4px;margin:0px;padding-left:0px;',
            items: [{
                xtype: "panel", id: "FirstTab_1", title: "首页", layout: "fit", iconCls: "icon_home", fitToFrame: true, //items: [initDefaultPanel(width, height)]
                html: '<iframe src="index" scrolling="auto" frameborder="0" width="100%" height="100%"></iframe>',
                tab_change: function () {
                    this.update('<iframe src="index" scrolling="auto" frameborder="0" width="100%" height="100%"></iframe>');
                }
            }],
            listeners: {
                "tabchange": function (TabPanel, item) {
                    if (item.items.length == 1) {
                        var main_panel = item.items.items[0];
                        if (main_panel && typeof main_panel.tab_change == "function") {
                            main_panel.tab_change();
                        }
                    } else {
                        if (typeof item.tab_change == "function") {
                            item.tab_change();
                        }
                    }
                },
                "add": function (TabPanel, item) {
                    if (item.closable) {
                        setTimeout(function () {
                            var id = 'tab-' + (item.id.split('-')[1] - 1 + 2); // +'-btnWrap';
                            var element = Ext.get(id);
                            if (element) {
                                element.dom.ondblclick = function () {
                                    if (item.closable)
                                        rightTabPanel.remove(item);
                                }
                            }
                            if (!isTabClosabled(item.item_id)) {
                                item.closable = !item.closable;
                                $("#" + id + "-closeEl").hide(); //隐藏关闭按钮
                            }
                        }, 50);
                    }
                },
                beforeremove: function (tp, item) {
                    return item.closable;
                },
                remove: function (tp, item) {
                    // if (item.id == "DynamicInfoModTab") { //|| c.id.indexOf("StaticFormPanelTab_") == 0
                    //     item.hide();
                    // }
                    tp.autoDestroy = true;
                }
            },
            /**
             * 返回指定index对应的Panel
             */
            getTabByIndex: function (index) {
                return this.items.itemAt(index);
            },
            /**
             * 返回指定index对应的标签页的Element对象
             */
            getStripByIndex: function (index) {
                return Ext.get(this.getTabEl(this.getTabByIndex(index)));
            },
            /**
             * 返回指定的panel在tabpanel里面的index
             */
            getTabIndex: function (p) {
                return this.items.indexOf(p);
            }
        });
    }
    return rightTabPanel;
}

//根据属性配置中添加一个tab
function addRightTabPanel(options) {
    if (rightTabPanel) {
        var tab = rightTabPanel.add(options);
        rightTabPanel.setActiveTab(tab);
        return tab;
    }
}

//根据tabId获取tab
function getRightTabPanel(tabId, notActive) {
    if (rightTabPanel && tabId) {
        var tab = rightTabPanel.queryById(tabId);
        if (tab) {
            if (!notActive) rightTabPanel.setActiveTab(tab);
            return tab;
        }
    }
    if (rightTabPanel) rightTabPanel.getActiveTab();
    return null;
}

//测试初始化一个panel
function initDefaultPanel(width, height) {
    return new Ext.Panel({
        id: "HomeDefaultPanel", border: false, layout: "fit",
        items: [{
            border: false, region: "center",
            fitToFrame: true, layout: "fit", autoScroll: true,
            html: '<iframe src="index.html" scrolling="auto" frameborder="0" width="100%" height="100%"></iframe>'
        }]
    });
}

//点击左边树菜单加载一个tab
function addTabPanel(record, tree) {
    if (record.raw.action) {
        try {
            var tabId = "tab_panel_" + record.raw.id;
            //var tab = rightTabPanel.items.getAt(tabId);
            var tab = getRightTabPanel(tabId);
            if (!tab) {
                if (record.raw.action && record.raw.action.indexOf("ItemClick") == 0) Ext.getBody().mask('请稍候，正在加载数据...');
                var tabPanel = null;
                if (record.raw.item_type == "method") {
                    tabPanel = eval(record.raw.action);  //创建一个面板
                } else if (record.raw.item_type == "page") {
                    tabPanel = document.createElement("IFRAME"); //创建一个iframe页面
                }
                if (tabPanel) {
                    if (rightTabPanel.items.length == max_tabs_items) {
                        Ext.alert.msg('提示', Ext.String.format("最多打开{0}个选项卡!", max_tabs_items));
                        return;
                    }
                    if (record.raw.item_type == "page") {
                        if (isLoadMask(record.raw.action)) Ext.getBody().mask('正在加载页面,请稍等...');
                        addRightTabPanel({
                            itemId: tabId, title: record.raw.text,
                            closable: true, item_id: record.raw.id,
                            xtype: "panel", iconCls: "", //node.attributes.iconCls
                            border: false, fitToFrame: true, layout: "fit",
                            html: '<iframe src="' + record.raw.action + '" frameborder="0" width="100%" height="100%"></iframe>'
                        });
                    } else {
                        addRightTabPanel({
                            closable: true, item_id: record.raw.id,
                            itemId: tabId, title: record.raw.text,
                            xtype: "panel", iconCls: "", //node.attributes.iconCls
                            layout: "fit", border: false, items: [tabPanel]
                        });
                    }
                }
            } else {
                tab.setTitle(record.raw.text);
                if (record.raw.item_type == "method") {
                    var grid = tab.queryById('main_grid');
                    if (grid && grid.store) {
                        grid.store.sorters.clear();
                        if (grid.xtype == "gridpanel" || grid.xtype == "grid") //分页列表
                            grid.store.load();
                        else if (grid.xtype == "treepanel") //树形列表
                            refreshTreeNode(grid);
                        else if (grid.xtype == "cascadetree") //级联树形列表
                            refreshTreeNode(grid);
                    } else if (grid && grid.data_load) {
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
                        else if (grid1 && grid1.data_load) grid1.data_load();
                    }
                } else if (record.raw.item_type == "page") {
                    if (isLoadMask(record.raw.action)) Ext.getBody().mask('正在加载页面,请稍等...');
                    tab.update('<iframe src="' + record.raw.action + '" frameborder="0" width="100%" height="100%"></iframe>');
                }
            }
        } catch (e) {
            Ext.getBody().unmask();
            errorBoxShow("请确定：" + record.raw.action + " 是否存在！");
        }
    }
}

//页面打开是否加遮罩层
function isLoadMask(page_url) {
    if (page_url.indexOf("druid/index") >= 0) return false;
    return true;
}

//加载tab时，判断rightTabPanel中是否已经存在
//存在时，定位到该tab
function isActiveTab(record) {
    Ext.getBody().unmask()
    var tabId = "tab_panel_" + record.raw.id;
    if (record.raw.idleaf) tabId = "tab_panel_editing_" + record.raw.id; //审批流程审批页面
    var tab = rightTabPanel.queryById(tabId);
    if (tab) {
        if (record.raw.text) tab.setTitle(record.raw.text);
        rightTabPanel.setActiveTab(tab);
        if (record.raw.idleaf) {
            var flow_panel = tab.queryById('flow_panel');
            if (flow_panel && flow_panel.data_load) {
                flow_panel.data_load(record);
            }
        }
        return true;
    }
    return false;
}

//根据tabpanel加载一个tab
function addTabPanelByPanel(tabpanel, record) {
    var tabId = "tab_panel_" + record.raw.id;
    if (record.raw.idleaf) tabId = "tab_panel_editing_" + record.raw.id; //审批流程审批页面
    var tab = rightTabPanel.queryById(tabId);
    if (!tab) {
        if (rightTabPanel.items.length == max_tabs_items) {
            Ext.alert.msg('提示', Ext.String.format("最多打开{0}个选项卡!", max_tabs_items));
            return;
        }
        addBrowseLog(record.get("id"));
        setUserPermission(tabpanel, record);
        var item_id = 0;
        var Method = record.raw.item_type;
        if ((Method == "method" && record.raw.action) || Method == "page") item_id = record.raw.id;  //赋值后，能使用“固定标签到面板”
        setTimeout(function () {
            addRightTabPanel({
                itemId: tabId, title: record.raw.text,
                closable: true, item_id: item_id,
                xtype: "panel", iconCls: record.raw.iconCls,
                layout: "fit", border: false, items: [tabpanel]
            });
        }, 10);
    } else if (record.raw.idleaf) {
        var flow_panel = tab.queryById('flow_panel');
        if (flow_panel && flow_panel.data_load) {
            flow_panel.data_load(record);
        }
    }
}

//根据tabpanel加载一个tab
function addTabPanelByTabId(tabpanel, title, tabId) {
    Ext.getBody().unmask();
    var tabId = "tab_panel_" + tabId;
    var tab = rightTabPanel.queryById(tabId);
    if (!tab) {
        if (rightTabPanel.items.length == max_tabs_items) {
            Ext.alert.msg('提示', Ext.String.format("最多打开{0}个选项卡!", max_tabs_items));
            return;
        }
        addRightTabPanel({
            itemId: tabId, title: title,
            xtype: "panel", iconCls: "", //node.attributes.iconCls
            closable: true, layout: "fit",
            border: false,
            items: [tabpanel]
        });
    }
}

//添加访问日志
function addBrowseLog(item_id) {
    Ext.Ajax.request({
        url: "monitor/log/add", method: "POST",
        params: {item_id: item_id},
        success: function (response, options) {
        },
        failure: function (response, options) {
        }
    });
}

//获取用户权限，并设置用户权限
function setUserPermission(tabpanel, record) {
    if (tabpanel != null) {
        tabpanel.on("render", function (panel, eOpts) {
            Ext.Ajax.request({
                method: "POST", url: path_url.info.util.user_permit,
                params: {item_id: record.get("id")},
                success: function (response, options) {
                    //alert(response.responseText);
                    var resp = Ext.JSON.decode(response.responseText);
                    if (resp.success) {
                        var permission = resp["power"]["data"];
                        record.set("all_item_ids", permission.all_item_ids);
                        record.set("item_ids", permission.item_ids);
                        record.set("data_per", permission.data_per);
                        record.set("data_ids", permission.data_ids);
                        setItemBtnDisabled(tabpanel, record);
                        var grid = tabpanel.queryById('main_grid');
                        if (grid && grid.store) {
                            if (!permission.data_per) permission.data_per = 0;
                            grid.store.proxy.extraParams.all_item_ids = permission.all_item_ids; //栏目权限
                            grid.store.proxy.extraParams.item_ids = permission.item_ids; //栏目权限
                            grid.store.proxy.extraParams.data_per = permission.data_per; //数据权限
                            grid.store.proxy.extraParams.data_ids = permission.data_ids;
                        }
                    }
                },
                failure: function (response, options) {
                    Ext.alert.msg('提示', "获取用户权限失败！");
                }
            });
        });
    }
}

//根据权限，设置栏目按钮状态
function setItemBtnDisabled(tabpanel, record) {
    try {
        if (tabpanel != null) {
            setBtnDisabledByIds(tabpanel, record.get("all_item_ids"), true);
            setBtnDisabledByIds(tabpanel, record.get("item_ids"), false);
        }
    } catch (e) {
        //Ext.alert.msg('提示', "用户超时，请重新登录！");
    }
}

//判断是否按钮权限
function isExistsByItemIds(item_ids, itemId, name, all_item_ids) {
    var is_exists = isExistsItemIds(item_ids, itemId);
    if (is_exists) return is_exists;
    var is_exists_all = isExistsItemIds(all_item_ids, itemId);
    if (!item_ids && !all_item_ids) is_exists = true;  //还没取到
    if (!is_exists && name) {
        Ext.alert.msg('提示', "您暂无" + name + "权限!");
    }
    return is_exists;
}

//判断是否按钮权限
function isExistsByItemId(obj, itemId, name) {
    try {
        if (obj.store && obj.store.proxy.extraParams.item_ids) {
            var item_ids = obj.store.proxy.extraParams.item_ids;
            var all_item_ids = obj.store.proxy.extraParams.all_item_ids;
            return isExistsByItemIds(item_ids, itemId, name, all_item_ids);
        }
        return isExistsByItemIds(obj.get("item_ids"), itemId, name, obj.get("all_item_ids"));
    } catch (e) {
        console.log("isExistsByItemId：%s", e);
    }
    return true;
}

