Ext.define('javascript.menu.system.code_other', {
    extend: ''
});

//enum代码生成窗口
function openCodeEnumBuildWinFn(grid, explain, enum_name) {
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
        items: [{
            maxWidth: 450,
            xtype: 'textfield',
            name: 'file_name',
            itemId: 'file_name',
            fieldLabel: '枚举文件名',
            value: enum_name,
            allowBlank: false,
            maxLength: 20
        }, {
            maxWidth: 450,
            xtype: 'textfield',
            name: 'explain',
            itemId: 'explain',
            fieldLabel: '文件说明',
            value: explain + "_枚举",
            allowBlank: true,
            maxLength: 20
        }]
    });
    var window = Ext.create('Ext.window.Window', {
        title: "Enum代码生成",
        width: 420,
        resizable: true,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: 'true',  // 弹出模态窗体
        layout: "fit",
        items: [form],
        buttonAlign: "right",
        buttons: [{
            text: "生成",
            minWidth: 70,
            handler: function () {
                var rootNode = grid.getRootNode();
                var childNodes = rootNode.childNodes;
                if (childNodes.length == 0) {
                    Ext.alert.msg('信息提示', '没有记录可以生成！');
                    return;
                }
                if (form.getForm().isValid()) {
                    var store_datas = [];
                    for (var i = 0; i < childNodes.length; i++) {
                        var record = childNodes[i];
                        store_datas[store_datas.length] = [record.get("value"), record.get("text")];
                    }
                    Ext.Ajax.request({
                        method: "POST", url: "system/code/build/enums",
                        params: {
                            explain: form.queryById('explain').getValue(),
                            file_name: form.queryById('file_name').getValue(),
                            store_datas: Ext.JSON.encode(store_datas)
                        },
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                Ext.alert.msg('提示', "生成成功!");
                                downLoadBuildFile(resp.filepath + "||" + resp.filename);
                                window.close();
                            } else {
                                Ext.alert.msg('提示', "生成失败!");
                            }
                        },
                        failure: function (response, options) {
                            Ext.alert.msg('提示', "生成失败!");
                        }
                    });
                }
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

//选择框数据源生成窗口
function openSelectSourceBuildWinFn(tree, field_value, field_text) {
    var form = Ext.create('Ext.form.Panel', {
        frame: false,
        autoScroll: true,
        bodyPadding: "15 15 10 15",
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [{
            xtype: 'textareafield', fieldLabel: '',
            itemId: 'select_source', name: 'select_source',
            allowBlank: true, height: 260, rows: 3, selectOnFocus: true,
            emptyText: '将数据源字符串拷贝使用到“表单设计管理”->“表单输入框定义”页面中，单选组、复选组选择框字段数据源配置', flex: 1
        }]
    });
    var window = Ext.create('Ext.window.Window', {
        title: "单选组、复选组选择框数据源",
        width: 500,
        resizable: true,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: 'true',  // 弹出模态窗体
        layout: "fit",
        items: [form],
        listeners: {
            "show": function (window, eOpts) {
                var store_datas = [];
                var childNodes = tree.getRootNode().childNodes;
                Ext.Array.each(childNodes, function (record, index) {
                    store_datas[store_datas.length] = [record.get(field_value), record.get(field_text)];
                });
                Ext.Ajax.request({
                    method: "POST", url: 'system/ds/select/source',
                    params: {store_datas: Ext.JSON.encode(store_datas)},
                    success: function (response, options) {
                        Ext.getBody().unmask();
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success) {
                            form.queryById("select_source").setValue(resp.data);
                        } else {
                            showMsgByJson(resp, "转换失败!");
                        }
                    },
                    failure: function (response, options) {
                        Ext.getBody().unmask();
                    }
                });
            }
        },
        buttonAlign: "right",
        buttons: [{
            text: "关闭",
            minWidth: 70,
            handler: function () {
                window.close();
            }
        }]
    });
    window.show();
}

//MyBatis数据源窗口
function openMyBatisDataWinFn() {
    var data_source = [{"id": "db_xinsite_release", "name": "master"}
        , {"id": "db_xinsite_demo", "name": "viceone"}];
    var db_tables_store = Ext.create('Ext.data.Store', {
        autoLoad: true,
        proxy: {
            type: 'ajax', url: "system/ds/mybatis/tables",
            extraParams: {db_key: data_source[0].name, db_name: data_source[0].id},
            reader: {
                type: 'json', root: 'root',
                totalProperty: 'totalProperty'
            }
        },
        fields: ['id', 'name']
    });
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
        items: [{
            xtype: 'fieldcontainer',
            fieldLabel: '数据库源',
            layout: 'hbox', anchor: '98%',
            defaults: {hideLabel: true},
            items: [{
                xtype: 'combobox',
                name: 'db_key',
                allowBlank: false,
                valueField: 'id', displayField: 'name',
                store: {
                    "fields": ["id", "name", "disabled"],
                    "data": data_source
                },
                queryMode: 'local', value: "master",
                editable: false, width: 80,
                listeners: {
                    "select": function (combo, records, e) {
                        if (records.length > 0) {
                            db_tables_store.proxy.extraParams.db_key = records[0].get("name");
                            db_tables_store.proxy.extraParams.db_name = records[0].get("id");
                            db_tables_store.reload();
                        }
                    }
                }
            }, {
                xtype: 'multicombobox',
                itemId: 'db_tables',
                name: 'db_tables',
                style: 'margin-left:1px',
                emptyText: '==选择数据库表==',
                allowBlank: false, isSelectAll: true,
                valueField: 'id', displayField: 'name',
                store: db_tables_store,
                queryMode: 'local',
                editable: false, flex: 1
            }]
        }, {
            xtype: 'textareafield',
            itemId: 'mybatis_source',
            name: 'mybatis_source',
            fieldLabel: '生成MyBatis数据源',
            allowBlank: true, height: 260, rows: 3, selectOnFocus: true,
            emptyText: '先选择数据库表，点击“生成”，将生成的字符串拷贝到“表单设计管理”->“数据库表管理”页面的“数据源生成”功能窗口中。', flex: 1
        }]
    });
    var window = Ext.create('Ext.window.Window', {
        title: "MyBatis数据源生成",
        width: 650,
        resizable: true,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: 'true',  // 弹出模态窗体
        layout: "fit",
        items: [form],
        buttonAlign: "right",
        buttons: [{
            text: "生成",
            minWidth: 70,
            handler: function () {
                if (form.getForm().isValid()) {
                    //Ext.getBody().mask('请稍等,正在生成数据...');
                    Ext.Ajax.request({
                        method: "POST", url: 'system/ds/mybatis/source',
                        params: {
                            db_key: db_tables_store.proxy.extraParams.db_key,
                            db_name: db_tables_store.proxy.extraParams.db_name,
                            db_tables: form.queryById("db_tables").getValue()
                        },
                        success: function (response, options) {
                            Ext.getBody().unmask();
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                form.queryById("mybatis_source").setValue(resp.data);
                            } else {
                                showMsgByJson(resp, "生成失败!");
                            }
                        },
                        failure: function (response, options) {
                            Ext.getBody().unmask();
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

