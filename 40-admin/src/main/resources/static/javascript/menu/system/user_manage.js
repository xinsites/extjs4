Ext.define('javascript.menu.system.user_manage', {
    extend: ''
});

function createPanel_User(treenode) {
    Ext.define('model_sys_user', {
        extend: 'Ext.data.Model',
        idProperty: 'user_id',
        fields: [{"name": "user_id", "type": "int", "text": "主键"},
            {"name": "user_name", "type": "string", "text": "用户姓名"},
            {"name": "login_name", "type": "string", "text": "登录名"},
            {"name": "head_photo", "type": "string", "text": "头像"},
            {"name": "user_sex", "type": "string", "text": "性别"},
            {"name": "org_id", "type": "string", "text": "所属机构"},
            {"name": "org_id_text", "type": "string", "text": "所属机构_文本值"},
            {"name": "role_id", "type": "string", "text": "用户角色"},
            {"name": "role_id_text", "type": "string", "text": "用户角色_文本值"},
            {"name": "dept_id", "type": "string", "text": "用户部门"},
            {"name": "dept_id_text", "type": "string", "text": "用户部门_文本值"},
            {"name": "user_state", "type": "bool", "text": "启用状态"},
            {"name": "birthday", "type": "string", "text": "出生日期"},
            {"name": "email", "type": "string", "text": "电子邮箱"},
            {"name": "phone", "type": "string", "text": "个人手机号"},
            {"name": "post_id", "type": "string", "text": "用户职位"},
            {"name": "post_id_text", "type": "string", "text": "用户职位_文本值"},
            {"name": "remark", "type": "string", "text": "备注"},
            {"name": "oicq", "type": "string", "text": "QQ"},
            {"name": "wechat", "type": "string", "text": "微信"},
            {"name": "workphone", "type": "string", "text": "工作手机号"},
            {"name": "subtelephone", "type": "string", "text": "分机号"},
            {"name": "serialcode", "type": "int", "text": "排序号"},
            {"name": "issys", "type": "int", "text": "是否自带账户"}]
    });

    function getNewData() {
        return Ext.create('model_sys_user', {
            'user_id': 0,
            'user_sex': '男',
            'user_state': 1
        });
    }

    var item_id = treenode.raw.id;
    var pageSize = getGridPageSize(item_id);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "system/user/grid",
            extraParams: {},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'user_id',
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
        model: 'model_sys_user'
    });
    var columns = [new Ext.grid.RowNumberer({width: 40, tdCls: 'blue'}),
        {text: 'user_id', width: 20, dataIndex: 'user_id', hideable: false, hidden: true}, {
            xtype: 'templatecolumn', align: 'center', hideable: false,
            text: '', width: 40, fixed: true, dataIndex: 'head_photo',
            tpl: ['<div class="user-header-img"><img src="{head_photo:htmlEncode}" onerror="this.src=\'images/default_avatar3.png\'"></div>']
        }, {
            text: '用户姓名',
            dataIndex: 'user_name',
            width: 160,
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
            text: '性别',
            dataIndex: 'user_sex',
            width: 100,
            fixed: true,
            align: 'center',
            sortable: true
        }, {
            text: '用户角色',
            dataIndex: 'role_id',
            width: 160,
            fixed: true,
            align: 'center',
            sortable: true,
            renderer: function (value, mata, record) {
                return record.get("role_id_text");
            }
        }, {
            text: '用户部门',
            dataIndex: 'dept_id',
            width: 160,
            fixed: true,
            align: 'center',
            sortable: true,
            renderer: function (value, mata, record) {
                return record.get("dept_id_text");
            }
        }, {
            xtype: 'checkcolumn',
            text: '启用状态',
            width: 120, fixed: true, dataIndex: 'user_state', stopSelection: false
        }, {
            text: '备注',
            dataIndex: 'remark',
            width: 120,
            fixed: false,
            align: 'left',
            sortable: true,
            renderer: function (val, meta, rec) {
                if (rec.get("issys") == "1")
                    return '<span style="color:blue;">[系统内置用户]</span>' + val;
                else
                    return val;
            }
        }];

    function userEditingInfoFn(record, Id, field, value, originalValue) {
        var item_ids = record.store.proxy.extraParams.item_ids;
        if (!isExistsByItemIds(item_ids, "btn_mod", "")) {
            record.set(field, originalValue);
            record.commit();
            return false;
        }
        Ext.Ajax.request({
            method: "POST", url: "system/user/editing",
            params: {item_id: item_id, Id: Id, field: field, value: value},
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    record.commit();
                }
                else {
                    Ext.alert.msg('提示', "保存失败!");
                    record.set(field, originalValue);
                    record.commit();
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "保存失败!");
                record.set(field, originalValue);
                record.commit();
            }
        });
    }

    //用户管理中主表单拖动排序
    function userDragSort(store, first_index) {
        if (store && store.getCount() > 0) {
            var sort_vals = [];
            store.each(function (record) {
                sort_vals[sort_vals.length] = record.get("user_id") + ":" + first_index--;
            });
            Ext.Ajax.request({
                method: "POST", url: "system/user/sort",
                params: {item_id: item_id, sort_vals: sort_vals.join(";")},
                success: function (response, options) {
                    store.load();
                },
                failure: function (response, options) {
                    store.load();
                }
            });
        }
    }

    //删除用户管理中的选中行
    function deleteSelectUser(grid, primarykey) {
        var records = grid.getSelectionModel().getSelection();
        var Ids = [];
        for (var i = 0; i < records.length; i++) {
            Ids[Ids.length] = records[i].get(primarykey);
            if (records[i].get("issys") == "1") {
                Ext.alert.msg('信息提示', (records.length > 1 ? "有" : "") + '系统内置用户，不能删除！');
                return;
            }
        }
        if (Ids.length == 0) {
            Ext.alert.msg('信息提示', '请选择要删除的用户！');
        }
        else {
            Ext.MessageBox.confirm("提示!", "您确定要删除选中的" + Ids.length + "条记录信息吗?", function (btn) {
                if (btn == "yes") {
                    Ext.Ajax.request({
                        method: "POST", url: "system/user/delete",
                        params: {item_id: item_id, ids: Ids.join(",")},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                if (grid.store.getCount() == records.length && grid.store.currentPage > 1)  //当前页全部删除并且不是第一页
                                    grid.store.previousPage();
                                else
                                    grid.store.reload();
                            }
                            else {
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
    }

    //重置用户密码
    function resetPwdTableRow(grid, primarykey) {
        var records = grid.getSelectionModel().getSelection();
        var Ids = [];
        for (var i = 0; i < records.length; i++) {
            Ids[Ids.length] = records[i].get(primarykey);
        }
        if (Ids.length == 0) {
            Ext.alert.msg('信息提示', '请选择要重置密码的用户！');
        }
        else {
            Ext.MessageBox.confirm("提示!", "您确定要重置选中的" + Ids.length + "条记录信息吗?", function (btn) {
                if (btn == "yes") {
                    Ext.Ajax.request({
                        method: "POST", url: "system/user/resetpwd",
                        params: {item_id: item_id, ids: Ids.join(",")},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                Ext.alert.msg('提示', "操作成功!");
                            }
                            else {
                                Ext.alert.msg('提示', "操作失败!");
                            }
                        },
                        failure: function (response, options) {
                            Ext.alert.msg('提示', "操作失败!");
                        }
                    });
                }
            });
        }
    }

    var add_type = "first", first_index = 1;
    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid",
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'grid-row-24-normal';
            },
            loadMask: true,
            preserveScrollOnRefresh: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_sort", "排序")) return false;
                    if (store.sorters.getCount() != 1) {
                        first_index = 1;
                        if (store.getCount() > 0)
                            first_index = store.getAt(0).get("serialcode");
                    } else {
                        Ext.alert.msg('提示', "只有默认排序才可以拖动排序!");
                        return false;
                    }
                },
                drop: function (node, data, dropRec, dropPosition) {
                    userDragSort(store, first_index);
                }
            }
        },
        border: false,
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi",
            checkOnly: false,
            enableKeyNav: true
        }),
        listeners: {
            'celldblclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (itemClickFlag != "user_name") {
                    Ext.getBody().mask('请稍等,正在获取数据...');
                    Ext.Ajax.request({
                        method: "POST", url: path_url.system.user.info,
                        params: {user_id: rec.raw.user_id},
                        success: function (response, options) {
                            Ext.getBody().unmask();
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                var formdata = Ext.create("model_sys_user", resp["user"]["data"]);
                                openEditWindow(formdata, treenode.raw.text);
                            }
                        },
                        failure: function (response, options) {
                            Ext.getBody().unmask();
                        }
                    });
                }
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: 'btn_add',
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add',
                handler: function (btn, pressed) {
                    openEditWindow(getNewData(), treenode.raw.text);
                }
            }, '-', {
                itemId: 'btn_del',
                xtype: 'button', text: '删除',
                minWidth: 60, iconCls: 'icon_delete',
                handler: function (btn, pressed) {
                    deleteSelectUser(grid, "user_id");
                }
            }, '-', {
                itemId: 'btn_reset_pwd',
                xtype: 'button', text: '重置密码',
                minWidth: 60, iconCls: 'icon_resetpwd',
                handler: function (btn, pressed) {
                    resetPwdTableRow(grid, "user_id");
                }
            }, '->', '<b>搜索:</b>',
                {
                    xtype: 'textfield', width: 220, emptyText: "按姓名/登录名检索，按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                designSearchByField(store, 'key_user', "user_name、login_name", field.getValue());
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
                                        gridSearchByDate(store, 'key_user', 0);
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
                                        gridSearchByDate(store, 'key_user', 2);
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
                                        gridSearchByDate(store, 'key_user', 6);
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
                                        gridSearchByDate(store, 'key_user', 30);
                                    }
                                }
                            },
                            {
                                text: '全部', checked: false, group: 'search-group', scope: this, listeners: {
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

    function openEditWindow(record, title) {
        var btn = grid.queryById('btn_add');
        var form = createObjectForm();
        var win_config = {
            title: '添加表单',
            animateTarget: btn.getId(),
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, record);  //初始化信息(新增时初始默认值，修改时填充修改值)
                },
                show: function (window, eOpts) {
                    var button = window.queryById("btn_save_form");
                    if (record.get("user_id") == 0 || !record.get("user_id")) {
                        window.setTitle(title + "-信息新增");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_add", ""));
                    } else {
                        window.setTitle(title + "-信息修改");
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
                                method: "POST", url: "system/user/save",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false,
                                params: {item_id: item_id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        grid.store.reload();
                                        if ($.cookie("submit_after") == "1") {
                                            Ext.alert.msg("提示", "保存成功！");
                                            win.close();
                                        } else if (record.get("user_id") == 0) {
                                            Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                            form.getForm().reset();
                                        } else {
                                            Ext.alert.msg("提示", "保存成功！");
                                            var values = form.getForm().getValues(false);
                                            designFormOriginalValue(form, Ext.create("model_sys_user", values));
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
            width: 640,
            height: 420,
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
            bodyPadding: '20 20 10 10',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'right', msgTarget: 'side', labelWidth: 80}
        };
        var btn_build_loginname = "btn_build_loginname";
        if (!$.cookie(btn_build_loginname)) $.cookie(btn_build_loginname, "2");
        form_config.items = [{xtype: 'hiddenfield', itemId: 'user_id', name: 'user_id'}, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
                    xtype: 'textfield',
                    name: 'user_name',
                    itemId: 'user_name',
                    fieldLabel: '用户姓名',
                    allowBlank: false,
                    maxLength: 15,
                    beforeLabelTextTpl: '*'
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'treepicker',
                    name: 'dept_id',
                    itemId: 'dept_id',
                    fieldLabel: '所在部门',
                    allowBlank: false,
                    multiSelect: false,
                    queryMode: 'remote',
                    selectMode: 'all',
                    store: getSysDataTreeStore('ds.sys.dept', false),
                    editable: false,
                    emptyText: '==请选择=='
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
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
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'singlecombobox',
                    name: 'role_id',
                    itemId: 'role_id',
                    fieldLabel: '用户角色',
                    allowBlank: true,
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getSysDataComboStore('ds.sys.role'),
                    editable: false,
                    maxLength: 50,
                    emptyText: '==请选择=='
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
                    xtype: 'radiogroup',
                    name: 'user_sex',
                    itemId: 'user_sex',
                    fieldLabel: '性别',
                    allowBlank: false,
                    items: [{"boxLabel": "男", "name": "user_sex", "inputValue": "男", "checked": true}, {
                        "boxLabel": "女",
                        "name": "user_sex",
                        "inputValue": "女"
                    }],
                    columns: [50, 50],
                    beforeLabelTextTpl: '*'
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'datefield',
                    name: 'birthday',
                    itemId: 'birthday',
                    fieldLabel: '出生日期',
                    allowBlank: true,
                    format: 'Y-m-d',
                    editable: false
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
                    xtype: 'singlecombobox',
                    name: 'post_id',
                    itemId: 'post_id',
                    fieldLabel: '用户职位',
                    allowBlank: true,
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    disableCls: "",
                    store: getCodeComboStore("work.post", {"id": "0", "name": "===请选择==="}),
                    editable: false,
                    maxLength: 50,
                    emptyText: '==请选择=='
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'email',
                    itemId: 'email',
                    fieldLabel: '电子邮箱',
                    allowBlank: true,
                    maxLength: 50,
                    vtype: 'email'
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
                    xtype: 'textfield',
                    name: 'workphone',
                    itemId: 'workphone',
                    fieldLabel: '工作手机号',
                    allowBlank: true,
                    maxLength: 15
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'phone',
                    itemId: 'phone',
                    fieldLabel: '个人手机号',
                    allowBlank: true,
                    maxLength: 15
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
                    xtype: 'textfield',
                    name: 'oicq',
                    itemId: 'oicq',
                    fieldLabel: 'QQ',
                    allowBlank: true,
                    maxLength: 20
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'wechat',
                    itemId: 'wechat',
                    fieldLabel: '微信',
                    allowBlank: true,
                    maxLength: 20
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            defaults: {border: false},
            items: [{
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '95%'},
                items: [{
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
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'subtelephone',
                    itemId: 'subtelephone',
                    fieldLabel: '分机号',
                    allowBlank: true,
                    maxLength: 50
                }]
            }]
        }, {
            xtype: 'textareafield',
            name: 'remark',
            itemId: 'remark',
            fieldLabel: '备注',
            allowBlank: true,
            height: 50,
            rows: 3,
            maxLength: 200,
            flex: 1
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

    var userSearchWin;

    function openSearchWin() {
        if (!userSearchWin) {
            var btn = grid.queryById("btn_complexsearch");
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
                    xtype: 'container',
                    layout: 'column',
                    defaults: {border: false},
                    items: [{
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '95%'},
                        items: [{
                            xtype: 'textfield',
                            name: 'user_name',
                            itemId: 'user_name',
                            fieldLabel: '用户姓名',
                            maxLength: 15
                        }]
                    }, {
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '99%'},
                        items: [{
                            xtype: 'treepicker',
                            name: 'dept_id',
                            itemId: 'dept_id',
                            fieldLabel: '所在部门',
                            multiSelect: false,
                            queryMode: 'remote',
                            selectMode: 'all',
                            store: getSysDataTreeStore('ds.sys.dept', false),
                            editable: false,
                            emptyText: '==请选择=='
                        }]
                    }]
                }, {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {border: false},
                    items: [{
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '95%'},
                        items: [{
                            xtype: 'textfield',
                            name: 'login_name',
                            itemId: 'login_name',
                            fieldLabel: '登录名'
                        }]
                    }, {
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '99%'},
                        items: [{
                            xtype: 'combobox',
                            name: 'role_id',
                            itemId: 'role_id',
                            fieldLabel: '用户角色',
                            valueField: 'id',
                            displayField: 'name',
                            queryMode: 'remote',
                            store: getSysDataComboStore('ds.sys.role'),
                            editable: false,
                            maxLength: 50,
                            emptyText: '==请选择=='
                        }]
                    }]
                }, {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {border: false},
                    items: [{
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '95%'},
                        items: [{
                            xtype: 'combobox',
                            name: 'post_id',
                            itemId: 'post_id',
                            fieldLabel: '用户职位',
                            valueField: 'id',
                            displayField: 'name',
                            queryMode: 'remote',
                            store: getCodeComboStore("work.post", {"id": "0", "name": "===请选择==="}),
                            editable: false,
                            maxLength: 50,
                            emptyText: '==请选择=='
                        }]
                    }, {
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '99%'},
                        items: [{
                            xtype: 'textfield',
                            name: 'workphone',
                            itemId: 'workphone',
                            fieldLabel: '工作手机号',
                            allowBlank: true,
                            maxLength: 15
                        }]
                    }]
                }, {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {border: false},
                    items: [{
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '95%'},
                        items: [{
                            xtype: 'textfield',
                            name: 'email',
                            itemId: 'email',
                            fieldLabel: '电子邮箱',
                            maxLength: 50,
                            vtype: 'email'
                        }]
                    }, {
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '99%'},
                        items: [{
                            xtype: 'textfield',
                            name: 'phone',
                            itemId: 'phone',
                            fieldLabel: '个人手机号',
                            maxLength: 15
                        }]
                    }]
                }, {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {border: false},
                    items: [{
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '95%'},
                        items: [{
                            xtype: 'radiogroup',
                            name: 'user_state',
                            itemId: 'user_state',
                            fieldLabel: '启用状态',
                            items: [{"boxLabel": "启用", "name": "user_state", "inputValue": "1"}, {
                                "boxLabel": "停用",
                                "name": "user_state",
                                "inputValue": "0"
                            }],
                            columns: [60, 60]
                        }]
                    }, {
                        columnWidth: 0.5,
                        layout: 'anchor',
                        defaults: {anchor: '99%'},
                        items: [{
                            xtype: 'radiogroup',
                            name: 'user_sex',
                            itemId: 'user_sex',
                            fieldLabel: '性别',
                            items: [{"boxLabel": "男", "name": "user_sex", "inputValue": "男"}, {
                                "boxLabel": "女",
                                "name": "user_sex",
                                "inputValue": "女"
                            }],
                            columns: [50, 50]
                        }]
                    }]
                }, {
                    xtype: 'textareafield',
                    name: 'remark',
                    itemId: 'remark',
                    fieldLabel: '备注',
                    height: 50,
                    rows: 3,
                    maxLength: 200,
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
            var win_config = {
                title: '用户查询',
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
                        }
                    }
                }, {
                    text: '关闭', minWidth: 70,
                    handler: function () {
                        userSearchWin.close();
                    }
                }]
            }

            function search() {
                var items = [];
                items[items.length] = {item_id: "user_name", operator: "like"};
                items[items.length] = "dept_id";
                items[items.length] = {item_id: "login_name", operator: "like"};
                items[items.length] = "role_id";
                items[items.length] = "post_id";
                items[items.length] = {item_id: "workphone", operator: "like"};
                items[items.length] = {item_id: "email", operator: "like"};
                items[items.length] = {item_id: "phone", operator: "like"};
                items[items.length] = "user_state";
                items[items.length] = "user_sex";
                items[items.length] = {item_id: "remark", operator: "like"};
                searchSingleTableReLoad(grid, designSearchByForm(form, items), "key_user");
                userSearchWin.close();
            }

            userSearchWin = Ext.create('widget.window', Ext.apply(win_config, {
                width: 610,
                height: 340,
                closable: true,
                closeAction: 'hide',
                plain: false,
                modal: true,
                layout: 'fit'
            }));
        }
        userSearchWin.show();
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}

