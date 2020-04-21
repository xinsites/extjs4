Ext.define('javascript.menu.system.role_manage', {
    extend: ''
});

function createPanel_Role(treenode) {
    var grid_fields = [{"name": "id", "type": "int", "text": "主键"},
        {"name": "pid", "type": "int", "text": "父结点"},
        {"name": "role_per_value", "type": "string", "text": "角色权限值"},
        {"name": "text", "type": "string", "text": "角色名称"},
        {"name": "org_id", "type": "string", "text": "所属公司"},
        {"name": "org_id_text", "type": "string", "text": "所属公司_文本值"},
        {"name": "role_state", "type": "bool", "text": "是否有效"},
        {"name": "user_num", "type": "int", "text": "角色用户数"},
        {"name": "issys", "type": "int", "text": "是否系统内置"},
        {"name": "ismembers", "type": "int", "text": "是否有角色成员"},
        {"name": "create_time", "type": "date", "text": "创建时间"},
        {"name": "role_remark", "type": "string", "text": "角色备注"},
        {"name": "serialcode", "type": "int", "text": "排序号"}];
    Ext.define('model_sys_role', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });
    var item_id = treenode.raw.id;
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            type: 'ajax', url: "system/role/tree",
            reader: {type: 'json', id: "id"},
            extraParams: {}
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: grid_fields,
        listeners: {
            'load': function (store, record) {
                setTimeout(function () {
                    var rec = store.getNodeById(grid.store.proxy.extraParams.role_id);
                    if (rec) tree.getView().getSelectionModel().select(rec)
                }, 100)
            }
        }
    });

    function getNewData() {
        return Ext.create('model_sys_role', {
            'role_id': 0
        });
    }

    //动态树拖动保存排序
    function saveDragSortFn(parent_record, index) {
        var childNodes = parent_record.childNodes;
        var str = [];
        for (var i = 0, len = childNodes.length; i < len; i++) {
            var record = childNodes[i];
            str[str.length] = record.get("id") + ":" + index++;
        }
        Ext.Ajax.request({
            method: "POST", url: "system/role/sort",
            params: {item_id: item_id, sort_vals: str.join(";")},
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    Ext.alert.msg('提示', "排序成功!");
                } else {
                    Ext.alert.msg('提示', "保存失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "保存失败!");
            }
        });
    }

    var tree = Ext.create('Ext.tree.Panel', {
        itemId: "main_grid",
        store: tree_store,
        width: 300, height: 400,
        useArrows: false,
        rowLines: false, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        viewConfig: {
            getRowClass: function () {
                return 'tree_panel_row_height18';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'treeviewdragdrop',
                allowLeafInserts: false, //叶子节点是否可拖动的配置，默认值为true
                containerScroll: true,
                dragGroup: 'role_draggroup',
                dropGroup: 'role_draggroup'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_sort", "排序")) return false;
                    if (data.records.length > 0) {
                        var parent_record = data.records[0].parentNode; //先获取结点的父结点
                        if (parent_record.childNodes) {
                            if (data.records.length == parent_record.childNodes.length) { //说明全部移动
                                parent_record.set("children", []);
                            }
                        }
                    }
                },
                nodedragover: function (targetNode, position, dragData) {
                    return true;
                },
                drop: function (node, data, dropRec, dropPosition) {
                    saveDragSortFn(data.records[0].parentNode, 1);
                }
            }
        },
        columns: [{
            xtype: 'treecolumn', text: '角色名称', dataIndex: 'text', flex: 1,
            renderer: function (val, meta, rec) {
                var str = val;
                str += '[<b style="color:blue;">' + rec.get("user_num") + '</b>]'
                if (rec.get("issys") == "1") str += '<span style="color:red;">[内置]</span>';
                if (rec.get("role_state") == "1") return str;
                return '<s style="color:#666;">' + str + '</s>';
            }
        }],
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                grid.store.proxy.extraParams.role_id = record.get("id");
                grid.store.proxy.extraParams.role_name = record.get("text");
                var displayfield = grid.queryById('role_member_grid_explain');
                displayfield.setValue('<span style="color:blue;">' + record.get("text") + '：用户列表</span>');
                var member_noleader = grid.queryById('member_noleader');
                if (member_noleader) {
                    member_noleader.setValue(false);
                    member_noleader.setVisible(false);
                }
                grid.store.removeAll(true);
                grid.store.load();
            },
            "itemcontextmenu": function (tree, sel_record, item, index, e, eOpts) {
                var me = this;
                //禁用浏览器的右键相应事件
                e.preventDefault();
                e.stopEvent();
                me.sel_record = sel_record;
                if (!me.ctxMenu) {
                    me.ctxMenu = new Ext.menu.Menu({
                        floating: true,
                        items: [{
                            itemId: "mod",
                            text: "修改",
                            disabled: !isExistsByItemId(treenode, "btn_mod", ""),
                            iconCls: 'icon_edit',
                            handler: function () {
                                var sel_record = me.sel_record;
                                openEditWindow(sel_record);
                            }
                        }, {
                            itemId: "delete",
                            text: "删除",
                            disabled: !isExistsByItemId(treenode, "btn_del", ""),
                            iconCls: 'icon_delete',
                            //tooltip: '父栏目不能删除',
                            handler: function () {
                                var sel_record = me.sel_record;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/role/delete",
                                            params: {item_id: item_id, ids: sel_record.get("id")},
                                            success: function (response, options) {
                                                var resp = Ext.JSON.decode(response.responseText);
                                                if (resp.success) {
                                                    sel_record.remove(true);
                                                } else {
                                                    Ext.alert.msg('提示', "删除失败!");
                                                }
                                            },
                                            failure: function (response, options) {
                                                Ext.alert.msg('提示', "删除失败!");
                                            }
                                        });
                                    }
                                });
                            }
                        }]
                    });
                }
                var ctxMenu = me.ctxMenu;
                var deleteItem = ctxMenu.getComponent("delete");
                deleteItem.setDisabled(me.sel_record.get("issys") == "1");
                ctxMenu.showAt(e.getXY()); //让右键菜单跟随鼠标位置
            }
        }
    });

    function openEditWindow(record) {
        var form = createObjectForm();
        var win_config = {
            title: '添加表单',
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, record);  //初始化信息(新增时初始默认值，修改时填充修改值)
                },
                show: function (window, eOpts) {
                    var button = window.queryById("btn_save_form");
                    if (record.get("role_id") == 0 || !record.get("role_id")) {
                        window.setTitle("角色新增");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_add", ""));
                    } else {
                        window.setTitle("角色修改");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_mod", ""));
                    }
                }
            },
            buttonAlign: "right",
            buttons: [{
                xtype: 'checkbox',
                checked: $.cookie("submit_after") == "1",
                labelWidth: 5, fieldLabel: ' ',
                boxLabel: '保存后关闭窗口',
                labelSeparator: "", labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        $.cookie("submit_after", checked ? "1" : "2");
                    }
                }
            }, "->", {
                itemId: "btn_save_form",
                text: '保存', minWidth: 70,
                listeners: {
                    click: function () {
                        if (form.isValid()) {
                            form.submit({
                                method: "POST", url: "system/role/save",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false,
                                params: {item_id: item_id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        tree.store.reload();
                                        if ($.cookie("submit_after") == "1") {
                                            Ext.alert.msg("提示", "保存成功！");
                                            win.close();
                                        } else if (record.get("role_id") == 0) {
                                            Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                            form.getForm().reset();
                                        } else {
                                            Ext.alert.msg("提示", "保存成功！");
                                            var values = form.getForm().getValues(false);
                                            designFormOriginalValue(form, Ext.create("model_sys_role", values));
                                        }
                                    }
                                },
                                failure: function (form, action) {
                                    showMsgBySubmit(action, "保存失败！");
                                }
                            });
                        } else {
                            Ext.alert.msg('无效输入', '请输入正确的信息!');
                        }
                    }
                }
            }, {
                text: '重置', minWidth: 70,
                handler: function () {
                    form.getForm().reset();
                }
            }, {
                text: '关闭', minWidth: 70,
                handler: function () {
                    win.close();
                }
            }]
        }
        var win = Ext.create('widget.window', Ext.apply(win_config, {
            width: 450,
            height: 310,
            maximizable: true,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit'
        }));
        win.show();
    }

    function createObjectForm() {
        var form_config = {
            border: false,
            bodyPadding: '20 15 10',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 80}
        };
        form_config.items = [{xtype: 'hiddenfield', name: 'id'}, {
            xtype: 'textfield',
            name: 'text',
            itemId: 'text',
            fieldLabel: '角色名称',
            allowBlank: false,
            maxLength: 50,
            beforeLabelTextTpl: '*'
        }, {
            xtype: 'textfield',
            name: 'role_per_value',
            itemId: 'role_per_value',
            fieldLabel: '角色权限值',
            allowBlank: true,
            maxLength: 25
        }, {
            xtype: 'displayfield',
            hideLabel: false,
            value: '控制器方法标注@RequiresRoles("***")',
            fieldStyle: 'color:#FF0000;',
            style: 'margin-top:0px;',
            fieldLabel: '  ',
            labelSeparator: ''
        }, {
            xtype: 'fieldcontainer',
            fieldLabel: ' ',
            labelSeparator: "",
            layout: 'hbox',
            defaults: {
                editable: false,
                hideLabel: true
            },
            defaultType: 'checkbox',
            items: [{
                xtype: 'checkbox',
                fieldLabel: ' ',
                labelSeparator: "",
                boxLabel: '是否有效',
                name: 'role_state',
                uncheckedValue: '0',
                inputValue: '1'
            }, {
                flex: 0, width: 50,
                xtype: 'displayfield',
                value: ''
            }]
        }, {
            xtype: 'textareafield',
            name: 'role_remark',
            itemId: 'role_remark',
            fieldLabel: '角色备注',
            allowBlank: true,
            height: 40,
            rows: 3,
            maxLength: 150,
            flex: 1
        }];
        return Ext.create('Ext.form.Panel', form_config);
    }

    var grid = getRoleMemberGrid(tree);
    return Ext.create('Ext.panel.Panel', {
        layout: "border", border: false,
        items: [{
            title: "用户角色",
            collapsible: false,
            region: 'west', split: {width: 5},
            width: 220, minWidth: 1, maxWidth: 300,
            tools: [{
                itemId: 'btn_relation',
                type: 'plus', tooltip: '添加角色&nbsp;',
                callback: function (panel, tool) {
                    if (isExistsByItemId(treenode, "btn_add", "添加角色")) {
                        openEditWindow(getNewData());
                    }
                }
            }, {
                type: 'refresh',
                tooltip: '刷新',
                callback: function (panel, tool) {
                    tree.store.load();
                    grid.store.proxy.extraParams.role_id = 0;
                    grid.store.proxy.extraParams.role_name = "";
                    grid.queryById('role_member_grid_explain').setValue('');
                    grid.queryById('text_search').setValue('');
                    grid.store.proxy.extraParams.searchdata = "";
                    var member_noleader = grid.queryById('member_noleader');
                    if (member_noleader) {
                        member_noleader.setVisible(true);
                    }
                    grid.store.load();
                }
            }],
            layout: 'fit', items: [tree]
        }, {
            region: 'center',
            layout: 'fit',
            items: [grid]
        }]
    });
}

