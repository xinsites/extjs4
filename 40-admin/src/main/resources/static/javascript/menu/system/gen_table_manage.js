Ext.define('javascript.menu.system.gen_table_manage', {
    extend: ''
});

//生成数据表
function createGenTablePanel(treenode) {
    var item_id = treenode.raw.id;
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            type: 'ajax', url: "system/gen/object/tree",
            reader: {type: 'json', id: "id"},
            extraParams: {item_id: item_id}
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true, object_type: ''},
        listeners: {
            'load': function (store, record) {
                treeExpand(record);
            }
        },
        fields: [{name: 'id', type: 'int'}
            , {name: 'pid', type: 'int'}
            , 'text', 'serialcode', 'object_type', 'layout_type', 'expand'],
        listeners: {
            'load': function (store, records) {
                setTimeout(function () {
                    var rec = store.getNodeById(grid.store.proxy.extraParams.oid);
                    if (rec) tree.getView().getSelectionModel().select(rec)
                }, 100);
            }
        }
    });

    Ext.define('model_gen_object', {
        extend: 'Ext.data.Model',
        idProperty: 'oid',
        fields: [{"name": "oid", "type": "int", "text": "主键"},
            {"name": "pid", "type": "int", "text": "父Id"},
            {"name": "object_name", "type": "string", "text": "对象名称"},
            {"name": "object_key", "type": "string", "text": "对象源标识"},
            {"name": "item_method", "type": "string", "text": "栏目点击方法"},
            {"name": "expanded", "type": "string", "text": "是否展开"}]
    });

    function treeExpand(record) {
        Ext.Array.each(record.childNodes, function (record, index) {
            if (record.get("expand") == "true") {
                if (!record.get('leaf') && !record.isExpanded()) {
                    setTimeout(function () {
                        treeExpand(record);
                        tree.getView().expand(record, false);
                    }, 50);
                }
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
        columns: [{
            xtype: 'treecolumn', text: '对象名称', dataIndex: 'text', flex: 1,
            editor: {
                allowBlank: false, maxLength: 50,
                xtype: 'textfield', selectOnFocus: true //点击编辑框后，变成全选状态 
            },
            renderer: function (val, meta, rec) {
                if (rec.get("object_type") == "flow") return val + "[流程]";
                else if (rec.get("layout_type").indexOf("tree") > 0) return val + "[树]";
                else return val;
            }
        }],
        viewConfig: {
            getRowClass: function () {
                return 'tree_panel_row_height18';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                removeTreeNode(grid.getRootNode());
                grid.store.proxy.extraParams.oid = record.get('id');

                grid.store.load();
                grid.getSelectionModel().deselectAll(true);
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
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
                                Ext.getBody().mask('请稍等,正在获取数据...');
                                Ext.Ajax.request({
                                    method: "POST", url: "system/gen/object/info",
                                    params: {oid: sel_record.get("id")},
                                    success: function (response, options) {
                                        Ext.getBody().unmask();
                                        var resp = Ext.JSON.decode(response.responseText);
                                        if (resp.success) {
                                            var formdata = Ext.create("model_gen_object", resp["object"]["data"]);
                                            openEditWindow(formdata, sel_record.parentNode, sel_record);
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
                            disabled: !isExistsByItemId(treenode, "btn_del", ""),
                            iconCls: 'icon_delete',
                            //tooltip: '父栏目不能删除',
                            handler: function () {
                                var sel_record = me.sel_record;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/gen/data/delete",
                                            params: {item_id: item_id, oid: sel_record.get("id")},
                                            success: function (response, options) {
                                                var resp = Ext.JSON.decode(response.responseText);
                                                if (resp.success) {
                                                    grid.store.load();
                                                    var parent_record = sel_record.parentNode; //先获取结点的父结点
                                                    sel_record.remove(true);
                                                    if (parent_record) {
                                                        if (parent_record.childNodes.length == 0) {
                                                            parent_record.set("leaf", true);
                                                        }
                                                    }
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
                if (isExistsByItemId(treenode, "btn_del", ""))
                    deleteItem.setDisabled(!sel_record.get('leaf'));
                ctxMenu.showAt(e.getXY()); //让右键菜单跟随鼠标位置
            }
        }
    });

    var grid_fields = [
        {name: 'id', type: 'int'},
        {name: 'pid', type: 'int'},
        {name: 'text', type: 'string'},
        {name: 'oid', type: 'int'},
        {name: 'object_name', type: 'string'},
        {name: 'object_type', type: 'string'},
        {name: 'table_name', type: 'string'},
        {name: 'table_key', type: 'string'},
        {name: 'table_type', type: 'string'},
        {name: 'tb_relation', type: 'string'},
        {name: 'system_fields', type: 'int'},
        {name: 'define_fields', type: 'int'}
    ];

    Ext.define('model_gen_table', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            type: 'ajax', url: "system/gen/table/tree", reader: {type: 'json', id: "id"},
            extraParams: {oid: 0, item_id: item_id, expanded: 1, checked: 1}
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: grid_fields
    });
    var columns = [
        {xtype: 'treecolumn', text: '数据库表中文名', dataIndex: 'text', flex: 1},
        {text: '数据库表名', width: 220, fixed: true, dataIndex: 'table_name'},
        {text: '数据库表Key', width: 220, fixed: true, dataIndex: 'table_key'},
        {
            text: '与主表关系', width: 100, align: 'center', fixed: true, dataIndex: 'tb_relation',
            renderer: function (val, meta, rec) {
                if (val == "主表")
                    return '<span style="color:blue;">' + val + '</span>';
                else if (val == "1对1")
                    return '<span style="color:green;">' + val + '</span>';
                else
                    return '<span style="color:red;">' + val + '</span>';
            }
        }, {
            text: '字段数', width: 90, align: 'center', fixed: true, dataIndex: 'system_fields',
            renderer: function (val, meta, rec) {
                meta.tdAttr = 'data-qtitle="提示" data-qtip="<b style=\'color:blue;\'>自定义字段</b>/<b style=\'color:green;\'>自带字段</b>&nbsp;&nbsp;"';
                return Ext.String.format('<b style="color:blue;">{0}</b>/<b style="color:green;">{1}</b>', rec.get("define_fields"), val)
            }
        }, {
            text: '列表类型', width: 120, fixed: true, dataIndex: 'table_type',
            renderer: function (val, meta, rec) {
                if (val == "table") return "普通列表";
                return '<span style="color:blue;">树形列表</span>';
            }
        }
    ];
    var grid = Ext.create('Ext.tree.Panel', {
        itemId: "main_grid1",
        store: store,
        useArrows: false,
        rowLines: true,
        emptyText: "没有数据!",
        bodyCls: 'grid_empty_text_parent',
        viewConfig: {
            getRowClass: function (record, rowIndex, rowParams, store) {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            enableTextSelection: true,
            loadingText: "数据加载中，请稍等..."
        },
        autoScroll: true, rootVisible: false,
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: 'btn_import',
                xtype: 'button', text: '数据导入',
                minWidth: 60, iconCls: 'icon_data_import',
                handler: function (btn, pressed) {
                    if (!isExistsByItemId(treenode, "btn_import", "数据导入")) return false;
                    openDataImportWinFn();
                }
            }]
        }]
    });

    var leftWidth = 250;
    if (getCookie("tree_obj_width")) leftWidth = Math.floor(getCookie("tree_obj_width"));

    return Ext.create('Ext.panel.Panel', {
        layout: "border", border: false,
        items: [{
            title: "数据对象",
            region: 'west', split: {width: 5},
            tools: [{
                type: 'help',
                tooltip: '作用说明',
                callback: function (panel, tool) {
                    openHelpWindow("html/gen_data.html?t=2", 600, 400, "表单数据作用说明");
                }
            }, {
                type: 'refresh',
                callback: function (panel, tool) {
                    tree.getSelectionModel().deselectAll(true);
                    tree.store.load();
                    grid.store.proxy.extraParams.oid = 0;
                    grid.store.load();
                    grid.getSelectionModel().deselectAll(true);
                }
            }],
            width: leftWidth, minWidth: 1, maxWidth: 500,
            layout: 'fit', items: [tree],
            listeners: {
                "resize": function (panel, width, height, oldWidth, oldHeight, eOpts) {
                    $.cookie("tree_obj_width", width);
                }
            }
        }, {
            region: 'center',
            layout: 'fit',
            items: [grid]
        }]
    });

    //数据导入窗口
    function openDataImportWinFn() {
        var form = Ext.create('Ext.form.Panel', {
            frame: false,
            bodyPadding: "15 20 10 20",
            fieldDefaults: {
                labelAlign: 'top',
                msgTarget: 'side',
                labelWidth: 80
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'filefield',
                name: 'import_file',
                fieldLabel: '导入文件',
                allowBlank: false,
                emptyText: '请选择文件(*.xinsite)',
                regex: /^.*?\.(xinsite)$/,
                regexText: "只能上传.xinsite类型的文件&nbsp;&nbsp;",
                buttonText: "浏览..."
            }, {
                xtype: 'displayfield',
                fieldLabel: ' ',
                labelSeparator: "",
                style: 'margin-top:4px;',
                value: '<span style="color:blue;">如果导入的数据表已经存在，做更新操作</span>'
            }]
        });

        var window = Ext.create('Ext.window.Window', {
            title: "设计表数据导入",
            animateTarget: grid.queryById('btn_import').getId(),
            width: 450,
            resizable: false,
            closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            items: [form],
            buttonAlign: "right",
            buttons: ["->", {
                text: "提交",
                minWidth: 70,
                handler: function () {
                    if (form.getForm().isValid()) {
                        form.getForm().submit({
                            submitEmptyText: false,
                            method: "POST", url: "system/gen/data/import",
                            waitTitle: '请稍等...', waitMsg: '正在提交信息...',
                            params: {item_id: item_id},
                            success: function (form, action) {   //成功后
                                var flag = action.result.success;
                                if (flag) {
                                    Ext.alert.msg("提示", "数据导入成功！");
                                    tree.store.load();
                                    grid.store.load();
                                    window.close();
                                } else {
                                    showMsgBySubmit(action, "数据导入失败!");
                                }
                            },
                            failure: function (form, action) {
                                showMsgBySubmit(action, "数据导入失败！");
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

    function openEditWindow(form_data, parent_record, cur_record) {
        var form = createObjectFormPanel(form_data);
        var win = Ext.create('widget.window', {
            width: 460,
            height: 280,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit',
            title: '修改数据对象',
            resizable: false,
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, form_data);
                }
            },
            buttonAlign: "right",
            buttons: ["->", {
                itemId: "btn_save_form",
                text: '保存', minWidth: 70,
                listeners: {
                    click: function () {
                        if (form.isValid()) {
                            form.submit({
                                method: "POST", url: "system/gen/object/mod",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false,
                                params: {item_id: item_id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        win.close();
                                        tree.store.load();
                                        Ext.alert.msg("提示", "保存成功！");
                                        if (action.result.msg) Ext.alert.msg('提示', action.result.msg, 5000);
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
        });
        win.show();
    }

    function createObjectFormPanel(record) {
        var form = Ext.create('Ext.form.Panel', {
            frame: false, bodyPadding: '20 20 5 20',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%'
            },
            items: [{
                xtype: 'hiddenfield',
                name: 'oid'
            }, {
                xtype: 'textfield',
                itemId: 'object_name',
                name: 'object_name',
                fieldLabel: '对象名称',
                maxLength: 50,
                allowBlank: false
            }, {
                xtype: 'textfield',
                itemId: 'item_method',
                name: 'item_method',
                fieldLabel: '栏目点击方法',
                maxLength: 50
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
                    fieldLabel: '',
                    itemId: 'expanded',
                    labelSeparator: "",
                    boxLabel: '默认展开',
                    name: 'expanded',
                    inputValue: 'true',
                    uncheckedValue: "false"
                }]
            }, {
                xtype: 'displayfield',
                fieldLabel: ' ',
                hideLabel: true,
                labelSeparator: "",
                style: 'margin-top:10px; margin-left:15px;',
                value: '<span style="color:blue;line-height:160%;">1、栏目点击方法与“菜单管理”中相应栏目“方法/地址”值必须相同；<br/>2、“菜单管理”中栏目“方法/地址”值对应到栏目点击方法，表示栏目与设计对象关联；</span>'
            }]
        });

        return form;
    }

}
