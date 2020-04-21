Ext.define('javascript.menu.system.organize_adminer', {
    extend: ''
});

//机构管理员列表(各机构管理员分配)
function getOrganizeAdminerGrid(grid, record, treenode) {
    Ext.define('model_sys_orgalloc', {
        extend: 'Ext.data.Model',
        idProperty: 'user_id',
        fields: [{"name": "user_id", "type": "int", "text": "主键"},
            {"name": "user_name", "type": "string", "text": "用户姓名"},
            {"name": "login_name", "type": "string", "text": "登录名"},
            {"name": "head_photo", "type": "string", "text": "头像"},
            {"name": "role_id", "type": "string", "text": "用户角色"},
            {"name": "role_id_text", "type": "string", "text": "用户角色_文本值"},
            {"name": "user_state", "type": "bool", "text": "启用状态"},
            {"name": "issys", "type": "int", "text": "是否自带账户"},
            {"name": "org_id", "type": "int", "text": "用户机构"},
            {"name": "Remark", "type": "string", "text": "备注"}]
    });
    var org_id = record.get("id"), org_name = record.get("text");

    function getNewData() {
        return Ext.create('model_sys_orgalloc', {
            'user_id': 0,
            'user_name': record.get("text"),
            'org_id': org_id
        });
    }

    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "system/org/managergrid",
            extraParams: {
                org_id: org_id
            },
            reader: {
                type: 'json', root: 'root',
                idProperty: 'user_id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records) {
                if (store.record) {
                    record = store.record; //已经更换了机构
                    org_id = record.get("id");
                    org_name = record.get("text");
                }
            }
        },
        sorters: [{
            property: 'serialcode',
            direction: 'asc'
        }],
        model: 'model_sys_orgalloc'
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
            fixed: false,
            align: 'left',
            sortable: true
        }, {
            text: '登录名',
            dataIndex: 'login_name',
            width: 140,
            fixed: true,
            align: 'left',
            sortable: true
        }, {
            xtype: 'checkcolumn',
            text: '启用状态', stopSelection: false,
            width: 100, fixed: true, dataIndex: 'user_state'
        }, {
            text: '用户角色',
            dataIndex: 'role_id_text',
            width: 140,
            fixed: true,
            align: 'left',
            sortable: false
        }, {
            text: '操作',
            dataIndex: 'role_id',
            width: 180,
            fixed: false,
            align: 'left',
            sortable: false,
            renderer: function (value, mata, record) {
                var str = '<span class="label label-2 label-primary" onclick="setItemClickFlag(\'role_id\')"><i class="fa fa-key"></i> 角色权限</span>';
                if (record.get("issys") == 0)
                    str += ' <span class="label label-2 label-danger" onclick="setItemClickFlag(\'user_id\')"><i class="fa fa-remove"></i> 移除</span>';
                else
                    str += ' <span class="label label-2 label-danger btn-disabled" onclick="setItemClickFlag(\'user_id\')"><i class="fa fa-remove"></i> 移除</span>';
                return str;
            }
        }];

    //打开管理员权限Tab
    var tabId = "ManagerPermissionTabediting";

    function OpenManagerPermissionTabediting(grid, record) {
        var tab = getRightTabPanel(tabId);
        if (!tab) {
            var panel = Ext.create('Ext.panel.Panel', {
                layout: "fit", border: false,
                items: [createManagerPermissionGrid(org_id, "role", record.get("role_id"))]
            });
            tab = addRightTabPanel({
                itemId: tabId, title: "管理员权限",
                xtype: "panel", iconCls: "",
                closable: true, layout: "fit",
                border: false,
                items: [panel]
            });
        } else {
            tab.queryById('manager_per_grid').Refresh(org_id, "role", record.get("role_id"));
        }
        var manager_per_grid = tab.queryById('manager_per_grid');
        var displayfield = manager_per_grid.queryById('manager_per_grid_explain');
        displayfield.setValue('<span style="color:blue;">' + org_name + '：管理员角色权限</span>');
    }

    //管理员移除
    function removeUserInfo(record) {
        Ext.MessageBox.confirm("提示!", "您确定要移除 “" + record.get("login_name") + "”管理员角色吗?", function (btn) {
            if (btn == "yes") {
                Ext.Ajax.request({
                    method: "POST", url: "system/role/user/setup",
                    params: {user_id: record.get("user_id")},
                    success: function (response, options) {
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success) {
                            grid.store.load();
                            user_grid.store.reload();
                        } else {
                            Ext.alert.msg('提示', "移除失败!");
                        }
                    },
                    failure: function (response, options) {
                        Ext.alert.msg('提示', "移除失败!");
                    }
                });
            }
        });
    }

    var user_grid = Ext.create('Ext.grid.Panel', {
        itemId: "managers_grid",
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
                    if (itemClickFlag == "role_id") OpenManagerPermissionTabediting(grid, record);
                    else if (itemClickFlag == "user_id") {
                        if (record.get("issys") == 0)
                            removeUserInfo(record);
                        else
                            Ext.alert.msg('提示', "系统内置用户，不能删除！");
                    }
                }
            },
            'celldblclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (itemClickFlag == "") openEditWindow(rec);
            }
        },
        border: false,
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'btn_manager',
                xtype: 'button', text: '新增管理员',
                minWidth: 60, iconCls: 'icon_member',
                handler: function (btn, pressed) {
                    openEditWindow(getNewData());
                }
            }, '->', {
                xtype: 'displayfield',
                hideLabel: true,
                itemId: "managers_grid_explain",
                value: '<span style="color:blue;">机构：' + record.get("text") + '</span>'
            }, ' ']
        }]
    });


    function openEditWindow(record) {
        var btn = user_grid.queryById('btn_manager');
        var form = createObjectForm(record.get("user_id"));
        var win_config = {
            title: '添加表单',
            animateTarget: btn.getId(),
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, record);  //初始化信息(新增时初始默认值，修改时填充修改值)
                },
                show: function (window, eOpts) {
                    if (record.get("user_id") == 0 || !record.get("user_id")) {
                        window.setTitle("管理员新增");
                    } else {
                        window.setTitle("管理员修改");
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
                                method: "POST", url: "system/org/managersave",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false,
                                params: {item_id: treenode.raw.id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        grid.store.load();
                                        user_grid.store.reload();
                                        if ($.cookie("submit_after") == "1") {
                                            Ext.alert.msg("提示", "保存成功！");
                                            win.close();
                                        } else if (record.get("user_id") == 0) {
                                            Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                            form.getForm().reset();
                                        } else {
                                            Ext.alert.msg("提示", "保存成功！");
                                            var values = form.getForm().getValues(false);
                                            designFormOriginalValue(form, Ext.create("model_sys_orgalloc", values));
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
            width: 420,
            maximizable: false,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit'
        }));
        win.show();
    }

    function createObjectForm(user_id) {
        var form_config = {
            border: false,
            bodyPadding: '20 15 10',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 80}
        };
        var btn_build_loginname = "btn_build_loginname";
        if (!$.cookie(btn_build_loginname)) $.cookie(btn_build_loginname, "2");
        form_config.items = [{xtype: 'hiddenfield', itemId: 'user_id', name: 'user_id'},
            {xtype: 'hiddenfield', itemId: 'org_id', name: 'org_id'}, {
                xtype: 'textfield',
                name: 'user_name',
                itemId: 'user_name',
                fieldLabel: '用户姓名',
                allowBlank: false,
                maxLength: 15,
                beforeLabelTextTpl: '*'
            }, {
                xtype: 'fieldcontainer',
                fieldLabel: '*登录名',
                layout: 'hbox',
                items: [{
                    xtype: 'textfield',
                    name: 'login_name',
                    itemId: 'login_name',
                    hideLabel: true,
                    allowBlank: false,
                    minLength: 2, maxLength: 20,
                    flex: 1, msgTarget: 'under',
                    regex: /^[a-z|A-Z|0-9|_]+$/,
                    regexText: '请输入字母数字下划线',
                    textValid: true,
                    validator: function (value) {
                        if (value == "") return true;
                        return this.textValid;
                    },
                    listeners: {
                        'change': function (textfield, newValue, oldValue) {
                            isExistLoginName(textfield);
                        },
                        'blur': function (textfield, the, oldValue) {
                            isExistLoginName(textfield);
                        }
                    }
                }, {
                    itemId: 'btn_Value',
                    minWidth: 55, frame: true,
                    cls: "x-btn-default-toolbar-small",
                    style: 'margin-left:5px;margin-top:2px',
                    xtype: 'splitbutton', text: '生成',
                    listeners: {
                        click: function () {
                            if ($.cookie(btn_build_loginname) == "1")
                                generateChinaSpell(form.queryById('login_name'), form.queryById('user_name').getValue(), "simple");
                            else
                                generateChinaSpell(form.queryById('login_name'), form.queryById('user_name').getValue(), "full", "true");
                        }
                    },
                    menu: [{
                        text: '简拼生成',
                        checked: $.cookie(btn_build_loginname) == "1",
                        group: btn_build_loginname,
                        xtype: 'menucheckitem',
                        handler: function () {
                            $.cookie(btn_build_loginname, "1");
                            form.queryById('btn_Value').fireEvent('click');
                        }
                    }, {
                        text: '全拼生成',
                        checked: $.cookie(btn_build_loginname) == "2",
                        group: btn_build_loginname,
                        xtype: 'menucheckitem',
                        handler: function () {
                            $.cookie(btn_build_loginname, "2");
                            form.queryById('btn_Value').fireEvent('click');
                        }
                    }]
                }]
            }, {
                xtype: 'checkbox',
                fieldLabel: ' ',
                labelSeparator: "",
                boxLabel: '重置密码',
                name: 'ResertPwd',
                hidden: user_id == 0,
                uncheckedValue: '0',
                inputValue: '1'
            }, {
                xtype: 'radiogroup',
                name: 'user_state',
                itemId: 'user_state',
                fieldLabel: '启用状态',
                allowBlank: true,
                items: [{"boxLabel": "启用", "name": "user_state", "inputValue": "1", "checked": true}, {
                    "boxLabel": "停用",
                    "name": "user_state",
                    "inputValue": "0"
                }],
                columns: [60, 60],
                value: '1',
                beforeLabelTextTpl: '*'
            }];
        var form = Ext.create('Ext.form.Panel', form_config);

        //用户登录名是否存在
        function isExistLoginName(textfield) {
            if (textfield.getValue()) {
                var user_id = form.queryById('user_id').getValue();
                Ext.Ajax.request({
                    method: "POST", url: path_url.system.user.isexist,
                    params: {user_id: user_id, login_name: textfield.getValue()},
                    success: function (response, options) {
                        textfield.textValid = true;
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success && resp.isexist == 1) {
                            textfield.textValid = '该登录名已经存在';
                        }
                        textfield.validate();
                    },
                    failure: function (response, options) {
                        textfield.textValid = true;
                    }
                });
            }
        }

        return form;
    }

    return user_grid;
}

