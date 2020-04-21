Ext.define('javascript.menu.system.code_manage', {
    extend: ''
});

//编码管理
function createPanel_CodeManage(treenode) {
    var item_id = treenode.raw.id, TypeText = "";
    var code_type_fields = [{name: 'id', type: 'int'},
        {name: 'text', type: 'string'},
        {name: 'pid', type: 'int'},
        {name: 'ispublic', type: 'string'},
        {name: 'data_key', type: 'string'},
        {name: 'expand', type: 'string'},
        {name: 'istree', type: 'string'},
        {name: 'issys', type: 'int'},
        {name: 'serialcode', type: 'int'}];

    Ext.define('model_sys_code_type', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: code_type_fields
    });
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: "system/code/type/tree",
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {id: 0}
        },
        root: {text: '编码类型', id: 0, pid: 0, expanded: true},
        fields: code_type_fields,
        listeners: {
            'load': function (store, record) {
                Ext.Array.each(record.childNodes, function (record, index) {
                    if (record.get("expand") == "true") {
                        if (!record.get('leaf') && !record.isExpanded()) {
                            setTimeout(function () {
                                grid.getView().expand(record, false);
                            }, 50);
                        }
                    }
                });
                setTimeout(function () {
                    var rec = store.getNodeById(grid.store.proxy.extraParams.codetype_id);
                    if (rec) tree.getView().getSelectionModel().select(rec)
                }, 100);
            }
        }
    });

    //新增树结点
    function addTreeNodeFn(tree, parent_record, animateTarget) {
        var childNodes = parent_record.childNodes;
        var record = parent_record.copy(rndNum(4), false);
        record.set("id", 0);
        record.set("pid", parent_record.get("id"));
        record.set("text", '新建分类');
        record.set("serialcode", childNodes.length + 1);
        record.set("ispublic", "0");
        record.set("istree", "0");
        record.set("leaf", true);
        record.set("allowDrag", true);
        record.set("data_key", "");
        record.parentNode = parent_record;
        openCodeTypeWinFn(tree, record, "add", record, animateTarget);
    }

    //动态树拖动保存排序
    function saveCodeTypeSortFn(parent_record) {
        var childNodes = parent_record.childNodes;
        var str = [];
        for (var i = 0, len = childNodes.length; i < len; i++) {
            var record = childNodes[i];
            record.set("pid", parent_record.get("id"));
            str[str.length] = record.get("id") + ":" + record.get("pid") + ":" + (i + 1);
        }
        Ext.Ajax.request({
            method: "POST", url: "system/code/type/sort",
            params: {item_id: item_id, sort_vals: str.join(";")},
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    //Ext.alert.msg('提示', "操作成功!");
                }
                else {
                    Ext.alert.msg('提示', "排序失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "排序失败!");
            }
        });
    }

    var tree = Ext.create('Ext.tree.Panel', {
        itemId: "main_grid",
        store: tree_store,
        useArrows: true,
        rowLines: false, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        viewConfig: {
            getRowClass: function () {
                return 'object_tree_panel_row_height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'treeviewdragdrop',
                allowLeafInserts: true, //叶子节点是否可拖动的配置，默认值为true
                containerScroll: true,
                dragGroup: 'codetype_group1',
                dropGroup: 'codetype_group1'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_type_sort", "排序")) return false;
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
                    saveCodeTypeSortFn(data.records[0].parentNode);
                }
            }
        },
        columns: [{
            xtype: 'treecolumn', text: '栏目名称', dataIndex: 'text', flex: 1,
            renderer: function (val, meta, rec) {
                if (rec.get("istree") == "1") return val + "[树]";
                else return val;
            }
        }],
        listeners: {
            'render': function (tree, eOpts) {
                // tree.store.on("load", function (treeview, record) {
                //     if (record.get("id") == "0") {  //首次加载
                //         setButtonDisabled(true);
                //         code_panel.queryById('code_grid').setTitle("数据编码-未选择分类");
                //         grid.store.proxy.extraParams.codetype_id = "0";
                //         grid.store.proxy.extraParams.data_key = "";
                //         grid.store.load();
                //     }
                // });
                tree.store.on("expand", function (record) {
                    if (record.get('action_type') == "add") {
                        addTreeNodeFn(tree, record);
                    }
                });
            },
            "itemclick": function (treeview, record, item, index, e) {
                treeview.toggleOnDblClick = false; //取消双击展开折叠菜单行为
                setTimeout(function () {
                    TypeText = record.get('text');
                    code_panel.queryById('code_grid').setTitle("数据编码-" + TypeText);
                    grid.queryById('text_search').reset();
                    grid.store.proxy.extraParams.codetype_id = record.get('id');
                    grid.store.proxy.extraParams.data_key = record.get('data_key');
                    grid.store.proxy.extraParams.text = "";
                    grid.store.proxy.extraParams.istree = record.get('istree');
                    grid.getView().plugins.allowLeafInserts = record.get('istree') == "1";
                    setButtonDisabled(false);
                    //grid.store.reload();
                    refreshTreeNode(grid);
                }, 100);
                if (!record.get('leaf') && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
            },
            'itemcontextmenu': function (tree, sel_record, item, index, e, eOpts) {
                var me = this;
                tree.select(sel_record);
                //禁用浏览器的右键相应事件  
                e.preventDefault();
                e.stopEvent();
                me.sel_record = sel_record;
                var childNodes = sel_record.childNodes;
                var child_count = childNodes.length;
                if (!me.ctxMenu) {
                    me.ctxMenu = new Ext.menu.Menu({
                        floating: true,
                        items: [{
                            itemId: "add",
                            text: "添加",
                            disabled: !isExistsByItemId(treenode, "btn_type_add", ""),
                            iconCls: 'icon_add',
                            handler: function () {
                                var sel_record = me.sel_record;
                                if (!sel_record.get('leaf') && child_count == 0) {  //不是叶子结点，子结点数为0，说明该结点还没加载
                                    Ext.alert.msg('提示', "等待加载完成！");
                                    sel_record.set('action_type', "add");   //标记在展开后，添加结点
                                    tree.expand(sel_record, false);
                                    return;
                                }
                                sel_record.expand(false);
                                addTreeNodeFn(tree, sel_record);
                            }
                        }, {
                            itemId: "mod",
                            text: "修改",
                            disabled: !isExistsByItemId(treenode, "btn_type_mod", ""),
                            iconCls: 'icon_edit',
                            handler: function () {
                                var sel_record = me.sel_record;
                                Ext.getBody().mask('请稍等,正在获取数据...');
                                Ext.Ajax.request({
                                    method: "POST", url: "system/code/type/info",
                                    params: {id: sel_record.get("id")},
                                    success: function (response, options) {
                                        Ext.getBody().unmask();
                                        var resp = Ext.JSON.decode(response.responseText);
                                        if (resp.success) {
                                            var data = resp["code_type"]["data"];
                                            var formdata = Ext.create("model_sys_code_type", data);
                                            formdata.set("text", data.name);
                                            formdata.set("expand", data.expanded);
                                            openCodeTypeWinFn(tree, formdata, "mod", sel_record);
                                        }
                                    },
                                    failure: function (response, options) {
                                        Ext.getBody().unmask();
                                    }
                                });
                            }
                        }, {
                            itemId: "delete",
                            text: "删除",
                            disabled: !isExistsByItemId(treenode, "btn_type_del", ""),
                            tooltip: '父结点与系统分类不能删除',
                            iconCls: 'icon_delete',
                            handler: function () {
                                var sel_record = me.sel_record;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/code/type/delete",
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
                if (isExistsByItemId(treenode, "btn_type_del", ""))
                    deleteItem.setDisabled(!sel_record.get('leaf') || sel_record.get('issys') == 1);
                ctxMenu.showAt(e.getXY()); //让右键菜单跟随鼠标位置  
            }
        }
    });

    function openCodeTypeWinFn(tree, formdata, action_type, record, animateTarget) {
        var form = Ext.create('Ext.form.Panel', {
            frame: false, bodyPadding: '15 15 5',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 80,
                anchor: '100%'
            },
            items: [{
                xtype: 'hiddenfield',
                name: 'id'
            }, {
                xtype: 'hiddenfield',
                name: 'pid'
            }, {
                xtype: 'textfield',
                itemId: 'text',
                name: 'text',
                maxLength: 50,
                fieldLabel: '分类名称',
                allowBlank: false
            }, {
                xtype: 'textfield',
                itemId: 'data_key',
                name: 'data_key',
                maxLength: 20,
                fieldLabel: '编码分类Key',
                allowBlank: false
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
                    boxLabel: '公共编码',
                    name: 'ispublic',
                    inputValue: '1'
                }, {
                    flex: 0, width: 20,
                    xtype: 'displayfield',
                    value: ''
                }, {
                    xtype: 'checkbox',
                    fieldLabel: '',
                    labelSeparator: "",
                    boxLabel: '默认展开',
                    name: 'expand',
                    uncheckedValue: 'false',
                    inputValue: 'true'
                }]
            }, {
                xtype: 'displayfield',
                hideLabel: true,
                style: 'margin-top:8px',
                value: '<span style="color:blue;">1、公共编码在所有组织单位都可使用。<br/>2、编码分类Key必须唯一，使用于“生成代码”下拉数据源。</span>'
            }
            ]
        });
        var parent_record = record.parentNode;
        var window = Ext.create('Ext.window.Window', {
            title: "标题", width: 380,
            resizable: false, closeAction: 'destroy',
            closable: false, modal: 'true',  // 弹出模态窗体  
            animateTarget: animateTarget,
            buttonAlign: "center", items: [form],
            listeners: {
                "show": function () {
                    designFormOriginalValue(form, formdata);
                    var button = window.queryById("btn_save_form");
                    if (action_type == "add") {
                        window.setTitle("编码分类新增");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_type_add", ""));
                    } else {
                        window.setTitle("编码分类修改");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_type_mod", ""));
                    }
                }
            },
            buttonAlign: "right",
            buttons: [{
                itemId: "btn_save_form",
                text: "保存", minWidth: 70,
                handler: function () {
                    if (form.getForm().isValid()) {
                        var Values = form.getForm().getValues(false);
                        var text = Values["text"];
                        var ispublic = Values["ispublic"];
                        var istree = Values["istree"];
                        var issys = Values["issys"];
                        var data_key = Values["data_key"];
                        form.getForm().submit({
                            method: "POST", url: "system/code/type/save",
                            waitTitle: '请稍等...', waitMsg: '正在提交信息...',
                            params: {item_id: item_id},
                            success: function (form, action) {   //成功后
                                var flag = action.result.success;
                                if (flag) {
                                    window.close();
                                    record.set("text", text);
                                    record.set("ispublic", ispublic);
                                    record.set("istree", istree);
                                    record.set("issys", issys);
                                    record.set("data_key", data_key);
                                    grid.store.proxy.extraParams.istree = istree;
                                    if (ispublic == "1") record.set("iconCls", "icon_code_pub");
                                    else record.set("iconCls", "");
                                    if (action_type == "add") {
                                        record.set("id", action.result.id);
                                        parent_record.set("leaf", false);
                                        parent_record.appendChild(record);
                                        tree.expand(parent_record, false);
                                        parent_record.commit();
                                        record.commit();
                                    } else {
                                        record.commit();
                                        if (grid.store.proxy.extraParams.codetype_id == action.result.id) {
                                            grid.store.proxy.extraParams.data_key = data_key;
                                        }
                                    }
                                } else {
                                    showMsgBySubmit(action, "保存失败！");
                                }
                            },
                            failure: function (form, action) {
                                showMsgBySubmit(action, "保存失败！");
                            }
                        });
                    }
                }
            }, {
                text: '重置',
                minWidth: 70,
                handler: function () {
                    designFormOriginalValue(form, record);
                }
            }, {
                text: "关闭",
                minWidth: 70,
                handler: function () {
                    window.close();
                }
            }]
        });
        window.show();
    };

    var fields_grid_code = [
        {name: 'id', type: 'int'},
        {name: 'pid', type: 'int'},
        {name: 'text', type: 'string'},
        {name: 'value', type: 'string'},
        {name: 'expand', type: 'string'},
        {name: 'enabled', type: 'bool'},
        {name: 'isshow', type: 'bool'},
        {name: 'codetype_id', type: 'int'},
        {name: 'issys', type: 'int'},
        {name: 'type_name', type: 'string'},
        {name: 'remark', type: 'string'}
    ];

    Ext.define('model_sys_code', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: fields_grid_code
    });

    function getNewData(codetype_id, pid) {
        return Ext.create('model_sys_code', {
            'id': 0,
            'pid': pid,
            'codetype_id': codetype_id,
            'expand': "false",
            'enabled': 1,
            'isshow': 1
        });
    }

    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: "system/code/tree",
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {isshow: 0, codetype_id: "0"}
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: fields_grid_code
    });
    var columns = [
        {
            xtype: 'treecolumn',
            text: '编码名称',
            dataIndex: 'text',
            width: 270,
            fixed: true,
            renderer: function (val, meta, rec) {
                if (rec.get("expand") == "true")
                    return val + '<span style="color:blue;">[默认展开]</span>';
                return val;
            }
        },
        {text: '编码值', width: 160, fixed: true, dataIndex: 'value'},
        //{text: '所属类型', width: 140, fixed: true, dataIndex: 'type_name'},
        {
            xtype: 'checkcolumn',
            text: '<span style="color:blue">是否可选</span>',
            width: 100,
            fixed: true,
            dataIndex: 'enabled',
            listeners: {
                'checkchange': function (field, recordIndex, checked, record) {
                    if (!isExistsByItemId(treenode, "btn_code_mod", "编辑")) {
                        record.set(field.dataIndex, !checked);
                        return false;
                    }
                    editingInfoFn(record, record.get("id"), field.dataIndex, checked ? "" : "disabled", !checked);
                }
            }
        },
        {
            xtype: 'checkcolumn',
            text: '<span style="color:blue">是否显示</span>',
            width: 100,
            fixed: true,
            dataIndex: 'isshow',
            listeners: {
                'checkchange': function (field, recordIndex, checked, record) {
                    if (!isExistsByItemId(treenode, "btn_code_mod", "编辑")) {
                        record.set(field.dataIndex, !checked);
                        return false;
                    }
                    editingInfoFn(record, record.get("id"), field.dataIndex, checked ? 1 : 0, !checked);
                }
            }
        },
        {
            text: '备注[data_key]', width: 160, dataIndex: 'remark', flex: 0.8, sortable: false,
            renderer: function (val, meta, rec) {
                if (val.length > 15) meta.tdAttr = 'data-qtip="' + val + '"';

                val = Ext.String.format("[{0}]{1}", store.proxy.extraParams.data_key, val);
                if (rec.get("issys") == "1")
                    return '<span style="color:blue;">[内编]</span>' + val;
                else
                    return val;
            }
        }
    ];

    function editingInfoFn(record, Id, field, value, originalValue) {
        var data_key = grid.store.proxy.extraParams.data_key;
        Ext.Ajax.request({
            method: "POST", url: "system/code/editing",
            params: {
                data_key: data_key, item_id: item_id,
                id: Id, field: field, value: value
            },
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    record.commit();
                }
                else {
                    showMsgByJson(resp, "保存失败!");
                    record.set(field, originalValue);
                    record.commit();
                }
            },
            failure: function (response, options) {
                showMsgByResponse(response, "保存失败!");
                record.set(field, originalValue);
                record.commit();
            }
        });
    }

    //设置按钮是否可选
    function setButtonDisabled(disabled) {
        grid.queryById('btn_add').setDisabled(disabled);
        grid.queryById('btn_test').setDisabled(disabled);
        if (grid.store.proxy.extraParams.istree == "1"
            || grid.store.getCount() == 0) {
            grid.queryById('btn_enum').setDisabled(true);
            grid.queryById('btn_select_source').setDisabled(true);
        } else {
            grid.queryById('btn_enum').setDisabled(disabled);
            grid.queryById('btn_select_source').setDisabled(disabled);
        }
    }

    var comboxType = "single";
    var grid = Ext.create('Ext.tree.Panel', {
        store: store,
        useArrows: false,
        rowLines: true,
        emptyText: "没有找到相关数据!",
        bodyCls: 'grid_empty_text_parent',
        viewConfig: {
            enableTextSelection: true,
            getRowClass: function () {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            //preserveScrollOnRefresh: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'treeviewdragdrop',
                allowLeafInserts: true, //叶子节点是否可拖动的配置，默认值为true
                containerScroll: true,
                dragGroup: 'code_draggroup',
                dropGroup: 'code_draggroup'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_code_sort", "排序")) return false;
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
                    saveTreeCodeSortFn(data.records[0].parentNode);
                }
            }
        },
        selModel: Ext.create("Ext.selection.RowModel", {
            mode: "single",
            enableKeyNav: true
        }),
        autoScroll: true, rootVisible: false,
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                treeview.toggleOnDblClick = false; //取消双击展开折叠菜单行为
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
            },
            'celldblclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex != "enabled" && rec.get('issys') != 1) {
                    openCodeManageWinFn(grid, rec, rec.parentNode, TypeText, item_id);
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
                            disabled: !isExistsByItemId(treenode, "btn_code_add", ""),
                            iconCls: 'icon_add',
                            handler: function () {
                                var sel_record = me.sel_record;
                                var codetype_id = grid.store.proxy.extraParams.codetype_id;
                                var pid = sel_record.get("id");
                                sel_record.expand(false);
                                openCodeManageWinFn(grid, getNewData(codetype_id, pid), sel_record, TypeText, item_id);
                            }
                        }, {
                            itemId: "mod",
                            text: "修改",
                            tooltip: '系统编码不能删除',
                            disabled: !isExistsByItemId(treenode, "btn_code_mod", ""),
                            iconCls: 'icon_edit',
                            handler: function () {
                                var sel_record = me.sel_record;
                                openCodeManageWinFn(grid, sel_record, sel_record.parentNode, TypeText, item_id);
                            }
                        }, {
                            itemId: "delete",
                            text: "删除",
                            disabled: !isExistsByItemId(treenode, "btn_code_del", ""),
                            iconCls: 'icon_delete',
                            tooltip: '父结点与系统编码不能删除',
                            handler: function () {
                                var sel_record = me.sel_record;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/code/delete",
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
                                                } else {
                                                    Ext.alert.msg('提示', resp.msg ? resp.msg : "删除失败!", 5000);
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
                if (isExistsByItemId(treenode, "btn_code_del", ""))
                    if (deleteItem) deleteItem.setDisabled(!sel_record.get('leaf') || sel_record.get('issys') == 1);

                if (isExistsByItemId(treenode, "btn_code_mod", ""))
                    var modItem = ctxMenu.getComponent("mod");
                if (modItem) modItem.setDisabled(sel_record.get('issys') == 1);

                var istree = grid.store.proxy.extraParams.istree;
                var add = ctxMenu.getComponent("add");
                if (isExistsByItemId(treenode, "btn_code_add", "")) {
                    if (add) add.setDisabled(istree != "1");
                }
                ctxMenu.showAt(e.getXY()); //让右键菜单跟随鼠标位置  
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'text_search', xtype: 'textfield', width: 220,
                emptyText: "按编码名称检索，按enter键...",
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == Ext.EventObject.ENTER) {
                            grid.store.proxy.extraParams.text = field.getValue().replace(/%/g, '/%').replace(/_/g, '/_');
                            grid.store.reload();
                        }
                    }
                }
            }, '-', {
                itemId: 'btn_add', disabled: true,
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add',
                handler: function (btn, pressed) {
                    var codetype_id = grid.store.proxy.extraParams.codetype_id;
                    openCodeManageWinFn(grid, getNewData(codetype_id, "0"), grid.getRootNode(), TypeText, item_id);
                }
            }, '-', {
                itemId: 'btn_test', disabled: true,
                xtype: 'splitbutton', text: '下拉框测试',
                minWidth: 60, iconCls: 'icon_combo_test',
                handler: function (btn, pressed) {
                    openComboxTestWinFn(comboxType, grid.store.proxy.extraParams.data_key, grid.store.proxy.extraParams.istree);
                },
                menu: {
                    items: [{
                        text: '单选', checked: true,
                        group: 'comboxType', xtype: 'menucheckitem',
                        checkHandler: function (item, checked) {
                            comboxType = "single";
                            openComboxTestWinFn(comboxType, grid.store.proxy.extraParams.data_key, grid.store.proxy.extraParams.istree);
                        }
                    }, {
                        text: '多选', checked: false,
                        group: 'comboxType', xtype: 'menucheckitem',
                        checkHandler: function (item, checked) {
                            comboxType = "multi";
                            openComboxTestWinFn(comboxType, grid.store.proxy.extraParams.data_key, grid.store.proxy.extraParams.istree);
                        }
                    }]
                }
            }, '-', {
                itemId: 'btn_enum', disabled: true,
                xtype: 'button', text: 'Enum生成',
                minWidth: 60, iconCls: 'icon_build',
                handler: function (btn, pressed) {
                    Ext.Ajax.request({
                        method: "POST", url: path_url.info.util.buildspell,
                        params: {text: TypeText, type: "simple", lower: "true"},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            var enum_name = "Enum_";
                            if (resp.success) enum_name += resp.value;
                            openCodeEnumBuildWinFn(grid, TypeText, enum_name);
                        },
                        failure: function (response, options) {
                        }
                    });
                }
            }, '-', {
                disabled: true,
                itemId: 'btn_select_source',
                xtype: 'button', text: '选择框数据源',
                minWidth: 60, iconCls: 'icon_select_source', //pressed: true,
                handler: function (btn, pressed) {
                    var rootNode = grid.getRootNode();
                    var childNodes = rootNode.childNodes;
                    if (childNodes.length > 100) {
                        Ext.alert.msg('提示', "选择数据源数据最多支持100条!", 5000);
                        return;
                    } else {
                        openSelectSourceBuildWinFn(grid, "value", "text");
                    }
                }
            }, '->', {
                xtype: 'checkbox',
                labelWidth: 100,
                fieldLabel: '只包含可见编码',
                labelSeparator: "",
                //checked: true,
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        grid.store.proxy.extraParams.isshow = checked ? 1 : 0;
                        grid.store.reload();
                    }
                }
            }, '  ']
        }]
    });

    //下拉框测试窗口
    function openComboxTestWinFn(comboxType, data_key, istree) {
        var field = null;
        var multiSelect = false;
        if (comboxType == "multi") multiSelect = true;
        if (istree == "1") {  //下拉树
            field = {
                itemId: 'test_input',
                fieldLabel: TypeText,
                xtype: 'treepicker',
                name: 'combox',
                //selectMode: 'all',
                multiSelect: multiSelect,
                queryMode: 'remote',
                pickerResizable: false,
                store: getCodeAllTreeStore(data_key, multiSelect),
                editable: false
            }
        } else {    //下拉列表
            if (multiSelect) {  //多选
                field = {
                    itemId: 'test_input',
                    fieldLabel: TypeText,
                    xtype: 'multicombobox',
                    isSelectAll: true,
                    name: 'combox',
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getCodeComboStore(data_key),
                    forceSelection: true,
                    minChars: 0, editable: false
                }
            } else {   //单选
                field = {
                    itemId: 'test_input',
                    fieldLabel: TypeText,
                    xtype: 'singlecombobox',
                    name: 'combox',
                    valueField: 'id',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getCodeComboStore(data_key),
                    forceSelection: true,
                    minChars: 0, editable: false
                }
            }
        }
        var form = Ext.create('Ext.form.Panel', {
            frame: false,
            autoScroll: true,
            bodyPadding: "15 20 10 20",
            defaultType: 'textfield',
            fieldDefaults: {
                labelAlign: 'left',
                msgTarget: 'side',
                labelWidth: 80
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [field, {
                xtype: 'checkbox',
                name: 'isSearch',
                fieldLabel: '  ',
                allowBlank: true,
                submitValue: false,
                boxLabel: '是否可查询筛选',
                inputValue: '1',
                uncheckedValue: '0',
                labelSeparator: '  ',
                listeners: {
                    'change': function (checkbox, newValue, oldValue, eOpts) {
                        var test_input = form.queryById('test_input');
                        if (test_input) {
                            test_input.setEditable(newValue);
                        }
                    }
                }
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:1px;margin-bottom:10px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'first_item_id',
                itemId: 'first_item_id',
                fieldLabel: '第一项值',
                emptyText: "加第一项的值，可为空,设置后“重置”",
                disabled: istree == "1" || multiSelect == true,
                submitValue: false,
                allowBlank: true,
                maxLength: 20
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'first_item_text',
                itemId: 'first_item_text',
                fieldLabel: '第一项文本',
                emptyText: "加第一项的文本，如“==请选择==”",
                disabled: istree == "1" || multiSelect == true,
                submitValue: false,
                allowBlank: true,
                maxLength: 20
            }]
        });
        var window = Ext.create('Ext.window.Window', {
            title: "下拉输入框测试",
            width: 420,
            resizable: true,
            closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体  
            layout: "fit",
            items: [form],
            listeners: {
                "afterrender": function (window, eOpts) {
                }
            },
            buttonAlign: "right",
            buttons: [{
                text: "查看",
                minWidth: 70,
                handler: function () {
                    if (form.getForm().isValid()) {
                        Ext.MessageBox.show({
                            title: 'get提交形式',
                            styleHtmlContent: true,
                            msg: "<div style='word-wrap:break-word; width:300px;'>" + unescape(form.getForm().getValues(true)) + "</div>",
                            buttons: Ext.Msg.OK,
                            icon: Ext.Msg.INFO
                        });
                    }
                }
            }, {
                text: '重置',
                minWidth: 70,
                handler: function () {
                    var test_input = form.queryById('test_input');
                    var name = form.queryById('first_item_text').getValue();
                    if (test_input) {
                        test_input.store.proxy.extraParams.id = form.queryById('first_item_id').getValue();
                        test_input.store.proxy.extraParams.name = name;
                        test_input.store.load();
                    }
                    test_input.reset();
                }
            }, {
                text: "关闭",
                minWidth: 70,
                handler: function () {
                    window.close();
                }
            }]
        });
        window.show();
    }

    //动态树拖动保存排序
    function saveTreeCodeSortFn(parent_record) {
        var childNodes = parent_record.childNodes;
        var str = [];
        for (var i = 0, len = childNodes.length; i < len; i++) {
            var record = childNodes[i];
            record.set("pid", parent_record.get("id"));
            str[str.length] = record.get("id") + ":" + record.get("pid") + ":" + (i + 1);
        }
        Ext.Ajax.request({
            method: "POST", url: "system/code/sort",
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

    var code_panel = Ext.create('Ext.panel.Panel', {
        layout: "border", border: false,
        items: [{
            title: "编码分类",
            region: 'west', split: {width: 5},
            tools: [{
                type: 'plus',
                itemId: "type_add",
                tooltip: '新增编码类型',
                callback: function (panel, tool) {
                    if (!isExistsByItemId(treenode, "btn_type_add", "新增编码类型")) return false;
                    addTreeNodeFn(tree, tree.getRootNode(), code_panel.queryById("type_add").getId());
                }
            }, {
                type: 'refresh',
                tooltip: '刷新',
                callback: function (panel, tool) {
                    refreshTreeNode(tree);
                    code_panel.queryById('code_grid').setTitle("数据编码-未选择分类");
                    grid.queryById('text_search').setValue('');
                    grid.store.proxy.extraParams.searchdata = "";
                    grid.store.proxy.extraParams.data_key = "";
                    grid.store.proxy.extraParams.codetype_id = "0";
                    grid.store.load();
                }
            }],
            width: 250, minWidth: 1, maxWidth: 500,
            layout: 'fit', items: [tree]
        }, {
            itemId: 'code_grid', title: "数据编码-未选择分类",
            region: 'center', layout: 'fit',
            tools: [{
                type: 'help',
                tooltip: '操作说明',
                callback: function (panel, tool) {
                    openHelpWindow("html/code_manage.html", 600, 450, "编码使用说明");
                }
            }],
            items: [grid]
        }]
    });
    return code_panel;
}

function openCodeManageWinFn(grid, cur_record, parent_record, TypeText, item_id) {
    var btn_build_codename = "btn_build_codename";
    if (!$.cookie(btn_build_codename)) $.cookie(btn_build_codename, "2");
    var form = Ext.create('Ext.form.Panel', {
        frame: false,
        bodyPadding: "15 20 10 20",
        fieldDefaults: {
            labelAlign: 'left',
            msgTarget: 'side',
            labelWidth: 80
        },
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [{
            xtype: 'hiddenfield',
            name: 'id'
        }, {
            xtype: 'hiddenfield',
            name: 'pid'
        }, {
            xtype: 'hiddenfield',
            name: 'codetype_id'
        }, {
            xtype: 'textfield',
            itemId: 'text',
            name: 'text',
            maxLength: 40,
            allowBlank: false,
            beforeLabelTextTpl: '*',
            fieldLabel: '编码名称',
            listeners: {
                'blur': function (textfield, the, oldValue) {
                    if ($.cookie("generate_fieldname") == "1") {
                        if ($.cookie(btn_build_codename) == "1")
                            generateChinaSpell(form.queryById('value'), form.queryById('text').getValue(), "simple");
                        else
                            generateChinaSpell(form.queryById('value'), form.queryById('text').getValue(), "full");
                    }
                }
            }
        }, {
            xtype: 'fieldcontainer',
            fieldLabel: '编码值',
            layout: 'hbox',
            items: [{
                itemId: 'value',
                xtype: 'textfield',
                name: 'value',
                msgTarget: 'under',
                minLength: 1, maxLength: 100,
                regex: /^[a-z|A-Z|0-9|_]+$/,
                regexText: '只能输入字母、数字、下划线',
                flex: 1, msgTarget: 'under',
                hideLabel: true, allowBlank: false
            }, {
                itemId: 'btn_Value',
                minWidth: 55, frame: true,
                cls: "x-btn-default-toolbar-small",
                style: 'margin-left:5px;margin-top:2px',
                xtype: 'splitbutton', text: '生成',
                listeners: {
                    click: function () {
                        if ($.cookie(btn_build_codename) == "1")
                            generateChinaSpell(form.queryById('value'), form.queryById('text').getValue(), "simple");
                        else
                            generateChinaSpell(form.queryById('value'), form.queryById('text').getValue(), "full");
                    }
                },
                menu: [{
                    text: '简拼生成',
                    checked: $.cookie(btn_build_codename) == "1",
                    group: btn_build_codename,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_build_codename, "1");
                        form.queryById('btn_Value').fireEvent('click');
                    }
                }, {
                    text: '全拼生成',
                    checked: $.cookie(btn_build_codename) == "2",
                    group: btn_build_codename,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_build_codename, "2");
                        form.queryById('btn_Value').fireEvent('click');
                    }
                }, '-', {
                    text: '自动生成',
                    checked: $.cookie("generate_fieldname") == "1",
                    checkHandler: function (item, checked) {
                        $.cookie("generate_fieldname", checked ? "1" : "2");
                    }
                }]
            }]
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
                boxLabel: '是否可选',
                name: 'enabled',
                inputValue: '1'
            }, {
                flex: 0, width: 20,
                xtype: 'displayfield',
                value: ''
            }, {
                xtype: 'checkbox',
                fieldLabel: ' ',
                labelSeparator: "",
                boxLabel: '是否显示',
                name: 'isshow',
                inputValue: '1'
            }, {
                flex: 0, width: 20,
                xtype: 'displayfield',
                value: ''
            }, {
                xtype: 'checkbox',
                fieldLabel: '',
                labelSeparator: "",
                boxLabel: '默认展开',
                name: 'expand',
                uncheckedValue: 'false',
                inputValue: 'true'
            }]
        }, {
            xtype: 'textareafield',
            name: 'remark',
            fieldLabel: '备注',
            allowBlank: true,
            height: 80,
            rows: 3,
            maxLength: 100,
            flex: 1
        }, {
            xtype: 'displayfield',
            hideLabel: true,
            style: 'margin-top:8px',
            value: '<span style="color:blue;">说明：在下拉树编码中，对于父结点，勾选“默认展开”后，打开下拉树选择时该结点将展开。</span>'
        }]
    });
    var window = Ext.create('Ext.window.Window', {
        title: "标题",
        animateTarget: grid.queryById('btn_add').getId(),
        width: 480,
        resizable: false,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: 'true',  // 弹出模态窗体  
        items: [form],
        listeners: {
            "show": function (window, eOpts) {
                designFormOriginalValue(form, cur_record);
                var pid = parent_record.get("id"), pid_text = parent_record.get("text");
                if (cur_record.get("id") == 0) {
                    var text = " [父结点-" + pid_text + "]";
                    if (pid == "0") text = " [一级结点]";
                    window.setTitle(TypeText + "-编码新增" + text);
                }
                else
                    window.setTitle(TypeText + "-编码修改");
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
            text: "保存",
            minWidth: 70,
            handler: function () {
                if (form.getForm().isValid()) {
                    form.getForm().submit({
                        url: "system/code/save",
                        method: "POST", waitTitle: '请稍等...',
                        waitMsg: '正在提交信息...', submitEmptyText: false,
                        params: {item_id: item_id},
                        success: function (basic_form, action) {   //成功后
                            var flag = action.result.success;
                            if (flag) {
                                //grid.store.reload();
                                var values = form.getForm().getValues(false);
                                if (cur_record.get("id") == 0) {
                                    refreshTreeNode(grid);
                                } else {
                                    cur_record.set("text", values["text"]);
                                    cur_record.set("value", values["value"]);
                                    cur_record.set("expand", values["expand"]);
                                    cur_record.set("enabled", values["enabled"]);
                                    cur_record.set("isshow", values["isshow"]);
                                    cur_record.set("remark", values["remark"]);
                                    cur_record.commit();
                                }
                                if ($.cookie("submit_after") == "1") {
                                    Ext.alert.msg("提示", "保存成功！");
                                    window.close();
                                } else if (cur_record.get("id") == 0) {
                                    Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                    form.getForm().reset();
                                } else {
                                    Ext.alert.msg("提示", "保存成功！");
                                    designFormOriginalValue(form, Ext.create("model_sys_code", values));
                                }
                            } else {
                                showMsgBySubmit(action, "保存失败！");
                            }
                        },
                        failure: function (form, action) {
                            showMsgBySubmit(action, "保存失败！");
                        }
                    });
                }
            }
        }, {
            text: '重置',
            minWidth: 70,
            handler: function () {
                form.getForm().reset();
            }
        }, {
            text: "关闭",
            minWidth: 70,
            handler: function () {
                window.close();
            }
        }]
    });
    window.show();
}


