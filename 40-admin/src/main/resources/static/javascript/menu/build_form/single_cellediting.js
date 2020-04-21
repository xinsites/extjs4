Ext.define('javascript.menu.build_form.single_cellediting', {
    extend: ''
});

//单表_单元格对象
function createPanelSingleCellediting(treenode) {
    Ext.define('model_key.single.cellediting', {
        extend: 'Ext.data.Model',
        idProperty: 'idleaf',
        fields: [{"name":"idleaf","type":"int","text":"主键"},
				{"name":"db_dyg_name","type":"string","text":"标题"},
				{"name":"db_dyg_sj","type":"string","text":"时间"},
				{"name":"db_dyg_dw","type":"string","text":"单位"},
				{"name":"db_dyg_dw_text","type":"string","text":"单位_文本值"},
				{"name":"db_dyg_sl","type":"int","text":"数量"},
				{"name":"db_dyg_je","type":"float","text":"金额"},
				{"name":"db_dyg_sfypz","type":"string","text":"是否有凭证"},
				{"name":"db_dyg_sfypz_text","type":"string","text":"是否有凭证_文本值"},
				{"name":"db_dyg_fkfs","type":"string","text":"付款方式"},
				{"name":"item_id","type":"int","text":"栏目号"},
				{"name":"create_time","type":"date","text":"创建时间"},
				{"name":"create_uid","type":"int","text":"创建用户"},
				{"name":"modify_time","type":"date","text":"修改时间"},
				{"name":"modify_uid","type":"int","text":"修改用户"},
				{"name":"org_id","type":"int","text":"机构号"},
				{"name":"dept_id","type":"int","text":"部门号"},
				{"name":"serialcode","type":"int","text":"排序号"},
				{"name":"isdel","type":"int","text":"删除标识"},
				{"name":"create_uid_text","type":"string","text":"创建用户"},
				{"name":"modify_uid_text","type":"string","text":"修改用户"},
				{"name":"dept_id_text","type":"string","text":"部门号"}]
    });
    var itemid = treenode.raw.id;
    var pageSize = getGridPageSize(itemid);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: 'build/single_cellediting/grid',
            extraParams: {item_id: itemid, is_data_per: treenode.raw.isdataper },
            reader: {
                type: 'json', root: 'root',
                idProperty: 'idleaf',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records) {
                if (store.getCount() > 0) {
                    var pagingtoolbar = grid.getComponent("pagingtoolbar");
                    var idleaf = pagingtoolbar.idleaf;
                    var index = store.indexOfId(idleaf);
                    if (idleaf > 0 && index >= 0) {
                        pagingtoolbar.idleaf = 0;
                        grid.editingPlugin.startEdit(index, 2);  //编辑该行第二列
                    }
                } else if (store.currentPage > 1) {
                    store.currentPage = 1;
                    store.load();
                }
            }
        },
        sorters: [{
            property: 'a1.serialcode',
            direction: 'desc'
        }, {
            property: 'a1.create_time',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_key.single.cellediting'
    });
    var columns = [new Ext.grid.RowNumberer({ width: 40, tdCls: 'blue' }),
        { text: '主键', width: 20, dataIndex: 'idleaf', hideable: false, hidden: true},{
  text: '人员姓名',
  dataIndex: 'db_dyg_name',
  width: 130,
  fixed: false,
  align: 'left',
  sortable: true,
editor:{
  xtype: 'textfield',
  name: 'db_dyg_name',
  itemId: 'db_dyg_name',
  fieldLab: '人员姓名',
  allowBlank: false,
  maxLength: 50
}
},{
  text: '时间',
  dataIndex: 'db_dyg_sj',
  width: 120,
  fixed: true,
  align: 'left',
  sortable: true,
editor:{
  xtype: 'datefield',
  name: 'db_dyg_sj',
  itemId: 'db_dyg_sj',
    allowBlank: false,
  format: 'Y-m-d',
  editable: false
}
},{
  text: '单位',
  dataIndex: 'db_dyg_dw',
  width: 110,
  fixed: false,
  align: 'left',
  sortable: true,
editor:{
  xtype: 'treepicker',
  name: 'db_dyg_dw',
  itemId: 'db_dyg_dw',
    allowBlank: false,
  multiSelect: false,
  queryMode: 'remote',
  store: getCodeLayerTreeStore("work.company",false),
  editable: false,
  maxLength: 50
},
renderer:function (value, mata, record) { 
 return getMultiTreeByIdsToText(record, 'db_dyg_dw', value, [], 'work.company'); 
}
},{
  text: '数量',
  dataIndex: 'db_dyg_sl',
  width: 90,
  fixed: false,
  align: 'left',
  sortable: true,
editor:{
  xtype: 'numberfield',
  name: 'db_dyg_sl',
  itemId: 'db_dyg_sl',
    allowBlank: true,
  maxLength: 8
}
},{
  text: '金额',
  dataIndex: 'db_dyg_je',
  width: 90,
  fixed: false,
  align: 'left',
  sortable: true,
editor:{
  xtype: 'numberfield',
  name: 'db_dyg_je',
  itemId: 'db_dyg_je',
    allowBlank: false,
  minValue: 0,
  step: 1,
  decimalPrecision: 2
}
},{
  text: '是否有凭证',
  dataIndex: 'db_dyg_sfypz',
  width: 90,
  fixed: true,
  align: 'center',
  sortable: true,
editor:{
  xtype: 'singlecombobox',
  name: 'db_dyg_sfypz',
  itemId: 'db_dyg_sfypz',
    allowBlank: false,
  valueField: 'id',
  displayField: 'name',
  queryMode: 'remote',
  store: getCodeComboStore("code.yes.no"),
  editable: false,
  maxLength: 15
},
renderer:function (value, mata, record) { 
 return getComboByIdToText(record, 'db_dyg_sfypz', value, [], 'code.yes.no'); 
}
},{
  text: '付款方式',
  dataIndex: 'db_dyg_fkfs',
  width: 200,
  fixed: true,
  align: 'left',
  sortable: true,
  xtype: 'checkgroupcolumn',
  enableChecked: true,
groupCheckValue:[["1","现金","60","1"],["2","支票","60","1"],["3","银行卡","60","0"]],
onCheckChange:function (view, cell, record, value) {
                                                var me = this;
                                                saveCelledingInfo(record, record.get('idleaf'), me.dataIndex, value, '', itemid);
                                            }
},{
  text: '创建时间',
  dataIndex: 'create_time',
  width: 150,
  fixed: true,
  align: 'left',
  sortable: true,
  renderer: Ext.util.Format.dateRenderer('Y-m-d H:i')
}];

    //单表_单元格编辑_拖动排序
    function sortGridRow(store, first_index) {
        if (store && store.getCount() > 0) {
            var sort_vals = [];
            store.each(function (record) {
                sort_vals[sort_vals.length] = record.get("idleaf") + ":" + first_index--;
            });
            Ext.Ajax.request({
                url: 'build/single_cellediting/sort', method: "POST",
                params: { sort_vals: sort_vals.join(";"), item_id: itemid },
                success: function (response, options) {
                    store.load();
                },
                failure: function (response, options) {
                    store.load();
                }
            });
        }
    }

    //单表_单元格编辑_删除选中行
    function deleteGridRow(grid, primarykey) {
        var records = grid.getSelectionModel().getSelection();
        var idleafs = [];
        for (var i = 0; i < records.length; i++) {
            idleafs[idleafs.length] = records[i].get(primarykey);
        }
        if (idleafs.length == 0) {
            Ext.alert.msg('信息提示', '请选择要删除的行！');
        }
        else {
            Ext.MessageBox.confirm("提示!", "您确定要删除选中的" + idleafs.length + "条记录信息吗?", function (btn) {
                if (btn == "yes") {
                    Ext.Ajax.request({
                        method: "POST", url: 'build/single_cellediting/delete',
                        params: { item_id: itemid, idleafs: idleafs.join(",") },
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                if (grid.store.getCount() == records.length && grid.store.currentPage > 1)  //当前页全部删除并且不是第一页
                                    grid.store.previousPage();
                                else
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
    }

    //单表_单元格编辑_单元格编辑保存
    function saveCelledingInfo(record, idleaf, field, value, originalValue, itemid) { //字段编辑保存
        var item_ids = record.store.proxy.extraParams.item_ids;
        if (!isExistsByItemIds(item_ids, "btn_mod", "")) {
            record.set(field, originalValue);
            record.commit();
            return false;
        }
        if (idleaf && idleaf > 0) {
            Ext.Ajax.request({
                method: "POST", url: 'build/single_cellediting/mod',
                params: {
                  item_id: itemid, idleaf: idleaf, field: field, value: value
                },
                success: function (response, options) {
                    var resp = Ext.JSON.decode(response.responseText);
                    if (resp.success) {
                        record.commit();
                    } else {
                        showMsgByJson(resp, "保存失败!");
                        record.set(field, originalValue);
                        record.commit();
                    }
                },
                failure: function (response, options) {
                    showMsgByResponse(response, "保存失败!");
                    record.set(field, originalValue);
                    record.commit();
                }
            });
        }
    }

    var add_type = "first", first_index = 1;
    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid",
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            preserveScrollOnRefresh: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                beforedrop: function (node, data, dropRec, dropPosition) {
                    if (!isExistsByItemId(treenode, "btn_sort", "排序")) return false;
                    if (store.sorters.getCount() != 1) {
                        first_index = 1;
                        if (store.getCount() > 0)
                            first_index = store.getAt(0).get("serialcode");
                    } else {
                        return false;
                    }
                },
                drop: function (node, data, dropRec, dropPosition) {
                    sortGridRow(store, first_index);
                }
            }
        },
        border: false,
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 1,
            onSpecialKey: cellediting_onSpecialKey,
            listeners: {
                beforeedit: function (editor, e) {
                    if (!isExistsByItemId(treenode, "btn_mod", "")) return false;
                    if (!cellediting_beforeedit(grid, this, e)) return false;
                    var xtype = e.column.getEditor().xtype, field = e.field;
                    if (xtype == "my97date") {
                        var record = e.record;
                        if (!e.column.getEditor().isChange) {
                            e.column.getEditor().isChange = true;
                            e.column.getEditor().on("change", function (field, eOpts) {
                                saveCelledingInfo(record, record.get("idleaf"), e.field, field.getValue(), e.originalValue, itemid);
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
                        saveCelledingInfo(record, record.get("idleaf"), e.field, value, e.originalValue, itemid);
                    }
                }
            }
        }],
        selModel: Ext.create("Ext.selection.CheckboxModel", {
            injectCheckbox: 1, //checkbox位于哪一列，默认值为0
            mode: "multi",
            checkOnly: false,
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
                xtype: 'button', text: '新增',
                minWidth: 60, iconCls: 'icon_add', //pressed: true,
                handler: function (btn, pressed) {
                    addGridRow();
                }
            }, '-', {
                itemId: 'btn_del',
                xtype: 'button', text: '删除',
                minWidth: 60, iconCls: 'icon_delete', //pressed: true,
                handler: function (btn, pressed) {
                    deleteGridRow(grid, "idleaf");
                }
            }, {
                xtype: 'tbseparator',
                hidden: false
            }, {
                xtype: 'splitbutton', text: '导出Excel',
                itemId: 'btn_excel', hidden: false,
                minWidth: 60, iconCls: 'icon_excel', //pressed: true,
                handler: function (btn, pressed) {
                   Ext.Ajax.request({
                        url: 'build/single_cellediting/excel', method: "POST",
                        params: {
                            item_id: itemid, table_key: 'key_single_cellediting',
                            linkType: store.proxy.extraParams.linkType,
                            searchdata: store.proxy.extraParams.searchdata
                        },
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            if (resp.success) {
                                downLoadBuildFile(resp.filepath + "||" + resp.filename);
                            }  else {
                                showMsgByJson(resp, "生成Excel失败!");
                            }
                        },
                        failure: function (response, options) {
                            showMsgByResponse(response, "生成Excel失败!");
                        }
                    });
                },
                menu: [{
                    text: '导出字段设置',
                    handler: function () {
                        openExcelFieldSetupWin(itemid, 'key_single_cellediting');
                    }
                }]
            }, '->', '<b>搜索:</b>',
             {
                 xtype: 'textfield', width: 220, emptyText: "标题关键字检索，请按enter键...",
                 listeners: {
                     specialkey: function (field, e) {
                         if (e.getKey() == Ext.EventObject.ENTER) {
                            designSearchByField(store, 'key_single_cellediting', "db_dyg_name", field.getValue());
                         }
                     }
                 }
             }, {
                 xtype: 'splitbutton', text: '高级搜索',
                 itemId: "btn_complexsearch",
                 iconCls: "icon_search", listeners: {
                     click: function () {
                         openHeightSearchWin(treenode, grid, itemid, 'key_single_cellediting');
                     }
                 },
                 menu: { items: [
                    { text: '今天', checked: false, group: 'search-group', scope: this, listeners: { click: function () { gridSearchByDate(store, 'key_single_cellediting', 0); } } },
                    { text: '最近三天', checked: false, group: 'search-group', scope: this, listeners: { click: function () { gridSearchByDate(store, 'key_single_cellediting', 2); } } },
                    { text: '最近一周', checked: false, group: 'search-group', scope: this, listeners: { click: function () { gridSearchByDate(store, 'key_single_cellediting', 6); } } },
                    { text: '最近一月', checked: false, group: 'search-group', scope: this, listeners: { click: function () { gridSearchByDate(store, 'key_single_cellediting', 30); } } },
                    { text: '全部', checked: false, group: 'search-group', scope: this, listeners: {
                        click: function () {
                            store.proxy.extraParams.searchdata = "";
                            store.reload();
                        }
                    }
                    }]
                 }
             }, "-"]
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', { cookieName: "grid_" + itemid })
        }]
    });

    function getNewData() {
        return Ext.create('model_key.single.cellediting', {
  'idleaf': 0,
  'db_dyg_name': userinfo.userName,
  'db_dyg_sj': (new Date()).Format('yyyy-MM-dd'),
  'db_dyg_dw': '52',
  'db_dyg_dw_text': '江春',
  'db_dyg_sl': 50,
  'db_dyg_je': 35.8,
  'db_dyg_sfypz': 'yes',
  'db_dyg_sfypz_text': '是',
  'db_dyg_fkfs': '1,2'
});
    }

    function addGridRow() {
        var new_record = getNewData(); //默认新行值SaveCelleding
        var formData = new_record.getData(false);
        formData["add_type"] = add_type;
        formData["item_id"] = itemid;
        Ext.Ajax.request({
            params: formData,
            method: "POST", url: 'build/single_cellediting/add',
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) {
                    var pagingtoolbar = grid.getComponent("pagingtoolbar");
                    pagingtoolbar.idleaf = resp.id;
                    var totalCount = store.getTotalCount();
                    if (totalCount == 0)
                        grid.store.load();
                    else {
                        if (add_type == "first")
                            pagingtoolbar.moveFirst();
                        else {
                            if (totalCount % store.pageSize == 0) { //如果最后一页是满页，会新增一页
                                var last_page = totalCount / store.pageSize;
                                store.loadPage(last_page + 1);
                            }
                            else {
                                pagingtoolbar.moveLast();
                            }
                        }
                    }
                } else {
                    showMsgByJson(resp, "新增失败!");
                }
            },
            failure: function (response, options) {
                showMsgByResponse(response, "新增失败!");
            }
        });
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}
