Ext.define('javascript.menu.monitor.system_log', {
    extend: ''
});

function createPanelSystemLog(treenode) {
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            type: 'ajax', url: "monitor/log/logtype",
            reader: {type: 'json', id: "id"},
            extraParams: {}
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: [{name: 'id', type: 'int'}, {name: 'pid', type: 'int'}, 'text']
    });
    var tree = Ext.create('Ext.tree.Panel', {
        store: tree_store,
        width: 260, height: 400,
        useArrows: true,
        rowLines: true, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        singleExpand: true, //如果每个分支只有1个节点可能展开,默认false
        viewConfig: {
            getRowClass: function () {
                return 'log_tree_panel_row_height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                grid.store.proxy.extraParams.log_type = record.get('id');
                grid.store.proxy.extraParams.name = record.get('text');
                grid.store.load();
                grid.getSelectionModel().deselectAll(true);
            }
        }
    });

    Ext.define('model_monitor_sys_log', {
        extend: 'Ext.data.Model',
        idProperty: 'log_id',
        fields: [{"name": "log_id", "type": "int", "text": "主键"},
            {"name": "create_time", "type": "date", "text": "创建时间"},
            {"name": "user_id", "type": "int", "text": "创建用户"},
            {"name": "user_name", "type": "string", "text": "创建用户"},
            {"name": "log_ip", "type": "string", "text": "IP地址"},
            {"name": "log_fun", "type": "string", "text": "功能位置"},
            {"name": "action_type", "type": "string", "text": "操作类型"},
            {"name": "log_result", "type": "string", "text": "执行结果"},
            {"name": "log_message", "type": "string", "text": "日志信息描述"}]
    });
    var item_id = treenode.raw.id;
    var pageSize = getGridPageSize(item_id);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "monitor/log/grid",
            extraParams: {item_id: item_id, is_data_per: treenode.raw.isdataper},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'log_id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records) {
                if (store.getCount() == 0 && store.currentPage > 1) {
                    store.currentPage = 1;
                    store.load();
                }
            }
        },
        sorters: [{
            property: 'serialcode',
            direction: 'desc'
        }, {
            property: 'create_time',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_monitor_sys_log'
    });
    var columns = [new Ext.grid.RowNumberer({width: 50, tdCls: 'blue'}),
        {text: 'log_id', width: 20, dataIndex: 'log_id', hideable: false, hidden: true}, {
            text: '操作时间',
            dataIndex: 'create_time',
            width: 145,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: Ext.util.Format.dateRenderer('Y-m-d H:i:s')
        }, {
            text: '操作用户',
            dataIndex: 'user_name',
            width: 140,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: 'IP地址',
            dataIndex: 'log_ip',
            width: 120,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '系统功能',
            dataIndex: 'log_fun',
            width: 220,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '操作类型',
            dataIndex: 'action_type',
            width: 80,
            fixed: true,
            align: 'center',
            sortable: true
        }, {
            text: '执行结果',
            dataIndex: 'log_result',
            width: 80,
            fixed: true,
            align: 'center',
            sortable: true,
            renderer: function (val, meta, rec) {
                if (val == "成功")
                    return '<span style="font-weight:bold;color:green;">成功</span>';
                else
                    return '<span style="font-weight:bold;color:red;">' + val + '</span>';
            }
        }, {
            text: '日志信息描述',
            dataIndex: 'log_message',
            width: 140,
            fixed: false,
            align: 'left',
            sortable: true
        }];
    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid",
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            enableTextSelection: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            'itemdblclick': function (gridview, rec, item, index) {
                //if (userinfo.IsSuperAdminer) 
                openLogShowWindow(rec);
            }
        },
        border: false,
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'btn_clear',
                xtype: 'button', text: '清空日志',
                minWidth: 60, iconCls: 'icon_clear', //pressed: true,
                handler: function (btn, pressed) {
                    openClearWindow();
                }
            }, '->', '<b>搜索:</b>',
                {
                    xtype: 'textfield', width: 220, emptyText: "按系统功能检索，请按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                var search_all = grid.queryById("search_all");
                                if (search_all) search_all.setChecked(true);
                                designSearchByField(store, 'key_log', "log_fun", field.getValue());
                            }
                        }
                    }
                }, {
                    xtype: 'splitbutton', text: '高级搜索',
                    itemId: "btn_complexsearch",
                    iconCls: "icon_search", listeners: {
                        click: function () {
                            openSearchWin(treenode, grid);
                        }
                    },
                    menu: {
                        items: [
                            {
                                text: '今天',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'key_log', 0);
                                    }
                                }
                            },
                            {
                                text: '最近三天',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'key_log', 2);
                                    }
                                }
                            },
                            {
                                text: '最近一周',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'key_log', 6);
                                    }
                                }
                            },
                            {
                                text: '最近一月',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'key_log', 30);
                                    }
                                }
                            },
                            {
                                text: '全部',
                                itemId: "search_all",
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        store.proxy.extraParams.searchdata = "";
                                        store.reload();
                                    }
                                }
                            }]
                    }
                }, "-"]
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', {cookieName: "grid_" + item_id})
        }]
    });

    function openClearWindow() {
        var btn = grid.queryById('btn_clear');
        var form = Ext.create('Ext.form.Panel', {
            frame: false,
            bodyPadding: "15 20 10 20",
            fieldDefaults: {
                labelAlign: 'top',
                msgTarget: 'side',
                labelWidth: 70
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'combobox',
                itemId: "keep_time",
                name: 'keep_time',
                labelStyle: 'margin-bottom:5px',
                fieldLabel: '日志保留时间',
                allowBlank: false,
                valueField: 'id', displayField: 'name',
                queryMode: 'local', value: "7",
                store: {
                    "fields": ["id", "name"],
                    "data": [{"id": "7", "name": "保留近一周"}, {"id": "30", "name": "保留近一个月"}, {
                        "id": "90",
                        "name": "保留近三个月"
                    }, {"id": "0", "name": "不保留，全部删除"}]
                },
                editable: false
            }]
        });
        var win = Ext.create('widget.window', {
            title: '清空日志',
            width: 360,
            height: 170,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit',
            animateTarget: btn.getId(),
            items: [form],
            listeners: {
                show: function (window, eOpts) {
                    if (grid.store.proxy.extraParams.name)
                        window.setTitle("清空" + grid.store.proxy.extraParams.name);
                }
            },
            buttonAlign: "right",
            buttons: ["->", {
                itemId: "btn_save_form",
                text: '确认', minWidth: 70,
                listeners: {
                    click: function () {
                        var keep_time = form.queryById('keep_time');
                        Ext.MessageBox.confirm("提示!", "清空日志记录，您确定要“" + keep_time.getRawValue() + "”的操作吗?", function (btn) {
                            if (btn == "yes") {
                                Ext.Ajax.request({
                                    method: "POST", url: "monitor/log/clear",
                                    params: {
                                        days: keep_time.getValue(), item_id: item_id,
                                        log_type: grid.store.proxy.extraParams.log_type
                                    },
                                    success: function (response, options) {
                                        var resp = Ext.JSON.decode(response.responseText);
                                        if (resp.success) {
                                            grid.store.reload();
                                            Ext.alert.msg('提示', "清空成功!");
                                            win.close();
                                        } else {
                                            Ext.alert.msg('提示', "清空失败!");
                                        }
                                    },
                                    failure: function (response, options) {
                                        Ext.alert.msg('提示', "清空失败!");
                                    }
                                });
                            }
                        });
                    }
                }
            }, {
                text: '关闭', minWidth: 70,
                handler: function () {
                    win.close();
                }
            }]
        });
        win.show();
    }

    var logSearchWin;

    function openSearchWin(treenode, grid) {
        if (!logSearchWin) {
            var btn = grid.queryById("btn_complexsearch");
            var start_time = new Date();
            start_time.addWeeks(-1);
            var form = Ext.create('Ext.form.Panel', {
                frame: false,
                bodyPadding: "15 20 10 20",
                fieldDefaults: {
                    labelAlign: 'left',
                    msgTarget: 'side',
                    labelWidth: 70
                },
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: '操作时间',
                    layout: 'hbox',
                    defaults: {
                        flex: 1,
                        editable: false,
                        hideLabel: true
                    },
                    defaultType: 'datefield',
                    items: [{
                        itemId: "createtime_s", name: 'createtime_s',
                        margin: '0 5 0 0', value: start_time,
                        format: 'Y-m-d', editable: false,
                        listeners: {
                            "select": function (field) {
                                var last_date = field.up('form').down("#createtime_e");
                                last_date.setMinValue(field.getValue());
                                last_date.expand();
                            }
                        }
                    }, {
                        flex: 0, width: 20,
                        xtype: 'displayfield',
                        value: '至'
                    }, {
                        itemId: "createtime_e", name: 'createtime_e',
                        fieldLabel: 'End', value: new Date(),
                        format: 'Y-m-d', editable: false,
                        listeners: {
                            "select": function (field) {
                                var first_date = field.up('form').down("#createtime_s");
                                first_date.setMaxValue(field.getValue());
                            }
                        }
                    }]
                }, {
                    fieldLabel: '操作用户',
                    xtype: 'combobox', itemId: "user_name",
                    valueField: 'id', displayField: 'name',
                    store: getSysDataComboStore('ds.sys.user'),
                    forceSelection: true, //true时，所选择的值限制在一个列表中的值，false时，允许用户设置任意的文本字段
                    minChars: 0, queryDelay: 300,
                    queryMode: 'remote',
                    listConfig: {
                        cls: "combobox-empty",
                        maxHeight: 500,
                        emptyText: "<div class='empty'>数据为空<div>",
                        resizable: false
                    },
                    listeners: {
                        'change': function (combocox, eOpts) {
                            if (!combocox.getValue()) combocox.clearValue();
                        }
                    }
                }, {
                    xtype: 'textfield',
                    itemId: "log_ip", name: 'log_ip',
                    maxLength: 25,
                    fieldLabel: 'IP地址'
                }, {
                    itemId: "log_result",
                    xtype: 'radiogroup',
                    name: 'log_result',
                    fieldLabel: '执行结果',
                    allowBlank: false,
                    items: [{"boxLabel": "成功", "name": "log_result", "inputValue": "成功"},
                        {"boxLabel": "失败", "name": "log_result", "inputValue": "失败"}],
                    columns: [70, 70]
                }, {
                    itemId: "log_message",
                    xtype: 'textareafield',
                    name: 'log_message',
                    fieldLabel: '日志信息',
                    height: 80, rows: 3,
                    maxLength: 100,
                    flex: 1
                }],
                listeners: {
                    afterRender: function (thisForm, options) {
                        this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {
                            enter: search,
                            scope: this
                        });
                    }
                }
            });
            logSearchWin = Ext.create('widget.window', {
                title: '日志查询',
                width: 460,
                height: 300,
                closable: true,
                closeAction: 'hide',
                plain: false,
                modal: true,
                layout: 'fit',
                animateTarget: btn.getId(),
                items: [form],
                buttonAlign: "right",
                buttons: ["->", {
                    text: '查询', minWidth: 70,
                    listeners: {
                        click: function () {
                            search();
                        }
                    }
                }, {
                    text: '清空', minWidth: 70,
                    listeners: {
                        click: function () {
                            form.getForm().reset();
                            form.queryById('createtime_s').setValue("");
                            form.queryById('createtime_e').setValue("");
                            form.queryById('createtime_e').setMinValue("1991-01-01");
                            form.queryById('createtime_s').setMaxValue("2999-01-01");
                        }
                    }
                }, {
                    text: '关闭', minWidth: 70,
                    handler: function () {
                        logSearchWin.close();
                    }
                }]
            });
        }

        function search() {
            var items = [];
            items[items.length] = {
                field: "create_time",
                operator: "between",
                start: "createtime_s",
                end: "createtime_e",
                valType: "date"
            };
            items[items.length] = {field: "user_id", item_id: "user_name", valType: "int"};
            items[items.length] = {item_id: "log_ip", operator: "like"};
            items[items.length] = {item_id: "log_result", operator: "=", valType: "int"};
            items[items.length] = {item_id: "log_message", operator: "like"};
            searchSingleTableReLoad(grid, designSearchByForm(form, items), "key_log");
            logSearchWin.close();
        }

        logSearchWin.show();
    }

    function openLogShowWindow(record) {
        var form = Ext.create('Ext.form.Panel', {
            frame: false,
            bodyPadding: "15 20 10 20",
            fieldDefaults: {
                labelAlign: 'left',
                msgTarget: 'side',
                labelWidth: 70
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'datefield',
                itemId: "create_time",
                name: 'create_time',
                readOnly: true,
                format: 'Y-m-d H:i:s', editable: false,
                fieldLabel: '操作时间'
            }, {
                xtype: 'textfield',
                itemId: "user_name", name: 'user_name',
                fieldLabel: '操作用户'
            }, {
                xtype: 'textfield',
                itemId: "log_ip", name: 'log_ip',
                fieldLabel: 'IP地址'
            }, {
                xtype: 'textfield',
                itemId: "log_fun",
                name: 'log_fun',
                fieldLabel: '系统功能'
            }, {
                xtype: 'textfield',
                itemId: "action_type",
                name: 'action_type',
                fieldLabel: '操作类型'
            }, {
                itemId: "log_result",
                xtype: 'radiogroup',
                name: 'log_result',
                fieldLabel: '执行结果',
                allowBlank: false,
                items: [{"boxLabel": "成功", "name": "log_result", "inputValue": "成功"}, {
                    "boxLabel": "失败",
                    "name": "log_result",
                    "inputValue": "失败"
                }],
                columns: [70, 70],
                value: '1'
            }, {
                itemId: "log_message",
                xtype: 'textareafield',
                name: 'log_message',
                fieldLabel: '日志信息',
                height: 80, rows: 3,
                flex: 1
            }]
        });
        var logShowWin = Ext.create('widget.window', {
            title: '日志查看',
            width: 480,
            height: 360,
            maximizable: true,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit',
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {

                },
                show: function (window, eOpts) {
                    designFormOriginalValue(form, logShowWin.record);
                }
            },
            buttonAlign: "right",
            buttons: ["->", {
                text: '关闭', minWidth: 70,
                handler: function () {
                    logShowWin.close();
                }
            }]
        });
        logShowWin.record = record;
        logShowWin.show();
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "border", border: false,
        items: [{
            title: "日志类型",
            region: 'west', split: {width: 5},
            width: 180, minWidth: 1, maxWidth: 500,
            layout: 'fit', items: [tree]
        }, {
            region: 'center',
            layout: 'fit',
            items: [grid]
        }]
    });
}

