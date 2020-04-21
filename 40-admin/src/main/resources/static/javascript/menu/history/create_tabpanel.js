//多表单创建Tab子表单：主表、1对1、1对多的Panel
function createMutilTabChailPanel(treenode, config_table, record, idleaf, isPosition) {
    var config = {itemId: "tab_" + config_table.itemid, title: config_table.table_explain, layout: "fit"};
    if (config_table.relation == "主表" || config_table.relation == "1对1") {
        if (!config_table.form_config) {
            Ext.alert.msg('提示', "请配置Form属性！");
        } else if (!config_table.form_items) {
            Ext.alert.msg('提示', "请生成表单items!");
        }
        var form_config = eval('(' + config_table.form_config + ')');
        form_config.items = eval('(' + config_table.form_items + ')');
        config.items = [Ext.create('Ext.form.Panel', form_config)];
    } else if (config_table.relation == "1对多") {
        if (config_table.layout_type == "single_cellediting")
            config.items = [new createTabGridCellediting(config_table, record, idleaf)];
        else if (config_table.layout_type == "single_rowediting")
            config.items = [new createTabGridRowediting(config_table, record, idleaf)];
        else if (config_table.layout_type == "single_winediting")
            config.items = [new createTabGridWinediting(config_table, record, idleaf)];
    } else if (config_table.relation == "附件列表") {
        config.items = createAttachmentGrid(treenode, idleaf, isPosition);
    }
    return config;
}


function designFormByCreateValues(forms, model_name) {
    if (forms && forms.length > 0) {
        var values = forms[0].getForm().getValues(false);
        designFormOriginalValue(forms[0], Ext.create(model_name, values));
    }
}

//多表单打开窗口时，点击重置
function resetMutilTable(tabs, obj_json, idleaf) {
    for (var i = 0; i < obj_json.config_tables.length; i++) {
        var config_table = obj_json.config_tables[i];
        if (config_table.relation == "主表" || config_table.relation == "1对1") {
            var forms = tabs.queryById("tab_" + config_table.itemid).query('form');
            if (forms.length > 0)
                forms[0].getForm().reset();
        }
        else if (idleaf == 0) {
            if (tabs.queryById("tab_" + config_table.itemid)) {
                var grids = tabs.queryById("tab_" + config_table.itemid).query('grid');
                if (grids && grids.length > 0) {
                    var grid = grids[0];
                    grid.store.proxy.extraParams.ids = "";
                    grid.store.proxy.extraParams.idleaf = idleaf;
                    grid.store.reload();
                }
            }
        }
    }
}

