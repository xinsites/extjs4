
//是否文本字段
function isTextField(type) {
    return type == "text" || type == "ntext" || type == "longtext" || type == "varchar(max)" || type == "nvarchar(max)";
}

//输入框默认文本值
var input_default_value = [
    {value_tip: "#Current_Date", value: new Date(), type: "object", text: '当前日期'}
    , {value_tip: "#Current_UserId", value: userinfo.userId, type: "object", text: '当前登录用户Id'}
    , {value_tip: "#Current_UserName", value: userinfo.userName, type: "object", text: '当前登录用户名'}
    , {value_tip: "#Current_OrgId", value: userinfo.orgId, type: "object", text: '当前登录用户所在组织Id'}
    , {value_tip: "#Current_OrganizeName", value: userinfo.organizeName, type: "object", text: '当前登录用户所在组织名称'}
    , {value_tip: "#Current_RoleId", value: userinfo.roleId, type: "object", text: '当前登录用户所属角色Id'}
    , {value_tip: "#Current_RoleName", value: userinfo.roleName, type: "object", text: '当前登录用户所属角色名称'}
    , {value_tip: "#Current_DeptId", value: userinfo.deptId, type: "object", text: '当前登录用户所在部门Id'}
    , {value_tip: "#Current_DeptName", value: userinfo.deptName, type: "object", text: '当前登录用户所在部门名称'}
    , {value_tip: "#Current_PostId", value: userinfo.postId, type: "object", text: '当前登录用户所属职位Id'}
    , {value_tip: "#Current_PostName", value: userinfo.postName, type: "object", text: '当前登录用户所属职位名称'}
    , {value_tip: "#Current_Phone", value: userinfo.phone, type: "object", text: '当前登录用户联系电话'}
];

