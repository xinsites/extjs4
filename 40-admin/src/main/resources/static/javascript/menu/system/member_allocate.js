Ext.define('javascript.menu.system.member_allocate', {
    extend: ''
});

function getMemberUserGrid(params_json, tree) {
    Ext.define('model_member_user', {
        extend: 'Ext.data.Model',
        idProperty: 'user_id',
        fields: [{"name": "user_id", "type": "int", "text": "主键"},
            {"name": "user_name", "type": "string", "text": "用户姓名"},
            {"name": "login_name", "type": "string", "text": "登录名"},
            {"name": "head_photo", "type": "string", "text": "头像"},
            {"name": "role_id", "type": "string", "text": "用户角色"},
            {"name": "role_id_text", "type": "string", "text": "用户角色_文本值"},
            {"name": "leader", "type": "string", "text": "直属领导"},
            {"name": "leader_text", "type": "string", "text": "直属领导_文本值"},
            {"name": "dept_id", "type": "string", "text": "用户部门"},
            {"name": "dept_id_text", "type": "string", "text": "用户部门_文本值"},
            {"name": "user_state", "type": "bool", "text": "启用状态"},
            {"name": "Remark", "type": "string", "text": "备注"}]
    });
    var pageSize = getGridPageSize("grid_member_user");
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: path_url.system.member.grid,
            extraParams: {
                table_name: params_json.table_name, table_id: params_json.table_id, man_type: params_json.man_type
            },
            reader: {
                type: 'json', root: 'root',
                idProperty: 'user_id',
                totalProperty: 'totalProperty'
            }
        },
        sorters: [{
            property: 'a1.serialcode',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_member_user',
        listeners: {
            'load': function (store, records) {
                if (store.params_json) params_json = store.params_json;
                if (store.getCount() == 0 && store.currentPage > 1) {
                    store.currentPage = 1;
                    store.load();
                }
            }
        }
    });
    var is_leader = params_json.table_name == "sys_user";
    var columns = [new Ext.grid.RowNumberer({width: 40, tdCls: 'blue'}),
        {
            xtype: 'templatecolumn', align: 'center', hideable: false,
            text: '', width: 40, fixed: true, dataIndex: 'head_photo',
            tpl: ['<div class="user-header-img"><img src="{head_photo:htmlEncode}" onerror="this.src=\'images/default_avatar3.png\'"></div>']
        }, {
            text: '用户姓名',
            dataIndex: 'user_name',
            width: 150,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '登录名',
            dataIndex: 'login_name',
            width: 160,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            text: '用户角色',
            dataIndex: 'role_id',
            width: 160,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: function (value, mata, record) {
                return getRoleNameFn(record, 'role_id', value);
            }
        }, {
            text: '直属领导',
            dataIndex: 'leader_text',
            hidden: !is_leader, hideable: is_leader,
            width: 150,
            fixed: true,
            align: 'left',
            sortable: false,
            renderer: function (val, meta, rec) {
                if (val != "")
                    return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'leader\')">' + val + '</span>';
                else
                    return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'leader\')">尚未配置</span>';
            }
        }, {
            text: '用户部门',
            dataIndex: 'dept_id',
            width: 140,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: function (value, mata, record) {
                return getDeptNameFn(record, 'dept_id', value);
            }
        }, {
            xtype: 'checkcolumn',
            text: '启用状态', stopSelection: false,
            width: 100, fixed: true, dataIndex: 'user_state'
        }, {
            text: '操作',
            dataIndex: 'user_id',
            width: 180,
            fixed: false,
            align: 'left',
            sortable: false,
            renderer: function (value, mata, record) {
                if (store.proxy.extraParams.table_id == 0)
                    return '<span class="label label-danger btn-disabled"><i class="fa fa-remove"></i> 移除</span>';
                else if (is_leader && record.get("leader") == 0)
                    return '<span class="label label-danger btn-disabled"><i class="fa fa-remove"></i> 移除</span>';
                return '<span class="label label-danger" onclick="setItemClickFlag(\'user_id\')"><i class="fa fa-remove"></i> 移除</span>';
            }
        }];

    //用户角色移除
    function removeMemberUser(record) {
        Ext.Ajax.request({
            method: "POST", url: path_url.system.member.delete,
            params: {
                user_id: record.get("user_id"),
                table_name: params_json.table_name, table_id: params_json.table_id, man_type: params_json.man_type
            },
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    if (params_json.grid) params_json.grid.store.reload();
                    user_grid.store.reload();
                } else {
                    Ext.alert.msg('提示', "成员用户移除失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "成员用户移除失败!");
            }
        });
    }

    var user_grid = Ext.create('Ext.grid.Panel', {
        itemId: "member_user_grid",
        store: store,
        disableSelection: false, //设置为true，则禁用选择模型
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        sortableColumns: false,
        viewConfig: {
            getRowClass: function () {
                return 'grid-row-24-normal';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            enableTextSelection: true
        },
        listeners: {
            "cellclick": function (treeview, td, cellIndex, record, tr, rowIndex, e) {
                var header = user_grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "user_id" && itemClickFlag == "user_id") {
                    removeMemberUser(record)
                } else if (header.dataIndex == "leader_text" && itemClickFlag == "leader") {
                    if (is_leader) {
                        params_json.user_grid = user_grid;
                        openUserLeaderSelectWinFn(params_json, record);
                    }
                }
            }
        },
        border: false,
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['<b>搜索:</b>',
                {
                    itemId: 'text_search', xtype: 'textfield', width: 220, emptyText: "按姓名/登录名检索，按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                designSearchByField(store, 'key_user', "user_name、login_name", field.getValue());
                            }
                        }
                    }
                }, '-', {
                    itemId: 'btn_member',
                    xtype: 'button', text: '成员分配',
                    minWidth: 60, iconCls: 'icon_member',
                    handler: function (btn, pressed) {
                        if (store.proxy.extraParams.table_id > 0) {
                            params_json.btn_id = btn.getId();
                            params_json.user_grid = user_grid;
                            var user_ids = [];
                            store.each(function (record) {
                                user_ids[user_ids.length] = record.get("user_id");
                            });
                            Ext.Ajax.request({
                                method: "POST", url: "system/member/users",
                                params: {
                                    table_name: store.proxy.extraParams.table_name,
                                    table_id: store.proxy.extraParams.table_id,
                                    man_type: store.proxy.extraParams.man_type
                                },
                                success: function (response, options) {
                                    var resp = Ext.JSON.decode(response.responseText);
                                    if (resp.success) {
                                        openMemberUserSelectFn(params_json, resp.user_ids);
                                    }
                                },
                                failure: function (response, options) {
                                    openMemberUserSelectFn(params_json, user_ids.join(","));
                                }
                            });
                        } else {
                            Ext.alert.msg('提示', "请选择一个对象!");
                        }
                    }
                }, '->', {
                    xtype: 'displayfield',
                    itemId: "member_user_grid_explain", maxHeight: 24,
                    value: '<span style="color:blue;">' + params_json.explain + '</span>'
                }, {
                    xtype: 'checkbox',
                    labelWidth: 85,
                    itemId: "member_noleader",
                    hidden: !is_leader,
                    fieldLabel: '无直属领导',
                    labelSeparator: "",
                    labelAlign: "right",
                    listeners: {
                        'change': function (item, checked) {
                            user_grid.store.proxy.extraParams.noleader = checked ? 1 : 0;
                            if (store.proxy.extraParams.table_id == 0) {
                                user_grid.store.reload();
                            }
                        }
                    }
                }, '  ']
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', {cookieName: "grid_member_user"})
        }]
    });
    return user_grid;

    //用户领导选择窗口
    function openUserLeaderSelectWinFn(params_json, record) {
        var leader_users = [];
        var childNodes = params_json.grid.getRootNode().childNodes;
        Ext.Array.each(childNodes, function (rec, index) {
            var item = {
                boxLabel: rec.get("text"),
                name: 'leader_id',
                inputValue: rec.get("id")
            };
            //if (rec.get("id") == record.get("leader")) item.checked = true;
            leader_users[leader_users.length] = item;
        });
        var forms = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '20 10 20 20',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 60},
            items: [{
                itemId: "item_name", xtype: 'displayfield',
                fieldLabel: '用户姓名', style: 'margin-bottom:0px',
                value: record.get("user_name")
            }, {
                xtype: 'radiogroup',
                fieldLabel: '直属领导',
                name: 'leader_id',
                itemId: 'leader_id',
                columns: 3, items: leader_users
            }]
        });

        var userLeaderWin = Ext.create('Ext.window.Window', {
            title: "直属领导选择",
            width: 400, minWidth: 200,
            resizable: false, closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            layout: "fit",
            items: [forms],
            listeners: {
                "show": function (window, eOpts) {
                    forms.queryById('leader_id').setValue({"leader_id": record.get("leader")});
                }
            },
            dockedItems: [{
                xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
                items: ["->", {
                    text: "保存",
                    minWidth: 70,
                    handler: function () {
                        if (forms.isValid()) {
                            forms.submit({
                                url: "system/user/leader/setup", method: "POST",
                                waitMsg: '请稍等，正在保存...',
                                params: {item_id: params_json.itemid, user_id: record.get("user_id")},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        userLeaderWin.close();
                                        if (params_json.grid) params_json.grid.store.reload();
                                        if (params_json.user_grid) params_json.user_grid.store.reload();
                                    } else {
                                        Ext.alert.msg('提示', '指定失败！');
                                    }
                                },
                                failure: function (form, action) {
                                    ajaxFailureTipMsg(form, action);
                                }
                            });
                        }
                    }
                }, {
                    text: '清空',
                    minWidth: 70,
                    handler: function () {
                        forms.queryById('leader_id').reset();
                    }
                }, {
                    text: "关闭",
                    minWidth: 70,
                    handler: function () {
                        userLeaderWin.close();
                    }
                }]
            }]
        });
        userLeaderWin.show();
    }
}