//多表单创建子表单“1对多”单元格编辑列表
function createTabGridCellediting(json, record, idleaf) {
    Ext.define(json.model_name, {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: json.model_fields
    });

    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: false, //是否自动加载
        proxy: {
            type: 'ajax', url: "info/util/grid",
            extraParams: {oid: json.oid, item_id: json.item_id, table: json.table_key, idleaf: idleaf},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                idleaf = store.proxy.extraParams.idleaf;
            }
        },
        model: json.model_name
    });
    var itemid = json.item_id;
    var columns = [new Ext.grid.RowNumberer({width: 30, tdCls: "blue"}),
        {text: 'id', width: 20, dataIndex: 'id', hideable: false, hidden: true}];
    for (var i = 0; i < json.columns_config.length; i++) {
        if (!json.columns_config[i].columns) continue;
        var column_obj = eval('(' + json.columns_config[i].columns + ')');
        var others = json.columns_config[i].others;
        if (others) {
            for (var j = 0; j < others.length; j++) {
                if (others[j].key && others[j].value) {
                    column_obj[others[j].key] = eval('(' + others[j].value + ')');
                }
            }
        }
        columns[columns.length] = column_obj;
    }

    var btn_add_type = "btn_add_cell";
    if (!$.cookie(btn_add_type)) $.cookie(btn_add_type, "2");
    var grid = Ext.create('Ext.grid.Panel', {
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
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!store.proxy.extraParams.mod_per) {
                        Ext.alert.msg('提示', "您暂无排序权限!");
                        return false;
                    }
                },
                drop: function (node, data, dropRec, dropPosition) {
                    //dragSortDesignChildTable(store, json.table_key);
                }
            }
        },
        border: false,
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 2,
            onSpecialKey: cellediting_onSpecialKey,
            listeners: {
                beforeedit: function (editor, e) {
                    if (!store.proxy.extraParams.mod_per) {
                        //Ext.alert.msg('提示', "您暂无修改权限!");
                        if (!grid.alloc_id || !grid.editor_fields) return false;
                    }
                    if (!cellediting_beforeedit(grid, this, e)) return false;
                    var xtype = e.column.getEditor().xtype, field = e.field;
                    if (xtype == "my97date") {
                        var record = e.record;
                        if (!e.column.getEditor().isChange) {
                            e.column.getEditor().isChange = true;
                            e.column.getEditor().on("change", function (field, eOpts) {
                                if (grid.alloc_id && grid.editor_fields) {
                                    trialUserSaveApplyEditingInfo(record, record.get("id"), e.field, field.getValue(), e.originalValue, grid, json.table_key);
                                } else {
                                    //celledingInfoSaveFn(record, "id", record.get("id"), e.field, field.getValue(), e.originalValue, itemid, json.table_key);
                                }
                            });
                        }
                    }
                    return true;
                },
                edit: function (editor, e) {
                    var record = e.record, xtype = e.column.getEditor().xtype;
                    var ischange = ischange_cellediting_edit(grid, this, e);
                    if (ischange) {
                        var value = e.value, format = e.column.getEditor().format;
                        if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") value = Ext.util.Format.date(value, format);
                        if (grid.alloc_id && grid.editor_fields) {
                            trialUserSaveApplyEditingInfo(record, record.get("id"), e.field, value, e.originalValue, grid, json.table_key);
                        } else {
                            //celledingInfoSaveFn(record, "id", record.get("id"), e.field, value, e.originalValue, itemid, json.table_key);
                        }
                    }
                }
            }
        }],
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi", //默认为多选multi;multi:多行选择，支持CTRL、SHIFT功能键，如果要进行多选，需要按住ctrl键。用shift可以进行区域选择；simple:多行选择，单击选中/取消选中行；single:单行选择；
            checkOnly: false, //如果值为true，则只用点击checkbox列才能选中此条记录
            enableKeyNav: true,
            onEditorTab: function (editingPlugin, e) {
                cellediting_onEditorTab(editingPlugin, this, e, isUpDown);
            }
        }),
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: 'btn_add',
                xtype: 'splitbutton', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    addNewRecordFn();
                },
                menu: [{
                    text: '新增到行首',
                    checked: $.cookie(btn_add_type) == "1",
                    group: btn_add_type,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_add_type, "1");
                        addNewRecordFn();
                    }
                }, {
                    text: '新增到行尾',
                    checked: $.cookie(btn_add_type) == "2",
                    group: btn_add_type,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_add_type, "2");
                        addNewRecordFn();
                    }
                }]
            }, '-', {
                itemId: 'btn_del',
                xtype: 'button', text: '删除',
                minWidth: 60, iconCls: 'icon_delete', //pressed: true,
                handler: function (btn, pressed) {
                    //deleteDesignTableRow(grid, "id", itemid, json.table_key);
                }
            }, '->', {
                xtype: 'checkbox',
                labelWidth: 90,
                fieldLabel: '回车向下移动',
                labelSeparator: "",
                labelAlign: "right",
                checked: isUpDown,
                listeners: {
                    'change': function (item, checked) {
                        isUpDown = checked;
                    }
                }
            }, '  ']
        }]
    });

    function addNewRecordFn() {
        var serialcode = 10000;
        var count = grid.store.getCount();
        if (grid.store.getCount() > 0) {
            if ($.cookie(btn_add_type) == "2")
                serialcode = grid.store.getAt(count - 1).get("serialcode") + 1;
            else
                serialcode = grid.store.getAt(0).get("serialcode") - 1;
        }
        var record = getNewData(serialcode);
        if ($.cookie(btn_add_type) == "2")
            addRow(record, count);
        else
            addRow(record, 0);
    }

    function getNewData(serialcode) {
        var addobject = eval('(' + json.addobject + ')');

        addobject.idleaf = idleaf;
        addobject.serialcode = serialcode;
        return Ext.create(json.model_name, addobject);
    }

    function addRow(record, index) {

    }

    var isUpDown = false;
    return grid;
}

