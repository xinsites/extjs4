Ext.define('javascript.menu.monitor.user_online', {
    extend: ''
});

//在线用户
function createPanelUserOnline(treenode) {
    Ext.define('model_monitor_online', {
        extend: 'Ext.data.Model',
        idProperty: 'sessionId',
        fields: [{"name": "sessionId", "type": "string", "text": "用户会话id"},
            {"name": "login_name", "type": "string", "text": "登录用户"},
            {"name": "dept_name", "type": "string", "text": "部门名称"},
            {"name": "ip_address", "type": "string", "text": "登录IP地址"},
            {"name": "login_location", "type": "string", "text": "登录地点"},
            {"name": "browser", "type": "string", "text": "浏览器名称"},
            {"name": "version", "type": "string", "text": "浏览器版本号"},
            {"name": "device", "type": "string", "text": "操作系统"},
            {"name": "status", "type": "string", "text": "在线状态"},
            {"name": "start_timestamp", "type": "string", "text": "session创建时间"},
            {"name": "last_access_time", "type": "string", "text": "最后访问时间"},
            {"name": "time_out", "type": "string", "text": "超时时间"}]
    });

    var itemid = treenode.raw.id;
    var pageSize = getGridPageSize(itemid);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: 'monitor/online/grid',
            extraParams: {item_id: itemid, is_data_per: treenode.raw.isdataper},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'sessionId',
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
            property: 'last_access_time',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_monitor_online'
    });
    var columns = [new Ext.grid.RowNumberer({width: 40, tdCls: 'blue'}),
        {text: '', width: 20, dataIndex: '', hideable: false, hidden: true}, {
            text: '会话Id',
            dataIndex: 'sessionId',
            width: 100,
            fixed: false,
            align: 'left',
            sortable: true
        }, {
            text: '用户登录名',
            dataIndex: 'login_name',
            width: 100,
            fixed: true,
            align: 'left',
            sortable: false
        }, {
            text: 'IP地址',
            dataIndex: 'ip_address',
            width: 110,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '登录地点',
            dataIndex: 'login_location',
            width: 100,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '登录设备',
            dataIndex: 'device',
            width: 100,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '浏览器名称',
            dataIndex: 'browser',
            width: 160,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: function (value, mata, record) {
                return value + " " + record.get("version");
            }
        }, {
            //     text: '在线状态',
            //     dataIndex: 'status',
            //     width: 80,
            //     fixed: true,
            //     align: 'center',
            //     sortable: true,
            //     renderer: function (value, mata, record) {
            //         if (value == "on_line") return '<b style="color:green; ">在线</b>';
            //         return '<b style="color:red; ">离线</b>';
            //     }
            // }, {
            text: '创建时间',
            dataIndex: 'start_timestamp',
            width: 130,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: Ext.util.Format.dateRenderer('Y-m-d H:i')
        }, {
            text: '最后访问时间',
            dataIndex: 'last_access_time',
            width: 130,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: Ext.util.Format.dateRenderer('Y-m-d H:i')
        }, {
            text: '操作',
            dataIndex: 'sessionId',
            width: 80,
            fixed: true,
            align: 'left',
            sortable: false,
            renderer: function (value, mata, record) {
                if (isExistsByItemId(treenode, "btn_kickout", ""))
                    return '<span class="label label-danger" onclick="setItemClickFlag(\'sessionId\')"><i class="fa fa-remove"></i> 踢出</span>';
                return '<span class="label label-danger btn-disabled"><i class="fa fa-remove"></i> 踢出</span>';
            }
        }];

    var add_type = "first", first_index = 1;
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
            preserveScrollOnRefresh: true,
            loadingText: "数据加载中，请稍等..."
        },
        border: false,
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi",
            checkOnly: false,
            enableKeyNav: true
        }),
        listeners: {
            "cellclick": function (treeview, td, cellIndex, record, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "sessionId" && itemClickFlag == "sessionId") {
                    if (isExistsByItemId(treenode, "btn_kickout", "强退用户"))
                        kickoutSelectUser(grid, record.get("sessionId"));
                }
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', '<b>搜索:</b>',
                {
                    xtype: 'textfield', width: 220, emptyText: "姓名/登录名检索，请按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                designSearchByField(store, 'key_user', "user_name、login_name", field.getValue());
                            }
                        }
                    }
                }, {
                    xtype: 'button', text: '强退用户',
                    itemId: 'btn_kickout',
                    iconCls: "icon_user_kickout",
                    handler: function (btn, pressed) {
                        kickoutSelectUser(grid);
                    }
                }, '->', {
                    xtype: 'displayfield',
                    value: '<span style="color:blue;">不规则退出延时3分钟</span>'
                }]
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', {cookieName: "grid_" + itemid})
        }]
    });

    //删除用户管理中的选中行
    function kickoutSelectUser(grid, sessionId) {
        var records = grid.getSelectionModel().getSelection();
        var Ids = [];
        if (sessionId) {
            Ids[Ids.length] = sessionId;
        } else {
            for (var i = 0; i < records.length; i++) {
                Ids[Ids.length] = records[i].get("sessionId");
            }
        }
        if (Ids.length == 0) {
            Ext.alert.msg('信息提示', '请选择要强制退出的用户！');
        } else {
            Ext.MessageBox.confirm("提示!", "您确定要强制退出选中的" + Ids.length + "条用户信息吗?", function (btn) {
                if (btn == "yes") {
                    Ext.Ajax.request({
                        method: "POST", url: 'monitor/online/kickout',
                        params: {sessionIds: Ids.join(",")},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                if (grid.store.getCount() == records.length && grid.store.currentPage > 1)
                                    grid.store.previousPage();
                                else
                                    grid.store.reload();
                            } else {
                                showMsgByJson(resp, "强制退出失败!", Ext.MessageBox.TipTime);
                            }
                        },
                        failure: function (response, options) {
                            showMsgByResponse(response, "强制退出失败!", Ext.MessageBox.TipTime);
                        }
                    });
                }
            });
        }
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}