//获取特殊默认值
function getSpecialDefaultValue(xtype, fieldvalue, treenode) {
    if (typeof fieldvalue == "string") {
        if (treenode) {
            fieldvalue = fieldvalue.replace("#Item_Id", treenode.raw.id);
            fieldvalue = fieldvalue.replace("#Item_Name", treenode.raw.text);
        }
        Ext.Array.each(input_default_value, function (item, index) {
            if (typeof fieldvalue == "string" && fieldvalue.indexOf(item.value_tip) >= 0) {
                if (fieldvalue.indexOf("#Current_Date") >= 0) {
                    if (xtype != "datefield" && xtype != "datetimefield" && xtype != "timefield") {
                        var attr = fieldvalue.match(/{#Current_Date(.*?)}/ig);
                        if (attr) {
                            for (var i = 0; i < attr.length; i++) {
                                var str = attr[i];
                                if (str == "{#Current_Date}")
                                    fieldvalue = fieldvalue.replace(str, (new Date()).Format('yyyy-MM-dd'));
                                else {
                                    var format = str.replace("{#Current_Date_", "").replace("}", "");
                                    fieldvalue = fieldvalue.replace(str, (new Date()).Format(format));
                                }
                            }
                        } else {
                            fieldvalue = fieldvalue.replace(item.value_tip, (new Date()).Format('yyyy-MM-dd'));
                        }
                    } else {
                        fieldvalue = new Date();
                        return false;
                    }
                } else {
                    fieldvalue = fieldvalue.replace(item.value_tip, item.value);
                }
                return;
            }
        });
    }
    return fieldvalue;
}

//刷新Grid行号
function updateRownumber(grid) {
    Ext.get(grid.el.dom).select('td.x-grid-cell-first div').each(function (el, c, index) {
        if (index > 0) el.setHTML(index + 1);
    });
}

//生成汉字拼音
function generateChinaSpell(textfield, text, type, lower) {
    if (text != "") {
        Ext.Ajax.request({
            method: "POST", url: path_url.info.util.buildspell,
            params: {text: text, type: type, lower: lower},
            success: function (response, options) {
                var resp = Ext.JSON.decode(response.responseText);
                if (resp.success) textfield.setValue(resp.value);
            },
            failure: function (response, options) {
            }
        });
    }
}

//获取用户名
function getUserNameFn(record, field, value) {
    return getFieldNameFn(record, field, value, path_url.info.util.username);
}

//获取角色名
function getRoleNameFn(record, field, value) {
    return getFieldNameFn(record, field, value, path_url.info.util.rolename);
}

//获取部门名
function getDeptNameFn(record, field, value) {
    return getFieldNameFn(record, field, value, path_url.info.util.deptname);
}

//根据Id获取字段名
function getFieldNameFn(record, field, value, path_url) {
    if (value == "0") return "";
    if (record.get(field + "_text"))
        return record.get(field + "_text");
    setTimeout(function (record, field, value) {
        if (!record.get(field + "_load")) {
            Ext.Ajax.request({
                method: "POST", url: path_url,
                params: {ids: value},
                success: function (response, options) {
                    var resp = Ext.JSON.decode(response.responseText);
                    record.set(field + "_load", "true");
                    if (resp.success) {
                        if (value != "" && resp.texts == "")
                            record.set(field + "_text", value);
                        else
                            record.set(field + "_text", resp.texts);
                        record.commit();
                    }
                },
                failure: function (response, options) {
                    record.set(field + "_load", "true");
                }
            });
        }
    }, 50, record, field, value);
}

//获取编码值文本
function getCodeTextFn(record, field, value, id_field, data_key) {
    if (!record.get(field + "_load")) {
        setTimeout(function (record, field, value, id_field, data_key) {
            Ext.Ajax.request({
                async: false,   //async 是否异步(true 异步, false 同步)
                method: "POST", url: "system/code/codetext",
                params: {
                    ids: value, id_field: id_field, data_key: data_key
                },
                success: function (response, options) {
                    var resp = Ext.JSON.decode(response.responseText);
                    record.set(field + "_load", "true");
                    if (resp.success) {
                        if (value != "" && resp.texts == "")
                            record.set(field + "_text", value);
                        else
                            record.set(field + "_text", resp.texts);
                        record.commit();
                    }
                },
                failure: function (response, options) {
                    record.set(field + "_load", "true");
                }
            });
        }, 20, record, field, value, id_field, data_key);
    }
}

//下拉单选本地加载获取文本值
function getComboByIdToText(record, field, value, datas, data_key) {
    //if (!value || value == "") return "";
    if (record.get(field + "_text"))
        return record.get(field + "_text");
    if (datas && datas.length > 0) {
        var text = "";
        for (var i = 0; i < datas.length; i++) {
            if (datas[i][0] == value) {
                text = datas[i][1];
                break;
            }
        }
        return text;
    }
    getCodeTextFn(record, field, value, "value", data_key);
    return "";
}

//下拉多选本地加载获取文本值
function getMultiComboByIdsToText(record, field, value, datas, data_key) {
    if (!value || value == "") return "";
    if (record.get(field + "_text"))
        return record.get(field + "_text");
    if (datas && datas.length > 0) {
        var texts = [];
        var ids = value.split(",");
        for (var i = 0; i < ids.length; i++) {
            for (var j = 0; j < datas.length; j++) {
                if (datas[j][0] == ids[i]) {
                    texts[texts.length] = datas[j][1];
                }
            }
        }
        return texts.join(",");
    }
    getCodeTextFn(record, field, value, "value", data_key);
    return "";
}

//下拉树本地加载获取文本值
function getMultiTreeByIdsToText(record, field, value, datas, data_key) {
    if (!value || value == "") return "";
    if (record.get(field + "_text"))
        return record.get(field + "_text");
    if (datas && datas.length > 0) {
        var texts = [];
        var ids = value.split(",");
        if (ids.length > 0)
            getTreeText(ids, datas, texts)
        return texts.join(",");
    }
    getCodeTextFn(record, field, value, "id", data_key);
    return "";
}

//获取树形目录本地加载时的文本值
function getTreeText(ids, datas, texts) {
    for (var j = 0; j < datas.length; j++) {
        var record = datas[j];
        for (var i = 0; i < ids.length; i++) {
            if (record.id == ids[i]) {
                texts[texts.length] = record.text;
                ids.splice(i, 1);
                break;
            }
        }
        if (ids.length > 0 && record.children)
            getTreeText(ids, record.children, texts);
    }
}

//显示列表是系统数据源时，下拉选择本地加载获取文本值
function getSysDataTextByKey(record, field, value, data_key) {
    if (record.get(field + "_text"))
        return record.get(field + "_text");
    if (!record.get(field + "_load")) {
        setTimeout(function (record, field, value, data_key) {
            Ext.Ajax.request({
                method: "POST", url: "system/ds/text",
                params: {data_key: data_key, ids: value},
                success: function (response, options) {
                    var resp = Ext.JSON.decode(response.responseText);
                    record.set(field + "_load", "true");
                    if (resp.success) {
                        if (value != "" && resp.texts == "")
                            record.set(field + "_text", value);
                        else
                            record.set(field + "_text", resp.texts);
                        record.commit();
                    }
                },
                failure: function (response, options) {
                    record.set(field + "_load", "true");
                }
            });
        }, 20, record, field, value, data_key);
    }
    return "";
}

function getRecordValue(record, field) {
    var value = record.get(field);
    if (typeof value == "undefined") value = record.raw[field];
    return value;
}

//form表单初始化数据(新增加载默认数据，修改加载修改数据)
function designFormOriginalValue(form, record) {
    if (!record) return;
    form.loadRecord(record);
    var editors = form.query('[isFormField]');
    for (var i = 0; i < editors.length; i++) {
        var xtype = editors[i].xtype,
            field = editors[i].name,
            value = getRecordValue(record, field);
        if (xtype == "checkboxgroup") {
            setTimeout(function (index) {
                var field = editors[index].name, obj = {};
                var value = record.raw[field] + "";
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
                var value = record.raw[field];
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
                var value = record.raw[editors[index].name]; //record.get(editors[index].name);
                if (value && value != "") {
                    value += "";
                    //alert(editors[index].delimiter);
                    var attr_ids = value.split(",");
                    editors[index].initInfo = value;
                    editors[index].setValue(attr_ids);
                    editors[index].originalValue = attr_ids;
                }
            }, 10, i);
            setTimeout(function (index) {
                var value = record.raw[editors[index].name]; //record.get(editors[index].name);
                if (value && value != "") {
                    value += "";
                    var attr_ids = value.split(",");
                    editors[index].initInfo = value;
                    editors[index].setValue(attr_ids);
                    editors[index].originalValue = attr_ids;
                }
            }, 850, i);
        } else if (xtype == "singlecombobox") { //单选下拉框、下拉树
            setTimeout(function (index) {
                var value = record.raw[editors[index].name]; //record.get(editors[index].name);
                if (value && value != "") {
                    editors[index].setValue(value);
                    editors[index].originalValue = value;
                }
            }, 10, i);
        } else if (xtype == "numberfield") {  //防止空值，填写后变成0
            if (value == 0) value = record.raw[field];
            editors[i].setValue(value);
            editors[i].originalValue = value;
        } else if (xtype == "datefield" || xtype == "datetimefield") {
            if (editors[i].format) {
                value = Ext.util.Format.date(value, editors[i].format);
                editors[i].setValue(value);
                editors[i].originalValue = value;
            }
        } else if (xtype == "timefield") { //时间框，发现时间框的originalValue值赋予后，不能选择了
            if (value && value != "") {
                editors[i].originalValue2 = value;
            }
        } else {
            if (value || value == 0) {
                editors[i].setValue(value);
                editors[i].originalValue = value;
            }
        }
    }
}

//判断form是否被修改过
function designFormisDirty(form) {
    var editors = form.query('[isFormField]');
    for (var i = 0; i < editors.length; i++) {
        var field = editors[i], xtype = editors[i].xtype;
        if (xtype == "filefield") continue;
        if (!field.isDirty()) continue;

        var originalValue = field.originalValue;
        var Value = field.getValue();
        if (xtype == "datefield" || xtype == "datetimefield") {
            if (field.format) {
                originalValue = Ext.util.Format.date(originalValue, field.format);
                Value = Ext.util.Format.date(Value, field.format);
                //originalValue = Value;
            }
        }
        else if (xtype == "timefield") {
            if (field.format) {
                originalValue = field.originalValue2;
                Value = Ext.util.Format.date(Value, field.format);
            }
        }
        else if (xtype == "htmleditor" || xtype == "ueditor" || xtype == "kindeditor") {
            originalValue = originalValue.replace(/<[^>]+>/g, "");
            Value = Value.replace(/<[^>]+>/g, ""); //去掉所有的html标记
        }
        if (originalValue != Value) {
            //field.focus(true, 500);
            return field;  //这个字段是脏数据，经过修改了
        }
    }
    return null;
}

//打开导出Excel字段设置窗口
function openExcelFieldSetupWin(itemid, tableKey) {
    Ext.define('model_excelfield_setup', {
        extend: 'Ext.data.Model',
        idProperty: 'fid',
        fields: [
            {name: 'fid', type: 'int'},
            {name: 'table_explain', type: 'string'},
            {name: 'field_explain', type: 'string'},
            {name: 'xtype_name', type: 'string'},
            {name: 'isexport', type: 'bool'},
            {name: 'serialcode', type: 'int'}
        ]
    });
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "design/excel/field/grid",
            extraParams: {
                isexport: $.cookie("excel_isexport"),
                show_table: $.cookie("excel_show_table"),
                table_key: tableKey, item_id: itemid
            },
            reader: {
                type: 'json', root: 'root',
                idProperty: 'fid',
                totalProperty: 'totalProperty'
            }
        },
        model: 'model_excelfield_setup'
    });

    var columns = [
        new Ext.grid.RowNumberer({width: 40, tdCls: "blue"}),
        {
            xtype: 'checkcolumn', text: '导出字段', width: 75,
            dataIndex: 'isexport', fixed: true, sortable: false
        }, {
            text: '字段名称', width: 100, dataIndex: 'field_explain', sortable: false,
            renderer: function (value, mata, record) {
                if ($.cookie("excel_show_table") == "1")
                    return Ext.String.format("[{1}]{0}", value, record.get("table_explain"));
                else
                    return value;
            }
        }, {
            text: '界面输入框', width: 140, fixed: true, dataIndex: 'xtype_name', sortable: false,
            renderer: function (val, meta, rec) {
                if (val != "") return val;
                else return "未定义";
            }
        }
    ];
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等...",
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            }
        },
        border: false,
        columns: columns
    });
    var excelFieldSetupWin = Ext.create('Ext.window.Window', {
        title: "导出字段及排序设置",
        width: 480, height: 350,
        resizable: true,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: true, layout: 'fit',
        items: [grid],
        dockedItems: [{
            xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
            items: [{
                itemId: "excel_isexport",
                xtype: 'checkbox',
                checked: $.cookie("excel_isexport") == "1",
                labelWidth: 5, fieldLabel: ' ',
                boxLabel: '只包含导出字段',
                labelSeparator: "", labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        $.cookie("excel_isexport", checked ? "1" : "2");
                        grid.store.proxy.extraParams.isexport = $.cookie("excel_isexport");
                        grid.store.sorters.clear();
                        grid.store.load();
                    }
                }
            }, {
                itemId: "excel_show_table",
                xtype: 'checkbox',
                checked: $.cookie("excel_show_table") == "1",
                labelWidth: 5, fieldLabel: ' ',
                boxLabel: '显示表名',
                labelSeparator: "", labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        $.cookie("excel_show_table", checked ? "1" : "2");
                        grid.store.proxy.extraParams.show_table = $.cookie("excel_show_table");
                        grid.store.load();
                    }
                }
            }, "->", {
                text: "保存",
                minWidth: 60,
                handler: function () {
                    var save_vals = [], first_index = 1, isexport = 0;
                    if (store.getCount() == store.getTotalCount()) {
                        store.each(function (record, i) {
                            if (record.get("isexport")) isexport++;
                            save_vals[save_vals.length] = record.get("fid") + ":" + (first_index++) + ":" + (record.get("isexport") ? "1" : "0");
                        });
                    }
                    else {
                        var attr = [];
                        store.each(function (record, i) {
                            attr[attr.length] = record.get("serialcode");
                        });
                        var attr_sort = attr.sort();
                        store.each(function (record, i) {
                            if (record.get("isexport")) isexport++;
                            save_vals[save_vals.length] = record.get("fid") + ":" + attr_sort[i] + ":" + (record.get("isexport") ? "1" : "0");
                        });
                    }
                    if (isexport > 0) {
                        Ext.Ajax.request({
                            method: "POST", url: "design/excel/field/save",
                            params: {
                                table_key: tableKey, item_id: itemid,
                                saveVal: save_vals.join(";")
                            },
                            success: function (response, options) {
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    excelFieldSetupWin.close();
                                    Ext.alert.msg('提示', "保存成功!");
                                }
                                else {
                                    Ext.alert.msg('提示', "保存失败!");
                                }
                            },
                            failure: function (response, options) {
                                Ext.alert.msg('提示', "保存失败!");
                            }
                        });
                    } else {
                        Ext.alert.msg('提示', "至少选择一个字段!");
                    }
                }
            }, {
                text: "清空",
                minWidth: 60,
                handler: function () {
                    Ext.Ajax.request({
                        method: "POST", url: "design/excel/field/clear",
                        params: {
                            table_key: tableKey, item_id: itemid
                        },
                        success: function (response, options) {
                            var resp = Ext.JSON.decode(response.responseText);
                            grid.store.proxy.extraParams.isexport = $.cookie("excel_isexport");
                            grid.store.proxy.extraParams.show_table = $.cookie("excel_show_table");
                            grid.store.sorters.clear();
                            grid.store.load();
                        },
                        failure: function (response, options) {
                            grid.store.load();
                        }
                    });
                }
            }, {
                text: "关闭",
                minWidth: 60,
                handler: function () {
                    excelFieldSetupWin.close();
                }
            }]
        }]
    });
    excelFieldSetupWin.show();
}

