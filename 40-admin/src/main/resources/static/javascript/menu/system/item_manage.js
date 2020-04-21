Ext.define('javascript.menu.system.item_manage', {
    extend: ''
});

function createPanel_Item(treenode) {
    var grid_fields = [{"name": "id", "type": "int", "text": "主键"},
        {"name": "pid", "type": "int", "text": "上级单位"},
        {"name": "text", "type": "string", "text": "栏目名称"},
        {"name": "item_method", "type": "string", "text": "页面方法"},
        {"name": "item_type", "type": "string", "text": "栏目类型"},
        {"name": "open_type", "type": "string", "text": "打开方式"},
        {"name": "per_value", "type": "string", "text": "权限标识"},
        {"name": "iconcls", "type": "string", "text": "栏目图标"},
        {"name": "expand", "type": "string", "text": "是否展开"},
        {"name": "fun_names", "type": "string", "text": "栏目功能"},
        {"name": "ishistory", "type": "bool", "text": "历史记录"},
        {"name": "isrecycle", "type": "bool", "text": "回收站"},
        {"name": "isfun", "type": "bool", "text": "栏目功能"},
        {"name": "create_time", "type": "date", "text": "创建时间"},
        {"name": "isdataper", "type": "bool", "text": "数据权限"},
        {"name": "isused", "type": "bool", "text": "是否使用"}];

    Ext.define('model_sys_item', {
        extend: 'Ext.data.Model',
        idProperty: 'id',
        fields: grid_fields
    });

    function getNewData(pid) {
        return Ext.create('model_sys_item', {
            'id': 0,
            'pid': pid,
            'isused': '1',
            'ishistory': '0',
            'isrecycle': '0',
            'isfun': '1',
            'item_type': 'method',
            'item_method': 'itemClick0(tree, record)',
            'open_type': 'tabediting'
        });
    }

    var item_id = treenode.raw.id;
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        remoteSort: false,
        proxy: {
            url: "system/item/tree",
            type: 'ajax', reader: {type: 'json', id: "id"},
            extraParams: {expanded: 0}
        },
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
            }
        },
        root: {text: '根节点', id: 0, pid: 0, expanded: true},
        fields: grid_fields
    });
    var columns = [{
        xtype: 'treecolumn', text: '栏目名称', width: 280, fixed: false, dataIndex: 'text',
        renderer: function (val, meta, rec) {
            if (rec.get("isused") == "0") {
                val = '<s style="color:#888;">' + val + '</s>';
            } else if (rec.get("item_method") == "" && rec.get("leaf")) {
                val = '<span style="color:red;">' + val + '</span>';
            }
            if (rec.get("expand") == "true")
                return val + '<span style="color:blue;">[默认展开]</span>';
            return val;
        }
    }, {
        xtype: 'checkcolumn', text: '列表权限', width: 80, fixed: true, dataIndex: 'per_value', stopSelection: false,
        listeners: {
            'checkchange': function (field, recordIndex, checked, record) {
                //editingInfoFn(record, record.get("id"), field.dataIndex, checked ? 1 : 0, !checked);
            }
        }
    }, {
        xtype: 'checkcolumn', text: '数据权限', width: 80, fixed: true, dataIndex: 'isdataper', stopSelection: false,
        listeners: {
            'checkchange': function (field, recordIndex, checked, record) {
                //editingInfoFn(record, record.get("id"), field.dataIndex, checked ? 1 : 0, !checked);
            }
        }
    }, {
        text: '栏目功能',
        dataIndex: 'fun_names',
        width: 160,
        fixed: false,
        align: 'left',
        flex: 1.8,
        sortable: false,
        renderer: function (val, meta, rec) {
            if (val != "") {
                return '<span style="color:blue;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'fun_names\')">' + val + '</span>';
            } else if (rec.get("isfun") == "1") {
                return '<span style="color:red;text-decoration: underline;cursor:pointer;" onclick="setItemClickFlag(\'fun_names\')">尚未配置</span>';
            }
            return val;
        }
    }];

    //单元格编辑
    function editingInfoFn(record, Id, field, value, originalValue) {
        Ext.Ajax.request({
            method: "POST", url: "system/item/editing",
            params: {item_id: item_id, Id: Id, field: field, value: value},
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

    //动态树拖动保存排序
    function saveDragSortFn(parent_record) {
        var childNodes = parent_record.childNodes;
        var str = [];
        for (var i = 0, len = childNodes.length; i < len; i++) {
            var record = childNodes[i];
            record.set("pid", parent_record.get("id"));
            str[str.length] = record.get("id") + ":" + record.get("pid") + ":" + (i + 1);
        }
        Ext.Ajax.request({
            method: "POST", url: "system/item/sort",
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

    var grid = Ext.create('Ext.tree.Panel', {
        itemId: "main_grid",
        store: store,
        useArrows: false,
        rowLines: true,
        forceFit: true,
        emptyText: "没有数据!",
        //multiSelect: true,
        autoScroll: true,
        bodyCls: 'grid_empty_text_parent',
        viewConfig: {
            getRowClass: function (record, rowIndex, rowParams, store) {
                if (record.get("isused") == "0")
                    return 'disabled-grid-tree-row-style';
                else
                    return 'custom-grid-tree-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            preserveScrollOnRefresh: true,
            plugins: {
                ptype: 'treeviewdragdrop',
                allowLeafInserts: true, //叶子节点是否可拖动的配置，默认值为true
                containerScroll: true,
                dragGroup: 'organize_draggroup',
                dropGroup: 'organize_draggroup'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_sort", "排序")) return false;
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
                    saveDragSortFn(data.records[0].parentNode);
                }
            }
        },
        autoScroll: true, rootVisible: false,
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 2
        }],
        listeners: {
            "itemclick": function (treeview, record, item, index, e) {
                treeview.toggleOnDblClick = false; //取消双击展开折叠菜单行为
                var leaf = record.get('leaf');
                if (!leaf && !record.isExpanded()) {
                    setTimeout(function () {
                        //Ext.alert.msg('提示', "itemclick=" + treeview.dblclick);
                        treeview.expand(record, false);
                    }, 50);
                }
            },
            "cellclick": function (treeview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (header.dataIndex == "fun_names" && itemClickFlag == "fun_names") {
                    if (isExistsByItemId(treenode, "btn_fun", "栏目功能设置")) openCommonItemFunGridWindow(grid, rec, treenode);
                }
            },
            "celldblclick": function (treeview, td, cellIndex, rec, tr, rowIndex, e) {
                var header = grid.columnManager.getColumns()[cellIndex];
                if (itemClickFlag != "fun_names") {
                    Ext.getBody().mask('请稍等,正在获取数据...');
                    Ext.Ajax.request({
                        method: "POST", url: "system/item/info",
                        params: {item_id: rec.get("id")},
                        success: function (response, options) {
                            Ext.getBody().unmask();
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                var formdata = Ext.create("model_sys_item", resp["item"]["data"]);
                                openEditWindow(formdata, rec.parentNode, rec);
                            }
                        },
                        failure: function (response, options) {
                            Ext.getBody().unmask();
                        }
                    });
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
                            itemId: "add",
                            text: "添加",
                            disabled: !isExistsByItemId(treenode, "btn_add", ""),
                            iconCls: 'icon_add',
                            handler: function () {
                                var sel_record = me.sel_record;
                                var pid = sel_record.get("id");
                                sel_record.expand(false);
                                openEditWindow(getNewData(pid), sel_record, null);
                            }
                        }, {
                            itemId: "mod",
                            text: "修改",
                            disabled: !isExistsByItemId(treenode, "btn_mod", ""),
                            iconCls: 'icon_edit',
                            handler: function () {
                                var sel_record = me.sel_record;
                                //openEditWindow(sel_record, sel_record.parentNode);
                                Ext.getBody().mask('请稍等,正在获取数据...');
                                Ext.Ajax.request({
                                    method: "POST", url: "system/item/info",
                                    params: {item_id: sel_record.get("id")},
                                    success: function (response, options) {
                                        Ext.getBody().unmask();
                                        var resp = Ext.JSON.decode(response.responseText);
                                        if (resp.success) {
                                            var formdata = Ext.create("model_sys_item", resp["item"]["data"]);
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
                            tooltip: '父栏目不能删除',
                            handler: function () {
                                var sel_record = me.sel_record;
                                Ext.MessageBox.confirm("提示!", "您确定要删除这条记录吗?", function (btn) {
                                    if (btn == "yes") {
                                        Ext.Ajax.request({
                                            method: "POST", url: "system/item/delete",
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
                if (isExistsByItemId(treenode, "btn_del", ""))
                    deleteItem.setDisabled(!sel_record.get('leaf'));
                ctxMenu.showAt(e.getXY()); //让右键菜单跟随鼠标位置  
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: [{
                itemId: 'btn_add',
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    openEditWindow(getNewData(0), grid.getRootNode(), null);
                }
            }, '-', {
                itemId: 'btn_setup',
                xtype: 'splitbutton', text: '栏目功能设置',
                minWidth: 60, iconCls: 'icon_item_setup', //pressed: true,
                handler: function (btn, pressed) {
                    if (isExistsByItemId(treenode, "btn_setup", "栏目功能设置")) {
                        var item_ids = getCheckItemIds();
                        if (item_ids != "") openItemFunConfigWinFn(grid, item_ids, grid.queryById("btn_setup"));
                    }
                },
                menu: [{
                    itemId: 'btn_fun', text: '常用功能',
                    minWidth: 60, iconCls: 'icon_item_fun', //pressed: true,
                    handler: function (btn, pressed) {
                        if (isExistsByItemId(treenode, "btn_fun", "常用功能"))
                            openCommonItemFunGridWindow(grid, getNewData(0), treenode);
                    }
                }]
            }, {
                itemId: "btn_attach",
                xtype: 'button', text: '附件类型',
                iconCls: "icon_upload_attach", listeners: {
                    click: function () {
                        if (isExistsByItemId(treenode, "btn_attach", "上传附件类型")) {
                            var item_ids = getCheckItemIds();
                            if (item_ids != "") openAttachTypeWinFn(grid, item_ids, grid.queryById("btn_attach"));
                        }
                    }
                }
            }, {
                xtype: 'button', text: '高级搜索',
                itemId: "btn_complexsearch",
                iconCls: "icon_search", listeners: {
                    click: function () {
                        openItemSearchWin(treenode, grid);
                    }
                }
            }, '->', {
                xtype: 'checkbox',
                labelWidth: 90,
                fieldLabel: '展开所有栏目',
                labelSeparator: "",
                labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        if (checked) grid.expandAll();
                        else grid.collapseAll();
                    }
                }
            }, '  ']
        }]
    });

    function getCheckItemIds() {
        var records = grid.getChecked();
        if (records.length > 0) {
            var ids = [];
            for (var i = 0; i < records.length; i++) {
                ids[ids.length] = records[i].get("id");
            }
            return ids.join(",");
        } else {
            Ext.alert.msg('提示', "请勾选要设置的栏目!");
            return "";
        }
    }

    //打开上传附件类型设置窗口
    function openAttachTypeWinFn(grid, item_ids, btn) {
        var types = getUploadFileTypeDatas();
        var types_field = [], attach_type_value = [];
        types.forEach(function (item) {
            types_field[types_field.length] = {boxLabel: item.id, name: 'attach_type', inputValue: item.id};
            attach_type_value[attach_type_value.length] = item.id;
        });
        var forms = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '20 15 10',
            autoScroll: true,
            layout: {type: 'vbox', align: 'stretch'},
            fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 60},
            items: [{
                itemId: "item_name", xtype: 'displayfield',
                fieldLabel: '栏目名称', style: 'margin-bottom:0px'
            }, {
                xtype: 'checkboxgroup',
                fieldLabel: '附件类型',
                name: 'attach_type',
                itemId: 'attach_type',
                columns: 4, items: types_field
            }]
        });

        var attachTypeWin = Ext.create('Ext.window.Window', {
            title: "允许上传的附件类型",
            animateTarget: btn.getId(),
            width: 400, minWidth: 200,
            resizable: false, closable: true,
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体
            layout: "fit",
            items: [forms],
            tools: [{
                type: 'help', tooltip: '&nbsp;作用于有附件列表的栏目&nbsp;&nbsp;&nbsp;&nbsp;',
                callback: function (panel, tool) {
                }
            }],
            listeners: {
                "show": function (window, eOpts) {
                    var records = grid.getChecked();
                    if (records.length > 0) {
                        var names = [];
                        for (var i = 0; i < records.length; i++) {
                            names[names.length] = records[i].get("text");
                            if (i > 1) break;
                        }
                        var item_name = names.join(",");
                        if (records.length > 2) item_name += "...";
                        forms.queryById('item_name').setValue('<span style="color:blue;">' + item_name + '</span>')
                    }
                    if (records.length == 1) {
                        Ext.Ajax.request({
                            method: "POST", url: "system/item/filetype/info",
                            params: {item_id: records[0].get("id")},
                            success: function (response, options) {
                                Ext.getBody().unmask();
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    var attach_type = forms.queryById('attach_type');
                                    if (attach_type) {
                                        attach_type.setValue({"attach_type": resp.uploadfile_type.split(",")});
                                    }
                                }
                            },
                            failure: function (response, options) {
                                Ext.getBody().unmask();
                            }
                        });
                    }
                }
            },
            dockedItems: [{
                xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
                items: [{
                    xtype: 'checkbox',
                    boxLabel: '全选', checked: false,
                    labelWidth: 5, labelAlign: "right",
                    fieldLabel: ' ', labelSeparator: "",
                    listeners: {
                        'change': function (item, checked) {
                            var attach_type = forms.queryById('attach_type');
                            if (attach_type) {
                                if (checked) attach_type.setValue({"attach_type": attach_type_value});
                                else attach_type.setValue({"attach_type": ""});
                            }
                        }
                    }
                }, "->", {
                    text: "保存",
                    minWidth: 70,
                    handler: function () {
                        if (forms.isValid()) {
                            forms.submit({
                                url: "system/item/filetype/save", method: "POST",
                                waitMsg: '请稍等，正在保存...',
                                params: {item_id: item_id, item_ids: item_ids},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        attachTypeWin.close();
                                        Ext.alert.msg('提示', '设置成功！');
                                    } else {
                                        Ext.alert.msg('提示', '设置失败！');
                                    }
                                },
                                failure: function (form, action) {
                                    ajaxFailureTipMsg(form, action);
                                }
                            });
                        }
                    }
                }, {
                    text: "关闭",
                    minWidth: 70,
                    handler: function () {
                        attachTypeWin.close();
                    }
                }]
            }]
        });
        attachTypeWin.show();
    }

    function openEditWindow(form_data, parent_record, cur_record) {
        var btn = grid.queryById('btn_add');
        var form = createFormPanel_ItemInfo(form_data);
        var win_config = {
            title: '添加表单',
            animateTarget: btn.getId(),
            items: [form],
            tools: [{
                type: 'help', tooltip: '说明',
                callback: function (panel, tool) {
                    openHelpWindow("html/item_help.html", 700, 500);
                }
            }],
            listeners: {
                afterrender: function (window, eOpts) {
                    designFormOriginalValue(form, form_data);
                },
                show: function (window, eOpts) {
                    var pid = parent_record.get("id"), pid_text = parent_record.get("text");
                    var button = window.queryById("btn_save_form");
                    if (form_data.get("id") == 0) {
                        if (pid == 0) window.setTitle("栏目新增[一级栏目]");
                        else window.setTitle(pid_text + "-新增子栏目");
                        button.setDisabled(!isExistsByItemId(treenode, "btn_add", ""));
                    } else {
                        var iconcls = form.queryById('btn_iconcls');
                        iconcls.setIconCls(form_data.get("iconcls"));
                        window.setTitle("栏目修改-" + form_data.get("text"));
                        button.setDisabled(!isExistsByItemId(treenode, "btn_mod", ""));
                    }
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
                itemId: "btn_save_form",
                text: '保存', minWidth: 70,
                listeners: {
                    click: function () {
                        // var values = form.getForm().getValues(false);
                        if (form.isValid()) {
                            form.submit({
                                method: "POST", url: "system/item/save",
                                waitMsg: '请稍等，正在保存...', submitEmptyText: false, //如果被置为 true,emptyText值将在form提交时一同发送默认为true
                                params: {item_id: item_id},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        var Values = form.getForm().getValues(false);
                                        //grid.store.reload();
                                        if (form_data.get("id") == 0) {
                                            var record = parent_record.copy(rndNum(4), false);
                                            record.set("id", action.result.id);
                                            record.set("text", Values["text"]);
                                            record.set("item_method", Values["item_method"]);
                                            record.set("per_value", Values["per_value"]);
                                            record.set("iconcls", Values["iconcls"]);
                                            record.set("iconCls", Values["iconcls"]);
                                            record.set("isused", Values["isused"]);
                                            record.set("isdataper", Values["isdataper"]);
                                            record.set("isfun", Values["isfun"]);
                                            record.set("expand", Values["expand"]);
                                            record.set("fun_names", "");
                                            record.set("checked", false);

                                            record.set("leaf", true);
                                            record.set("allowDrag", true);

                                            parent_record.set("leaf", false);
                                            parent_record.appendChild(record);
                                            grid.expand(parent_record, false);
                                            parent_record.commit();
                                            record.commit();
                                        } else if (cur_record) {
                                            cur_record.set("text", Values["text"]);
                                            cur_record.set("item_method", Values["item_method"]);
                                            cur_record.set("per_value", Values["per_value"]);
                                            cur_record.set("iconcls", Values["iconcls"]);
                                            cur_record.set("iconCls", Values["iconcls"]);
                                            cur_record.set("isused", Values["isused"]);
                                            cur_record.set("isdataper", Values["isdataper"])
                                            cur_record.set("isfun", Values["isfun"]);
                                            cur_record.set("expand", Values["expand"]);
                                            cur_record.commit();
                                        }
                                        if ($.cookie("submit_after") == "1") {
                                            Ext.alert.msg("提示", "保存成功！");
                                            win.close();
                                        } else if (form_data.get("id") == 0) {
                                            Ext.alert.msg("提示", "保存成功，请新增下一条！");
                                            form.getForm().reset();
                                        } else {
                                            Ext.alert.msg("提示", "保存成功！");
                                            designFormOriginalValue(form, Ext.create("model_sys_item", Values));
                                        }
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
        }
        var win = Ext.create('widget.window', Ext.apply(win_config, {
            width: 540,
            height: 380,
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
        items: [grid]
    });
}

//创建【基本信息】Form表单
function createFormPanel_ItemInfo(record) {
    var form_config = {
        border: false,
        bodyPadding: '26 30 10',
        autoScroll: true,
        layout: {type: 'vbox', align: 'stretch'},
        fieldDefaults: {labelAlign: 'right', msgTarget: 'side', labelWidth: 70}
    };
    form_config.items = [{xtype: 'hiddenfield', name: 'id'},
        {xtype: 'hiddenfield', name: 'pid'}, {
            xtype: 'textfield',
            name: 'text',
            itemId: 'text',
            fieldLabel: '*栏目名称',
            allowBlank: false,
            maxLength: 50
        }, {
            itemId: 'item_type',
            xtype: 'radiogroup',
            fieldLabel: '栏目类型',
            columns: [100, 100, 80],
            name: 'item_type', allowBlank: false,
            items: [{boxLabel: '创建页面', name: 'item_type', inputValue: 'method'},
                {boxLabel: '嵌套页面', name: 'item_type', inputValue: 'page'},
                {boxLabel: '菜单目录', name: 'item_type', inputValue: 'list'}],
            listeners: {
                change: function (group, rec, oldrecord) {
                    var item_type = group.getValue()["item_type"];
                    var allowBlank = (item_type == "list");
                    //var open_type = form.queryById('open_type');
                    var item_method = form.queryById('item_method');
                    item_method.allowBlank = allowBlank;
                    //open_type.allowBlank = allowBlank;
                    if (allowBlank) {
                        //open_type.clearValue();
                        item_method.setValue("");
                    } else {
                        //open_type.setValue(record.get("open_type"));
                        item_method.setValue(record.get("item_method"));
                    }
                    var item_method = form.queryById('item_method');
                    item_method.selectText(0, 50);
                }
            }
        }, {
            xtype: 'fieldcontainer',
            fieldLabel: '方法/地址',
            layout: 'hbox',
            items: [{
                xtype: 'textfield',
                name: 'item_method',
                itemId: 'item_method',
                flex: 1, allowBlank: false,
                //selectOnFocus: true,
                hideLabel: true, maxLength: 50
            }, {
                cls: "x-btn-default-toolbar-small",
                style: 'margin-left:5px;margin-top:2px',
                xtype: 'button', text: '生成',
                handler: function (btn, pressed) {
                    var item_method = form.queryById('item_method');
                    item_method.setValue(Ext.String.format("itemClick{0}(tree, record)", record.get("id")));
                }
            }]
        }, {
            //     xtype: 'singlecombobox',
            //     name: 'open_type',
            //     itemId: 'open_type',
            //     fieldLabel: '打开方式',
            //     allowBlank: false,
            //     valueField: 'id',
            //     displayField: 'name',
            //     queryMode: 'local',
            //     store: {
            //         "fields": ["id", "name"],
            //         "data": [{"id": "tabediting", "name": "选项卡"}, {"id": "winediting", "name": "新窗口"}]
            //     },
            //     editable: false,
            //     maxLength: 50
            // }, {
            xtype: 'textfield',
            name: 'per_value',
            itemId: 'per_value',
            fieldLabel: '列表权限值',
            emptyText: "页面有列表时的权限值，如system:item:grid",
            allowBlank: true,
            maxLength: 50
        }, {
            xtype: 'displayfield',
            hideLabel: false,
            value: '控制器列表方法标注@RequiresPermissions("***")',
            fieldStyle: 'color:#FF0000;',
            style: 'margin-top:0px;',
            fieldLabel: '  ',
            labelSeparator: ''
        }, {
            xtype: 'fieldcontainer',
            fieldLabel: '栏目图标',
            layout: 'hbox',
            items: [{
                xtype: 'definepicker',
                name: 'iconcls',
                itemId: 'iconCls',
                fieldLabel: '栏目图标',
                flex: 1, allowBlank: true,
                runFunction: 'iconClsPickerFn(me)',
                hideLabel: true, editable: false,
                listeners: {
                    change: function (item, value) {
                        var iconcls = form.queryById('btn_iconcls');
                        iconcls.setIconCls(value);
                    }
                }
            }, {
                itemId: 'btn_iconcls',
                width: 25, iconCls: '',
                tooltip: '点击清空栏目图标',
                cls: "x-btn-default-toolbar-small",
                style: 'margin-left:5px;margin-top:2px',
                xtype: 'button', text: ' ',
                handler: function () {
                    var iconCls = form.queryById('iconCls');
                    iconCls.setValue("");
                }
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
                boxLabel: '是否可用',
                name: 'isused',
                inputValue: '1',
                uncheckedValue: "0"
            }, {
                flex: 0, width: 30,
                xtype: 'displayfield',
                value: ''
            }, {
                xtype: 'checkbox',
                fieldLabel: ' ',
                labelSeparator: "",
                boxLabel: '数据权限',
                name: 'isdataper',
                inputValue: '1',
                uncheckedValue: "0"
            }, {
                flex: 0, width: 30,
                xtype: 'displayfield',
                value: ''
            }, {
                xtype: 'checkbox',
                fieldLabel: '',
                itemId: 'expanded',
                labelSeparator: "",
                boxLabel: '默认展开',
                name: 'expand',
                inputValue: 'true',
                uncheckedValue: "false"
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
                boxLabel: '栏目功能',
                name: 'isfun',
                inputValue: '1',
                uncheckedValue: "0"
            }, {
                flex: 0, width: 30,
                xtype: 'displayfield',
                value: ''
            }, {
                xtype: 'checkbox',
                fieldLabel: ' ',
                labelSeparator: "",
                boxLabel: '历史记录',
                itemId: 'ishistory',
                name: 'ishistory',
                inputValue: '1',
                uncheckedValue: "0"
            }, {
                flex: 0, width: 30,
                xtype: 'displayfield',
                value: ''
            }, {
                xtype: 'checkbox',
                fieldLabel: ' ',
                labelSeparator: "",
                boxLabel: '回收站',
                itemId: 'isrecycle',
                name: 'isrecycle',
                inputValue: '1',
                uncheckedValue: "0"
            }]
        }
        //    , {
        //        xtype: 'displayfield',
        //        hideLabel: true,
        //        style: 'margin-top:5px',
        //        value: '<span style="color:blue;line-height: 22px;  ">说明：在菜单栏目，对于父结点，勾选“默认展开”后，打开菜单栏目时该结点将展开。</span>'
        //    }
    ];
    var form = Ext.create('Ext.form.Panel', form_config);
    return form;
}

//自定义图标选择器（Picket弹出框）
function iconClsPickerFn(field) {
    Ext.define('model_iconcls', {
        extend: 'Ext.data.Model',
        fields: [{name: 'IconCls'}],
        idProperty: 'IconCls'
    });
    var myData = [['icon_upload'], ['icon_expand'],
        ['icon_collapse'], ['icon_add'], ['icon_edit'], ['icon_delete'], ['icon_list'],
        ['icon_app'], ['icon_excel'], ['icon_search'], ['icon_setup'],
        ['icon_help'], ['icon_save'], ['icon_test'], ['icon_useradd'],
        ['icon_write'], ['icon_preview'], ['icon_build'], ['icon_explain'],
        ['icon_code_build'], ['icon_refresh'], ['icon_options'], ['icon_leader'],
        ['icon_personer'], ['icon_member'], ['icon_clear'], ['icon_code_pub'],
        ['icon_code_com'], ['icon_combo_test'], ['icon_resetpwd'], ['icon_permissionn']];
    var store = Ext.create('Ext.data.ArrayStore', {
        model: 'model_iconcls',
        data: myData
    });
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        disableSelection: true, //设置为true，则禁用选择模型
        columnLines: false,
        hideHeaders: true,
        forceFit: true,
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            }
        },
        listeners: {
            'itemclick': function (gridview, record, item, index) {
                field.setValue(record.get("IconCls"));
                field.collapse();
            }
        },
        columns: [{
            menuDisabled: true,
            align: 'center',
            xtype: 'actioncolumn',
            fixed: true, width: 50,
            items: [{
                getClass: function (v, meta, rec) {
                    return rec.get("IconCls");
                }
            }]
        }, {
            text: '名称', width: 120, sortable: false, dataIndex: 'IconCls'
        }]
    });
    return Ext.create('Ext.panel.Panel', {
        border: true,
        floating: true,
        height: 240,
        minWidth: 260,
        resizable: false,
        //autoScroll: true,
        layout: 'fit',
        items: [grid]
    });
}

//打开栏目功能设置窗口
function openItemFunConfigWinFn(grid, item_ids, btn) {
    var tree_store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: false,
        proxy: {
            type: 'ajax', url: "system/fun/treecombo",
            reader: {
                totalProperty: 'totalProperty',
                type: 'json', id: "id", root: 'root'
            },
            extraParams: {}
        },
        root: {text: '根节点', id: -1, expanded: true}
    });
    var tree = Ext.create('Ext.ux.CascadeTree', {
        frame: false, border: false,
        autoScroll: true,
        rootVisible: false,
        store: tree_store,
        queryMode: 'remote',
        multiSelect: true,
        singleClickExpand: true,
        viewConfig: {
            loadMask: false
        }
    });
    var pre_per_val = getCookie("pre_per");
    if (!pre_per_val) pre_per_val = "system:item";
    var forms = Ext.create('Ext.form.Panel', {
        border: false,
        bodyPadding: '20 15 10',
        autoScroll: true,
        layout: {type: 'vbox', align: 'stretch'},
        fieldDefaults: {labelAlign: 'left', msgTarget: 'side', labelWidth: 70},
        items: [{
            xtype: 'textfield',
            name: 'pre_permission',
            itemId: 'pre_permission',
            fieldLabel: '权限值前缀',
            value: pre_per_val,
            emptyText: "前缀+权限值构成栏目权限值",
            allowBlank: false, maxLength: 50
        }, {
            itemId: 'fun_ids',
            layout: 'fit', border: true,
            items: [tree], flex: 1
        }]
    });

    var type = "替换", refresh = true;
    var itemFunConfigWin = Ext.create('Ext.window.Window', {
        title: "多栏目功能批量设置",
        animateTarget: btn.getId(),
        height: 400, width: 300,
        minWidth: 200,
        resizable: true,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: 'true',  // 弹出模态窗体  
        layout: "fit",
        items: [forms],
        listeners: {
            "show": function (window, eOpts) {
            }
        },
        dockedItems: [{
            xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
            items: [{
                xtype: 'checkbox',
                boxLabel: '刷新列表', checked: true,
                labelWidth: 5, labelAlign: "right",
                fieldLabel: ' ', labelSeparator: "",
                listeners: {
                    'change': function (item, checked) {
                        refresh = checked;
                    }
                }
            }, "->", {
                text: "保存",
                minWidth: 70,
                handler: function () {
                    if (forms.isValid()) {
                        var records = tree.getChecked();
                        var fun_id = [];
                        for (var i = 0; i < records.length; i++) {
                            fun_id[fun_id.length] = records[i].raw.id;
                        }
                        var pre_permission = forms.queryById('pre_permission').getValue();
                        Ext.Ajax.request({
                            method: "POST", url: "system/fun/batch/save",
                            params: {
                                pre_permission: pre_permission, item_ids: item_ids,
                                fun_ids: fun_id.join(","), type: type
                            },
                            success: function (response, options) {
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    setCookie("pre_per", pre_permission);
                                    if (refresh) refreshTreeNode(grid);
                                    itemFunConfigWin.close();
                                    Ext.alert.msg('提示', "设置成功!");
                                }
                                else {
                                    Ext.alert.msg('提示', "设置失败!");
                                }
                            },
                            failure: function (response, options) {
                                Ext.alert.msg('提示', "设置失败!");
                            }
                        });
                    }
                }
            }, {
                text: "关闭",
                minWidth: 70,
                handler: function () {
                    itemFunConfigWin.close();
                }
            }]
        }]
    });
    itemFunConfigWin.show();
}
