Ext.define('javascript.menu.system.organize_manage', {
    extend: ''
});

function createPanel_Organize(treenode) {
    var grid_fields = [{"name": "id", "type": "int", "text": "主键"},
        {"name": "pid", "type": "int", "text": "上级单位"},
        {"name": "text", "type": "string", "text": "机构名称"},
        {"name": "company_name", "type": "string", "text": "机构名称"},
        {"name": "nature", "type": "string", "text": "机构性质"},
        {"name": "nature_text", "type": "string", "text": "机构性质_文本值"},
        {"name": "short_name", "type": "string", "text": "机构简称"},
        {"name": "build_time", "type": "string", "text": "成立时间"},
        {"name": "siteurl", "type": "string", "text": "官网"},
        {"name": "email", "type": "string", "text": "电子邮箱"},
        {"name": "phone", "type": "string", "text": "电话"},
        {"name": "fax", "type": "string", "text": "传真"},
        {"name": "leader", "type": "string", "text": "负责人"},
        {"name": "leader_text", "type": "string", "text": "负责人_文本值"},
        {"name": "ismanagers", "type": "int", "text": "是否有管理员"},
        {"name": "postal_code", "type": "string", "text": "邮编"},
        {"name": "province_id", "type": "string", "text": "所在省"},
        {"name": "provinceid_text", "type": "string", "text": "所在省_文本值"},
        {"name": "city_id", "type": "string", "text": "所在市"},
        {"name": "cityid_text", "type": "string", "text": "所在市_文本值"},
        {"name": "county_id", "type": "string", "text": "所在县"},
        {"name": "countyid_text", "type": "string", "text": "所在县_文本值"},
        {"name": "address", "type": "string", "text": "详细地址"},
        {"name": "remark", "type": "string", "text": "备注"},
        {"name": "serialcode", "type": "int", "text": "排序号"}];

    Ext.define('model_sys_organize', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });

    function getNewData(pid) {
        return Ext.create('model_sys_organize', {
            'id': 0,
            'pid': pid
        });
    }

    var item_id = treenode.raw.id;
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: "system/org/tree",
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {expanded: 0}
        },
        listeners: {
            'load': function (store, records) {
                var rootNode = grid.getRootNode();
            }
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: grid_fields
    });
    var columns = [ //new Ext.grid.RowNumberer({ width: 40, tdCls: 'blue' }),
        {
            xtype: 'treecolumn', text: '机构名称', width: 220, fixed: true, dataIndex: 'text'// flex: 1,
        }, {
            text: '机构性质',
            dataIndex: 'nature',
            width: 160,
            fixed: true,
            align: 'left',
            sortable: false,
            renderer: function (value, mata, record) {
                return getComboByIdToText(record, 'nature', value, [], 'company.type');
            }
        }, {
            text: '成立时间',
            dataIndex: 'build_time',
            width: 120,
            fixed: true,
            align: 'left',
            sortable: false
        }, {
            text: '系统管理员',
            dataIndex: 'ismanagers',
            width: 120,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: function (val, meta, rec) {
                if (val != 0) {
                    return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'ismanagers\')">管理员分配</span>';
                } else {
                    return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'ismanagers\')">尚未分配</span>';
                }
            }
        }, {
            text: '负责人',
            dataIndex: 'leader_text',
            width: 120,
            fixed: true,
            align: 'left',
            sortable: false
        }, {
            text: '详细地址',
            dataIndex: 'address',
            width: 200,
            fixed: true,
            //flex: 0.9,
            align: 'left',
            sortable: false
        }, {
            text: '备注',
            dataIndex: 'remark',
            width: 180, flex: 0.8,
            fixed: true,
            align: 'left',
            sortable: false
        }];

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
            method: "POST", url: "system/org/sort",
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
                dragGroup: 'organize_draggroup',
                dropGroup: 'organize_draggroup'
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
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 2
        }],
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
            },
            'cellclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "ismanagers" && itemClickFlag == "ismanagers") {
                    var item_ids = store.proxy.extraParams.item_ids;
                    if (isExistsByItemIds(item_ids, "btn_manager", "管理员分配"))
                        openManagerAllocateTabediting(grid, rec);
                }
            },
            'celldblclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (itemClickFlag != "ismanagers") {
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
                            tooltip: '父机构不能删除',
                            handler: function () {
                                var sel_record = me.sel_record;
                                if (!isExistsByItemId(treenode, "btn_del", "删除")) return false;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/org/delete",
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
            items: ['-', {
                itemId: 'btn_add',
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    openEditWindow(getNewData(0), grid.getRootNode());
                }
            }, '->', '<b>搜索:</b>', {
                itemId: 'text_search', xtype: 'textfield', width: 240, emptyText: "请输入关键字检索，按enter键...",
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == Ext.EventObject.ENTER) {
                            var key_field = store.proxy.extraParams.key_field;
                            if (!key_field) key_field = "company_name";
                            designSearchByField(store, 'key_organize', key_field, field.getValue());
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
                            text: '机构名称',
                            checked: true,
                            group: 'search-group',
                            scope: this,
                            listeners: {
                                click: function () {
                                    setSearchKey(grid, 'key_organize', "company_name", "机构名称");
                                }
                            }
                        },
                        {
                            text: '机构简称',
                            checked: false,
                            group: 'search-group',
                            scope: this,
                            listeners: {
                                click: function () {
                                    setSearchKey(grid, 'key_organize', "short_name", "机构简称");
                                }
                            }
                        },
                        {
                            text: '机构地址',
                            checked: false,
                            group: 'search-group',
                            scope: this,
                            listeners: {
                                click: function () {
                                    setSearchKey(grid, 'key_organize', "address", "机构地址");
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
                                    setSearchKey(grid, 'key_organize', "", "全部");
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
            method: "POST", url: "system/org/info",
            params: {org_id: rec.raw.id},
            success: function (response, options) {
                Ext.getBody().unmask();
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    var formdata = Ext.create("model_sys_organize", resp["organize"]["data"]);
                    formdata.set("id", resp["organize"]["data"].org_id);
                    openEditWindow(formdata, rec.parentNode);
                }
            },
            failure: function (response, options) {
                Ext.getBody().unmask();
            }
        });
    }

    //打开管理员分配Tab
    var tabId = "ManagerAllocateTabediting";

    function openManagerAllocateTabediting(grid, record) {
        var tab = getRightTabPanel(tabId);
        if (!tab) {
            var panel = Ext.create('Ext.panel.Panel', {
                layout: "fit", border: false,
                items: [getOrganizeAdminerGrid(grid, record, treenode)]
            });
            tab = addRightTabPanel({
                itemId: tabId, title: "管理员分配",
                xtype: "panel", iconCls: "",
                closable: true, layout: "fit",
                border: false,
                items: [panel]
            });
        } else {
            var managers_grid = tab.queryById('managers_grid');
            var displayfield = managers_grid.queryById('managers_grid_explain');
            displayfield.setValue('<span style="color:blue;">机构：' + record.get("text") + '</span>');
            managers_grid.store.removeAll(true);
            managers_grid.store.proxy.extraParams.org_id = record.get('id');
            managers_grid.store.record = record;
            managers_grid.store.load();
        }
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
                    var leader = form.queryById("leader");
                    if (leader) leader.setDisabled(record.get("id") == 0);

                    var button = window.queryById("btn_save_form");
                    if (record.get("id") == 0) {
                        if (pid == 0) window.setTitle("机构新增[一级机构]");
                        else window.setTitle(pid_text + "-新增子机构");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_add", ""));
                    } else {
                        window.setTitle("机构修改");
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
                                method: "POST", url: "system/org/save",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false, //如果被置为 true,emptyText值将在form提交时一同发送默认为true
                                params: {item_id: item_id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        grid.store.reload();
                                        if ($.cookie("submit_after") == "1") {
                                            Ext.alert.msg("提示", "保存成功！");
                                            win.close();
                                        }
                                        else if (record.get("id") == 0) {
                                            Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                            form.getForm().reset();
                                        } else {
                                            Ext.alert.msg("提示", "保存成功！");
                                            var values = form.getForm().getValues(false);
                                            designFormOriginalValue(form, Ext.create("model_sys_organize", values));
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
            width: 700,
            height: 440,
            minWidth: 600,
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
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 70}
        };
        form_config.items = [{xtype: 'hiddenfield', name: 'id'}, {
            xtype: 'hiddenfield',
            name: 'pid',
            itemId: 'pid',
            fieldLabel: '上级单位',
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
                    name: 'company_name',
                    itemId: 'company_name',
                    fieldLabel: '机构名称',
                    allowBlank: false,
                    maxLength: 50,
                    beforeLabelTextTpl: '*'
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'combobox',
                    name: 'nature',
                    itemId: 'nature',
                    fieldLabel: '机构性质',
                    allowBlank: false,
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    beforeLabelTextTpl: '*',
                    store: getCodeComboStore('company.type'),
                    editable: false,
                    maxLength: 25,
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
                    name: 'short_name',
                    itemId: 'short_name',
                    fieldLabel: '机构简称',
                    allowBlank: true,
                    maxLength: 50
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'datefield',
                    name: 'build_time',
                    itemId: 'build_time',
                    fieldLabel: '成立时间',
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
                    xtype: 'combobox',
                    name: 'leader',
                    itemId: 'leader',
                    fieldLabel: '负责人',
                    allowBlank: true,
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getSysDataComboStore('ds.sys.user'),
                    editable: false,
                    maxLength: 25,
                    hideEmptyLabel: false,
                    emptyText: '==请选择=='
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'phone',
                    itemId: 'phone',
                    fieldLabel: '电话',
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
                    allowBlank: true,
                    maxLength: 50,
                    vtype: 'email'
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'fax',
                    itemId: 'fax',
                    fieldLabel: '传真',
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
                    name: 'postal_code',
                    itemId: 'postal_code',
                    fieldLabel: '邮编',
                    allowBlank: true,
                    maxLength: 20,
                    regex: /^[1-9]{1}(\d){5}$/,
                    regexText: '请输入正确的邮政编码'
                }]
            }, {
                columnWidth: 0.5,
                layout: 'anchor',
                defaults: {anchor: '99%'},
                items: [{
                    xtype: 'textfield',
                    name: 'siteurl',
                    itemId: 'siteurl',
                    fieldLabel: '官网',
                    allowBlank: true,
                    maxLength: 50,
                    vtype: 'url'
                }]
            }]
        }, {
            xtype: 'textfield',
            name: 'address',
            itemId: 'address',
            fieldLabel: '详细地址',
            style: 'margin-left:1px',
            emptyText: '请输入详细地址',
            allowBlank: true,
            maxLength: 100
        }, {
            xtype: 'textareafield',
            name: 'remark',
            itemId: 'remark',
            fieldLabel: '备注',
            allowBlank: true,
            height: 80,
            rows: 3,
            maxLength: 500,
            flex: 1
        }];
        var form = Ext.create('Ext.form.Panel', form_config);
        return form;
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}