function setSearchKey(grid, table_name, key, text) {
    var sb_btn = grid.queryById("btn_search");
    if (sb_btn) {
        sb_btn.setText(text);
        grid.store.proxy.extraParams.key_field = key;

        var text_search = grid.queryById('text_search');
        if (text_search) {
            if (text == "全部") text_search.setValue("");
            designSearchByField(grid.store, table_name, key, text_search.getValue());
        }
    }
}

//列表高级查询根据关键字查询信息记录(通用)
function designSearchByForm(form, items) {
    var datas = [];
    Ext.Array.each(items, function (item, index) {
        var item_id = item;
        if (typeof (item) == 'object') item_id = item.item_id;
        var operator = item.operator ? item.operator : "=";
        var valType = item.valType ? item.valType : "string";
        if (operator == "between") {
            var field = form.queryById(item.start);
            var value = getEditorFieldValue(field);
            var value2 = getEditorFieldValue(form.queryById(item.end));
            if (value != "" || value2 != "") {
                var data = {};
                data.field = item.field;
                if (!data.field) data.field = item.start.replace("_s", "");
                if (!data.field) data.field = item.end.replace("_e", "");
                data.operator = operator;
                data.value = value;
                data.value2 = value2;
                data.fieldType = field.xtype;
                data.valType = valType;
                datas[datas.length] = data;
            }
        } else {
            var data = getSearchDataByItemId(form, item_id, operator, valType);
            if (data) {
                if (item.field) data.field = item.field;
                datas[datas.length] = data;
            }
        }
    });
    return datas;
}