//多表单创建子表单“1对多”行编辑列表
function createTabGridRowediting(json, record, idleaf) {
    Ext.define(json.model_name, {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: json.model_fields
    });

    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: false, //是否自动加载
        proxy: {
            type: 'ajax', url: "info/util/grid",
            extraParams: {oid: json.oid, item_id: json.item_id, table: json.table_key, idleaf: idleaf},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                idleaf = store.proxy.extraParams.idleaf;
            }
        },
        model: json.model_name
    });
    var itemid = json.item_id;
    var columns = [new Ext.grid.RowNumberer({width: 30, tdCls: "blue"}),
        {text: 'id', width: 20, dataIndex: 'id', hideable: false, hidden: true}];
    for (var i = 0; i < json.columns_config.length; i++) {
        if (!json.columns_config[i].columns) continue;
        var column_obj = eval('(' + json.columns_config[i].columns + ')');
        var others = json.columns_config[i].others;
        if (others) {
            for (var j = 0; j < others.length; j++) {
                if (others[j].key && others[j].value) {
                    column_obj[others[j].key] = eval('(' + others[j].value + ')');
                }
            }
        }
        columns[columns.length] = column_obj;
    }

    var btn_add_type = "btn_add_row";
    if (!$.cookie(btn_add_type)) $.cookie(btn_add_type, "2");
    var grid = Ext.create('Ext.grid.Panel', {
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
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!store.proxy.extraParams.mod_per) {
                        Ext.alert.msg('提示', "您暂无排序权限!");
                        return false;
                    }
                },
                drop: function (node, data, dropRec, dropPosition) {
                    //dragSortDesignChildTable(store, json.table_key);
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
                    if (!store.proxy.extraParams.mod_per) {
                        //Ext.alert.msg('提示', "您暂无修改权限!");
                        if (!grid.alloc_id || !grid.editor_fields) return false;
                    }
                    return rowediting_beforeedit(grid, this, e);
                },
                edit: function (editor, e) {
                    var me = this, rowIdx = e.rowIdx, record = e.record;
                    rowediting_edit_forms(grid, me, e);
                    setAddBtnDisabled(false);
                    var formData = record.getData(false);
                    formData["oid"] = json.oid;
                    formData["table"] = json.table_key;
                    formData["item_id"] = itemid;
                    if (grid.alloc_id && grid.editor_fields) {
                        trialUserSaveApplyChildInfo(formData, json.table_key, grid, record);
                    } else if (!grid.alloc_id) {

                    }
                },
                canceledit: function (editor, e) {
                    setAddBtnDisabled(false);
                    if (e.record.get("id") == 0) {
                        store.remove(e.record);
                    }
                },
                validateedit: function (editor, e) {
                    return rowediting_validateedit(this, columns);
                }
            }
        }],
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi", //默认为多选multi;multi:多行选择，支持CTRL、SHIFT功能键，如果要进行多选，需要按住ctrl键。用shift可以进行区域选择；simple:多行选择，单击选中/取消选中行；single:单行选择；
            checkOnly: false, //如果值为true，则只用点击checkbox列才能选中此条记录
            enableKeyNav: true
        }),
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: 'btn_add',
                xtype: 'splitbutton', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    addNewRecordFn();
                },
                menu: [{
                    text: '新增到行首',
                    checked: $.cookie(btn_add_type) == "1",
                    group: btn_add_type,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_add_type, "1");
                        addNewRecordFn();
                    }
                }, {
                    text: '新增到行尾',
                    checked: $.cookie(btn_add_type) == "2",
                    group: btn_add_type,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_add_type, "2");
                        addNewRecordFn();
                    }
                }]
            }, '-', {
                itemId: 'btn_del',
                xtype: 'button', text: '删除',
                minWidth: 60, iconCls: 'icon_delete', //pressed: true,
                handler: function (btn, pressed) {
                    //deleteDesignTableRow(grid, "id", itemid, json.table_key);
                }
            }]
        }]
    });

    function setAddBtnDisabled(disabled) {
        var btn = grid.queryById('btn_add');
        btn.setDisabled(disabled);
    }

    function addNewRecordFn() {
        var serialcode = 10000;
        var count = grid.store.getCount();
        if (grid.store.getCount() > 0) {
            if ($.cookie(btn_add_type) == "2")
                serialcode = grid.store.getAt(count - 1).get("serialcode") + 1;
            else
                serialcode = grid.store.getAt(0).get("serialcode") - 1;
        }
        setAddBtnDisabled(true);
        var record = getNewData(serialcode);
        if ($.cookie(btn_add_type) == "2")
            addRow(record, count);
        else
            addRow(record, 0);
    }

    function getNewData(serialcode) {
        var addobject = eval('(' + json.addobject + ')');

        addobject.idleaf = idleaf;
        addobject.serialcode = serialcode;
        return Ext.create(json.model_name, addobject);
    }

    function addRow(record, index) {
        grid.store.insert(index, record);
        grid.getView().refresh(); //刷新
        grid.editingPlugin.startEdit(index, 2);
    }

    return grid;
}

