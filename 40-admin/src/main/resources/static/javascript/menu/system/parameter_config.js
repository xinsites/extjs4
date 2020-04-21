Ext.define('javascript.menu.system.parameter_config', {
    extend: ''
});

function createPanel_Config(treenode) {
    var itemid = treenode.raw.id;
    var tabId = "tab_panel_" + treenode.raw.id;
    Ext.define('model_sys_config', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'org_id', type: 'int'},
            {name: 'item_id', type: 'int'},
            {name: 'config_key', type: 'string'},
            {name: 'field_explain', type: 'string'},
            {name: 'config_editor', type: 'string'},
            {name: 'config_value', type: 'string'},
            {name: 'config_text', type: 'string'},
            {name: "issys", "type": "int"}
        ]
    });

    function getNewData(config_editor) {
        return Ext.create('model_sys_config', {
            'id': 0,
            'org_id': 0,
            'item_id': itemid,
            'config_editor': config_editor
        });
    }

    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true,
        proxy: {
            type: 'ajax', url: "system/config/grid",
            extraParams: {item_id: itemid},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'id',
                totalProperty: 'totalProperty'
            }
        },
        model: 'model_sys_config'
    });
    var columns = [
        new Ext.grid.RowNumberer({width: 40, tdCls: "blue"}),
        {
            text: '变量名称', width: 170, fixed: true, align: 'right', dataIndex: 'config_key', sortable: false,
            renderer: function (val, meta, rec) {
                return '<span style="color:blue;">' + val + '</span>';
            }
        },
        {
            text: '变量值(<span style="color:blue">可编</span>)', width: 80, dataIndex: 'config_value', sortable: false,
            editor: {
                xtype: 'textfield', maxLength: 50
            },
            renderer: function (val, meta, rec) {
                if (rec.get("config_key") == "add_password") return getLenChar("*", val.length);
                else if (rec.get("config_key") == "reset_password") return getLenChar("*", val.length);
                else if (rec.get("config_text") != "") return rec.get("config_text");
                return val;
            }
        },
        {
            text: '变量描述', width: 100, dataIndex: 'field_explain', hideable: false, sortable: false,
            renderer: function (val, meta, rec) {
                if (rec.get("issys") == "1") {
                    return '<span style="color:blue;">[内置]</span>' + val;
                } else {
                    return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'btn_del\')">[删除]</span>' + val;
                }
            }
        }
    ];
    var originalText = "";
    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid",
        store: store,
        multiSelect: false,
        columnLines: true,
        disableSelection: false,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            enableTextSelection: true
        },
        listeners: {
            'cellclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "field_explain" && itemClickFlag == "btn_del") {
                    if (isExistsByItemId(treenode, "btn_del", "系统参数配置删除"))
                        deleteGridRow(grid, rec.get("id"));
                }
            }
        },
        selModel: Ext.create("Ext.selection.RowModel", {
            mode: "single",
            enableKeyNav: true,
            onEditorTab: function (editingPlugin, e) {
                cellediting_onEditorTab(editingPlugin, this, e, false);
            }
        }),
        border: false,
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 2,
            onSpecialKey: cellediting_onSpecialKey,
            listeners: {
                beforeedit: function (editor, e) {
                    if (!isExistsByItemId(treenode, "btn_mod", "编辑")) return false;
                    var record = e.record, editor = e.column.getEditor();
                    var xtype = editor.xtype;
                    if (e.field == "config_value") {
                        if (record.get("config_editor")) {  //动态改变编辑框
                            try {
                                editor = eval('(' + record.get("config_editor") + ')');
                            } catch (e) {
                                return false;
                            }
                        } else {
                            editor = {xtype: 'textfield', maxLength: 50};
                        }
                        editor.fieldLabel = "";
                        editor.labelSeparator = "";
                        grid.headerCt.getGridColumns()[e.colIdx].setEditor(editor);
                        xtype = editor.xtype;
                    }
                    if (xtype == "textareafield") {
                        openTextAreaFieldWinFn(record, editor);
                        return false;
                    }

                    if (!cellediting_beforeedit(grid, this, e)) return false;
                    originalText = record.get("config_text");
                    return true;
                },
                edit: function (editor, e) {
                    var record = e.record, xtype = e.column.getEditor().xtype;
                    var ischange = ischange_cellediting_edit(grid, this, e);
                    if (ischange) {
                        var value = e.value, format = e.column.getEditor().format;
                        if (xtype == "datefield" || xtype == "datetimefield") value = Ext.util.Format.date(value, format);
                        rowCellInfoSaveFn(record, xtype, value, originalText, e.originalValue, record.get("config_text"));
                    }
                }
            }
        }],
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', '<b>搜索:</b>',
                {
                    xtype: 'textfield', width: 220, emptyText: "变量名称检索，请按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                designSearchByField(store, 'key_config', "config_key", field.getValue());
                            }
                        }
                    }
                }, {
                    xtype: 'button', text: '新增',
                    itemId: 'btn_add', iconCls: "icon_add",
                    handler: function (btn, pressed) {
                        var records = grid.getSelectionModel().getSelection();
                        if (records.length > 0) {
                            openAddWindow(records[0]);
                        } else {
                            Ext.alert.msg('提示', "请选择一条记录作参考!");
                        }
                    }
                }, '->', {
                    xtype: 'displayfield', maxHeight: 24,
                    value: '<span style="color:blue;">新增时以选择的记录拷贝新增</span>'
                }]
        }]
    });

    function deleteGridRow(grid, id) {
        Ext.MessageBox.confirm("提示!", "您确定要删除这条配置信息吗?", function (btn) {
            if (btn == "yes") {
                Ext.Ajax.request({
                    method: "POST", url: "system/config/delete",
                    params: {item_id: itemid, id: id},
                    success: function (response, options) {
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success) {
                            grid.store.reload();
                        } else {
                            showMsgByJson(resp, "删除失败!");
                        }
                    },
                    failure: function (response, options) {
                        showMsgByResponse(response, "删除失败!");
                    }
                });
            }
        });
    }

    //编辑系统配置中的单元格
    function rowCellInfoSaveFn(record, xtype, value, text, originalValue, originalText) { //字段编辑保存
        Ext.Ajax.request({
            method: "POST", url: "system/config/editing",
            params: {
                id: record.get("id"), config_key: record.get("config_key"),
                item_id: itemid, value: value, text: text
            },
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    record.set("config_text", text);
                    record.commit();
                }
                else {
                    Ext.alert.msg('提示', "保存失败!");
                    record.set("config_value", originalValue);
                    record.set("config_text", originalText);
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

    //textareafield编辑框窗口
    function openTextAreaFieldWinFn(record, editor) {
        var form = Ext.create('Ext.form.Panel', {
            autoScroll: true, border: false,
            defaultType: 'textfield',
            fieldDefaults: {
                labelAlign: 'left',
                msgTarget: 'qtip',
                labelWidth: 100
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [editor]
        });
        var width = 500, height = 500;
        if (editor.width) width = editor.width;
        if (editor.height) height = editor.height;
        var FieldEditWin = Ext.create('Ext.window.Window', {
            title: record.get("field_explain"),
            width: width, height: height, form: form,
            resizable: true, closable: true, maximizable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            layout: "fit",
            items: [form],
            listeners: {
                "show": function (window, eOpts) {
                    var field = form.queryById(record.get("config_key"))
                    if (field) field.setValue(record.get("config_value"));
                }
            },
            buttonAlign: "right",
            buttons: [{
                text: "保存",
                minWidth: 70,
                handler: function () {
                    var field = form.queryById(record.get("config_key"));
                    if (field) {
                        Ext.Ajax.request({
                            method: "POST", url: "system/config/editing",
                            params: {
                                id: record.get("id"), config_key: record.get("config_key"),
                                item_id: itemid, value: field.getValue(), text: record.get("config_text")
                            },
                            success: function (response, options) {
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    record.set("config_value", field.getValue());
                                    record.commit();
                                    FieldEditWin.close();
                                } else {
                                    Ext.alert.msg('提示', "保存失败!");
                                }
                            },
                            failure: function (response, options) {
                                Ext.alert.msg('提示', "保存失败!");
                            }
                        });
                    }
                }
            }, {
                text: '重置',
                minWidth: 70,
                handler: function () {
                    var field = form.queryById(record.get("config_key"))
                    if (field) field.setValue(record.get("config_value"));
                }
            }, {
                text: "关闭",
                minWidth: 70,
                handler: function () {
                    FieldEditWin.close();
                }
            }]
        });
        FieldEditWin.show();
    }

    function openAddWindow(record) {
        var btn = grid.queryById('btn_add');
        var form = createConfigForm(record);
        var jsonviewer = function () {
            var edit = form.queryById('config_editor');
            return {
                format: function () {
                    var text = edit.getValue().split("\n").join(" ");
                    var t = [];
                    var tab = 0;
                    var inString = false;
                    for (var i = 0, len = text.length; i < len; i++) {
                        var c = text.charAt(i);
                        if (inString && c === inString) {
                            if (text.charAt(i - 1) !== '\\') {
                                inString = false;
                            }
                        } else if (!inString && (c === '"' || c === "'")) {
                            inString = c;
                        } else if (!inString && (c === ' ' || c === "\t")) {
                            c = '';
                        } else if (!inString && c === ':') {
                            c += ' ';
                        } else if (!inString && c === ',') {
                            c += "\n" + String.space(tab * 2);
                        } else if (!inString && (c === '[' || c === '{')) {
                            tab++;
                            c += "\n" + String.space(tab * 2);
                        } else if (!inString && (c === ']' || c === '}')) {
                            tab--;
                            c = "\n" + String.space(tab * 2) + c;
                        }
                        t.push(c);
                    }
                    edit.setValue(t.join(''));
                }
            };
        }();
        var win = Ext.create('widget.window', {
            title: '新增系统参数',
            animateTarget: btn.getId(),
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    jsonviewer.format();
                }
            },
            width: 450,
            height: 380,
            maximizable: false,
            resizable: true,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit',
            buttonAlign: "right",
            buttons: ["->", {
                itemId: "btn_save_form",
                text: '保存', minWidth: 70,
                listeners: {
                    click: function () {
                        if (form.isValid()) {
                            form.submit({
                                method: "POST", url: "system/config/add",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false,
                                params: {item_id: itemid},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        grid.store.reload();
                                        Ext.alert.msg("提示", "新增成功！");
                                        win.close();
                                    } else {
                                        showMsgBySubmit(action, "新增失败！");
                                    }
                                },
                                failure: function (form, action) {
                                    showMsgBySubmit(action, "新增失败！");
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
                    jsonviewer.format();
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

    function createConfigForm(record) {
        var form_config = {
            border: false,
            bodyPadding: '20 15 10',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 80}
        };
        form_config.items = [{xtype: 'hiddenfield', name: 'id'}, {
            xtype: 'textfield',
            name: 'config_key',
            itemId: 'config_key',
            fieldLabel: '变量名称',
            value: "config_key1",
            allowBlank: false,
            maxLength: 50,
            beforeLabelTextTpl: '*'
        }, {
            xtype: 'textfield',
            name: 'field_explain',
            itemId: 'field_explain',
            fieldLabel: '变量描述',
            value: "系统变量1",
            allowBlank: false,
            maxLength: 50,
            beforeLabelTextTpl: '*'
        }, {
            xtype: 'textareafield',
            name: 'config_editor',
            itemId: 'config_editor',
            fieldLabel: '输入框配置',
            beforeLabelTextTpl: '*',
            value: record.get("config_editor"),
            allowBlank: false, maxLength: 1000,
            rows: 3, height: 40, flex: 1
        }];
        return Ext.create('Ext.form.Panel', form_config);
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}