//列表高级查询根据关键字查询信息记录(通用)
function designSearchByField(store, tableKey, field, value) {
    if (!field) return;
    var searchs = {};
    searchs.tableKey = tableKey;
    searchs.datas = [];
    var attrs = field.split("、");
    for (var i = 0; i < attrs.length; i++) {
        if (value) {
            searchs.datas[i] = {};
            searchs.datas[i].field = attrs[i];
            searchs.datas[i].operator = "like";
            searchs.datas[i].value = value.replace(/%/g, '/%').replace(/_/g, '/_');
            searchs.datas[i].fieldType = "textfield";
            searchs.datas[i].valType = "string";
        }
    }
    store.proxy.extraParams.searchdata = Ext.JSON.encode([searchs]);
    if (attrs.length > 1)
        store.proxy.extraParams.linkType = "or";
    else
        store.proxy.extraParams.linkType = "and";
    store.load();
}

//列表高级查询最近天数的信息记录(通用)
function gridSearchByDate(store, tableKey, late_days) {
    var searchs = {};
    searchs.tableKey = tableKey;
    searchs.datas = [];
    searchs.datas[0] = {};
    searchs.datas[0].field = "create_time";
    searchs.datas[0].operator = "late";
    searchs.datas[0].value = late_days;
    searchs.datas[0].fieldType = "datefield";
    searchs.datas[0].valType = "string";
    store.proxy.extraParams.searchdata = Ext.JSON.encode([searchs]);
    store.proxy.extraParams.linkType = "and";
    store.load();
}

function getSearchByVal(field, operator, value, fieldType, valType) {
    var data = {};
    data.field = field;
    data.operator = operator;
    data.value = value;
    data.fieldType = fieldType;
    data.valType = valType;
    return data;
}

function getSearchDataByItemId(form, item_id, operator, valType) {
    var field = form.queryById(item_id);
    var value = getEditorFieldValue(field);
    if (value) {
        var data = {};
        data.field = field.name;
        data.operator = operator;
        data.value = value;
        data.fieldType = field.xtype;
        data.valType = valType;
        return data;
    }
    return "";
}

//高级查询重新加载(单表)
function searchSingleTableReLoad(grid, datas, table_key, alias) {
    if (datas && datas.length > 0) {
        var searchs = [];
        searchs[searchs.length] = {tableKey: table_key, datas: datas, alias: alias};
        grid.store.proxy.extraParams.searchdata = Ext.JSON.encode(searchs);
        grid.store.proxy.extraParams.linkType = "and";
    } else {
        grid.store.proxy.extraParams.searchdata = "";
        grid.store.proxy.extraParams.linkType = "";
    }
    grid.store.load();
}

