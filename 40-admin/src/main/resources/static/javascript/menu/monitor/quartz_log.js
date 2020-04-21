Ext.define('javascript.menu.monitor.quartz_log', {
    extend: ''
});

//定时任务运行日志
function createPanelQuartzLog(treenode) {
    Ext.define('model_monitor_task_log', {
        extend: 'Ext.data.Model',
        idProperty: 'log_id',
        fields: [{"name": "log_id", "type": "string", "text": "主键"},
            {"name": "job_id", "type": "int", "text": "工作任务Id"},
            {"name": "job_name", "type": "string", "text": "工作任务名称"},
            {"name": "run_time", "type": "string", "text": "任务运行时间"},
            {"name": "run_result", "type": "string", "text": "运行结果"},
            {"name": "run_log", "type": "string", "text": "运行日志"},
            {"name": "create_time", "type": "date", "text": "创建时间"}
        ]
    });
    var item_id = treenode.raw.id;
    var pageSize = getGridPageSize(item_id);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "monitor/quartz/log/grid",
            extraParams: {item_id: item_id},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'log_id',
                totalProperty: 'totalProperty'
            }
        },
        sorters: [{
            property: 'a1.create_time',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_monitor_task_log'
    });
    var columns = [new Ext.grid.RowNumberer({width: 40, tdCls: 'blue'}),
        {
            text: '工作任务名称',
            dataIndex: 'job_name',
            width: 180, fixed: true,
            align: 'left', sortable: false
        }, {
            text: '运行时间',
            dataIndex: 'run_time',
            width: 160,
            fixed: true,
            align: 'center',
            sortable: false
        }, {
            text: '运行结果',
            dataIndex: 'run_result',
            width: 120,
            fixed: true,
            align: 'center',
            renderer: function (value, mata, record) {
                if (value.indexOf("失败") >= 0) return '<span style="font-weight:bold;color:red;">' + value + '</span>';
                return '<span style="font-weight:bold;color:green;">' + value + '</span>';
            }
        }, {
            text: '运行日志',
            dataIndex: 'run_log',
            width: 200,
            fixed: false,
            align: 'left',
            sortable: false
        }
    ];

    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid",
        store: store,
        disableSelection: false, //设置为true，则禁用选择模型
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        sortableColumns: false,
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            enableTextSelection: true,
            preserveScrollOnRefresh: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            'itemdblclick': function (gridview, rec, item, index) {
                openRecordShowWindow(rec);
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
            }, '->', '<b>搜索:</b>', {
                itemId: "search_keyword", xtype: 'textfield', width: 220,
                emptyText: "运行日志关键字检索，请按enter键...",
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == Ext.EventObject.ENTER) {
                            var search_all = grid.queryById("search_all");
                            if (search_all) search_all.setChecked(true);
                            designSearchByField(store, 'simple_job_log', "run_log", field.getValue());
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
                                    gridSearchByDate(store, 'simple_job_log', 0);
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
                                    gridSearchByDate(store, 'simple_job_log', 2);
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
                                    gridSearchByDate(store, 'simple_job_log', 6);
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
                                    gridSearchByDate(store, 'simple_job_log', 30);
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
                itemId: "job_id", name: 'job_id',
                xtype: 'combo', editable: false,
                valueField: 'id', displayField: 'name',
                width: 160, emptyText: "--请选择定时任务--",
                store: {
                    autoLoad: true, // 必须自动加载, 否则在编辑的时候load
                    proxy: {
                        type: 'ajax', url: "monitor/quartz/log/combo",
                        extraParams: {},
                        reader: {
                            type: 'json', root: 'root',
                            totalProperty: 'totalProperty'
                        }
                    },
                    fields: ['id', 'name']
                },
                queryMode: 'remote'
            }, {
                xtype: 'combobox',
                itemId: "keep_time",
                name: 'keep_time',
                labelStyle: 'margin-bottom:5px',
                fieldLabel: '记录保留时间',
                allowBlank: false,
                valueField: 'id', displayField: 'name',
                queryMode: 'local', value: "7",
                store: {
                    "fields": ["id", "name"],
                    "data": [{"id": "7", "name": "保留近一周"}, {"id": "30", "name": "保留近一个月"}, {
                        "id": "90",
                        "name": "保留近三个月"
                    }, {"id": "180", "name": "保留最近半年"}, {"id": "365", "name": "保留最近一年"}, {
                        "id": "0",
                        "name": "不保留，全部删除"
                    }]
                },
                editable: false
            }]
        });
        var win = Ext.create('widget.window', {
            title: '清空任务日志',
            width: 360,
            height: 200,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit',
            animateTarget: btn.getId(),
            items: [form],
            buttonAlign: "right",
            buttons: ["->", {
                itemId: "btn_save_form",
                text: '确认', minWidth: 70,
                listeners: {
                    click: function () {
                        var keep_time = form.queryById('keep_time');
                        var job_id = form.queryById('job_id');
                        Ext.MessageBox.confirm("提示!", "清空任务运行记录，您确定要“" + keep_time.getRawValue() + "”的操作吗?", function (btn) {
                            if (btn == "yes") {
                                Ext.Ajax.request({
                                    method: "POST", url: "monitor/quartz/log/clear",
                                    params: {
                                        item_id: item_id,
                                        days: keep_time.getValue(),
                                        job_id: job_id.getValue()
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

    var searchWin;

    function openSearchWin(treenode, grid) {
        if (!searchWin) {
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
                    fieldLabel: '记录时间',
                    layout: 'hbox',
                    defaults: {
                        flex: 1,
                        editable: false,
                        hideLabel: true
                    },
                    defaultType: 'datefield',
                    items: [{
                        itemId: "run_time_s", name: 'run_time_s',
                        margin: '0 5 0 0', value: start_time,
                        format: 'Y-m-d', editable: false,
                        listeners: {
                            "select": function (field) {
                                var last_date = field.up('form').down("#run_time_e");
                                last_date.setMinValue(field.getValue());
                                last_date.expand();
                            }
                        }
                    }, {
                        flex: 0, width: 20,
                        xtype: 'displayfield',
                        value: '至'
                    }, {
                        itemId: "run_time_e", name: 'run_time_e',
                        fieldLabel: 'End', value: new Date(),
                        format: 'Y-m-d', editable: false,
                        listeners: {
                            "select": function (field) {
                                var first_date = field.up('form').down("#run_time_s");
                                first_date.setMaxValue(field.getValue());
                            }
                        }
                    }]
                }, {
                    fieldLabel: '定时任务',
                    itemId: "job_id", name: 'job_id',
                    xtype: 'combo', editable: false,
                    valueField: 'id', displayField: 'name',
                    store: {
                        autoLoad: true, // 必须自动加载, 否则在编辑的时候load
                        proxy: {
                            type: 'ajax', url: "monitor/quartz/log/combo",
                            extraParams: {},
                            reader: {
                                type: 'json', root: 'root',
                                totalProperty: 'totalProperty'
                            }
                        },
                        fields: ['id', 'name']
                    },
                    queryMode: 'remote'
                }, {
                    itemId: "run_result",
                    xtype: 'radiogroup',
                    name: 'run_result',
                    fieldLabel: '运行结果',
                    allowBlank: false,
                    items: [{"boxLabel": "成功", "name": "run_result", "inputValue": "成功"},
                        {"boxLabel": "失败", "name": "run_result", "inputValue": "失败"}],
                    columns: [70, 70]
                }, {
                    itemId: "run_log",
                    xtype: 'textareafield',
                    name: 'run_log',
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
            searchWin = Ext.create('widget.window', {
                title: '定时任务日志查询',
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
                            form.queryById('run_time_s').setValue("");
                            form.queryById('run_time_e').setValue("");
                            form.queryById('run_time_e').setMinValue("1991-01-01");
                            form.queryById('run_time_s').setMaxValue("2999-01-01");
                        }
                    }
                }, {
                    text: '关闭', minWidth: 70,
                    handler: function () {
                        searchWin.close();
                    }
                }]
            });
        }

        function search() {
            var items = [];
            items[items.length] = {
                field: "run_time",
                operator: "between",
                start: "run_time_s",
                end: "run_time_e",
                valType: "string"
            };
            items[items.length] = {item_id: "job_id", valType: "int"};
            items[items.length] = {item_id: "run_result", operator: "="};
            items[items.length] = {item_id: "run_log", operator: "like"};
            searchSingleTableReLoad(grid, designSearchByForm(form, items), "simple_job_log");
            searchWin.close();
        }

        searchWin.show();
    }

    function openRecordShowWindow(record) {
        var form = Ext.create('Ext.form.Panel', {
            autoScroll: true, border: false,
            defaultType: 'textfield',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 100
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'textareafield',
                itemId: 'run_log', name: 'run_log',
                height: 300, flex: 1, fieldLabel: '',
                margin: "0", allowBlank: true  // 表单项非空
            }]
        });
        var recordShowWin = Ext.create('widget.window', {
            title: '任务运行日志查看',
            width: 480,
            height: 420,
            maximizable: true,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit',
            items: [form],
            listeners: {
                show: function (window, eOpts) {
                    designFormOriginalValue(form, recordShowWin.record);
                }
            },
            buttonAlign: "right",
            buttons: ["->", {
                text: '关闭', minWidth: 70,
                handler: function () {
                    recordShowWin.close();
                }
            }]
        });
        recordShowWin.record = record;
        recordShowWin.show();
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}


