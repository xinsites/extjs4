Ext.define('javascript.menu.permission.manage', {
    extend: ''
});

//系统管理->权限管理
function createPanel_Permission(treenode) {
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
        {"name": "isdataper", "type": "bool", "text": "数据权限"},
        {"name": "isused", "type": "bool", "text": "是否使用"}];

    Ext.define('model_power_manage', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });

    function getNewData(pid) {
        return Ext.create('model_power_manage', {
            'id': 0,
            'pid': pid,
            'isused': '1'
        });
    }

    var itemid = treenode.raw.id;
    var tb_type = "role", tb_id = 0;
    var is_fun_sync = false, is_real_save = false, is_permission_data = false;
    var permission_data = [];
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: path_url.system.power.tree,
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {self: 0}
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
                        grid.queryById('expand_all').setChecked(false);
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
            if (rec.get("isused") == "0") {
                val = '<s style="color:#888;">' + val + '</s>';
            }
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
            if (tb_id > 0) {
                var me = this;
                record.set('checked', true);
                var pNode = record.parentNode;
                for (; pNode != null; pNode = pNode.parentNode) {
                    pNode.set("checked", true);
                }
                if (is_real_save) //选择了对象，实时保存
                    saveRealPermissionFn(record);
            } else {
                Ext.alert.msg('信息提示', '请选择一个对象！');
            }
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
        itemId: "main_grid",
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
                        saveRealPermissionFn(record);
                    } else if (record.get("leaf") && record.childNodes.length == 0) {  //单结点
                        saveRealPermissionFn(record);
                    }
                }
            },
            "cascadechange": function (record, checked, e) { //级联选择被调用
                //alert(record.get("text"))
                setRecordFunSync(record); //功能同步
            },
            "cellclick": function (treeview, td, cellIndex, rec, tr, rowIndex, e) {
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
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ["<b>对象:</b>", {
                xtype: 'combobox',
                name: 'per_type',
                itemId: 'obj3_lxzq',
                valueField: 'id',
                displayField: 'name',
                queryMode: 'local', width: 70,
                store: {"fields": ["id", "name"], "data": [{"id": "role", "name": "角色"}, {"id": "user", "name": "用户"}]},
                editable: false, value: '角色',
                listeners: {
                    "select": function (comboBox, eOpts) {
                        var per_obj_id = grid.queryById('per_obj_id');
                        per_obj_id.obj_id = "";
                        per_obj_id.setValue("");
                        permission_data = [];
                        is_permission_data = false;
                        if (comboBox.getValue() == "user") {
                            tb_type = "user";
                            per_obj_id.runFunction = "selectUserTriggerFn(me)";
                            per_obj_id.emptyText = "==请选择用户==";
                        } else {
                            tb_type = "role";
                            per_obj_id.runFunction = "selectRoleTriggerFn(me)";
                            per_obj_id.emptyText = "==请选择角色==";
                        }
                        per_obj_id.applyEmptyText();
                        setButtonDisabled(true);
                        grid.store.proxy.extraParams.tb_type = tb_type;
                        grid.store.proxy.extraParams.tb_id = "";
                        var check_all = grid.queryById('check_all').getValue();
                        if (check_all) {
                            //grid.queryById('expand_all').setChecked(false);
                            grid.queryById('check_all').setValue(false);
                        } else {
                            setAllCheckedFn(grid.getRootNode(), false);
                        }
                    }
                }
            }, {
                xtype: 'definetrigger',
                itemId: 'per_obj_id',
                name: 'per_obj_id',
                emptyText: '==请选择角色==',
                runFunction: "selectRoleTriggerFn(me)",
                editable: false, width: 160,
                listeners: {
                    'render': function (item) {
                        item.getEl().on('click', function (p) {
                            if (item.runFunction.indexOf("selectUserTriggerFn") >= 0)
                                selectUserTriggerFn(item);
                            else
                                selectRoleTriggerFn(item);
                        });
                    },
                    'change': function (trigger, newValue, oldValue) {
                        //Ext.alert.msg('trigger', 'obj_id=' + trigger.obj_id);
                        tb_id = trigger.obj_id;
                        if (tb_id > 0 && tb_id != "") {
                            grid.store.proxy.extraParams.tb_id = tb_id;
                            getPermissionInfo(tb_id, false);
                        }
                    }
                }
            }, '-', {
                itemId: 'btn_permission',
                xtype: 'button', text: '保存权限',
                minWidth: 60, iconCls: 'icon_save',
                handler: function (btn, pressed) {
                    Ext.MessageBox.confirm("提示!", "您确定要保存当前对象的权限吗?", function (btn) {
                        if (btn == "yes") {
                            saveBtnPermissionFn();
                        }
                    });
                }
            }, '-', {
                itemId: 'btn_refresh',
                xtype: 'button', text: '刷新',
                minWidth: 60, iconCls: 'icon_refresh',
                handler: function (btn, pressed) {
                    //grid.queryById('check_all').setValue(false);
                    getPermissionInfo(tb_id, true);
                }
            }, '-', {
                iconCls: 'icon_other',
                minWidth: 60, text: '其他',
                menu: [
                    {
                        itemId: 'btn_clear_per',
                        text: '清除权限', minWidth: 60,
                        iconCls: 'icon_clear', //xtype: 'button',
                        handler: function (btn, pressed) {
                            Ext.MessageBox.confirm("提示!", "您确定要清除当前对象的所有权限吗?", function (btn) {
                                if (btn == "yes") {
                                    Ext.Ajax.request({
                                        method: "POST", url: path_url.system.power.clear,
                                        params: {tb_type: tb_type, tb_id: tb_id},
                                        success: function (response, options) {
                                            Ext.getBody().unmask();
                                            var resp = Ext.JSON.decode(response.responseText);
                                            if (resp.success) {
                                                permission_data = [];
                                                is_permission_data = false;
                                                setCheckedPermission(grid.getRootNode());
                                                Ext.alert.msg('提示', "清除成功!");
                                            } else {
                                                Ext.alert.msg('提示', "清除失败!");
                                            }
                                        },
                                        failure: function (response, options) {
                                            Ext.alert.msg('提示', "清除失败!");
                                        }
                                    });
                                }
                            });
                        }
                    }, {
                        itemId: 'btn_data_per',
                        text: '数据权限', minWidth: 60,
                        iconCls: 'icon_dataper',//xtype: 'button',
                        handler: function (btn, pressed) {
                            openEditDataPermissionWindow(null);
                        }
                    }]
            }, '->', {
                xtype: 'checkbox',
                itemId: 'check_all',
                labelWidth: 30,
                fieldLabel: '全选',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        //grid.queryById('real_save').setValue(false);
                        //is_permission_data = false;
                        //if (checked) grid.expandAll();
                        setAllCheckedFn(grid.getRootNode(), checked);
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
                itemId: "btn_complexsearch",
                iconCls: "icon_search",
                xtype: 'splitbutton', text: '高级搜索',
                handler: function (button, e) {
                    openItemSearchWin(treenode, grid);
                },
                menu: {
                    items: [{
                        text: '展开所有栏目', itemId: 'expand_all', checked: false,
                        checkHandler: function (item, checked) {
                            if (checked) grid.expandAll();
                            else grid.collapseAll();
                        }
                    }, {
                        text: '只包含配置栏目', checked: false,
                        checkHandler: function (item, checked) {
                            grid.store.proxy.extraParams.tb_type = tb_type;
                            grid.store.proxy.extraParams.self = checked ? 1 : 0;
                            refreshTreeNode(grid);
                        }
                    }, "-", {
                        text: '操作说明',
                        iconCls: "icon_explain",
                        handler: function () {
                            openHelpWindow("html/permission_manage.html", 550, 380, "权限操作说明");
                        }
                    }]
                }
            }, '  ']
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
                    //grid.queryById('expand_all').setChecked(false);
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
                check: record.get('checked'),
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
            check: record.get('checked'),
            item_id: record.get('id'),
            fun_ids: record.get('fun_ids')
        };
    }

    //实时保存该结点的权限(包括父结点及所有子结点)
    function saveRealPermissionFn(record) {
        if (!isExistsByItemId(treenode, "btn_permission", "栏目功能权限")) return false;
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
                    setTimeout(function () {
                        setCheckedPermission(record.parentNode);
                    }, 10);
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
    function saveBtnPermissionFn() {
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
                    setTimeout(function () {
                        setCheckedPermission(grid.getRootNode());
                    }, 10);
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
        grid.queryById('btn_clear_per').setDisabled(disabled);
        grid.queryById('btn_refresh').setDisabled(disabled);
        grid.queryById('btn_data_per').setDisabled(disabled);
        if (!disabled) {
            grid.queryById('btn_permission').setDisabled(!isExistsByItemId(treenode, "btn_permission", ""));
            grid.queryById('btn_clear_per').setDisabled(!isExistsByItemId(treenode, "btn_clear_per", ""));
            grid.queryById('btn_data_per').setDisabled(!isExistsByItemId(treenode, "btn_data_per", ""));
        }
    }

    //该结点是否功能同步
    function setRecordFunSync(record) {
        if (is_fun_sync) {
            if (record.get("checked")) {
                record.set("fun_ids", record.get("all_fun_ids"));
            } else {
                record.set("fun_ids", "");
            }
            record.commit();
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

    function setAllCheckedFn(node, checked) {
        Ext.getBody().mask('正在操作,请稍等...');
        setTimeout(function () {
            var bool = setAllChecked(node, checked);
            Ext.getBody().unmask();
        }, 10);
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
        return true;
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
            root: {
                text: userinfo.organizeName ? userinfo.organizeName : 'XXXX公司',
                id: '0', checked: false, expanded: true
            }
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
                        if (newValue == "5" && tree_store.proxy.extraParams.data_key != "ds.sys.dept") {
                            tree_store.proxy.extraParams.data_key = "ds.sys.dept"
                            tree.getRootNode().removeAll(false);
                            tree.store.load();
                        } else if (newValue == "6" && tree_store.proxy.extraParams.data_key != "ds.sys.user") {
                            tree_store.proxy.extraParams.data_key = "ds.sys.user"
                            tree.getRootNode().removeAll(false);
                            tree.store.load();
                        }
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
                    var button = window.queryById("btn_save_form");
                    button.setDisabled(!isExistsByItemId(treenode, "btn_data_per", ""));
                }
            },
            buttonAlign: "right",
            buttons: [{
                itemId: "btn_save_form",
                text: '保存', minWidth: 70,
                listeners: {
                    click: function () {
                        var datas = [];
                        if (record == null) {
                            getAllPermission(grid.getRootNode(), datas, true);  //批量数据权限保存
                        } else {
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
                setTimeout(function () {
                    setButtonDisabled(true);
                }, 100);
            }
        }
    });
}

function selectUserTriggerFn(field) {
    var tree = Ext.create('Ext.tree.Panel', {
        store: getSysDataTreeStore('ds.sys.dept', false),
        rowLines: false, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        dockedItems: [{
            xtype: 'toolbar', ui: 'footer', dock: 'top', layout: {pack: 'left'},
            items: ["<div style='height:26px;line-height:26px; '>用户部门</div>"]
        }],
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                grid.store.proxy.extraParams.dept_id = record.get("id");
                grid.store.reload();
            }
        }
    });

    var fields = ['id', 'name', 'login_name'];
    var store = Ext.create('Ext.data.Store', {
        autoLoad: true, //是否自动加载
        proxy: {
            url: "system/user/combo", type: 'ajax',
            extraParams: {dept_id: 0},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'id',
                totalProperty: 'totalProperty'
            }
        },
        fields: fields
    });
    var columns = [
        {text: 'user_id', width: 20, fixed: true, dataIndex: 'id', hideable: false, hidden: true},
        {text: '用户名', width: 80, sortable: false, dataIndex: 'name'},
        {text: '登录名', width: 70, sortable: false, dataIndex: 'login_name'}
    ];
    var user_id = 0, user_name = "";
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        multiSelect: false,
        columnLines: false,
        hideHeaders: true, //如果为true，则隐藏列的标题栏。
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            'itemclick': function (gridview, record, item, index) {
                user_id = record.get("id");
                user_name = record.get("name");
                if ($.cookie("sel_user_after") != "1") {
                    wind.close();
                    setTimeout(function () {
                        field.obj_id = user_id;
                        field.setValue(user_name);
                    }, 30);
                }
            }
        },
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['<b>搜索:</b>', {
                xtype: 'textfield', flex: 1, emptyText: "用户名/登录名检索，请按enter键...",
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == Ext.EventObject.ENTER) {
                            grid.store.proxy.extraParams.query = field.getValue().replace(/%/g, '/%').replace(/_/g, '/_');
                            grid.store.reload();
                        }
                    }
                }
            }]
        }],
        columns: columns
    });

    var wind = Ext.create('widget.window', {
        title: '选择用户',
        width: 560,
        height: 430,
        closable: true,
        autoShow: true,
        animateTarget: field.getId(),
        closeAction: 'destroy',  //destroy，hide
        plain: false, modal: true,
        layout: 'border',
        items: [{
            //title: "用户部门",
            region: 'west', split: {width: 4},
            width: 170, minWidth: 1, maxWidth: 200,
            layout: 'fit', items: [tree]
        }, {
            region: 'center',
            layout: 'fit',
            items: [grid]
        }],
        tools: [{
            type: 'refresh', tooltip: '刷新',
            callback: function (panel, tool) {
                tree.getSelectionModel().deselectAll(true);
                grid.store.proxy.extraParams.dept_id = 0;
                grid.store.reload();
            }
        }],
        buttons: [{
            xtype: 'checkbox',
            checked: $.cookie("sel_user_after") == "1",
            labelWidth: 5, fieldLabel: ' ',
            boxLabel: '单击确定后关闭窗口',
            labelSeparator: "", labelAlign: "right",
            listeners: {
                'change': function (item, checked) {
                    $.cookie("sel_user_after", checked ? "1" : "2");
                }
            }
        }, "->", {
            text: '确定', minWidth: 70,
            listeners: {
                click: function () {
                    if (user_id != "0" && user_name != "") {
                        field.obj_id = user_id;
                        field.setValue(user_name);
                        wind.close();
                    } else {
                        Ext.alert.msg("提示", "请选择一个用户！");
                    }
                }
            }
        }, {
            text: '关闭', minWidth: 70,
            handler: function () {
                wind.close();
            }
        }],
        buttonAlign: "right"
    });
}

function selectRoleTriggerFn(field) {
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: path_url.system.ds.storedata,
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {data_key: "ds.sys.role", data_type: "tree", noshows: "1"}
        },
        root: {text: '编码类型', id: 0, pid: 0, expanded: true},
        fields: [{name: 'id', type: 'int'},
            {name: 'text', type: 'string'},
            {name: 'pid', type: 'int'},
            {name: 'serialcode', type: 'int'}]
    });

    var tree = Ext.create('Ext.tree.Panel', {
        store: tree_store,
        rowLines: false, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        viewConfig: {
            getRowClass: function () {
                return 'object_tree_panel_row_height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                wind.close();
                setTimeout(function () {
                    field.obj_id = record.get("id");
                    field.setValue(record.get("text"));
                }, 30);
            }
        }
    });

    var wind = Ext.create('widget.window', {
        title: '选择角色',
        width: 260,
        height: 360,
        layout: 'fit',
        closable: true,
        autoShow: true,
        animateTarget: field.getId(),
        closeAction: 'destroy',  //destroy，hide
        plain: false, modal: true,
        items: [tree]
    });
}