//高级查询重新加载(多表)
function searchMultiTableReLoad(grid, searchs) {
    if (searchs && searchs.length > 0) {
        grid.store.proxy.extraParams.searchdata = Ext.JSON.encode(searchs);
        grid.store.proxy.extraParams.linkType = "and";
    } else {
        grid.store.proxy.extraParams.searchdata = "";
        grid.store.proxy.extraParams.linkType = "";
    }
    grid.store.load();
}

//设计表高级查询获取查询信息
function getDesignSearchDatas(grid) {
    var datas = designSearchDatas[designSearchWin.item_id];
    if (!datas) datas = [];
    grid.plugins[0].completeEdit();
    grid.store.each(function (record) {
        var value = record.get("fieldvalue");
        var value2 = record.get("fieldvalue2");
        var operator = record.get("operator");
        if (value != "" || value2 != "" || operator == "is_null" || operator == "is_not_null") {
            var index = getIndex(datas, record.get("fid"));
            datas[index] = {};
            datas[index].fid = record.get("fid");
            datas[index].operator = operator;
            datas[index].value = value;
            datas[index].value2 = value2;
            datas[index].fieldvalue_text = record.get("fieldvalue_text");
            datas[index].fieldvalue2_text = record.get("fieldvalue2_text");
            datas[index].operatorRemark = record.get("operator_desc");
        }
    });

    function getIndex(datas, fid) {
        for (var i = 0; i < datas.length; i++) {
            if (datas[i].fid == fid) return i;
        }
        return datas.length;
    }

    return datas
}

//设计表高级查询获取查询信息
function getDesignSearchs(grid) {
    var searchs = [], searchs_datas = {};
    grid.store.each(function (record) {
        var value = record.get("fieldvalue");
        var value2 = record.get("fieldvalue2");
        var operator = record.get("operator");
        if (value != "" || value2 != "" || operator == "is_null" || operator == "is_not_null") {
            var table_key = record.get("table_key");
            var datas = searchs_datas[table_key];
            if (!datas) datas = [];
            var data = {};
            data.field = record.get("field_name");
            data.operator = operator;
            data.value = value;
            data.value2 = value2;
            data.fieldType = record.get("field_type");
            data.valType = record.get("value_type");
            datas[datas.length] = data;
            searchs_datas[table_key] = datas;
        }
    });
    for (var table_key in searchs_datas) {
        searchs[searchs.length] = {tableKey: table_key, datas: searchs_datas[table_key]};
    }
    return searchs;
}

var designSearchWin, designOperatorDatas, designSearchDatas;

function openHeightSearchWin(record, list_grid, itemid, tableKey) {
    if (!designSearchWin) {
        designSearchDatas = {}; //用户查询数据本地存储(按对象存储，每个对象又有多个表)
        var grid = createHeightSearchGrid(record, itemid, tableKey);
        designSearchWin = Ext.create('widget.window', {
            title: '高级查询',
            width: 720, height: 432,
            maximizable: true, closable: true,
            resizable: true, closeAction: 'hide',
            layout: "fit", modal: 'true',  //弹出模态窗体 
            tools: [{
                type: 'help',
                tooltip: '操作符是区间时，查询值2才能输入值',
                handler: function (event, toolEl, panel) {
                    Ext.alert.msg('提示', "操作符是区间时，查询值2才能输入值")
                }
            }],
            items: [grid],
            buttonAlign: "right",  //'right', 'left' 和 'center'(对于所有的buttons/fbar默认为'right'，对于toolbar 则默认为'left')
            buttons: [{
                itemId: "check_config",
                xtype: 'checkbox',
                checked: $.cookie("check_config") == "1",
                labelWidth: 5, fieldLabel: ' ',
                boxLabel: '只包含定制字段',
                labelSeparator: "", labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        grid.queryById('search_tabname').setDisabled(checked);
                        $.cookie("check_config", checked ? "1" : "2");
                        if (checked)
                            grid.store.proxy.extraParams.fids = $.cookie("sh_" + designSearchWin.item_id);
                        else
                            grid.store.proxy.extraParams.fids = "";
                        grid.store.reload();
                    }
                }
            }, {
                itemId: "show_table",
                xtype: 'checkbox',
                checked: $.cookie("show_table") == "1",
                labelWidth: 5, fieldLabel: ' ',
                boxLabel: '显示表名',
                labelSeparator: "", labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        $.cookie("show_table", checked ? "1" : "2");
                        grid.store.reload();
                    }
                }
            }, "->", {
                text: '查询',
                handler: function () {
                    grid.plugins[0].completeEdit();
                    designSearchDatas[designSearchWin.item_id] = getDesignSearchDatas(grid);
                    var search_grid = Ext.getCmp(designSearchWin.search_grid_id);
                    if (search_grid) {
                        var searchs = getDesignSearchs(grid);
                        var linkType = grid.queryById('linkType');
                        search_grid.store.proxy.extraParams.searchdata = Ext.JSON.encode(searchs);
                        search_grid.store.proxy.extraParams.linkType = linkType.getValue().linkType;
                        search_grid.store.load();
                    }
                    designSearchWin.close();
                }
            }, {
                text: '清空',
                handler: function () {
                    designSearchDatas[designSearchWin.item_id] = [];
                    grid.plugins[0].completeEdit();
                    grid.store.load();
                }
            }, {
                text: '关闭',
                handler: function () {
                    designSearchWin.close();
                }
            }, "  "],
            listeners: {
                'show': function (window, eOpts) {
                    if (!designOperatorDatas) {
                        Ext.Ajax.request({
                            method: "POST", url: "design/search/operator",
                            params: {},
                            success: function (response, options) {
                                var resp = Ext.JSON.decode(response.responseText);
                                if (resp.success) {
                                    designOperatorDatas = Ext.JSON.decode(resp.data); //alert(resp.data["checkbox_"]);
                                }
                            }
                        });
                    }
                    var item_id = designSearchWin.item_id;
                    if (grid.store.item_id != item_id) {
                        grid.store.item_id = item_id;
                        var table_key = designSearchWin.table_key;
                        var search_combo = grid.queryById('search_tabname');
                        if (search_combo) {
                            search_combo.clearValue();
                            search_combo.store.proxy.extraParams.table_key = table_key;
                            search_combo.store.load();
                        }
                        grid.store.proxy.extraParams.item_id = item_id;
                        grid.store.proxy.extraParams.tid = 0;  //默认查询主表
                        grid.store.proxy.extraParams.table_key = table_key;
                        if ($.cookie("check_config") == "1")  //只包含定制字段
                            grid.store.proxy.extraParams.fids = $.cookie("sh_" + item_id);
                        else
                            grid.store.proxy.extraParams.fids = "";
                        grid.store.load();
                    }
                }
            }
        });
    }
    designSearchWin.table_key = tableKey;
    designSearchWin.item_id = itemid + "";
    designSearchWin.search_grid_id = list_grid.getId();
    designSearchWin.show();
}

