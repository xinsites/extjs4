/**
 * 插件下拉列表多选
 */
Ext.define('Ext.ux.MultiComboBox', {
    extend: 'Ext.form.ComboBox',
    alias: 'widget.multicombobox',
    xtype: 'multicombobox',
    disableCls: "disabled",  //不可用选项字段，字段值为不可用样式，必须先定义好，该值不存在或者为空表示可以选择
    isSelectAll: false,  //是否带选择全部
    forceSelection: true,   //所选择的值限制在一个列表中的值，false时，允许用户设置任意的文本字段。
    minChars: 0,            //自动查询的最小字符数
    delimiter: ",",
    validate: function () {
        var errs = [];
        var val = this.getRawValue();
        var id_val = this.getValue() + "";
        //        if (this.valueField == "id") val = this.getValue();
        if (this.store.data && this.store.data.items.length > 0) {
            if (!this.allowBlank && this.forceSelection) {
                if (this.xtype == "multicombobox") {
                    var ssRawValues = [];
                    if (val) {
                        if ((this.id === "projectList") || val.indexOf(this.delimiter) > 0) {
                            ssRawValues = val.split(this.delimiter);
                        }
                        else {
                            ssRawValues = val.split(this.delimiter);
                        }
                    }
                    if (this.valueField == "id" && id_val.split) ssRawValues = id_val.split(",");
                    for (var ii = 0; ii < ssRawValues.length; ii++) {
                        var selectedValue = ssRawValues[ii];
                        if (ssRawValues[ii].trim) {
                            selectedValue = ssRawValues[ii].trim();
                        } else if (trim) {
                            selectedValue = trim(selectedValue);
                        }
                        var rec = this.findRecord(this.valueField, selectedValue);
                        if (!rec) {
                            if (ssRawValues[ii]) {
                                errs.push("无效的选择 [" + ssRawValues[ii] + "]");
                            } else {
                                errs.push("该输入项为必输项");
                            }
                        }
                    }

                }
                else {
                    var rec = this.findRecord(this.displayField, val);
                    if (!rec)
                        errs.push("无效的选择");
                }
            }
        }
        if (errs && errs.length > 0) {
            var error = errs[0];
            this.markInvalid(error);
            return false;
        } else if (!this.allowBlank && !val) {
            this.markInvalid(this.getErrors());
            return false;
        }
        else {
            this.clearInvalid();
            return true;
        }
    },
    clearValue: function () {
        //var coboxhtml = this.getEl().getHTML();  
        try {
            var coboxhtml = this.getEl().dom;
            if (coboxhtml != null) {
                var checkboxs = this.picker.listEl.el.dom.children;
                if (checkboxs != null) {
                    for (var i = 0; i < checkboxs.length; i++) {
                        var checkbox = checkboxs[i];
                        checkbox.children[0].checked = false;
                    }
                }
            }
        } catch (e) {
            if (typeof (console) != "undefined" && console != null) {
                console.log(e.toString());
            }
        }
        this.setValue([]);
    },
    initComponent: function () {
        var valueField = this.valueField;
        this.multiSelect = true;
        var me = this;
        var thisid = this.id;
        var allOptId = thisid + "_allOpt";
        this.allOptId = allOptId;
        var disableCls = this.disableCls;
        var tpl = '<tpl for="."><div class="x-boundlist-item {' + disableCls + '}"><input type=checkbox {' + disableCls + '}><span>{' + this.displayField + '}</span></div></tpl>';
        if (this.isSelectAll) tpl = '<div class="mt-boundlist-item" onclick="clickAllOptionDiv(\'' + thisid + '\')"><input onclick="clickAllOptionInput(this)" type="checkbox" id="' + allOptId + '"><span>选择全部</span></div>' + tpl;
        this.listConfig = {
            tpl: tpl,
            //tpl: '<div class="mt-boundlist-item" onclick="clickAllOptionDiv(\'' + thisid + '\')"><input onclick="clickAllOptionInput(this)" type="checkbox" id="' + allOptId + '">选择全部</div><tpl for="."><div class="x-boundlist-item"><input type=checkbox>{' + this.displayField + '}</div></tpl>',
            onItemSelect: function (record) {
                var node = this.getNode(record);
                if (node) {
                    if (!me.isDisabled(record)) {  //可用
                        Ext.fly(node).addCls(this.selectedItemCls);
                        var checkboxs = node.getElementsByTagName("input");
                        if (checkboxs != null) {
                            var checkbox = checkboxs[0];
                            checkbox.checked = true;
                        }
                    }
                }
                var isAllSelected = true;
                var store = this.getStore();
                if (store != null && store.getTotalCount() > 0) {
                    for (var i = 0; i < store.getTotalCount(); i++) {
                        var recordTemp = store.getAt(i);
                        var itemTemp = this.getNode(recordTemp);
                        var isSelectedTemp = this.isSelected(itemTemp);
                        if (!isSelectedTemp && !recordTemp.get(disableCls)) {
                            isAllSelected = false;
                            break;
                        }
                    }
                } else {
                    isAllSelected = false;
                }

                if (isAllSelected) {
                    me.selectAllOpt();
                }
            },
            onItemDeselect: function (record) {
                var node = this.getNode(record);
                if (node) {
                    if (!me.isDisabled(record)) {  //可用
                        Ext.fly(node).removeCls(this.selectedItemCls);

                        var checkboxs = node.getElementsByTagName("input");
                        if (checkboxs != null) {
                            var checkbox = checkboxs[0];
                            checkbox.checked = false;
                            me.deselectAllOpt();
                        }
                    }
                }
            },
            listeners: {
                itemclick: function (view, record, item, index, e, eOpts) {
                    if (me.isDisabled(record)) return false; //不可用，选择无效
                    var isSelected = view.isSelected(item);
                    var checkboxs = item.getElementsByTagName("input");
                    if (checkboxs != null) {
                        var checkbox = checkboxs[0];
                        if (!isSelected) {
                            checkbox.checked = true;
                        } else {
                            checkbox.checked = false;
                        }
                    }
                },
                beforeselect: function (combo, record, index, e) {
                    if (me.isDisabled(record)) return false; //不可用，选择无效
                    return true;
                }
            }
        }

        me.initInfo = me.value;
        me.on({
            scope: me,
            'render': function (comboBox, eOpts) {
                if (me.queryMode == "remote") {  //远程数据
                    var attr_ids = [];
                    if (me.initInfo) attr_ids = me.initInfo.split(me.delimiter);
                    comboBox.store.load(function (records, operation, success) {
                        comboBox.setValue(attr_ids);
                        //comboBox.initDisabled(me);
                    });
                }
            }
        });
        this.callParent();
    },
    isDisabled: function (record) {
        var disableCls = this.disableCls;
        if (!disableCls) return false;  //没有设置不可用样式字段，不可用无效
        var disabled = record.raw.disabled;
        return (disabled == "disabled" || disabled == true || disabled == "1" || disabled == "True"); //不可选
    },
    initDisabled: function (comboBox) {  //循环遍历结点，设置可用状态
        //        var disabled = comboBox.disableCls;
        //        var store = combobox.getStore();
        //        if (store != null) {
        //            for (var i = 0; i < store.getTotalCount(); i++) {
        //                var record = store.getAt(i);
        //                if (record.get(disabled)) {  //不可用
        //                }
        //            }
        //        }
    },
    deselectAllOpt: function () {
        var allOptInput = document.getElementById(this.allOptId);
        if (allOptInput != null) {
            allOptInput.checked = false;
        }
    },
    selectAllOpt: function () {
        var allOptInput = document.getElementById(this.allOptId);
        if (allOptInput != null) {
            allOptInput.checked = true;
        }
    },
    // setRawValue: function (value) {
    //     //alert(value);
    // },
    onExpand: function () {
        var me = this, value = me.getValue();
        if (value && value != "") {
            value += "";
            var attr_ids = value.split(",");
            me.setValue(attr_ids);
        }
        else {
            me.clearValue();
        }
    }
});

