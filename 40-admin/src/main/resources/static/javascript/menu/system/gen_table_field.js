Ext.define('javascript.menu.system.gen_table_field', {
    extend: ''
});

function createGenTableFieldPanel(treenode) {
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
            , 'text', 'serialcode', 'object_type', 'layout_type', 'object_pre', 'group_id', 'expand'],
        listeners: {
            'load': function (store, records) {
                setTimeout(function () {
                    var rec = store.getNodeById(grid.store.proxy.extraParams.oid);
                    if (rec) tree.getView().getSelectionModel().select(rec)
                }, 100);
            }
        }
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
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    treeview.expand(record, false);
                }
                var tree_table = grid.queryById('object_table');
                grid.store.proxy.extraParams.oid = record.get('id');
                tree_table.store.proxy.extraParams.oid = record.get('id');
                tree_table.store.load();

            }
        }
    });

    Ext.define('model_gen_field', {
        extend: 'Ext.data.Model',
        idProperty: 'fid',
        fields: [
            {name: 'fid', type: 'int'},
            {name: 'tid', type: 'int'},
            {name: 'object_name', type: 'string'},
            {name: 'table_name', type: 'string'},
            {name: 'table_explain', type: 'string'},
            {name: 'tb_relation', type: 'string'},
            {name: 'field_name', type: 'string'},
            {name: 'field_explain', type: 'string'},
            {name: 'field_tag', type: 'string'},
            {name: 'xtype', type: 'string'},
            {name: 'xtype_name', type: 'string'},
            {name: 'editor_search', type: 'string'},
            {name: 'isdefine', type: 'int'},
            {name: 'issearchfield', type: 'bool'},
            {name: 'serialcode', type: 'int'}
        ]
    });

    var table_name = "";
    var pageSize = getGridPageSize("table_field");
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "system/gen/field/grid",
            extraParams: {tid: 0, item_id: item_id},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'fid',
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
        pageSize: pageSize,
        model: 'model_gen_field'
    });
    var columns = [
        new Ext.grid.RowNumberer({width: 40, tdCls: "blue"}),
        {text: 'fid', width: 20, dataIndex: 'fid', hideable: false, hidden: true},
        {text: 'tid', width: 20, dataIndex: 'tid', hideable: false, hidden: true},
        {text: '数据对象', width: 145, dataIndex: 'object_name', fixed: true, sortable: false},
        {text: '数据库表', width: 185, dataIndex: 'table_name', fixed: true, sortable: false},
        {text: '字段名称', width: 140, dataIndex: 'field_name', fixed: true, sortable: false},
        {
            text: '字段中文描述', width: 110, dataIndex: 'field_explain', sortable: false,
            renderer: function (val, meta, rec) {
                if (rec.get("field_tag") == "title")
                    return val + "<span style='color:blue;'>[标题]</span>";
                return val;
            }
        },
        {
            xtype: 'checkcolumn',
            text: '<span style="color:blue">查询字段</span>',
            width: 80,
            fixed: true,
            dataIndex: 'issearchfield',
            listeners: {
                'checkchange': function (field, recordIndex, checked, record) {
                    if (!isExistsByItemId(treenode, "btn_mod", "编辑")) {
                        record.set(field.dataIndex, !checked);
                        return false;
                    }
                    if (record.get("tb_relation") == "附件列表") {
                        Ext.alert.msg('提示', "附件列表不能修改!");
                        record.set(field.dataIndex, !checked);
                        record.commit();
                    } else {
                        editingFieldInfoFn(record, record.get("fid"), field.dataIndex, checked ? 1 : 0, !checked);
                    }
                }
            }
        },
        {
            text: '定义类型', width: 80, align: 'center', dataIndex: 'isdefine', fixed: true, sortable: false,
            renderer: function (val, meta, rec) {
                if (val == "1")
                    return '<span style="color:blue;">自定义</span>';
                else
                    return '<span style="color:green;">自带</span>';
            }
        },
        {
            text: '<span style="color:blue">查询输入框</span>',
            width: 150,
            dataIndex: 'xtype_name',
            fixed: true,
            sortable: false,
            renderer: function (val, meta, rec) {
                if (rec.get("tb_relation") == "附件列表")
                    return '<span style="color:green;">无需定义</span>';
                else if (val != "" && rec.get("editor_search") != "")
                    return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'xtype_name\')">' + val + '</span>';
                else
                    return '<span style="color:red;font-weight:bold;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'xtype_name\')">未定义</span>';
            }
        }
    ];

    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid1",
        item_id: item_id,
        tb_relation: "",
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            preserveScrollOnRefresh: true,
            enableTextSelection: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_sort", "排序")) return false;
                    if (grid.store.proxy.extraParams.tid == 0) {
                        Ext.alert.msg('提示', "先选定一个数据库表，才能排序。");
                        return false;
                    }
                    return true;
                },
                drop: function (node, data, dropRec, dropPosition) {
                    if (store && store.getCount() > 0) {
                        var sort_vals = [], first_index = store.getAt(0).get("serialcode");
                        store.each(function (record) {
                            sort_vals[sort_vals.length] = record.get("fid") + ":" + first_index++;
                        });
                        Ext.Ajax.request({
                            url: "system/gen/field/sort", method: "POST",
                            params: {
                                item_id: item_id,
                                sort_vals: sort_vals.join(";"), field: "serialcode"
                            },
                            success: function (response, options) {
                                store.load();
                            },
                            failure: function (response, options) {
                                store.load();
                            }
                        });
                    }
                }
            }
        },
        listeners: {
            'cellclick': function (gridview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "xtype_name" && itemClickFlag == "xtype_name") {
                    if (!isExistsByItemId(treenode, "btn_mod", "输入框定义")) return false;
                    openSaveEditorFormWinFn(grid, rec);
                }
            }
        },
        border: false,
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['<b>搜索:</b>', {
                itemId: 'object_table',
                xtype: 'treepicker', editable: false,
                emptyText: "==选择数据库表==",
                minPickerWidth: 300,
                pickerResizable: false,
                // hideHeaders: true,
                pickerEmptyText: "暂无记录!",
                width: 180, multiSelect: false,
                store: Ext.create('Ext.data.TreeStore', {
                    nodeParam: 'node', autoLoad: false,
                    proxy: {
                        type: 'ajax', url: "system/gen/table/tree",
                        reader: {
                            type: 'json', id: "id"
                        },
                        extraParams: {oid: 0, expanded: 0}
                    },
                    root: {text: '根节点', id: '0', expanded: true},
                    listeners: {
                        'load': function (store, record) {
                            grid.store.proxy.extraParams.tid = 0;
                            var tree_table = grid.queryById('object_table');
                            tree_table.clearValue();
                            grid.store.proxy.extraParams.tid = 0;
                            if (record.childNodes.length == 1) {
                                // var tid = record.childNodes[0].get("id");
                                // tree_table.setValue(tid);
                                // grid.store.proxy.extraParams.tid = tid;
                                // tree_table.fireEvent('select', tree_table, record.childNodes[0]);
                                setTimeout(function () {
                                    tree_table.getPicker().getView().expand(record.childNodes[0], false);
                                }, 50);
                                grid.store.load();
                            } else if (record.childNodes.length == 0) {
                                grid.store.proxy.extraParams.tid = -1;
                                grid.store.load();
                            } else {
                                grid.store.load();
                            }
                        }
                    },
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'text', type: 'string'},
                        {name: 'oid', type: 'int'},
                        {name: 'object_name', type: 'string'},
                        {name: "object_type", "type": "string"},
                        {name: 'table_name', type: 'string'},
                        {name: 'tab_name', type: 'string'},
                        {name: 'tb_relation', type: 'string'},
                        {name: 'layout_type', type: 'string'},
                        {name: 'system_fields', type: 'int'},
                        {name: 'define_fields', type: 'int'}
                    ]
                }),
                columns: [{
                    xtype: 'treecolumn',
                    text: '数据库表(<b style="color:blue;">自定义字段</b>/<b style="color:green;">自带字段</b>)',
                    dataIndex: 'text', width: 280, sortable: false,
                    renderer: function (val, meta, rec) {
                        return val + '(<b style="color:blue;">' + rec.get("define_fields") + '</b>/<b style="color:green;">' + rec.get("system_fields") + '</b>)';
                    }
                }],
                listeners: {
                    'select': function (comboBox, record) {
                        tree.tree_sel_record = record;
                        grid.tb_relation = record.get("tb_relation");
                        grid.layout_type = record.get('layout_type');
                        grid.table_name = record.get('text');
                        grid.store.proxy.extraParams.tid = record.get('id');
                        grid.store.proxy.extraParams.object_type = record.get('object_type');
                        grid.store.loadPage(1);
                    }
                },
                queryMode: 'remote'
            }, {
                itemId: "btn_complexsearch",
                iconCls: "icon_search",
                xtype: 'button', text: '高级搜索',
                handler: function (button, e) {
                    openFieldSearchWinFn(tree, grid)
                }
            }, "->", {
                xtype: 'checkbox',
                labelWidth: 85,
                fieldLabel: '包含自带字段',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        grid.store.proxy.extraParams.isdefine = checked ? 1 : 0;
                        grid.store.reload();
                    }
                }
            }, '  ']
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', {cookieName: "grid_table_field"})
        }]
    });

    function editingFieldInfoFn(record, id, field, value, originalValue) {
        Ext.Ajax.request({
            method: "POST", url: "system/gen/field/editing",
            params: {
                item_id: item_id, id: id,
                field: field, value: value
            },
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
                tooltip: '刷新字段列表',
                callback: function (panel, tool) {
                    tree.getSelectionModel().deselectAll(true);
                    tree.store.load();
                    grid.store.proxy.extraParams.oid = 0;
                    grid.store.proxy.extraParams.searchdata = "";
                    grid.store.proxy.extraParams.tid = 0;
                    grid.store.load();
                    grid.getSelectionModel().deselectAll(true);
                    grid.queryById('object_table').reset();
                    grid.queryById('object_table').clearValue();
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

    function getTypeData(attr) {
        var typeData = [];
        Ext.Array.each(attr, function (name, index, countriesItSelf) {
            typeData[typeData.length] = {'id': name, 'name': name};
        });
        return typeData;
    }

    var fieldSearchWin; //表字段查询
    function openFieldSearchWinFn(tree, grid) {
        if (!fieldSearchWin) {
            var typeData = getTypeData(['char', 'datetime', 'int', 'bigint', 'numeric', 'varchar', 'text', 'longtext']);
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
                    itemId: 'field_explain',
                    xtype: 'textfield',
                    name: 'field_explain',
                    maxLength: 25,
                    allowBlank: true,
                    fieldLabel: '字段中文'
                }, {
                    itemId: 'field_name',
                    xtype: 'textfield',
                    name: 'field_name',
                    msgTarget: 'under',
                    fieldLabel: '字段名称',
                    regex: /^[a-z|A-Z|0-9|_]+$/,
                    regexText: '只能输入字母、数字、下划线'
                }, {
                    itemId: 'field_tag',
                    fieldLabel: '字段标识',
                    emptyText: '==请选择==',
                    valueField: 'id', displayField: 'name',
                    xtype: 'combobox', editable: false,
                    store: getCodeComboStore('design.field.flag'),
                    hiddenName: "field_tag", name: 'field_tag',
                    allowBlank: true, queryMode: 'remote'
                }, {
                    itemId: "xtype",
                    fieldLabel: '输入框类型',
                    hiddenName: "xtype", name: 'xtype',
                    xtype: 'combo', editable: false,
                    valueField: 'id', displayField: 'name',
                    emptyText: "==请选择==",
                    store: {
                        autoLoad: true, // 必须自动加载, 否则在编辑的时候load
                        proxy: {
                            type: 'ajax', url: "system/gen/xtype/combo",
                            extraParams: {},
                            reader: {
                                type: 'json', root: 'root',
                                totalProperty: 'totalProperty'
                            }
                        },
                        fields: ['id', 'name']
                    },
                    queryMode: 'remote'
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

            function search() {
                var items = [];
                items[items.length] = {item_id: "field_explain", operator: "like"};
                items[items.length] = {item_id: "field_name", operator: "like"};
                items[items.length] = {item_id: "field_tag", valType: "string"};
                items[items.length] = {item_id: "xtype", valType: "string"};
                searchSingleTableReLoad(grid, designSearchByForm(form, items), "key_field_gen", "a");
                fieldSearchWin.close();
            }

            fieldSearchWin = Ext.create('Ext.window.Window', {
                title: "字段查询",
                animateTarget: grid.queryById('btn_complexsearch').getId(),
                width: 450,
                resizable: false,
                closable: true,
                closeAction: 'hide',  //destroy，hide
                modal: 'true',  // 弹出模态窗体
                items: [form],
                buttonAlign: "right",
                buttons: ["->", {
                    text: "查询",
                    minWidth: 70,
                    handler: function () {
                        search();
                    }
                }, {
                    text: '清空',
                    minWidth: 70,
                    handler: function () {
                        form.getForm().reset();
                    }
                }, {
                    text: "关闭",
                    minWidth: 70,
                    handler: function () {
                        fieldSearchWin.close();
                    }
                }]
            });
        }

        fieldSearchWin.show();
    }

    //输入框属性配置后，打开测试窗口
    function openSaveEditorFormWinFn(grid, record) {
        var form = Ext.create('Ext.form.Panel', {
            autoScroll: true,
            bodyPadding: "15 15 10 15",
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
            items: [{
                xtype: 'hiddenfield',
                name: 'fid', value: record.get("fid")
            }, {
                itemId: 'editor_form',
                xtype: 'textareafield',
                name: 'editor_form',
                height: 300, fieldLabel: '',
                flex: 1, allowBlank: true  // 表单项可空
            }]
        });
        var editor_form = "";
        var window = Ext.create('Ext.window.Window', {
            title: "查询输入框重新配置+测试",
            width: 500,
            resizable: true,
            closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            layout: "fit",
            items: [form],
            listeners: {
                "afterrender": function (window, eOpts) {
                    $("textarea[name='editor_form']").attr("spellcheck", false).css("font-size", "14px"); //.css("font-weight", "bold")
                    Ext.Ajax.request({
                        method: "POST", url: "system/gen/editor/info",
                        params: {fid: record.get("fid")},
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                editor_form = resp["field"]["data"]["editor_search"];
                                var text = editor_form.split("\n").join(" ");
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
                                editor_form = t.join('');
                                form.queryById('editor_form').setValue(editor_form);
                            }
                        },
                        failure: function (response, options) {
                        }
                    });
                }
            },
            buttonAlign: "right",
            buttons: [{
                text: "测试",
                minWidth: 70,
                handler: function () {
                    try {
                        var fieldvalue = form.queryById('editor_form').getValue();
                        if (fieldvalue != "") {
                            var field = eval("(" + fieldvalue + ")");
                            openFormFieldTestWinFn(grid, record.get("field_name"), field);
                        } else {
                            Ext.alert.msg('提示', "没有配置值！");
                        }
                    } catch (e) {
                        Ext.alert.msg('提示', "配置出错，请检查各项配置值！");
                        return false;
                    }
                }
            }, "->", {
                text: "保存",
                minWidth: 70,
                handler: function () {
                    if (form.isValid()) {
                        form.submit({
                            url: "system/gen/editor/save", method: "POST",
                            waitMsg: '请稍等，正在保存...',
                            params: {item_id: item_id},
                            success: function (basic_form, action) {
                                var flag = action.result.success;
                                if (flag) {
                                    grid.store.reload();
                                    window.close();
                                    Ext.alert.msg('提示', '保存成功！');
                                } else {
                                    Ext.alert.msg('提示', '保存失败！');
                                }
                            },
                            failure: function (form, action) {
                                ajaxFailureTipMsg(form, action);
                            }
                        });
                    } else {
                        Ext.alert.msg('无效输入', '请输入正确的信息!');
                    }
                }
            }, {
                text: '重置',
                minWidth: 70,
                handler: function () {
                    form.queryById('editor_form').setValue(editor_form);
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

    //输入框属性配置后，打开测试窗口
    function openFormFieldTestWinFn(grid, name, field) {
        var value = "";
        if (!field) {
            var xtype = grid.store.proxy.extraParams.xtype;
            if (xtype == "") xtype = "textfield";
            var field = {xtype: xtype, name: name};
            grid.store.each(function (record) {
                var fieldvalue = record.get("fieldvalue");
                if (fieldvalue != "") {
                    if (fieldvalue == "null") fieldvalue = "  "; //null表示空值
                    if (record.get("field_key") == "value") value = fieldvalue;
                    if (fieldvalue == "colorPickerFn(me)") {
                        field.xtype = "pickercolor";
                    } else {
                        if (fieldvalue.indexOf("#Current_") >= 0) {
                            //特殊默认值
                            value = getSpecialDefaultValue(xtype, fieldvalue);
                            field[record.get("field_key")] = value;
                        } else if (record.get("field_key") != "runFunction" && fieldvalue.indexOf("(") >= 0 && fieldvalue.indexOf(")") >= 0) {
                            //自定义选择器中属性runFunction必须是字符串，同时又是需要运行的函数
                            //新增时默认值,当是日期输入框时new Date()表示当前日期，但是value属性是字符型，必须转换成对象
                            field[record.get("field_key")] = eval("(" + fieldvalue + ")");
                        } else if (record.get("value_type").indexOf("string") >= 0) {
                            field[record.get("field_key")] = fieldvalue;
                        } else if (!(xtype == "checkboxgroup" && record.get("field_key") == "value")) {
                            field[record.get("field_key")] = eval("(" + fieldvalue + ")");
                        }
                        if (xtype == "my97date" || xtype == "ueditor" || xtype == "kindeditor") {
                            var field_key = record.get("field_key");
                            var attr = field_key.split("_");
                            if (attr.length == 2) {
                                if (!field[attr[0]]) field[attr[0]] = {};
                                field[attr[0]][attr[1]] = field[field_key];
                            }
                        }
                    }
                }
            });
        }
        try {
            //alert(Ext.JSON.encode(field));
            var form = Ext.create('Ext.form.Panel', {
                frame: false,
                autoScroll: true,
                bodyPadding: "15 20 10 20",
                defaultType: 'textfield',
                fieldDefaults: {
                    labelAlign: 'left',
                    msgTarget: 'side',
                    labelWidth: 100
                },
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype: 'hiddenfield',
                    name: 'id', value: "1"
                }, field]
            });
        } catch (e) {
            Ext.alert.msg('提示', "配置出错，请检查各项配置值！");
            return false;
        }

        var width = 450;
        if (xtype == "ueditor" || xtype == "kindeditor") width = 700;
        var window = Ext.create('Ext.window.Window', {
            title: "查询输入框测试",
            //animateTarget: grid.queryById('addRow_btn').getId(),
            width: width,
            resizable: true,
            closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            layout: "fit",
            items: [form],
            listeners: {
                "afterrender": function (window, eOpts) {
                    if (value != "") {
                        var editors = form.query('[isFormField]');
                        for (var i = 0; i < editors.length; i++) {
                            var xtype = editors[i].xtype,
                                field = editors[i].name;
                            if (xtype == "checkboxgroup") {
                                setTimeout(function (index) {
                                    var field = editors[index].name, obj = {};
                                    if (value) {
                                        obj[field] = value.split(",");
                                        editors[index].setValue(obj);
                                        editors[index].originalValue = value;
                                        var boxs = editors[index].query('>[isFormField]');
                                        for (var i = 0; i < boxs.length; i++) {
                                            boxs[i].originalValue = boxs[i].getValue(); //不然有脏数据
                                        }
                                    }
                                }, 10, i);
                            } else if (xtype == "radiogroup") {
                                setTimeout(function (index) {
                                    var field = editors[index].name, obj = {};
                                    obj[field] = value;
                                    editors[index].setValue(obj);
                                    editors[index].originalValue = value;
                                    var boxs = editors[index].query('>[isFormField]');
                                    for (var i = 0; i < boxs.length; i++) {
                                        boxs[i].originalValue = boxs[i].getValue(); //不然有脏数据
                                    }
                                }, 10, i);
                            } else if (xtype == "multicombobox") { //多选下拉框
                                setTimeout(function (index) {
                                    var attr_ids = value.split(",");
                                    editors[index].setValue(attr_ids);
                                    editors[index].originalValue = attr_ids;
                                }, 10, i);
                            } else if (xtype == "singlecombobox") { //多选下拉框
                                setTimeout(function (index) {
                                    editors[index].setValue(value);
                                    editors[index].originalValue = value;
                                }, 10, i);
                            }
                        }
                    }
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
                        //Ext.alert.msg('提示', form.getForm().getValues(true));
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

}