//创建高级查询列表
function createHeightSearchGrid(record, itemid, tableKey) {
    Ext.define('model_design_search', {
        extend: 'Ext.data.Model',
        idProperty: 'fid',
        fields: [
            {name: 'tid', type: 'int'},
            {name: 'fid', type: 'int'},
            {name: 'tab_name', type: 'string'},
            {name: 'table_key', type: 'string'},
            {name: 'field_name', type: 'string'},
            {name: 'field_explain', type: 'string'},
            {name: 'field_type', type: 'string'},
            {name: 'value_type', type: 'string'},
            {name: 'operator', type: 'string'},
            {name: 'operator_desc', type: 'string'},
            {name: 'fieldvalue', type: 'string'},
            {name: 'fieldvalue_text', type: 'string'},
            {name: 'fieldvalue2', type: 'string'},
            {name: 'fieldvalue2_text', type: 'string'},
            {name: 'editor', type: 'string'}
        ]
    });

    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: false, //是否自动加载
        proxy: {
            type: 'ajax', url: "design/search/grid",
            extraParams: {table_key: tableKey, tid: 0},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'fid',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                var item_id = designSearchWin.item_id;
                var search_datas = designSearchDatas[item_id];
                if (search_datas && search_datas.length > 0) {
                    Ext.Array.each(search_datas, function (data, i) {
                        var record = store.getById(data.fid);
                        if (record) {
                            record.set("operator", search_datas[i].operator);
                            record.set("fieldvalue", search_datas[i].value);
                            record.set("fieldvalue2", search_datas[i].value2);
                            record.set("fieldvalue_text", search_datas[i].fieldvalue_text);
                            record.set("fieldvalue2_text", search_datas[i].fieldvalue2_text);
                            record.set("operator_desc", search_datas[i].operatorRemark);
                            record.commit();
                        }
                    });
                }
            }
        },
        model: 'model_design_search'
    });

    var operator_data = [{'id': "=", 'name': '等于'}];
    var store_operator = Ext.create('Ext.data.Store', {
        autoDestroy: true,
        fields: ['id', 'name'],
        data: operator_data
    });

    var columns = [
        new Ext.grid.RowNumberer({width: 40, tdCls: "blue"}), {
            text: '查询字段',
            width: 180,
            dataIndex: 'field_explain',
            align: 'right',
            groupable: false,
            fixed: true,
            hideable: false,
            sortable: false,
            locked: false,
            renderer: function (value, mata, record) {
                if ($.cookie("show_table") == "1")
                    return Ext.String.format("{0}[{1}]", value, record.get("tab_name"));
                else
                    return value;
            }
        },
        {
            text: '操作符',
            width: 80,
            sortable: false,
            align: 'center',
            groupable: false,
            fixed: true,
            hideable: false,
            dataIndex: 'operator',
            editor: {
                valueField: 'id', displayField: 'name',
                xtype: 'combobox', editable: false,
                store: store_operator,
                queryMode: 'local',
                listeners: {
                    change: function (combo, newValue, oldValue) {
                        setTimeout(function () {
                            if (combo.store.rowIdx) {
                                grid.editingPlugin.startEdit(combo.store.rowIdx, 3);
                            }
                            combo.store.rowIdx = null;
                        }, 10);
                    }
                }
            },
            renderer: function (value, mata, record) {
                return record.get("operator_desc");
            }
        },
        {
            text: '查询值1',
            width: 100,
            sortable: false,
            groupable: false,
            fixed: false,
            hideable: false,
            dataIndex: 'fieldvalue',
            editor: {
                xtype: 'textfield', maxLength: 50
            },
            renderer: function (value, mata, record) {
                var val = record.get("operator");
                if (val == "is_null")
                    return "<b style='color:red'>为空</b>";
                else if (val == "is_not_null")
                    return "<b style='color:red'>不为空</b>";
                return record.get("fieldvalue_text");
            }
        },
        {
            text: '查询值2', width: 100, sortable: false, groupable: false, fixed: false, dataIndex: 'fieldvalue2',
            editor: {
                xtype: 'textfield', maxLength: 50
            },
            renderer: function (value, mata, record) {
                return record.get("fieldvalue2_text");
            }
        },
        {text: 'fid', width: 20, dataIndex: 'fid', hideable: false, hidden: true}
    ];
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        disableSelection: true, //设置为true，则禁用选择模型
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        sortableColumns: false,
        //enableColumnHide: false,
        columnLines: true,
        viewConfig: {
            getRowClass: function () {
                // 在这里添加自定样式 改变这个表格的行高
                return 'custom-grid-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        border: false,
        plugins: [{
            ptype: 'cellediting',
            clicksToEdit: 1,
            listeners: {
                beforeedit: function (ed, e) {
                    var record = e.record, editor = e.column.getEditor();
                    var xtype = editor.xtype;

                    if (xtype == "combobox" || xtype == "singlecombobox") {
                        if (e.field == "operator") {  //动态改变操作符下拉框值
                            editor.store.rowIdx = null;
                            editor.reset();
                            editor.store.removeAll(false);
                            var name = record.get("field_type") + "_";
                            if (name == "panelpicker_" || name == "trigger_")
                                name += record.get("value_type");
                            if (designOperatorDatas && designOperatorDatas[name]) {
                                editor.store.insert(0, eval(designOperatorDatas[name]));
                            }
                            else {
                                editor.store.insert(0, operator_data);
                            }
                        }
                        setTimeout(function () {
                            try {
                                editor.expand();
                                if (e.field == "operator") editor.store.rowIdx = e.rowIdx;
                            } catch (e) {
                            }
                        }, 10);
                    }
                    if (e.field == "fieldvalue") {  //查询值1编辑之前，判断是不是为空或不为空的操作符
                        var val = record.get("operator");
                        if (val == "is_null" || val == "is_not_null") return false;
                        if (record.get("editor")) {  //动态改变查询值1编辑框
                            try {
                                editor = eval('(' + record.get("editor") + ')');
                            } catch (e) {
                            }
                        } else {
                            editor = {xtype: 'textfield', maxLength: 50};
                        }
                        grid.headerCt.getGridColumns()[e.colIdx].setEditor(editor);
                        xtype = editor.xtype;
                    } else if (e.field == "fieldvalue2") {  //查询值2编辑之前，判断是不是区间操作符
                        if (record.get("operator") != "between") return false;
                        if (record.get("editor")) {  //动态改变查询值2编辑框
                            try {
                                editor = eval('(' + record.get("editor") + ')');
                            } catch (e) {
                            }
                        } else {
                            editor = {xtype: 'textfield', maxLength: 50};
                        }
                        grid.headerCt.getGridColumns()[e.colIdx].setEditor(editor);
                        xtype = editor.xtype;
                    }

                    if (xtype == "checkboxgroup" || xtype == "radiogroup") {
                        setTimeout(function () {
                            var field = e.column.getEditor().name, obj = {};
                            obj[field] = record.get(e.field).split(",");
                            e.column.getEditor().setValue(obj);
                            var boxs = e.column.getEditor().query('>[isFormField]');
                            for (var i = 0; i < boxs.length; i++) {
                                boxs[i].originalValue = boxs[i].getValue(); //不然有脏数据
                            }
                        }, 10);
                    } else if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") {
                        setTimeout(function () {
                            e.column.getEditor().expand();
                        }, 10);
                    } else if (xtype == "combobox" || xtype == "singlecombobox" || xtype == "multicombobox" || xtype == "treepicker" || xtype == "definepicker") { //下拉框，多选下拉框，下拉树
                        setTimeout(function () {
                            if (xtype == "treepicker") e.column.getEditor().setValue(e.value);
                            if (e.column.getEditor().expand) e.column.getEditor().expand();
                        }, 10);
                    }
                    if (xtype == "multicombobox") { //多选下拉框
                        var value = e.value;
                        if (value && value != "") {
                            value += "";
                            var attr_ids = value.split(",");
                            setTimeout(function () {
                                e.column.getEditor().setValue(attr_ids);
                            }, 10);
                        }
                    }
                    return true;
                },
                edit: function (ed, e) {
                    var record = e.record, editor = e.column.getEditor();
                    var xtype = editor.xtype;
                    var format = editor.format;
                    if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") {
                        record.set(e.field, Ext.util.Format.date(e.value, format));
                        record.set(e.field + "_text", Ext.util.Format.date(e.value, format));
                    } else if (xtype == "combobox" || xtype == "singlecombobox" || xtype == "multicombobox" || xtype == "treepicker") {
                        if (e.field == "operator") {
                            var val = editor.getValue();
                            if (val == "is_null" || val == "is_not_null") {
                                record.set("fieldvalue", "");
                                record.set("fieldvalue_text", "");
                            }
                            record.set("operator_desc", editor.getRawValue());
                        } else {
                            record.set(e.field + "_text", editor.getRawValue());
                        }
                    } else if (xtype == "my97date") {
                        setTimeout(function () {
                            record.set(e.field, editor.getValue());
                            record.set(e.field + "_text", editor.getValue());
                        }, 200);
                    } else if (xtype == "checkboxgroup" || xtype == "radiogroup") {
                        var boxs = editor.getChecked(), ids = [], texts = [];
                        for (var i = 0; i < boxs.length; i++) {
                            ids[ids.length] = boxs[i].inputValue;
                            texts[texts.length] = boxs[i].boxLabel;
                        }
                        record.set(e.field, ids.join(","));
                        record.set(e.field + "_text", texts.join(","));
                    } else {
                        record.set(e.field + "_text", e.value);
                    }
                    record.commit();
                }
            }
        }],
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: "search_tabname",
                disabled: $.cookie("check_config") == "1",
                xtype: 'combobox', editable: false,
                valueField: 'id', displayField: 'name',
                emptyText: "--请选择所属表单--",
                store: {
                    autoLoad: false, // 必须自动加载, 否则在编辑的时候load
                    proxy: {
                        type: 'ajax', url: "design/search/table/combo",
                        extraParams: {table_key: tableKey},
                        reader: {
                            type: 'json', root: 'root',
                            totalProperty: 'totalProperty'
                        }
                    },
                    listeners: {
                        'beforeload': function (store, records, successful, eOpts) {
                            var btn_search_config = grid.queryById('search_config');
                            if (btn_search_config) btn_search_config.setDisabled(true);
                        },
                        'load': function (store, records, successful, eOpts) {
                            var btn_search_config = grid.queryById('search_config');
                            if (btn_search_config) btn_search_config.setDisabled(false);
                        }
                    },
                    fields: ['id', 'name']
                },
                width: 150,
                listeners: {
                    'select': function (comboBox, eOpts) {
                        grid.store.proxy.extraParams.tid = comboBox.getValue();
                        grid.store.load();
                    }
                },
                queryMode: 'remote'
            }, '-', {
                itemId: 'search_config',
                xtype: 'button', text: '查询定制',
                minWidth: 60, iconCls: 'icon_setup', //pressed: true,
                handler: function (btn, pressed) {
                    openHeightSearchConfigField(grid, store.proxy.extraParams.table_key);
                }
            }, '->', {
                itemId: "linkType",
                xtype: 'radiogroup',
                columns: [60, 60],
                items: [
                    {boxLabel: '并且', name: 'linkType', inputValue: 'and', checked: true},
                    {boxLabel: '或者', name: 'linkType', inputValue: 'or'}]
            }, ' ']
        }]
    });
    return grid;
}