//角色成员Grid
function getRoleMemberGrid(tree) {
    Ext.define('model_sys_role_members', {
        extend: 'Ext.data.Model',
        idProperty: 'user_id',
        fields: [{"name": "user_id", "type": "int", "text": "主键"},
            {"name": "user_name", "type": "string", "text": "用户姓名"},
            {"name": "login_name", "type": "string", "text": "登录名"},
            {"name": "head_photo", "type": "string", "text": "头像"},
            {"name": "role_id", "type": "string", "text": "用户角色"},
            {"name": "role_id_text", "type": "string", "text": "用户角色_文本值"},
            {"name": "dept_id", "type": "string", "text": "用户部门"},
            {"name": "dept_id_text", "type": "string", "text": "用户部门_文本值"},
            {"name": "user_state", "type": "bool", "text": "启用状态"},
            {"name": "Remark", "type": "string", "text": "备注"}]
    });
    var pageSize = getGridPageSize("grid_role_user");
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "system/role/user/grid",
            extraParams: {role_id: 0, role_name: ""},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'user_id',
                totalProperty: 'totalProperty'
            }
        },
        sorters: [{
            property: 'serialcode',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_sys_role_members'
    });
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
            sortable: false,
            renderer: function (val, meta, rec) {
                if (val != "")
                    return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'set_role\')">' + rec.get("role_id_text") + '</span>';
                else
                    return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'set_role\')">尚未配置</span>';
            }
        }, {
            text: '用户部门',
            dataIndex: 'dept_id',
            width: 160,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: function (value, mata, record) {
                return record.get("dept_id_text");
            }
        }, {
            xtype: 'checkcolumn',
            text: '启用状态', stopSelection: false,
            width: 120, fixed: true, dataIndex: 'user_state'
        }, {
            text: '操作',
            dataIndex: 'role_id',
            width: 180,
            fixed: false,
            align: 'left',
            sortable: false,
            renderer: function (value, mata, record) {
                if (record.get("role_id") > 0)
                    return '<span class="label label-danger" onclick="setItemClickFlag(\'role_del\')"><i class="fa fa-remove"></i> 移除</span>';
                else
                    return '<span class="label label-danger btn-disabled"><i class="fa fa-remove"></i> 移除</span>';
            }
        }];

    //用户角色移除
    function removeUserRole(record) {
        Ext.Ajax.request({
            method: "POST", url: "system/role/user/setup",
            params: {user_id: record.get("user_id")},
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    tree.store.reload();
                    user_grid.store.reload();
                } else {
                    Ext.alert.msg('提示', "用户角色移除失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "用户角色移除失败!");
            }
        });
    }

    var user_grid = Ext.create('Ext.grid.Panel', {
        itemId: "role_member_grid",
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
                if (header.dataIndex == "role_id") {
                    if (itemClickFlag == "role_del") {
                        if (record.get("role_id") > 0)
                            removeUserRole(record);
                        else
                            Ext.alert.msg('提示', "空角色无需移除！");
                    } else if (itemClickFlag == "set_role") {
                        openUserRoleSelectWinFn(record);
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
                        if (store.proxy.extraParams.role_id > 0) {
                            var user_ids = [];
                            store.each(function (record) {
                                user_ids[user_ids.length] = record.get("user_id");
                            });
                            Ext.Ajax.request({
                                method: "POST", url: "system/role/user/sel",
                                params: {role_id: store.proxy.extraParams.role_id},
                                success: function (response, options) {
                                    var resp = Ext.JSON.decode(response.responseText);
                                    if (resp.success) {
                                        openRoleUserSelectFn(tree, user_grid, btn.getId(), resp.user_ids);
                                    }
                                },
                                failure: function (response, options) {
                                    openRoleUserSelectFn(tree, user_grid, btn.getId(), user_ids.join(","));
                                }
                            });
                        } else {
                            Ext.alert.msg('提示', "请选择一个角色!");
                        }
                    }
                }, '->', {
                    xtype: 'displayfield', maxHeight: 24,
                    itemId: "role_member_grid_explain",
                    value: '<span style="color:blue;"></span>'
                }, {
                    xtype: 'checkbox',
                    labelWidth: 85,
                    itemId: "member_noleader",
                    fieldLabel: '无角色成员',
                    labelSeparator: "",
                    labelAlign: "right",
                    listeners: {
                        'change': function (item, checked) {
                            user_grid.store.proxy.extraParams.norole = checked ? 1 : 0;
                            user_grid.store.load();
                        }
                    }
                }, ' ']
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', {cookieName: "grid_role_user"})
        }]
    });
    return user_grid;

    //用户角色选择窗口
    function openUserRoleSelectWinFn(record) {
        var roles = [];
        var childNodes = tree.getRootNode().childNodes;
        Ext.Array.each(childNodes, function (rec, index) {
            var item = {
                boxLabel: rec.get("text"),
                name: 'role_id',
                inputValue: rec.get("id")
            };
            roles[roles.length] = item;
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
                fieldLabel: '用户角色',
                name: 'role_id',
                itemId: 'role_id',
                columns: 3, items: roles
            }]
        });

        var userRoleWin = Ext.create('Ext.window.Window', {
            title: "用户角色选择",
            width: 450, minWidth: 200,
            resizable: false, closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            layout: "fit",
            items: [forms],
            listeners: {
                "show": function (window, eOpts) {
                    forms.queryById('role_id').setValue({"role_id": record.get("role_id")});
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
                                url: "system/role/user/setup", method: "POST",
                                waitMsg: '请稍等，正在保存...',
                                params: {user_id: record.get("user_id")},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        userRoleWin.close();
                                        tree.store.reload();
                                        user_grid.store.reload();
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
                        forms.queryById('role_id').reset();
                    }
                }, {
                    text: "关闭",
                    minWidth: 70,
                    handler: function () {
                        userRoleWin.close();
                    }
                }]
            }]
        });
        userRoleWin.show();
    }
}

//打开角色用户选择
function openRoleUserSelectFn(grid, user_grid, btn_id, user_ids) {
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
    var role_id = user_grid.store.proxy.extraParams.role_id;
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
        //title: '数据视图 (0 项选中)',
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
                    //this.up('panel').setTitle('数据视图 (' + l + ' 项项选中)');
                },
                itemclick: function (dv, node, item, index, e, ept) {
                    var isSelected = !dv.isSelected(item);
                    user_sel[node.get("id")] = isSelected;
                }
            }
        })
    });
    var wind = Ext.create('widget.window', {
        title: '角色成员分配--' + user_grid.store.proxy.extraParams.role_name,
        width: 800, height: 500,
        closable: true, autoShow: true,
        animateTarget: btn_id,
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
                        id_vals[id_vals.length] = key + ":" + user_sel[key];
                    }
                    Ext.Ajax.request({
                        method: "POST", url: "system/role/user/save",
                        params: {IdVal: id_vals.join(";"), role_id: role_id},
                        success: function (response, options) {
                            grid.store.reload();
                            user_grid.store.reload();
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

