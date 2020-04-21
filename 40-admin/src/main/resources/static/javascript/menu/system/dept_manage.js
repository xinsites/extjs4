Ext.define('javascript.menu.system.dept_manage', {
    extend: ''
});

function createPanel_Dept(treenode) {
    var grid_fields = [{"name": "id", "type": "int", "text": "主键"},
        {"name": "pid", "type": "int", "text": "上级单位"},
        {"name": "text", "type": "string", "text": "部门名称"},
        {"name": "dept_name", "type": "string", "text": "部门名称"},
        {"name": "dept_code", "type": "string", "text": "部门编号"},
        {"name": "dept_type", "type": "string", "text": "部门性质"},
        {"name": "dept_type_text", "type": "string", "text": "部门性质_文本值"},
        {"name": "dept_leader", "type": "string", "text": "部门领导"},
        {"name": "dept_leader_text", "type": "string", "text": "部门领导_文本值"},
        {"name": "dept_person", "type": "string", "text": "部门负责人"},
        {"name": "dept_person_text", "type": "string", "text": "部门负责人_文本值"},
        {"name": "dept_short_name", "type": "string", "text": "部门简称"},
        {"name": "dept_phone", "type": "string", "text": "电话号码"},
        {"name": "dept_fax", "type": "string", "text": "传真"},
        {"name": "dept_remark", "type": "string", "text": "备注"},
        {"name": "serialcode", "type": "int", "text": "排序号"},
        {"name": "create_time", "type": "date", "text": "创建时间"},
        {"name": "modify_time", "type": "date", "text": "修改时间"}];

    Ext.define('model_sys_dept', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });

    function getNewData(pid) {
        return Ext.create('model_sys_dept', {
            'id': 0,
            'pid': pid,
            'dept_type': '1',
            'dept_type_text': '常规部门'
        });
    }

    var item_id = treenode.raw.id;
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: "system/dept/tree",
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {expanded: 0}
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: grid_fields
    });
    var columns = [{
        xtype: 'treecolumn',
        text: '部门名称',
        width: 230,
        fixed: true,
        dataIndex: 'text', // flex: 1.2,
        editor: {
            allowBlank: false, minLength: 2, maxLength: 50,
            xtype: 'textfield', selectOnFocus: false //点击编辑框后，变成全选状态 
        }
    }, {
        text: '部门编号',
        dataIndex: 'dept_code',
        width: 150, //flex: 0.5,
        fixed: true,
        align: 'left',
        sortable: true
    }, {
        text: '部门类型',
        dataIndex: 'dept_type',
        width: 120, //flex: 0.4,
        fixed: true,
        align: 'left',
        sortable: true,
        renderer: function (value, mata, record) {
            return getComboByIdToText(record, 'dept_type', value, [], 'dept.type');
        }
    }, {
        text: '部门领导',
        dataIndex: 'dept_leader_text',
        width: 120, //flex: 0.6,
        fixed: true,
        align: 'left',
        sortable: true,
        renderer: function (val, meta, rec) {
            if (val != "")
                return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'dept_leader\')">' + val + '</span>';
            else
                return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'dept_leader\')">尚未配置</span>';
        }
    }, {
        text: '部门负责人',
        dataIndex: 'dept_person_text',
        width: 90, //flex: 0.6,
        fixed: false,
        align: 'left',
        sortable: true,
        renderer: function (val, meta, rec) {
            if (val != "")
                return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'dept_person\')">' + val + '</span>';
            else
                return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'dept_person\')">尚未配置</span>';
        }
    }, {
        text: '备注',
        dataIndex: 'dept_remark',
        width: 180, //flex: 0.8,
        fixed: false,
        align: 'left',
        sortable: true
    }];

    //单元格编辑
    function editingInfoFn(record, Id, field, value, originalValue) {
        Ext.Ajax.request({
            method: "POST", url: "system/dept/editing",
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

    //动态树拖动保存排序
    function saveDragSortFn(parent_record) {
        var childNodes = parent_record.childNodes;
        var str = [];
        for (var i = 0, len = childNodes.length; i < len; i++) {
            var record = childNodes[i];
            record.set("pid", parent_record.get("id"));
            str[str.length] = record.get("id") + ":" + record.get("pid") + ":" + (i + 1);
        }
        Ext.Ajax.request({
            method: "POST", url: "system/dept/sort",
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

    var grid = Ext.create('Ext.tree.Panel', {
        itemId: "main_grid",
        store: store,
        useArrows: false,
        rowLines: true,
        forceFit: true,
        emptyText: "没有数据!",
        bodyCls: 'grid_empty_text_parent',
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'treeviewdragdrop',
                allowLeafInserts: true, //叶子节点是否可拖动的配置，默认值为true
                containerScroll: true,
                dragGroup: 'dept_draggroup',
                dropGroup: 'dept_draggroup'
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
                    saveDragSortFn(data.records[0].parentNode);
                }
            }
        },
        autoScroll: true, rootVisible: false,
        // plugins: [{
        //     ptype: 'cellediting',
        //     clicksToEdit: 2,
        //     onSpecialKey: cellediting_onSpecialKey
        // }],
        selModel: Ext.create("Ext.selection.RowModel", {
            mode: "single",
            enableKeyNav: true,
            onEditorTab: function (editingPlugin, e) {
                cellediting_onEditorTab(editingPlugin, this, e, false);
            }
        }),
        listeners: {
            // beforeedit: function (editor, e) {
            //     if (!isExistsByItemId(treenode, "btn_mod", "编辑")) return false;
            //     return true;
            // },
            // edit: function (editor, e) {
            //     grid.plugins[0].completeEdit();
            //     var record = e.record;
            //     var ischange = e.originalValue + "" != e.value + "";
            //     if (ischange) {
            //         var value = e.value;
            //         editingInfoFn(record, record.get("id"), e.field, value, e.originalValue);
            //     }
            // },
            "itemclick": function (treeview, record, item, index, e) {
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
            },
            'cellclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "dept_leader_text" && itemClickFlag == "dept_leader") {
                    if (isExistsByItemId(treenode, "btn_member", "部门领导分配")) {
                        var Json = {
                            win_title: "部门领导分配",
                            grid: grid,
                            btn_id: "",
                            table_name: "sys_dept",
                            table_id: rec.get("id"),
                            man_type: 1
                        };
                        openMemberUserSelectFn(Json, rec.get("dept_leader"));
                    }
                }
                else if (header.dataIndex == "dept_person_text" && itemClickFlag == "dept_person") {
                    if (isExistsByItemId(treenode, "btn_member", "部门负责人分配")) {
                        var Json = {
                            win_title: "部门负责人分配",
                            grid: grid,
                            btn_id: "",
                            table_name: "sys_dept",
                            table_id: rec.get("id"),
                            man_type: 2
                        };
                        openMemberUserSelectFn(Json, rec.get("dept_person"));
                    }
                }
            },
            'celldblclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                if (itemClickFlag != "dept_leader" && itemClickFlag != "dept_person") {
                    dbClickModInfo(rec);
                }
            },
            'itemcontextmenu': function (tree, sel_record, item, index, e, eOpts) {
                var me = this;
                //禁用浏览器的右键相应事件  
                e.preventDefault();
                e.stopEvent();
                me.sel_record = sel_record;
                if (!me.ctxMenu) {
                    me.ctxMenu = new Ext.menu.Menu({
                        floating: true,
                        items: [{
                            itemId: "add",
                            text: "添加",
                            disabled: !isExistsByItemId(treenode, "btn_add", ""),
                            iconCls: 'icon_add',
                            handler: function () {
                                var sel_record = me.sel_record;
                                var pid = sel_record.get("id");
                                sel_record.expand(false);
                                openEditWindow(getNewData(pid), sel_record);
                            }
                        }, {
                            itemId: "mod",
                            text: "修改",
                            disabled: !isExistsByItemId(treenode, "btn_mod", ""),
                            iconCls: 'icon_edit',
                            handler: function () {
                                var sel_record = me.sel_record;
                                dbClickModInfo(sel_record);
                            }
                        }, {
                            itemId: "delete",
                            text: "删除",
                            disabled: !isExistsByItemId(treenode, "btn_del", ""),
                            iconCls: 'icon_delete',
                            tooltip: '父部门不能删除',
                            handler: function () {
                                var sel_record = me.sel_record;
                                if (!isExistsByItemId(treenode, "btn_del", "删除")) return false;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/dept/delete",
                                            params: {item_id: item_id, Id: sel_record.get("id")},
                                            success: function (response, options) {
                                                var resp = Ext.JSON.decode(response.responseText);
                                                if (resp.success) {
                                                    var parent_record = sel_record.parentNode; //先获取结点的父结点
                                                    sel_record.remove(true);
                                                    if (parent_record) {
                                                        if (parent_record.childNodes.length == 0) {
                                                            parent_record.set("leaf", true);
                                                        }
                                                    }
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
                        }]
                    });
                }
                var ctxMenu = me.ctxMenu;
                var deleteItem = ctxMenu.getComponent("delete");
                if (isExistsByItemId(treenode, "btn_del", ""))
                    deleteItem.setDisabled(!sel_record.get('leaf'));
                ctxMenu.showAt(e.getXY()); //让右键菜单跟随鼠标位置  
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'btn_add',
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    openEditWindow(getNewData(0), grid.getRootNode());
                }
            },
                '->', '<b>搜索:</b>', {
                    itemId: 'text_search', xtype: 'textfield', width: 220, emptyText: "请输入关键字检索，按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                var key_field = store.proxy.extraParams.key_field;
                                if (!key_field) key_field = "dept_name";
                                designSearchByField(store, 'key_dept', key_field, field.getValue());
                            }
                        }
                    }
                }, {
                    xtype: 'splitbutton', text: '选择条件',
                    itemId: "btn_search", iconCls: "icon_search",
                    handler: function (button, e) {
                        setTimeout(function () {
                            button.showMenu();
                        }, 70);
                    },
                    menu: {
                        items: [
                            {
                                text: '部门名称',
                                checked: true,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        setSearchKey(grid, 'key_dept', "dept_name", "部门名称");
                                    }
                                }
                            },
                            {
                                text: '部门简称',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        setSearchKey(grid, 'key_dept', "dept_short_name", "部门简称");
                                    }
                                }
                            },
                            {
                                text: '部门编号',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        setSearchKey(grid, 'key_dept', "dept_code", "部门编号");
                                    }
                                }
                            },
                            {
                                text: '全部',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        setSearchKey(grid, 'key_dept', "", "全部");
                                    }
                                }
                            }]
                    }
                }, '  ']
        }]
    });

    function dbClickModInfo(rec) {
        Ext.getBody().mask('请稍等,正在获取数据...');
        Ext.Ajax.request({
            method: "POST", url: "system/dept/info",
            params: {dept_id: rec.raw.id},
            success: function (response, options) {
                Ext.getBody().unmask();
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    var formdata = Ext.create("model_sys_dept", resp["dept"]["data"]);
                    formdata.set("id", resp["dept"]["data"].dept_id);
                    openEditWindow(formdata, rec.parentNode);
                }
            },
            failure: function (response, options) {
                Ext.getBody().unmask();
            }
        });
    }

    function openEditWindow(record, parent_record) {
        var btn = grid.queryById('btn_add');
        var form = createObjectForm();
        var win_config = {
            title: '添加表单',
            animateTarget: btn.getId(),
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, record);  //初始化信息(新增时初始默认值，修改时填充修改值)
                    if (record.get("id") == 0) form.getForm().reset();
                },
                show: function (window, eOpts) {
                    var pid = parent_record.get("id"), pid_text = parent_record.get("text");
                    var button = window.queryById("btn_save_form");
                    if (record.get("id") == 0) {
                        if (pid == 0) window.setTitle("部门新增[一级部门]");
                        else window.setTitle(pid_text + "-新增子部门");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_add", ""));
                    } else {
                        window.setTitle("部门修改");
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
                                method: "POST", url: "system/dept/save",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false,
                                params: {item_id: item_id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        grid.store.reload();
                                        if ($.cookie("submit_after") == "1") {
                                            Ext.alert.msg("提示", "保存成功！");
                                            win.close();
                                        } else if (record.get("dept_id") == 0) {
                                            Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                            form.getForm().reset();
                                        } else {
                                            Ext.alert.msg("提示", "保存成功！");
                                            var values = form.getForm().getValues(false);
                                            designFormOriginalValue(form, Ext.create("model_sys_dept", values));
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
            width: 600,
            height: 340,
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
            bodyPadding: '15 15 10 15',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 80}
        };
        form_config.items = [{xtype: 'hiddenfield', name: 'id'}, {
            xtype: 'hiddenfield',
            name: 'pid',
            itemId: 'pid',
            fieldLabel: '上级部门',
            allowBlank: false
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
                    name: 'dept_name',
                    itemId: 'dept_name',
                    fieldLabel: '部门名称',
                    allowBlank: false,
                    maxLength: 50,
                    beforeLabelTextTpl: '*'
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'dept_code',
                    itemId: 'dept_code',
                    fieldLabel: '部门编号',
                    allowBlank: true,
                    maxLength: 25
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
                    name: 'dept_short_name',
                    itemId: 'dept_short_name',
                    fieldLabel: '部门简称',
                    allowBlank: true,
                    maxLength: 50
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'combobox',
                    name: 'dept_type',
                    itemId: 'dept_type',
                    fieldLabel: '部门性质',
                    allowBlank: true,
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getCodeComboStore('dept.type'),
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
                    xtype: 'textfield',
                    name: 'dept_phone',
                    itemId: 'dept_phone',
                    fieldLabel: '电话号码',
                    allowBlank: true,
                    maxLength: 20
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'dept_fax',
                    itemId: 'dept_fax',
                    fieldLabel: '传真',
                    allowBlank: true,
                    maxLength: 25
                }]
            }]
        }, {
            xtype: 'textareafield',
            name: 'dept_remark',
            itemId: 'dept_remark',
            fieldLabel: '备注',
            allowBlank: true,
            height: 50,
            rows: 3,
            maxLength: 200,
            flex: 1
        }];
        return Ext.create('Ext.form.Panel', form_config);
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}