//打开查询字段定制窗口
function openHeightSearchConfigField(grid, tableKey) {
    var search_tabname = grid.queryById('search_tabname');
    var btn_search_config = grid.queryById('search_config');
    Ext.define('customsearchconfig', {
        extend: 'Ext.data.Model',
        idProperty: 'fid',
        fields: [
            {name: 'fid', type: 'string'},
            {name: 'search_field', type: 'bool'},
            {name: 'tab_name', type: 'string'},
            {name: 'field_name', type: 'string'},
            {name: 'field_explain', type: 'string'},
            {name: 'field_type', type: 'string'},
            {name: 'value_type', type: 'string'}
        ]
    });
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "design/search/config",
            extraParams: {table_key: tableKey, tid: 0},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'fid',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records, successful, eOpts) {
                var ids = $.cookie("sh_" + designSearchWin.item_id);
                if (ids) {
                    var attr = ids.split(",");
                    Ext.Array.each(attr, function (fid) { //对查询的结果进行遍历
                        var record = store.getById(fid);
                        if (record) {
                            record.set("search_field", 1);
                            record.commit();
                        }
                    });
                }
            }
        },
        model: 'customsearchconfig'
    });

    var columns = [
        new Ext.grid.RowNumberer({width: 40, tdCls: "blue"}),
        {
            xtype: 'checkcolumn',
            header: '常用查询', dataIndex: 'search_field',
            width: 80, fixed: true, sortable: false,
            listeners: {
                'checkchange': function (field, recordIndex, checked, record) {
                }
            }
        },
        {text: '查询字段', width: 100, dataIndex: 'field_explain', align: 'left', hideable: false},
        {text: '表单名称', width: 160, fixed: true, dataIndex: 'tab_name', align: 'left', hideable: false},
        {text: 'fid', width: 20, dataIndex: 'fid', hideable: false, hidden: true}
    ];
    var main_grid = Ext.create('Ext.grid.Panel', {
        store: store,
        disableSelection: true, //设置为true，则禁用选择模型
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        sortableColumns: false,
        columnLines: true,
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        border: false,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', {
                itemId: "search_tabname",
                xtype: 'combobox', editable: false,
                valueField: 'id', displayField: 'name',
                emptyText: "--请选择所属表单--",
                store: search_tabname.store,
                width: 150,
                listeners: {
                    'select': function (comboBox, eOpts) {
                        main_grid.store.proxy.extraParams.tid = comboBox.getValue();
                        main_grid.store.load();
                    }
                },
                queryMode: 'local'
            }, {
                text: '查全部',
                handler: function () {
                    main_grid.queryById('search_tabname').clearValue();
                    main_grid.store.proxy.extraParams.tid = "";
                    main_grid.store.load();
                }
            }, '-', {
                text: '确定', minWidth: 60,
                handler: function () {
                    var ids = [];
                    main_grid.store.each(function (record) {
                        if (record.get("search_field")) ids[ids.length] = record.get("fid");
                    });
                    $.cookie("sh_" + designSearchWin.item_id, ids.join(","));
                    win.close();
                }
            }, '->', {
                text: '清空配置',
                handler: function () {
                    main_grid.store.each(function (record) {
                        record.set("search_field", 0);
                        //record.commit();
                    });
                }
            }]
        }],
        columns: columns
    });
    var win = Ext.create('widget.window', {
        title: '查询字段定制',
        animateTarget: btn_search_config.getId(),
        width: 500, height: 360,
        maximizable: false,
        closable: true, autoShow: true,
        closeAction: 'destroy',
        plain: false, modal: true, layout: 'fit',
        items: [main_grid],
        listeners: {
            beforerender: function (window, eOpts) {
                var position = btn_search_config.getPosition();
                window.setPosition(position[0], position[1] + 24, true);
            },
            close: function (window, eOpts) {
                if (designSearchWin.queryById('check_config').getValue()) {
                    grid.store.proxy.extraParams.fids = $.cookie("sh_" + designSearchWin.item_id);
                    grid.store.load();
                }
            }
        }
    });
}

