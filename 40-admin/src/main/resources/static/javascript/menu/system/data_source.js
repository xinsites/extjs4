Ext.define('javascript.menu.system.data_source', {
    extend: ''
});

//系统数据源
function createPanel_DataSource(treenode) {
    var item_id = treenode.raw.id, type_text = "";
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: "system/ds/tree",
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {id: 0}
        },
        root: {text: '系统数据源', id: 0, pid: 0, expanded: true},
        fields: [{name: 'id', type: 'int'},
            {name: 'text', type: 'string'},
            {name: 'pid', type: 'int'},
            {name: 'query_field', type: 'string'},
            {name: 'data_key', type: 'string'},
            {name: 'data_type', type: 'string'},
            {name: 'data_page', type: 'string'},
            {name: 'serialcode', type: 'int'}],
        listeners: {
            'load': function (store, record) {
                setTimeout(function () {
                    var rec = store.getNodeById(right_tree.store.proxy.extraParams.data_id);
                    if (rec) left_tree.getView().getSelectionModel().select(rec)
                }, 100)
            }
        }
    });

    //动态树拖动保存排序
    function saveDataSourceFn(parent_record) {
        var childNodes = parent_record.childNodes;
        var str = [];
        for (var i = 0, len = childNodes.length; i < len; i++) {
            var record = childNodes[i];
            record.set("pid", parent_record.get("id"));
            str[str.length] = record.get("id") + ":" + record.get("pid") + ":" + (i + 1);
        }
        Ext.Ajax.request({
            method: "POST", url: "system/ds/sort",
            params: {item_id: item_id, sort_vals: str.join(";")},
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (!resp.success) Ext.alert.msg('提示', "排序失败!");
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "排序失败!");
            }
        });
    }

    var left_tree = Ext.create('Ext.tree.Panel', {
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
                    if (!isExistsByItemId(treenode, "btn_data_sort", "排序")) return false;
                    if (data.records.length > 0) {
                        var parent_record = data.records[0].parentNode; //先获取结点的父结点
                        if (parent_record && parent_record.childNodes) {
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
                    saveDataSourceFn(data.records[0].parentNode);
                }
            }
        },
        columns: [{
            xtype: 'treecolumn', text: '数据源', dataIndex: 'text', flex: 1,
            renderer: function (val, meta, rec) {
                if (rec.get("data_type") == "tree") return val + "[树]";
                else return val;
            }
        }],
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                treeview.toggleOnDblClick = false; //取消双击展开折叠菜单行为
                type_text = record.get('text');
                right_tree.queryById('text_search').reset();
                right_tree.store.proxy.extraParams.query = "";
                right_tree.store.proxy.extraParams.data_id = record.get('id');
                right_tree.store.proxy.extraParams.type_text = record.get('text');
                right_tree.store.proxy.extraParams.data_key = record.get('data_key');
                right_tree.store.proxy.extraParams.data_type = record.get('data_type');
                right_tree.store.proxy.extraParams.istree = record.get("data_type") == "tree";
                setButtonDisabled(false);
                right_tree.queryById('btn_select_source').setDisabled(record.get("data_type") == "tree");
                right_tree.getRootNode().removeAll(false);
                right_tree.store.load();

                if (!record.get('leaf') && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
            }
        }
    });

    function editingInfoFn(record, Id, field, value, originalValue) {
        var data_key = right_tree.store.proxy.extraParams.data_key;
        Ext.Ajax.request({
            method: "POST", url: "system/ds/editing",
            params: {
                item_id: item_id,
                Id: Id, field: field,
                value: value, data_key: data_key
            },
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    record.commit();
                } else {
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

    //下拉框测试窗口
    function openComboxTestWinFn(comboxType, data_type, data_key) {
        var field = null;
        var multiSelect = false;
        if (comboxType == "multi") multiSelect = true;
        if (data_type == "tree") {  //下拉树
            field = {
                itemId: 'test_input',
                fieldLabel: type_text,
                xtype: 'treepicker',
                name: 'combox',
                //selectMode: 'all',
                multiSelect: multiSelect,
                queryMode: 'remote',
                pickerResizable: false,
                store: getSysDataTreeStore(data_key, multiSelect),
                editable: false
            }
        } else {    //下拉列表
            var first_item;
            if (multiSelect) {  //多选
                field = {
                    itemId: 'test_input',
                    fieldLabel: type_text,
                    xtype: 'multicombobox',
                    isSelectAll: true,
                    name: 'combox',
                    valueField: 'value',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getSysDataComboStore(data_key),
                    forceSelection: true,
                    minChars: 0, editable: false
                }
            } else {   //单选
                field = {
                    itemId: 'test_input',
                    fieldLabel: type_text,
                    xtype: 'singlecombobox',
                    name: 'combox',
                    valueField: 'value',
                    displayField: 'name',
                    queryMode: 'remote',
                    store: getSysDataComboStore(data_key),
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
                disabled: data_type == "tree" || multiSelect == true,
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
                disabled: data_type == "tree" || multiSelect == true,
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
                        test_input.store.proxy.extraParams.value = form.queryById('first_item_id').getValue();
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

    //设置按钮是否可用
    function setButtonDisabled(disabled) {
        var btn_test = right_tree.queryById('btn_test');
        if (btn_test) btn_test.setDisabled(disabled);
    }

    var right_tree = getDataSourceTree(treenode, openComboxTestWinFn, editingInfoFn);

    var code_panel = Ext.create('Ext.panel.Panel', {
        layout: "border", border: false,
        items: [{
            title: "系统数据源",
            region: 'west', split: {width: 5},
            width: 260, minWidth: 1, maxWidth: 500,
            layout: 'fit', items: [left_tree]
        }, {
            itemId: 'data_grid', region: 'center',
            layout: 'fit', items: [right_tree]
        }],
        listeners: {
            'render': function (panel, eOpts) {
                setButtonDisabled(true);
            }
        }
    });

    return code_panel;
}

//数据源树形目录与一次加载全部列表
function getDataSourceTree(treenode, openComboxTestWinFn, editingInfoFn) {
    var fields_grid_code = [
        {name: 'id', type: 'string'},
        {name: 'pid', type: 'int'},
        {name: 'text', type: 'string'},
        {name: 'value', type: 'string'},
        {name: 'expand', type: 'string'},
        {name: 'enabled', type: 'bool'},
        {name: 'isshow', type: 'bool'}
    ];

    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false, animCollapse: false,
        proxy: {
            type: 'ajax', url: path_url.system.ds.storedata,
            reader: {type: 'json', id: "id"},
            //reader: { type: 'json', id: "id", totalProperty: 'totalProperty', root: 'root' },
            extraParams: {method: "", queryType: "SysData", isshow: 0}
        },
        listeners: {
            'load': function (store, record) {
                nodeExpand(record);
            }
        },
        fields: fields_grid_code,
        root: {text: '根节点', id: 0, pid: 0, expanded: true}
    });

    function nodeExpand(record) {
        Ext.Array.each(record.childNodes, function (record, index) {
            if (record.raw.expand == "true") {
                if (!record.get('leaf') && !record.isExpanded()) {
                    setTimeout(function () {
                        record.expand(false);
                    }, 50);
                }
                nodeExpand(record);
            }
        });
    }

    var columns = [{
        xtype: 'treecolumn', text: '编码名称', dataIndex: 'text', width: 270, fixed: true,
        editor: {
            allowBlank: false, minLength: 1, maxLength: 50,
            xtype: 'textfield', selectOnFocus: false //点击编辑框后，变成全选状态
        }
    },
        {
            text: '编码值', width: 160, fixed: true, dataIndex: 'value',
            renderer: function (val, meta, rec) {
                if (right_tree.store.proxy.extraParams.istree)
                    return rec.get("id");
                return val;
            }
        },
        {
            xtype: 'checkcolumn',
            text: '是否可选(<span style="color:blue">可编</span>)',
            width: 120,
            fixed: true,
            dataIndex: 'enabled',
            listeners: {
                'checkchange': function (field, recordIndex, checked, record) {
                    if (!isExistsByItemId(treenode, "btn_data_mod", "编辑")) {
                        record.set(field.dataIndex, !checked);
                        return false;
                    }
                    editingInfoFn(record, record.get("id"), field.dataIndex, checked ? "" : "disabled", !checked);
                }
            }
        },
        {
            xtype: 'checkcolumn',
            text: '是否可见(<span style="color:blue">可编</span>)',
            width: 120,
            fixed: true,
            dataIndex: 'isshow',
            listeners: {
                'checkchange': function (field, recordIndex, checked, record) {
                    if (!isExistsByItemId(treenode, "btn_data_mod", "编辑")) {
                        record.set(field.dataIndex, !checked);
                        return false;
                    }
                    editingInfoFn(record, record.get("id"), field.dataIndex, checked ? 1 : 0, !checked);
                }
            }
        },
        {
            text: '数据源[data_key]', width: 220, fixed: true, flex: 0.8, dataIndex: 'id',
            renderer: function (val, meta, rec) {
                var name = store.proxy.extraParams.type_text;
                var data_key = store.proxy.extraParams.data_key;
                return Ext.String.format('{0}[{1}]', name, data_key);
            }
        }
    ];

    var comboxType = "single";
    var right_tree = Ext.create('Ext.tree.Panel', {
        store: store,
        useArrows: false,
        rowLines: true,
        //emptyText: "没有找到相关数据!",
        //bodyCls: 'grid_empty_text_parent',
        viewConfig: {
            enableTextSelection: true,
            getRowClass: function () {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        selModel: Ext.create("Ext.selection.RowModel", {
            mode: "single",
            enableKeyNav: true
        }),
        autoScroll: true, rootVisible: false,
        listeners: {
            beforeedit: function (editor, e) {
                if (!isExistsByItemId(treenode, "btn_data_mod", "编辑")) return false;
                return true;
            },
            edit: function (editor, e) {
                grid.plugins[0].completeEdit();
                var record = e.record;
                var ischange = e.originalValue + "" != e.value + "";
                if (ischange) {
                    var value = e.value;
                    editingInfoFn(record, record.get("id"), e.field, value, e.originalValue);
                }
            },
            "itemclick": function (treeview, record, item, index, e) {
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'text_search', xtype: 'textfield', width: 220,
                emptyText: "请输入关键字检索，按enter键...",
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == Ext.EventObject.ENTER) {
                            right_tree.store.proxy.extraParams.query = field.getValue().replace(/%/g, '/%').replace(/_/g, '/_');
                            right_tree.store.load();
                        }
                    },
                    change: function (field, newValue, oldValue, e) {
                    }
                }
            }, '-', {
                itemId: 'btn_test',
                xtype: 'splitbutton', text: '下拉框测试',
                minWidth: 60, iconCls: 'icon_combo_test',
                handler: function (btn, pressed) {
                    var data_type = right_tree.store.proxy.extraParams.data_type;
                    var data_key = right_tree.store.proxy.extraParams.data_key;
                    openComboxTestWinFn(comboxType, data_type, data_key);
                },
                menu: {
                    items: [{
                        text: '单选', checked: true,
                        group: 'right_tree_test', xtype: 'menucheckitem',
                        checkHandler: function (item, checked) {
                            comboxType = "single";
                            var data_type = right_tree.store.proxy.extraParams.data_type;
                            var data_key = right_tree.store.proxy.extraParams.data_key;
                            openComboxTestWinFn(comboxType, data_type, data_key);
                        }
                    }, {
                        text: '多选', checked: false,
                        group: 'right_tree_test', xtype: 'menucheckitem',
                        checkHandler: function (item, checked) {
                            comboxType = "multi";
                            var data_type = right_tree.store.proxy.extraParams.data_type;
                            var data_key = right_tree.store.proxy.extraParams.data_key;
                            openComboxTestWinFn(comboxType, data_type, data_key);
                        }
                    }]
                }
            }, '-', {
                itemId: 'btn_mybatis_source',
                xtype: 'button', text: 'MyBatis数据源',
                minWidth: 60, iconCls: 'icon_mybatis_source', //pressed: true,
                handler: function (btn, pressed) {
                    openMyBatisDataWinFn();
                }
            }, '-', {
                disabled: true,
                itemId: 'btn_select_source',
                xtype: 'button', text: '选择框数据源',
                minWidth: 60, iconCls: 'icon_select_source', //pressed: true,
                handler: function (btn, pressed) {
                    var rootNode = right_tree.getRootNode();
                    var childNodes = rootNode.childNodes;
                    if (childNodes.length > 100) {
                        Ext.alert.msg('提示', "选择数据源数据最多支持100条!", 5000);
                        return;
                    } else {
                        openSelectSourceBuildWinFn(right_tree, "value", "text");
                    }
                }
            }, '->', {
                xtype: 'checkbox',
                labelWidth: 110,
                fieldLabel: '只包含可见数据源',
                labelSeparator: "",
                checked: false,
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        right_tree.store.proxy.extraParams.isshow = checked ? 1 : 0;
                        refreshTreeNode(right_tree);
                        //right_tree.store.load();
                    }
                }
            }, '  ']
        }]
    });

    return right_tree;
}

