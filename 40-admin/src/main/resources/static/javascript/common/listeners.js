/*
* @Author: zhangxiaxin
* @Date: 2019-10-16
* @Mark: Ext单元格编辑与行编辑各种监听
*/

//cellediting编辑前，各编辑框处理，返回true编辑，false不编辑
function cellediting_beforeedit(grid, me, e) {
    var xtype = e.column.getEditor().xtype, field = e.field;
    var editor_fields = grid.editor_fields;
    //流程申请子表单设置只读
    if (editor_fields && editor_fields.indexOf(field) == -1) return false;

    var record = e.record;
    if (e.field == "config_value") {
        if (record.get("editor")) {
            try {
                editor = eval('(' + record.get("editor") + ')');
            } catch (e) {
                return false;
            }
        } else if (record.get("config_editor")) {
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

    if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") {
        setTimeout(function () {
            e.column.getEditor().expand();
        }, 10);
    } else if (xtype == "combobox" || xtype == "singlecombobox" || xtype == "multicombobox"
        || xtype == "treepicker" || xtype == "gridpicker") { //下拉框，多选下拉框，下拉树
        setTimeout(function () {
            e.column.getEditor().expand();
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
}

//cellediting编辑，值是否改变
function ischange_cellediting_edit(grid, me, e) {
    var record = e.record;
    var ischange = e.originalValue + "" != e.value + "";
    var xtype = e.column.getEditor().xtype;
    var format = e.column.getEditor().format;
    if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") {
        ischange = Ext.util.Format.date(e.originalValue, format) != Ext.util.Format.date(e.value, format);
        record.set(e.field, Ext.util.Format.date(e.value, format));
        if (!ischange) record.commit();  //没改变提交下，防止出现红三角
    } else if (xtype == "combobox" || xtype == "singlecombobox" || xtype == "multicombobox" || xtype == "treepicker") { //下拉框，多选下拉框，下拉树
        if (e.column.renderer) {
            record.set(e.field + "_text", e.column.getEditor().getRawValue());
            record.commit();
        }
    } else if (xtype == "my97date") {
        setTimeout(function () {
            record.set(e.field, e.column.getEditor().getValue());
            record.commit();
        }, 200);
    }
    return ischange;
}

//cellediting编辑，特殊按键
function cellediting_onSpecialKey(ed, field, e) {
    if (e.getKey() === e.TAB || e.getKey() === e.ENTER || e.keyCode == 37
        || e.keyCode == 39 || e.keyCode == 38 || e.keyCode == 40) {
        e.stopEvent();
        if (ed) {
            ed.onEditorTab(e);
        }
        var sm = ed.up('tablepanel').getSelectionModel();
        if (sm.onEditorTab) {
            return sm.onEditorTab(ed.editingPlugin, e);
        }
    }
}

//cellediting编辑时按键监听
function cellediting_onEditorTab(editingPlugin, me, e, isUpDown) {
    var view = me.views[0],
        record = editingPlugin.getActiveRecord(),
        header = editingPlugin.getActiveColumn(),
        position = view.getPosition(record, header),
        direction = e.shiftKey ? 'left' : 'right';

    // keyCode: 37(左), 38(上), 39(右), 40(下)
    if (e.keyCode == 37) {
        direction = 'left';
    } else if (e.keyCode == 38) {
        direction = 'up';
    } else if (e.keyCode == 40) {
        direction = 'down';
    } else if (e.keyCode == 13 && isUpDown) {
        direction = 'down';
    }
    var rowIdx = position.row;
    var colIdx = position.column;
    do {
        position = view.walkCells(position, direction, e, me.preventWrap);
    } while (position && (!position.columnHeader.getEditor(record) || !editingPlugin.startEditByPosition(position)));

    if (e.keyCode == 13 && isUpDown) {
        if (position.row) {
            rowIdx = position.row;
            colIdx = position.column;
        }
        setTimeout(function () {
            editingPlugin.startEdit(rowIdx, colIdx);
        }, 10);
    }
}


//rowediting编辑前，返回true编辑，false不编辑
function rowediting_beforeedit(grid, me, e) {
    var editor = me.editor, record = e.record;
    var editors = editor.query('>[isFormField]');
    var editor_fields = grid.editor_fields;
    for (var i = 0; i < editors.length; i++) {
        var xtype = editors[i].xtype, field = editors[i].name;
        if (editor_fields && editor_fields.indexOf(field) == -1) {
            editors[i].setReadOnly(true); //流程申请子表单设置只读
            if (typeof editors[i].setFieldStyle === "function")
                editors[i].setFieldStyle("background-color: #FFFCFD;");  //FFFFFC
        }
        if (xtype == "checkboxgroup") {
            setTimeout(function (index) {
                var field = editors[index].name, obj = {};
                obj[field] = record.get(field).split(",");
                editors[index].setValue(obj);

                var boxs = editors[index].query('>[isFormField]');
                for (var i = 0; i < boxs.length; i++) {
                    boxs[i].originalValue = boxs[i].getValue(); //不然有脏数据
                }
            }, 10, i);
        } else if (xtype == "radiogroup") {
            setTimeout(function (index) {
                var field = editors[index].name, obj = {};
                obj[field] = record.get(field);
                editors[index].setValue(obj);
                var boxs = editors[index].query('>[isFormField]');
                for (var i = 0; i < boxs.length; i++) {
                    boxs[i].originalValue = boxs[i].getValue(); //不然有脏数据
                }
            }, 10, i);
        } else if (xtype == "multicombobox") { //多选下拉框
            setTimeout(function (index) {
                var value = record.get(editors[index].name);
                if (value && value != "") {
                    value += "";
                    var attr_ids = value.split(",");
                    editors[index].setValue(attr_ids);
                }
            }, 10, i);
        }
    }
    return true;
}

//rowediting编辑时，各编辑框处理
function rowediting_edit_forms(grid, me, e) {
    var editor = me.editor, record = e.record, context = me.context;
    var editors = editor.query('>[isFormField]');
    for (var i = 0; i < editors.length; i++) {
        var xtype = editors[i].xtype;
        var format = editors[i].format;
        var field = editors[i].name;
        if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") {
            var ischange = Ext.util.Format.date(context.originalValues[field], format) != Ext.util.Format.date(context.newValues[field], format);
            record.set(field, Ext.util.Format.date(context.newValues[field], format));
            if (!ischange) record.commit();  //没改变提交下，防止出现红三角
        } else if (xtype == "combobox" || xtype == "singlecombobox" || xtype == "multicombobox" || xtype == "treepicker") {  //下拉框，多选下拉框，下拉树
            if (record.get(field + "_text")) {
                record.set(field + "_text", editors[i].getRawValue());
                record.commit();
            }
        } else if (xtype == "checkboxgroup") {
            record.set(field, "clear");
            record.set(field, context.newValues[field][field]);
            record.commit();
        } else if (xtype == "radiogroup") {
            record.set(field, "clear");
            record.set(field, editors[i].getValue()[field]);
            record.commit();
        } else if (xtype == "checkbox") {
            record.set(field, editors[i].getValue() ? 1 : 0);
            record.commit();
        }
    }
}

//rowediting保存时再次检验，返回true提交，false不提交
function rowediting_validateedit(me, columns) {
    var tip_msg = "";
    var editor = me.editor;
    var editors = editor.query('>[isFormField]');
    for (var i = 0; i < editors.length; i++) {
        if (!editors[i].isValid()) {
            tip_msg += getEditorFieldLabel(editors[i], columns);
            tip_msg += editors[i].getErrors().join("<br/>") + "<br/>";
        }
    }
    if (tip_msg) {
        Ext.alert.msg('错误提示', tip_msg);
        return false;
    }
    return true;
}