function clickAllOptionDiv(comboxId) {
    if (comboxId != null && comboxId.length > 0) {
        var allOptInputId = comboxId + "_allOpt";
        var allOptInput = document.getElementById(allOptInputId);
        clickAllOptionInput(allOptInput);
    }
}

function clickAllOptionInput(allOptInput) {
    if (allOptInput != null) {
        var allOptInputId = allOptInput.id;
        var allOptInputIdArray = allOptInputId.split("_allOpt");
        var comboxId = allOptInputIdArray[0];
        var isChecked = allOptInput.checked;
        var combobox = Ext.getCmp(comboxId);
        var boundList = combobox.getPicker();
        if (boundList != null) {
            var selModel = boundList.getSelectionModel();
            selModel.deselectOnContainerClick = false;
        }
        if (isChecked) {
            allOptInput.checked = false;
            if (combobox != null) {
                combobox.setValue([]);
            }
        } else {
            allOptInput.checked = true;
            if (combobox != null) {
                var allValueArray = getAllStoreValueArryByAtt(combobox); //取可用结点
                combobox.setValue(allValueArray);
            }
        }
    }
}

function getAllStoreValueArryByAtt(combobox) {
    var store = combobox.getStore(), key = combobox.valueField;
    var valueArray = [], disabled = combobox.disableCls;
    if (store != null) {
        for (var i = 0; i < store.getTotalCount(); i++) {
            var record = store.getAt(i);
            if (!record.get(disabled)) { //取可用结点
                if (record.get(key)) valueArray.push(record.get(key));
            }
        }
    }
    return valueArray;
}