//打开用户选择器
function openDeptLeaderSelectFn(grid, btn, record, type) {
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
    var UserIds = record.get("dept_leader");
    if (type == 2) UserIds = record.get("dept_person");
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            url: path_url.system.member.deptusers,
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {dept_id: record.get("id")}
        },
        model: 'model_dept_user',
        listeners: {
            'load': function (store, records) {
                var data_view = right_panel.queryById('data_view');
                var selection = data_view.getSelectionModel();
                Ext.Array.each(records, function (record, index) {
                    Ext.Array.each(UserIds.split(","), function (id, index) {
                        if (id == record.get("id"))
                            selection.select(record, true, true);
                    });
                });
            }
        }
    });
    var select_count = UserIds.split(",").length;
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
            simpleSelect: type == 1 ? false : true, //多选，而不需要用户按住Shift或Ctrl键
            multiSelect: true,
            trackOver: true,
            overItemCls: 'x-item-over',
            itemSelector: 'div.card-box',
            emptyText: '暂无用户',
            prepareData: function (data) {
                Ext.apply(data, {
                    //RoleIdCss: data.role_id == record.get("role_id") ? "x-item-selected" : ""
                });
                return data;
            },
            listeners: {
                selectionchange: function (dv, nodes) {
                    select_count = nodes.length;
                    //this.up('panel').setTitle('数据视图 (' + l + ' 项项选中)');
                },
                beforeselect: function (dv, node, selections, ept) {
                    var data_view = right_panel.queryById('data_view');
                    var selection = data_view.getSelectionModel();
                    if (selection.getSelection().length == 0) select_count = 0;
                    if (select_count == 2 && type == 1) {
                        Ext.alert.msg('信息提示', '最多选择2个部门领导！');
                        return false;
                    } else if (select_count == 3 && type == 2) {
                        Ext.alert.msg('信息提示', '最多选择3个部门负责人！');
                        return false;
                    }
                    return true;
                }
            }
        })
    });
    var wind = Ext.create('widget.window', {
        title: record.get("text") + "--" + (type == 1 ? "部门领导" : "部门负责人"),
        width: 630, height: 500,
        closable: true, autoShow: true,
        maximizable: true,
        closeAction: 'destroy',  //destroy，hide
        plain: false, modal: true,
        layout: 'fit',
        items: [right_panel],
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
                    var data_view = right_panel.queryById('data_view');
                    var selection = data_view.getSelectionModel();
                    Ext.Array.each(selection.getSelection(), function (record, index) {
                        id_vals[id_vals.length] = record.get("id");
                    });
                    Ext.Ajax.request({
                        method: "POST", url: path_url.system.member.save,
                        params: {
                            IdVal: id_vals.join(","), table_name: "sys_dept", table_id: record.get("id"), man_type: type
                        },
                        success: function (response, options) {
                            UserIds = id_vals.join(",");
                            grid.store.reload();
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