//多表单创建子表单“1对多”窗口编辑列表
function createTabGridWinediting(json, record, idleaf) {
    Ext.define(json.model_name, {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: json.model_fields
    });
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: false, //是否自动加载
        proxy: {
            type: 'ajax', url: "info/util/grid",
            extraParams: {oid: json.oid, item_id: json.item_id, table: json.table_key, idleaf: idleaf},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                idleaf = store.proxy.extraParams.idleaf;
            }
        },
        model: json.model_name
    });
    var itemid = json.item_id;
    var columns = [new Ext.grid.RowNumberer({width: 30, tdCls: "blue"}),
        {text: 'id', width: 20, dataIndex: 'id', hideable: false, hidden: true}];
    for (var i = 0; i < json.columns_config.length; i++) {
        if (!json.columns_config[i].columns) continue;
        var column_obj = eval('(' + json.columns_config[i].columns + ')');
        var others = json.columns_config[i].others;
        if (others) {
            for (var j = 0; j < others.length; j++) {
                if (others[j].key && others[j].value) {
                    column_obj[others[j].key] = eval('(' + others[j].value + ')');
                }
            }
        }
        columns[columns.length] = column_obj;
    }

    var btn_add_type = "btn_add_window";
    if (!$.cookie(btn_add_type)) $.cookie(btn_add_type, "2");
    var grid = Ext.create('Ext.grid.Panel', {
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
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!store.proxy.extraParams.mod_per) {
                        Ext.alert.msg('提示', "您暂无排序权限!");
                        return false;
                    }
                },
                drop: function (node, data, dropRec, dropPosition) {
                    //dragSortDesignChildTable(store, json.table_key);
                }
            }
        },
        listeners: {
            'itemdblclick': function (gridview, record, item, index) {

            }
        },
        border: false,
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi", //默认为多选multi;multi:多行选择，支持CTRL、SHIFT功能键，如果要进行多选，需要按住ctrl键。用shift可以进行区域选择；simple:多行选择，单击选中/取消选中行；single:单行选择；
            checkOnly: false, //如果值为true，则只用点击checkbox列才能选中此条记录
            enableKeyNav: true
        }),
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: 'btn_add',
                xtype: 'splitbutton', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    addNewRecordFn();
                },
                menu: [{
                    text: '新增到行首',
                    checked: $.cookie(btn_add_type) == "1",
                    group: btn_add_type,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_add_type, "1");
                        addNewRecordFn();
                    }
                }, {
                    text: '新增到行尾',
                    checked: $.cookie(btn_add_type) == "2",
                    group: btn_add_type,
                    xtype: 'menucheckitem',
                    handler: function () {
                        $.cookie(btn_add_type, "2");
                        addNewRecordFn();
                    }
                }]
            }, '-', {
                itemId: 'btn_del',
                xtype: 'button', text: '删除',
                minWidth: 60, iconCls: 'icon_delete', //pressed: true,
                handler: function (btn, pressed) {
                    //deleteDesignTableRow(grid, "id", itemid, json.table_key);
                }
            }]
        }]
    });

    function addNewRecordFn() {
        var serialcode = 10000;
        var count = grid.store.getCount();
        if (grid.store.getCount() > 0) {
            if ($.cookie(btn_add_type) == "2")
                serialcode = grid.store.getAt(count - 1).get("serialcode") + 1;
            else
                serialcode = grid.store.getAt(0).get("serialcode") - 1;
        }
        var record = getNewData(serialcode);
        addRow(record);
    }

    function getNewData(serialcode) {
        var addobject = eval('(' + json.addobject + ')');
        addobject.idleaf = idleaf;
        addobject.serialcode = serialcode;
        return Ext.create(json.model_name, addobject);
    }

    function addRow(record) {
        if (!json.win_config) {
            Ext.alert.msg('提示', "请配置窗口属性!");
            return;
        }
        else if (!json.form_config) {
            Ext.alert.msg('提示', "请配置Form属性！");
            return;
        }
        else if (!json.form_items) {
            Ext.alert.msg('提示', "请生成表单items!");
            return;
        }
        var win_config = eval('(' + json.win_config + ')');
        var form_config = eval('(' + json.form_config + ')');
        form_config.items = eval('(' + json.form_items + ')');

        if (!win_config.animateTarget)
            win_config.animateTarget = grid.queryById('btn_add').getId();
        var form = Ext.create('Ext.form.Panel', form_config);
        var windows_config = {
            title: '添加表单',
            items: [form],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, record);
                    if (grid.editor_fields) setFlowApplyFormEditorFields(window, form, grid.editor_fields);
                    else if (!store.proxy.extraParams.mod_per) setFlowApplyFormEditorFields(window, form, "");
                },
                show: function (window, eOpts) {
                    if (!store.proxy.extraParams.mod_per && !grid.editor_fields) {
                        var button = window.queryById("btn_save_form");
                        if (button) button.setDisabled(true);
                    }
                    if (record.get("idleaf") == 0 || !record.get("idleaf"))
                        window.setTitle(json.table_explain + "-信息新增");
                    else
                        window.setTitle(json.table_explain + "-信息修改");
                }
            },
            buttonAlign: "right",
            buttons: [{
                itemId: "btn_save_form",
                text: '提交', minWidth: 70,
                handler: function () {
                    if (form.isValid()) {
                        if (grid.alloc_id && grid.editor_fields) {
                            var formData = form.getForm().getValues(false);
                            trialUserSaveApplyChildInfo(formData, json.table_key, grid, window);
                        } else if (!grid.alloc_id) {

                        }
                    } else {
                        Ext.alert.msg('无效输入', '请输入正确的信息!');
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
                    window.close();
                }
            }]
        }
        var window = Ext.create('widget.window', Ext.apply(windows_config, win_config));
        window.show();
    }

    return grid;
}