////模板多选，每个下拉之前加上一个checkbox
//Ext.define('Ext.ux.SMultiComboBox', {
//    extend: 'Ext.form.ComboBox',
//    alias: 'widget.smulticombobox', //简单的下拉多选
//    xtype: 'smulticombobox',
//    initComponent: function () {
//        this.multiSelect = true;
//        this.listConfig = {
//            itemTpl: Ext.create('Ext.XTemplate',
//                    '<input type=checkbox style="position:absolute; top:1px;"><span style="padding-left:20px;">{name}</span>'),
//            onItemSelect: function (record) {
//                var node = this.getNode(record);
//                if (node) {
//                    Ext.fly(node).addCls(this.selectedItemCls);
//                    var checkboxs = node.getElementsByTagName("input");
//                    if (checkboxs != null) {
//                        var checkbox = checkboxs[0];
//                        checkbox.checked = true;
//                    }
//                }
//            },
//            listeners: {
//                itemclick: function (view, record, item, index, e, eOpts) {
//                    var isSelected = view.isSelected(item);
//                    var checkboxs = item.getElementsByTagName("input");
//                    if (checkboxs != null) {
//                        var checkbox = checkboxs[0];
//                        if (!isSelected) {
//                            checkbox.checked = true;
//                        } else {
//                            checkbox.checked = false;
//                        }
//                    }
//                }
//            }
//        }
//        this.callParent();
//    },
//    clearValue: function () {
//        try {
//            var coboxhtml = this.getEl().dom;
//            if (coboxhtml != null) {
//                var checkboxs = this.picker.listEl.el.dom.children;
//                if (checkboxs != null) {
//                    for (var i = 0; i < checkboxs.length; i++) {
//                        var checkbox = checkboxs[i];
//                        checkbox.children[0].checked = false;
//                    }
//                }
//            }
//        } catch (e) {
//            //            if (typeof (console) != "undefined" && console != null) {
//            //                console.log(e.toString());
//            //            }
//        }
//        this.setValue([]);
//        this.callParent();
//    }
//});