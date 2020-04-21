Ext.define('javascript.menu.system.item_function', {
    extend: ''
});

//打开常用功能管理窗口
function openCommonItemFunGridWindow(grid, item_record, treenode) {
    var btn = grid.queryById('btn_setup');
    var item_id = item_record.get("id");

    Ext.define('model_sys_item_fun', {
        extend: 'Ext.data.Model',
        idProperty: 'fun_id',
        fields: [{"name": "fun_id", "type": "int", "text": "主键"},
            {"name": "item_id", "type": "string", "text": "栏目Id"},
            {"name": "name", "type": "string", "text": "功能名称"},
            {"name": "itemid", "type": "string", "text": "itemid"},
            {"name": "per_value", "type": "string", "text": "控制器权限值"},
            {"name": "serialcode", "type": "int", "text": "排序号"},
            {"name": "isdel", "type": "int", "text": "删除"}]
    });

    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: false, //是否自动加载
        proxy: {
            type: 'ajax', url: "system/fun/grid",
            extraParams: {item_id: item_id},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'fun_id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                fun_id = store.proxy.extraParams.fun_id;
            }
        },
        model: 'model_sys_item_fun'
    });

    var columns = [new Ext.grid.RowNumberer({width: 30, tdCls: "blue"}),
        {
            text: '功能名称',
            dataIndex: 'name',
            width: 50,
            fixed: false,
            align: 'left',
            sortable: true,
            editor: {
                xtype: 'textfield',
                name: 'name',
                allowBlank: false,
                maxLength: 50
            }
        }, {
            text: '前端控件ItemId值',
            dataIndex: 'itemid',
            width: 60,
            fixed: false,
            align: 'left',
            sortable: true,
            editor: {
                xtype: 'textfield',
                name: 'itemid',
                allowBlank: false,
                maxLength: 30
            }
        }, {
            text: '控制器权限值',
            dataIndex: 'per_value',
            width: 80,
            fixed: false,
            align: 'left',
            sortable: true,
            editor: {
                xtype: 'textfield',
                name: 'per_value',
                allowBlank: true,
                maxLength: 30
            },
            renderer: function (val, meta, rec) {
                if (rec.get("isdel") == 1) {
                    var str = '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'btn_recovery\')">恢复删除</span>';
                    return str + ' || <span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'btn_delete\')">永久删除</span>';
                }
                return val;
            }
        }];

    //设计表中主表单拖动排序
    function dragSortTable(store) {
        if (store && store.getCount() > 0) {
            var index = 1, sort_vals = [];
            store.each(function (record) {
                sort_vals[sort_vals.length] = record.get("fun_id") + ":" + index++;
            });
            if (store.proxy.extraParams.fun_id == 0) {  //是新增
                var attr = [];
                store.each(function (record) {
                    attr[attr.length] = record.get("fun_id");
                });
                store.proxy.extraParams.ids = attr.join(",");
            }
            Ext.Ajax.request({
                method: "POST", url: "system/fun/sort",
                params: {item_id: item_id, sort_vals: sort_vals.join(";")},
                success: function (response, options) {
                    if (item_id > 0) fun_grid.isAction = true;
                    store.load();
                },
                failure: function (response, options) {
                    store.load();
                }
            });
        }
    }

    var fun_grid = Ext.create('Ext.grid.Panel', {
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        sortableColumns: false,
        viewConfig: {
            getRowClass: function () {
                // 在这里添加自定样式 改变这个表格的行高
                return 'custom-grid-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                drop: function (node, data, dropRec, dropPosition) {
                    dragSortTable(store);
                }
            }
        },
        border: false,
        plugins: [{
            ptype: 'rowediting',
            clicksToEdit: 2,
            errorSummary: false,
            listeners: {
                beforeedit: function (editor, e) {
                    return rowediting_beforeedit(grid, this, e);
                },
                edit: function (editor, e) {
                    var me = this, rowIdx = e.rowIdx, record = e.record;
                    rowediting_edit_forms(grid, me, e);
                    var formData = record.getData(false);
                    Ext.Ajax.request({
                        method: "POST", url: "system/fun/rowediting",
                        params: formData,
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                if (item_id > 0) fun_grid.isAction = true;
                                record.set("fun_id", resp.id);
                                record.commit();
                                if (resp.msg) Ext.alert.msg('提示', resp.msg);
                            } else {
                                showMsgByJson(resp, "保存失败!");
                                if (!resp.msg) store.reload();
                                else fun_grid.editingPlugin.startEdit(rowIdx, 2);
                            }
                        },
                        failure: function (response, options) {
                            Ext.alert.msg("提示", "保存失败!");
                            store.reload();
                        }
                    });
                },
                canceledit: function (editor, e) {
                    if (e.record.get("fun_id") == 0) {
                        store.remove(e.record);
                    }
                },
                validateedit: function (editor, e) {
                    return rowediting_validateedit(this, columns);
                }
            }
        }],
        listeners: {
            'cellclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = fun_grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "per_value") {
                    if (itemClickFlag == "btn_recovery")
                        restoreGridRow(fun_grid, rec.get("fun_id"));
                    else if (itemClickFlag == "btn_delete")
                        deleteGridRow(fun_grid, rec.get("fun_id"));
                }
            }
        },
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi", //默认为多选multi;multi:多行选择，支持CTRL、SHIFT功能键，如果要进行多选，需要按住ctrl键。用shift可以进行区域选择；simple:多行选择，单击选中/取消选中行；single:单行选择；
            checkOnly: false, //如果值为true，则只用点击checkbox列才能选中此条记录
            enableKeyNav: true
        }),
        columns: columns
    });

    var title = "常用栏目功能管理";
    if (item_id > 0) title = "栏目功能配置--" + item_record.get("text");
    var win_config = {
        title: title,
        animateTarget: btn.getId(),
        items: [fun_grid],
        listeners: {
            afterrender: function (window, eOpts) {
                fun_grid.store.proxy.extraParams.item_id = item_id;
                fun_grid.store.reload();
            },
            show: function (window, eOpts) {
                fun_grid.isAction = false;
            },
            close: function (window, eOpts) {
                if (fun_grid.isAction) {
                    var fun_names = [];
                    fun_grid.store.each(function (rec) {
                        fun_names[fun_names.length] = rec.get("name");
                    });
                    item_record.set("fun_names", fun_names.join(","));
                    item_record.commit();
                    //grid.store.reload();
                }
            }
        },
        buttonAlign: "right",
        buttons: [{
            xtype: 'checkbox',
            labelWidth: 5, fieldLabel: ' ',
            boxLabel: '包含删除记录',
            labelSeparator: "", labelAlign: "right",
            listeners: {
                'change': function (item, checked) {
                    fun_grid.store.proxy.extraParams.isdel = checked ? 1 : 0;
                    fun_grid.store.reload();
                }
            }
        }, "->", {
            itemId: "btn_save_form",
            text: '新增', minWidth: 70,
            handler: function () {
                addNewRecordFn();
            }
        }, {
            text: '删除', minWidth: 70,
            handler: function () {
                deleteTableRow(fun_grid, "fun_id");
            }
        }, {
            text: '关闭', minWidth: 70,
            handler: function () {
                win.close();
            }
        }]
    }

    //删除设计表中的选中行
    function deleteTableRow(grid, primarykey) {
        var records = grid.getSelectionModel().getSelection();
        var Ids = [];
        for (var i = 0; i < records.length; i++) {
            Ids[Ids.length] = records[i].get(primarykey);
        }
        if (Ids.length == 0) {
            Ext.alert.msg('信息提示', '请选择要删除的行！');
        }
        else {
            Ext.MessageBox.confirm("提示!", "您确定要删除选中的" + Ids.length + "条记录信息吗?", function (btn) {
                if (btn == "yes") {
                    Ext.Ajax.request({
                        method: "POST", url: "system/fun/deleteing",
                        params: {item_id: item_id, fun_ids: Ids.join(",")},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                if (item_id > 0) grid.isAction = true;
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

    function deleteGridRow(grid, fun_id) {
        Ext.MessageBox.confirm("提示!", "您确定要永久删除这条功能信息吗?", function (btn) {
            if (btn == "yes") {
                Ext.Ajax.request({
                    method: "POST", url: "system/fun/deleted",
                    params: {item_id: item_id, fun_id: fun_id},
                    success: function (response, options) {
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success) {
                            if (item_id > 0) grid.isAction = true;
                            grid.store.reload();
                        } else {
                            showMsgByJson(resp, "恢复失败!");
                        }
                    },
                    failure: function (response, options) {
                        showMsgByResponse(response, "恢复失败!");
                    }
                });
            }
        });
    }

    function restoreGridRow(grid, fun_id) {
        Ext.MessageBox.confirm("提示!", "您确定要恢复这条功能信息吗?", function (btn) {
            if (btn == "yes") {
                Ext.Ajax.request({
                    method: "POST", url: "system/fun/restore",
                    params: {item_id: item_id, fun_id: fun_id},
                    success: function (response, options) {
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success) {
                            if (item_id > 0) grid.isAction = true;
                            grid.store.reload();
                        } else {
                            showMsgByJson(resp, "恢复失败!");
                        }
                    },
                    failure: function (response, options) {
                        showMsgByResponse(response, "恢复失败!");
                    }
                });
            }
        });
    }

    function addNewRecordFn() {
        var serialcode = 1;
        var count = fun_grid.store.getCount();
        if (fun_grid.store.getCount() > 0) {
            serialcode = fun_grid.store.getAt(count - 1).get("serialcode") + 1;
        }
        var record = getNewData(serialcode);
        fun_grid.store.insert(count, record);
        fun_grid.getView().refresh(); //刷新
        fun_grid.editingPlugin.startEdit(count, 2);
    }

    function getNewData(serialcode) {
        var addobject = {
            'fun_id': 0,
            'name': "功能名称",
            'itemid': "btn_add", //btn_add
            'per_value': "add" //add
        };
        if (fun_grid.store.getCount() > 0) {
            var record = fun_grid.store.getAt(fun_grid.store.getCount() - 1);
            addobject.name = record.get("name");
            addobject.itemid = record.get("itemid");
            addobject.per_value = record.get("per_value");
        }
        addobject.item_id = item_id;
        addobject.serialcode = serialcode;
        return Ext.create("model_sys_item_fun", addobject);
    }

    var win = Ext.create('widget.window', Ext.apply(win_config, {
        width: 560,
        height: item_id == 0 ? 385 : 360,
        closable: true,
        closeAction: 'destroy',
        plain: false,
        modal: true,
        layout: 'fit'
    }));
    win.show();
}