//多表单创建子表单文件上传列表
function createAttachmentGrid(treenode, idleaf, isPosition) {
    var item_id = 0;
    if (treenode != null) item_id = treenode.raw.id;
    Ext.define('model_attach_file', {
        extend: 'Ext.data.Model',
        idProperty: 'attach_id',
        fields: [
            {name: 'attach_id', type: 'int'},
            {name: 'idleaf', type: 'int'},
            {name: 'serialcode', type: 'int'},
            {name: 'attach_name', type: 'string'},
            {name: 'attach_type', type: 'string'},
            {name: 'attach_size', type: 'string'},
            {name: 'attach_add', type: 'string'}
        ]
    });
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: false, //是否自动加载
        proxy: {
            type: 'ajax', url: "info/file/grid",
            extraParams: {item_id: item_id, idleaf: idleaf, ids: ""}, //, idleaf: idleaf, ids: Att_Ids
            reader: {
                type: 'json', root: 'root',
                idProperty: 'attach_id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                idleaf = store.proxy.extraParams.idleaf;
            }
        },
        model: 'model_attach_file'
    });
    var columns = [
        new Ext.grid.RowNumberer({width: 30, fixed: true, tdCls: "blue"}),
        {text: 'attach_id', dataIndex: 'attach_id', hideable: false, hidden: true},
        {text: 'serialcode', dataIndex: 'serialcode', hideable: false, hidden: true},
        {
            text: '文件名称', width: 230, dataIndex: 'attach_name', sortable: false,
            editor: {
                allowBlank: false, minLength: 2, maxLength: 50,
                xtype: 'textfield', selectOnFocus: false  //点击编辑框后，变成全选状态 
            }
        }, //自定义文件名
        {text: '文件类型', width: 90, dataIndex: 'attach_type', sortable: false},
        {text: '文件大小', width: 90, dataIndex: 'attach_size', sortable: false},
        {
            text: '下载',
            width: 80,
            fixed: true,
            dataIndex: 'attach_add',
            align: 'center',
            renderer: function (value, metaData, record) {
                return getA(value);
            }
        }
    ];

    function getA(attach_add) {
        //return Ext.String.format('<a href="{0}" target="_blank">下载</a>', attach_add);
        return Ext.String.format('<a href="javascript:void(0)" target="_blank" onclick="setItemClickFlag(\'attach_add\')">下载</a>', attach_add);
    }

    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "object_att",
        store: store, border: false,
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
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!store.proxy.extraParams.mod_per) {
                        Ext.alert.msg('提示', "您暂无排序权限!");
                        return false;
                    }
                },
                drop: function (node, data, dropRec, dropPosition) {
                    var index = 1, sort_vals = [];
                    store.each(function (record) {
                        sort_vals[sort_vals.length] = record.get("attach_id") + ":" + index++;
                    });
                    //Ext.Msg.alert("提示", '排序 ' + sort_vals.join(";"));
                    Ext.Ajax.request({
                        url: "info/file/sort", method: "POST",
                        params: {sort_vals: sort_vals.join(";")},
                        success: function (response, options) {
                            store.load();
                        },
                        failure: function (response, options) {
                            store.load();
                        }
                    });
                }
            }
        },
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 2
        }],
        listeners: {
            'cellclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "attach_add" && itemClickFlag == "attach_add") {
                    var attach_name = rec.get("attach_name");
                    var index1 = attach_name.lastIndexOf(".");
                    if (index1 > 0) attach_name = attach_name.substring(0, index1);
                    downLoadFile(rec.get("attach_add") + "||" + attach_name + "." + rec.get("attach_type"));
                }
            },
            beforeedit: function (editor, e) {
                if (!store.proxy.extraParams.mod_per) {
                    //Ext.alert.msg('提示', "您暂无修改权限!");
                    return false;
                }
                return true;
            },
            edit: function (editor, e) {
                var record = e.record;
                var ischange = e.originalValue + "" != e.value + "";
                var attach_id = record.get("attach_id");
                if (ischange) {
                    Ext.Ajax.request({
                        method: "POST", url: "info/file/rename",
                        params: {id: attach_id, name: e.value},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (!resp.success)
                                record.set("attach_name", e.originalValue);
                            record.commit();
                        },
                        failure: function (response, options) {
                            record.set("attach_name", e.originalValue);
                            record.commit();
                        }
                    });
                }
            }
        },
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi", //默认为多选multi;multi:多行选择，支持CTRL、SHIFT功能键，如果要进行多选，需要按住ctrl键。用shift可以进行区域选择；simple:多行选择，单击选中/取消选中行；single:单行选择；
            checkOnly: false, //如果值为true，则只用点击checkbox列才能选中此条记录
            enableKeyNav: true
        }),
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: 'btn_add',
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true, 
                handler: function (btn, pressed) {
                    if (treenode == null) {
                        Ext.alert.msg('信息提示', '预览模式，不可操作！');
                        return;
                    }
                    if (typeof (treenode.raw.uploadfile_type) == 'undefined') {
                        Ext.Ajax.request({
                            method: "POST", url: "system/item/filetype/info",
                            params: {item_id: item_id},
                            success: function (response, options) {
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    treenode.raw.uploadfile_type = resp.uploadfile_type;
                                } else {
                                    //showMsgByJson(resp, "未获取到上传的附件类型!");
                                }
                                addFile(treenode, btn);
                            }
                        });
                    } else {
                        addFile(treenode, btn);
                    }
                }
            }, '-', {
                itemId: 'btn_del',
                xtype: 'button', text: '删除',
                minWidth: 60, iconCls: 'icon_delete', //pressed: true, 
                handler: function (btn, pressed) {
                    var ids = [];
                    var records = grid.getSelectionModel().getSelection();
                    for (var i = 0; i < records.length; i++) {
                        ids[ids.length] = records[i].get("attach_id");
                    }
                    if (ids.length > 0) {
                        Ext.MessageBox.confirm("提示!", "您确定要删除选中的记录信息吗?", function (btn) {
                            if (btn == "yes") {
                                Ext.Ajax.request({
                                    method: "POST", url: "info/file/delete",
                                    params: {ids: ids.join(",")},
                                    success: function (response, options) {
                                        var resp = Ext.JSON.decode(response.responseText);
                                        if (!resp.success)
                                            errorBoxShow('删除失败！');
                                        else
                                            grid.store.reload();
                                    },
                                    failure: function (response, options) {
                                        errorBoxShow('删除失败！');
                                    }
                                });
                            }
                        });
                    }
                    else {
                        Ext.alert.msg('信息提示', '请选择要删除的行！');
                    }
                }
            }, '->', '拖动排序']
        }]
    });

    return grid;

    function addFile(treenode, btn) {
        var file_types = "";
        if (treenode.raw.uploadfile_type)
            file_types = convertFileType(treenode.raw.uploadfile_type);
        else
            file_types = getUploadFileType();

        var win = Ext.create('widget.window', {
            title: '添加附件(' + file_types + ')',
            //x: position[0], y: position[1] + 24,
            animateTarget: btn.getId(),
            width: 700, height: 400,
            closable: false, border: false,
            resizable: true, autoShow: true,
            closeAction: 'destroy',  //destroy，hide
            plain: false, modal: true, layout: 'fit',
            items: [
                {
                    itemId: 'uploadPanel',
                    xtype: 'uploadPanel',
                    addFileBtnText: '选择文件',
                    file_size_limit: getFileMaxSize(), //MB
                    file_upload_limit: 5,
                    file_types: file_types,
                    file_types_description: '自定义文件',
                    upload_url: "info/file/swfupload",
                    post_params: {item_id: item_id, idleaf: store.proxy.extraParams.idleaf}
                }
            ],
            listeners: {
                beforerender: function (window, eOpts) {
                    if (isPosition) {
                        var position = btn.getPosition();
                        window.setPosition(position[0], position[1] + 24, true);
                    }
                }
            },
            buttonAlign: "right",  //'right', 'left' 和 'center'(对于所有的buttons/fbar默认为'right'，对于toolbar 则默认为'left')
            buttons: [{
                text: '确定',
                handler: function () {
                    var grid = win.getComponent("uploadPanel");
                    var status = 0, status1 = 0, status2 = 0, ids = [];
                    grid.store.each(function (record) {
                        if (record.get("attach_id") > 0)
                            ids[ids.length] = record.get("attach_id");
                        status = record.get("status");
                        if (status == -1) status1++;
                        else if (status == -2) status2++;
                    });
                    if (status2 > 0) {
                        alertBoxShow("文件列表中有正在上传的文件！");
                    } else if (status1 > 0 && ids.length == 0) {
                        alertBoxShow("选择文件后并未上传，请点击“上传”按钮！");
                    } else if (status1 > 0 && ids.length > 0) {
                        Ext.MessageBox.confirm("提示!", "还有未上传的文件，是否确定退出?", function (btn) {
                            if (btn == "yes") {
                                confirmBtnFn(ids);
                            }
                        });
                    } else if (ids.length > 0) {
                        confirmBtnFn(ids);
                    } else {
                        win.close();
                    }
                }
            }, {
                text: '关闭',
                handler: function () {
                    var grid = win.getComponent("uploadPanel");
                    if (!grid) {
                        win.close();
                        return;
                    }
                    var status = 0;
                    grid.store.each(function (record) {
                        if (record.get("status") == -2) {
                            status = record.get("status");
                            return;
                        }
                    });
                    if (status == -2) {
                        Ext.alert.msg('提示', "列表中有正在上传的文件，请“取消上传”，再关闭！")
                    } else {
                        var ids = [];
                        grid.store.each(function (record) {
                            if (record.get("attach_id") > 0)
                                ids[ids.length] = record.get("attach_id");
                        });
                        if (ids.length > 0) {  //关闭删除已经上传的附件
                            Ext.Ajax.request({
                                method: "POST", url: "info/file/delete",
                                params: {ids: ids.join(",")},
                                success: function (response, options) {
                                },
                                failure: function (response, options) {
                                }
                            });
                        }
                        win.close();
                    }
                }
            }]
        });

        function confirmBtnFn(ids) {
            var att_ids = store.proxy.extraParams.ids;
            if (att_ids == "")
                store.proxy.extraParams.ids = ids.join(",");
            else
                store.proxy.extraParams.ids = att_ids + "," + ids.join(",");
            grid.store.reload();
            win.close();
        }
    }
}