//打开成员用户选择(领导、负责人)，统一保存
function openMemberUserSelectFn(params_json, user_ids) {
    var tree = Ext.create('Ext.tree.Panel', {
        store: getSysDataTreeStore('ds.sys.dept', false),
        rowLines: false, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                store.proxy.extraParams.dept_id = record.get("id");
                store.reload();
            }
        }
    });

    var ImageModel = Ext.define('model_dept_user', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'login_name'},
            {name: 'user_name'},
            {name: 'head_photo'},
            {name: 'role_id'},
            {name: 'dept_name'}]
    });
    var user_sel = {};

    function initUserSel(user_ids) {
        for (var key in user_sel) delete user_sel[key];
        Ext.Array.each(user_ids.split(","), function (user_id, index) {
            if (user_id) {
                user_sel[user_id] = true;
            }
        });
    }

    initUserSel(user_ids);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            url: path_url.system.member.deptusers,
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {dept_id: 0}
        },
        model: 'model_dept_user',
        listeners: {
            'load': function (store, records) {
                var data_view = right_panel.queryById('data_view');
                var selection = data_view.getSelectionModel();
                selection.deselectAll(true);
                Ext.Array.each(records, function (record, index) {
                    for (var key in user_sel) {
                        if (user_sel[key] && key) {
                            if (key == record.get("id"))
                                selection.select(record, true, true);
                        }
                    }
                });
            }
        }
    });

    var right_panel = Ext.create('Ext.panel.Panel', {
        bodyStyle: "padding:5px 0px 0px 5px;",
        defaults: {margin: '0 0 0 0'},
        layout: 'fit', border: false,
        dockedItems: [{
            xtype: 'toolbar', ui: 'footer', dock: 'top', layout: {pack: 'left'},
            items: [{
                xtype: 'textfield', flex: 1, emptyText: "用户名/登录名检索，请按enter键...",
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == Ext.EventObject.ENTER) {
                            store.proxy.extraParams.query = field.getValue().replace(/%/g, '/%').replace(/_/g, '/_');
                            store.reload();
                        }
                    }
                }
            }, {
                xtype: 'button', text: '刷新',
                cls: "x-btn-default-toolbar-small",
                style: 'margin-left:0px;margin-top:2px',
                handler: function (btn, pressed) {
                    tree.getSelectionModel().deselectAll(true);
                    store.proxy.extraParams.dept_id = 0;
                    store.reload();
                }
            }]
        }],
        items: Ext.create('Ext.view.View', {
            itemId: "data_view",
            store: store,
            tpl: [
                '<tpl for=".">',
                '<div class="card-box" id="{login_name:stripTags}">',
                '<div class="card-box-img"><img src="{head_photo:htmlEncode}" onerror="this.src=\'images/default_avatar3.png\'" title="{user_name:htmlEncode}"></div>',
                '<div class="card-box-content"><div>账号：{login_name:htmlEncode}</div><div>姓名：{user_name:htmlEncode}</div><div>部门：{dept_name:htmlEncode}</div></div>',
                '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
            ],
            autoScroll: true,
            simpleSelect: true, //多选，而不需要用户按住Shift或Ctrl键
            multiSelect: true,
            trackOver: true,
            overItemCls: 'x-item-over',
            itemSelector: 'div.card-box',
            emptyText: '暂无用户',
            prepareData: function (data) {
                Ext.apply(data, {});
                return data;
            },
            listeners: {
                selectionchange: function (dv, nodes) {
                    var l = nodes.length;
                },
                itemclick: function (dv, node, item, index, e, ept) {
                    var isSelected = !dv.isSelected(item);
                    user_sel[node.get("id")] = isSelected;
                }
            }
        })
    });
    var wind = Ext.create('widget.window', {
        title: params_json.win_title,
        width: 800, height: 500,
        closable: true, autoShow: true,
        animateTarget: params_json.btn_id,
        maximizable: true,
        closeAction: 'destroy',  //destroy，hide
        plain: false, modal: true,
        layout: 'border',
        items: [{
            region: 'west', split: {width: 4},
            width: 170, minWidth: 1, maxWidth: 200,
            layout: 'fit', items: [tree]
        }, {
            region: 'center',
            layout: 'fit',
            items: [right_panel]
        }],
        buttons: [{
            xtype: 'checkbox',
            checked: $.cookie("submit_after") == "1",
            labelWidth: 5, fieldLabel: ' ',
            boxLabel: '单击确定后关闭窗口',
            labelSeparator: "", labelAlign: "right",
            listeners: {
                'change': function (item, checked) {
                    $.cookie("submit_after", checked ? "1" : "2");
                }
            }
        }, "->", {
            text: '确定', minWidth: 70,
            listeners: {
                click: function () {
                    var id_vals = [];
                    for (var key in user_sel) {
                        if (user_sel[key] && key) id_vals[id_vals.length] = key;
                    }
                    Ext.Ajax.request({
                        method: "POST", url: path_url.system.member.save,
                        params: {
                            IdVal: id_vals.join(","),
                            table_name: params_json.table_name,
                            table_id: params_json.table_id,
                            man_type: params_json.man_type
                        },
                        success: function (response, options) {
                            if (params_json.grid) params_json.grid.store.reload();
                            if (params_json.user_grid) params_json.user_grid.store.reload();

                            Ext.alert.msg('提示', "保存成功!");
                            if ($.cookie("submit_after") == "1") wind.close();
                            else store.reload();
                        },
                        failure: function (response, options) {
                            Ext.alert.msg('提示', "保存失败!");
                        }
                    });
                }
            }
        }, {
            text: '重置', minWidth: 70,
            handler: function () {
                tree.getSelectionModel().deselectAll(true);
                store.proxy.extraParams.dept_id = 0;
                initUserSel(user_ids);
                store.reload();
            }
        }, {
            text: '清空', minWidth: 70,
            handler: function () {
                tree.getSelectionModel().deselectAll(true);
                store.proxy.extraParams.dept_id = 0;
                initUserSel("");
                store.reload();
            }
        }, {
            text: '关闭', minWidth: 70,
            handler: function () {
                wind.close();
            }
        }],
        buttonAlign: "right"
    });
}
