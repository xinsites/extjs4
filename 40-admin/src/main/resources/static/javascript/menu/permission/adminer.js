Ext.define('javascript.menu.permission.adminer', {
    extend: ''
});

//各机构管理员权限配置
function createManagerPermissionGrid(org_id, tb_type, tb_id) {
    var grid_fields = [{"name": "id", "type": "int", "text": "主键"},
        {"name": "pid", "type": "int", "text": "上级单位"},
        {"name": "text", "type": "string", "text": "栏目名称"},
        {"name": "iconCls", "type": "string", "text": "栏目图标"},
        {"name": "expand", "type": "string", "text": "是否展开"},
        {"name": "fun_ids", "type": "string", "text": "栏目功能"},
        {"name": "all_fun_ids", "type": "string", "text": "所有栏目功能"},
        {"name": "data_per", "type": "string", "text": "数据权限"},
        {"name": "data_ids", "type": "string", "text": "自定义权限"},
        {"name": "checkgroup", "type": "string", "text": "栏目功能复选框组"},
        {"name": "serialcode", "type": "int", "text": "排序号"},
        {"name": "create_time", "type": "date", "text": "创建时间"},
        {"name": "isdataper", "type": "bool", "text": "数据权限"}];

    Ext.define('model_power_adminer', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });

    function getNewData(pid) {
        return Ext.create('model_power_adminer', {
            'id': 0,
            'pid': pid,
            'isused': '1'
        });
    }

    var is_fun_sync = false, is_real_save = false, is_permission_data = false;
    var permission_data = [];
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: path_url.system.power.tree,
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {org_id: org_id, self: 0}
        },
        listeners: {
            "expand": function (record) {
                setItemClickFlag('tree_expand');
            },
            'load': function (store, record) {
                var is_realsave = grid.queryById('real_save').getValue();
                grid.queryById('real_save').setValue(false);
                setTimeout(function () {
                    if (record.get("id") == "0") {  //首次加载
                        setCheckedPermission(grid.getRootNode());
                        grid.queryById('check_all').setValue(false);
                    } else if (is_permission_data) { //加载权限信息
                        setCheckedPermission(record);
                    }
                    grid.queryById('real_save').setValue(is_realsave);

                    Ext.Array.each(record.childNodes, function (record, index) {
                        if (record.get("expand") == "true") {
                            if (!record.get('leaf') && !record.isExpanded()) {
                                grid.getView().expand(record, false);
                            }
                        }
                    });
                }, 100);
            }
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: grid_fields
    });
    var dataPer_column = getDataPerArray();
    var columns = [{
        xtype: 'treecolumn', text: '栏目名称', width: 280, fixed: false, dataIndex: 'text',
        renderer: function (val, meta, rec) {
            if (rec.get("expand") == "true")
                return val + '<span style="color:blue;">[默认展开]</span>';
            return val;
        }
    }, {
        text: '栏目功能',
        dataIndex: 'fun_ids',
        width: 160,
        fixed: false,
        align: 'left',
        flex: 1.8,
        sortable: false,
        xtype: 'checkdefinecolumn',
        checkGroupField: "checkgroup",
        enableChecked: true,
        onCheckChange: function (view, cell, record, value) {
            var me = this;
            record.set('checked', true);
            var pNode = record.parentNode;
            for (; pNode != null; pNode = pNode.parentNode) {
                pNode.set("checked", true);
            }
            if (tb_id > 0 && is_real_save) //选择了对象，实时保存
                SaveRealPermissionFn(record);
        }
    }, {
        text: '数据权限', width: 120, fixed: true, dataIndex: 'isdataper',
        renderer: function (val, meta, rec) {
            //return val;
            if (val) {
                if (rec.get("data_per") && rec.get("data_per") != "0")
                    return '<span style="color:blue;text-decoration: underline;cursor:pointer;">' + getComboByIdToText(rec, 'data_per', rec.get("data_per"), dataPer_column, "") + '</span>';
                else
                    return '<span style="color:red;text-decoration: underline;cursor:pointer;">数据权限配置</span>';
            }
        }
    }];

    var grid = Ext.create('Ext.ux.CascadeTree', {
        itemId: "manager_per_grid",
        store: store,
        useArrows: false,
        rowLines: true,
        forceFit: true,
        emptyText: "没有数据!",
        queryMode: 'remote',
        cascade: 'both',
        singleClickExpand: true,
        clickType: "checked",
        bodyCls: 'grid_empty_text_parent',
        viewConfig: {
            getRowClass: function (record, rowIndex, rowParams, store) {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        autoScroll: true, rootVisible: false,
        listeners: {
            "checkchange": function (record, checked, e) {
                setRecordFunSync(record); //功能同步
                if (tb_id > 0 && is_real_save && !itemClickFlag) {//选择了对象，实时保存
                    if (!record.get("leaf") && record.childNodes.length > 0) {  //有子结点
                        SaveRealPermissionFn(record);
                    } else if (record.get("leaf") && record.childNodes.length == 0) {  //单结点
                        SaveRealPermissionFn(record);
                    }
                }
            },
            "cascadechange": function (record, checked, e) { //级联选择被调用
                setRecordFunSync(record); //功能同步
            },
            "celldblclick": function (treeview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "isdataper") {
                    if (tb_id > 0 && tb_id != "") {
                        rec.set("checked", true);
                        var pNode = rec.parentNode;
                        for (; pNode != null; pNode = pNode.parentNode) {
                            pNode.set("checked", true);
                        }
                        openEditDataPermissionWindow(rec);
                    } else {
                        Ext.alert.msg('提示', "请选择一个操作对象[用户/角色]");
                    }
                }
            }
        },
        columns: columns,
        Refresh: function (_OrgId, _ObjType, _ObjId) {
            var isRefresh_tree = (_OrgId != org_id);
            var isEqual_Obj = (tb_type == _ObjType && tb_id == _ObjId);
            org_id = _OrgId;
            tb_type = _ObjType;
            tb_id = _ObjId;

            if (isRefresh_tree) { //刷新栏目树、刷新对象权限
                grid.getRootNode().removeAll(false);
                grid.store.proxy.extraParams.org_id = org_id;
                grid.store.load();
                setTimeout(function () {
                    getPermissionInfo(tb_id, true);
                }, 100);
            } else if (!isEqual_Obj) { //机构没变，刷新对象权限
                getPermissionInfo(tb_id, true);
            }
        },
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'btn_permission',
                xtype: 'button', text: '保存',
                minWidth: 60, iconCls: 'icon_save',
                handler: function (btn, pressed) {
                    Ext.MessageBox.confirm("提示!", "您确定要保存当前管理员的权限吗?", function (btn) {
                        if (btn == "yes") {
                            SaveBtnPermissionFn();
                        }
                    });
                }
            }, '-', {
                itemId: 'btn_data_per',
                xtype: 'button', text: '数据权限',
                minWidth: 60, iconCls: 'icon_dataper',
                handler: function (btn, pressed) {
                    openEditDataPermissionWindow(null);
                }
            }, '-', {
                itemId: 'btn_refresh',
                xtype: 'button', text: '刷新',
                minWidth: 60, iconCls: 'icon_refresh',
                handler: function (btn, pressed) {
                    getPermissionInfo(tb_id, true);
                }
            }, '-', {
                xtype: 'displayfield',
                hideLabel: true,
                itemId: "manager_per_grid_explain",
                value: '<span style="color:blue;"></span>'
            }, '->', {
                xtype: 'checkbox',
                itemId: 'check_all',
                labelWidth: 30,
                fieldLabel: '全选',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        setAllChecked(grid.getRootNode(), checked);
                    }
                }
            }, '-', {
                xtype: 'checkbox',
                itemId: 'fun_sync',
                labelWidth: 55,
                fieldLabel: '功能同步',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        is_fun_sync = checked;
                    }
                }
            }, '-', {
                xtype: 'checkbox',
                itemId: 'real_save',
                labelWidth: 55,
                fieldLabel: '实时保存',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        is_real_save = checked;
                    }
                }
            }, '-', {
                xtype: 'checkbox',
                labelWidth: 55,
                fieldLabel: '全部展开',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        if (checked) grid.expandAll();
                        else grid.collapseAll();
                    }
                }
            }]
        }]
    });

    function getPermissionInfo(tb_id, isRefresh) {
        if (isRefresh) Ext.getBody().mask('请稍候，正在刷新...');
        else Ext.getBody().mask('请稍候，正在获取权限...');
        Ext.Ajax.request({
            method: "POST", url: path_url.system.power.info,
            params: {tb_id: tb_id, tb_type: tb_type},
            success: function (response, options) {
                Ext.getBody().unmask();
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    permission_data = resp.data;
                    is_permission_data = true;
                    grid.queryById('check_all').setValue(false);
                    setButtonDisabled(false);
                    if (!isRefresh && grid.store.proxy.extraParams.self == 1) {
                        refreshTreeNode(grid);
                    } else {
                        setCheckedPermission(grid.getRootNode());
                    }
                } else {
                    Ext.alert.msg('提示', "获取失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "获取失败!");
            }
        });
    }

    //功能权限
    function getPermissionObject(record, btn_save) {
        record.commit();
        if (record.get('checked')) { //只获取选中的栏目
            var obj = {
                Check: record.get('checked'),
                item_id: record.get('id'),
                isdataper: record.get('isdataper') ? 1 : 0,
                fun_ids: record.get('fun_ids')
            };
            if (btn_save && record.get('isdataper')) { //按钮保存 并且 有数据权限
                obj.data_per = record.get('data_per');
                obj.data_ids = record.get('data_ids');
                if (obj.data_per == "") obj.data_per = 0;
            }
            return obj;
        }
    }

    //实时功能权限
    function getRealPermissionObject(record, btn_save) {
        record.commit();
        return {
            Check: record.get('checked'),
            item_id: record.get('id'),
            fun_ids: record.get('fun_ids')
        };
    }

    //实时保存该结点的权限(包括父结点及所有子结点)
    function SaveRealPermissionFn(record) {
        var datas = [];
        pNode = record.parentNode;
        for (; pNode != null; pNode = pNode.parentNode) {
            if (pNode.get("id") != 0) datas[datas.length] = getRealPermissionObject(pNode);
        }
        record.cascadeBy(function (record) {
            datas[datas.length] = getRealPermissionObject(record);
        });
        if (tb_type == "user")
            Ext.getBody().mask('请稍候，正在实时保存用户权限...');
        else
            Ext.getBody().mask('请稍候，正在实时保存角色权限...');
        Ext.Ajax.request({
            method: "POST", url: path_url.system.power.realsave,
            params: {tb_type: tb_type, tb_id: tb_id, power_info: Ext.JSON.encode(datas)},
            success: function (response, options) {
                Ext.getBody().unmask();
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    permission_data = resp.data;
                    is_permission_data = true;
                    Ext.alert.msg('提示', "保存成功!");
                } else {
                    Ext.alert.msg('提示', "保存失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "保存失败!");
                Ext.getBody().unmask();
            }
        });
    }

    //获取所有权限值
    function getAllPermission(node, datas, btn_save, item_ids) {
        var childNodes = node.childNodes;
        for (var i = 0; i < childNodes.length; i++) {
            var record = childNodes[i];
            if (record.get('checked')) {
                datas[datas.length] = getPermissionObject(record, btn_save);
            } else if (item_ids) {
                var sel_record = getPermissionNode(record);
                if (sel_record) item_ids[item_ids.length] = record.get("id");
            }
            getAllPermission(record, datas, btn_save, item_ids);
        }
    }

    //按钮保存该结点的权限
    function SaveBtnPermissionFn() {
        var datas = [], item_ids = [];
        getAllPermission(grid.getRootNode(), datas, true, item_ids);
        if (tb_type == "user")
            Ext.getBody().mask('请稍候，正在保存用户权限...');
        else
            Ext.getBody().mask('请稍候，正在保存角色权限...');
        Ext.Ajax.request({
            method: "POST", url: path_url.system.power.btnsave,
            params: {
                tb_type: tb_type, tb_id: tb_id,
                item_ids: item_ids.join(","),
                power_info: Ext.JSON.encode(datas)
            },
            success: function (response, options) {
                Ext.getBody().unmask();
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    permission_data = resp.data;
                    is_permission_data = true;
                    Ext.alert.msg('提示', "保存成功!");
                } else {
                    Ext.alert.msg('提示', "保存失败!");
                }
            },
            failure: function (response, options) {
                Ext.alert.msg('提示', "保存失败!");
                Ext.getBody().unmask();
            }
        });
    }

    //设置按钮是否可用
    function setButtonDisabled(disabled) {
        grid.queryById('btn_permission').setDisabled(disabled);
        grid.queryById('btn_refresh').setDisabled(disabled);
        grid.queryById('btn_data_per').setDisabled(disabled);
    }

    //该结点是否功能同步
    function setRecordFunSync(record) {
        if (is_fun_sync) {
            if (record.get("checked")) {
                record.set("fun_ids", record.get("all_fun_ids"));
            } else {
                record.set("fun_ids", "");
            }
        }
    }

    //该结点的功能权限
    function getPermissionNode(record) {
        for (var i = 0; i < permission_data.length; i++) {
            if (permission_data[i].item_id == record.get("id")) {
                return permission_data[i];
            }
        }
        return "";
    }

    //按获取的权限值，递归遍历选中状态
    function setCheckedPermission(node) {
        var childNodes = node.childNodes;
        for (var i = 0; i < childNodes.length; i++) {
            var record = childNodes[i];
            var sel_record = getPermissionNode(record);
            record.set("checked", sel_record ? true : false);
            record.set("fun_ids", sel_record ? sel_record.fun_ids : "");
            record.set("data_per", sel_record ? sel_record.data_per : "");
            record.set("data_ids", sel_record ? sel_record.data_ids : "");
            record.commit();
            setCheckedPermission(record);
        }
    }

    //树功能权限全部选中设置
    function setAllChecked(node, checked) {
        var childNodes = node.childNodes;
        for (var i = 0; i < childNodes.length; i++) {
            var record = childNodes[i];
            record.set("checked", checked);
            setRecordFunSync(record);
            setAllChecked(record, checked);
        }
    }

    //获取所有选中部门值
    function getAllDeptIds(node, datas) {
        var childNodes = node.childNodes;
        for (var i = 0; i < childNodes.length; i++) {
            var record = childNodes[i];
            if (record.get('checked'))
                datas[datas.length] = record.get('id');
            getAllDeptIds(record, datas);
        }
    }

    //自定义部门设置选中状态
    function setCheckedDept(node, data_ids) {
        var childNodes = node.childNodes;
        for (var i = 0; i < childNodes.length; i++) {
            var record = childNodes[i];
            for (var j = 0; j < data_ids.length; j++) {
                if (record.get('id') == data_ids[j]) {
                    record.set("checked", true);
                    record.commit();
                    break;
                }
            }
            setCheckedDept(record, data_ids);
        }
    }

    function openEditDataPermissionWindow(record) {
        var tree_store = Ext.create('Ext.data.TreeStore', {
            nodeParam: 'node', autoLoad: false,
            proxy: {
                type: 'ajax', url: path_url.system.ds.storedata,
                reader: {
                    type: 'json', id: "id"
                },
                extraParams: {data_key: "ds.sys.dept", data_type: "tree", isCheck: true}
            },
            listeners: {
                'load': function (store, records) {
                    if (record != null) {
                        var data_ids = record.get("data_ids");
                        if (data_ids != "")
                            setCheckedDept(tree.getRootNode(), data_ids.split(","));
                    }
                }
            },
            root: {text: userinfo.organizeName ? userinfo.organizeName : 'XXXX公司', id: '0', checked: false, expanded: true}
        });
        var tree = Ext.create('Ext.ux.CascadeTree', {
            store: tree_store,
            border: false, layout: 'fit',
            clickType: "item", cascade: 'both',
            rowLines: false, hideHeaders: true,
            autoScroll: true, rootVisible: true,
            viewConfig: {
                getRowClass: function () {
                    return 'custom-grid-tree-row-height';
                },
                loadMask: true,
                loadingText: "数据加载中，请稍等..."
            }
        });

        var forms = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '20 15 10',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 70},
            items: [{
                xtype: 'combobox',
                name: 'data_per',
                itemId: 'data_per',
                fieldLabel: '数据权限',
                //allowBlank: false,
                valueField: 'id',
                displayField: 'name',
                queryMode: 'local', beforeLabelTextTpl: '*',
                store: {"fields": ["id", "name"], "data": dataPer_store},
                editable: false, value: '0',
                listeners: {
                    "change": function (combobox, newValue, oldValue, e) {
                        forms.queryById('data_ids').setDisabled(newValue != "5" && newValue != "6");
                    }
                }
            }, {
                itemId: 'data_ids', flex: 1,
                layout: 'fit', border: true,
                items: [tree], disabled: true
            }]
        });
        var win_config = {
            title: '栏目数据权限配置',
            items: [forms],
            listeners: {
                show: function (window, eOpts) {
                    if (record != null) {
                        if (record.get("data_per"))
                            forms.queryById('data_per').setValue(record.get("data_per"));
                        else
                            forms.queryById('data_per').setValue("0");
                    }
                }
            },
            buttonAlign: "right",
            buttons: [{
                itemId: "btn_save_form",
                text: '保存', minWidth: 70,
                listeners: {
                    click: function () {
                        var datas = [];
                        if (record == null)
                            getAllPermission(grid.getRootNode(), datas, true);  //批量数据权限保存
                        else {
                            var pNode = record.parentNode;
                            for (; pNode != null; pNode = pNode.parentNode) {
                                if (pNode.get("id") != 0) datas[datas.length] = getPermissionObject(pNode, true);
                            }
                            datas[datas.length] = getPermissionObject(record, true);  //某个栏目数据权限保存
                        }
                        if (datas.length == 0) {
                            win.close();
                            Ext.alert.msg('提示', "至少选择一个栏目赋予权限!");
                            return;
                        }
                        var data_per = forms.queryById('data_per').getValue();
                        var data_ids = [];
                        if (data_per == "5") {  //自定义部门，获取选择的部门
                            getAllDeptIds(tree.getRootNode(), data_ids);
                        }

                        Ext.getBody().mask('请稍候，正在保存数据权限...');
                        Ext.Ajax.request({
                            method: "POST", url: path_url.system.power.datasave,
                            params: {
                                tb_type: tb_type, tb_id: tb_id,
                                data_per: data_per, data_ids: data_ids.join(","),
                                power_info: Ext.JSON.encode(datas)
                            },
                            success: function (response, options) {
                                Ext.getBody().unmask();
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    permission_data = resp.data;
                                    is_permission_data = true;
                                    Ext.alert.msg('提示', "保存成功!");
                                    if (record == null) setCheckedPermission(grid.getRootNode());
                                    else setCheckedPermission(record.parentNode);
                                    win.close();
                                } else {
                                    Ext.alert.msg('提示', "保存失败!");
                                }
                            },
                            failure: function (response, options) {
                                Ext.alert.msg('提示', "保存失败!");
                                Ext.getBody().unmask();
                            }
                        });
                    }
                }
            }, {
                text: '关闭', minWidth: 70,
                handler: function () {
                    win.close();
                }
            }]
        }
        var win = Ext.create('widget.window', Ext.apply(win_config, {
            width: 320,
            height: 450,
            closable: true,
            closeAction: 'destroy',
            plain: false,
            modal: true,
            layout: 'fit'
        }));
        win.show();
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid],
        listeners: {
            "render": function (panel, e) {
                setButtonDisabled(true);
                getPermissionInfo(tb_id, false);
            }
        }
    });
}