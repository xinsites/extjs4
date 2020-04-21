/**
 * 插件下拉列表单选（可设置不可用）
 */
Ext.define('Ext.ux.SingleComboBox', {
    extend: 'Ext.form.ComboBox',
    alias: 'widget.singlecombobox',
    xtype: 'singlecombobox',
    disableCls: "disabled",  //不可用选项字段，字段值为不可用样式.disabled，必须先定义好，该值不存在或者为空表示可以选择
    forceSelection: true,   //所选择的值限制在一个列表中的值，false时，允许用户设置任意的文本字段。
    minChars: 0,            //自动查询的最小字符数
    validate: function () {
        var errs = [];
        var val = this.getRawValue();
        var id_val = this.getValue();
        if (this.store.data && this.store.data.items.length > 0) {
            if (!this.allowBlank && this.forceSelection) {
                if (this.xtype == "singlecombobox") {
                    var rec = this.findRecord(this.valueField, id_val);
                    if (!rec) rec = this.findRecord(this.valueField, id_val + "");
                    if (!rec) {
                        if (val) errs.push("无效的选择 [" + val + "]");
                        else errs.push("无效的选择");
                    }
                } else {
                    var rec = this.findRecord(this.displayField, val);
                    if (!rec) errs.push("无效的选择");
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
        } else {
            this.clearInvalid();
            return true;
        }
    },
    initComponent: function () {
        var valueField = this.valueField;
        var me = this;
        var thisid = this.id;
        var disableCls = this.disableCls;
        var tpl = '<tpl for="."><div class="x-boundlist-item {' + disableCls + '}">{' + this.displayField + '}&nbsp;</div></tpl>';
        this.listConfig = {
            tpl: tpl,
            //itemTpl: '<tpl for=".">' + '<div class="{disableCls}" >' + '{name}&nbsp;' + '</div>' + '</tpl>',
            onItemSelect: function (record) {
                var node = this.getNode(record);
                if (node) {
                    Ext.fly(node).addCls(this.selectedItemCls);
                    var checkboxs = node.getElementsByTagName("input");
                }
            },
            onItemDeselect: function (record) {
                var node = this.getNode(record);
                if (node) Ext.fly(node).removeCls(this.selectedItemCls);
            },
            listeners: {
                beforeselect: function (combo, record, index, e) {
                    if (me.isDisabled(record)) return false; //不可用，选择无效
                    return true;
                }
            }
        }
        if (me.store && me.queryMode != "local") {
            this.store.on("load", function (combocox, record) {
                if (me.originalValue) {
                    me.setValue(me.originalValue);
                }
            });
        }
        this.callParent();
    },
    isDisabled: function (record) {
        var disableCls = this.disableCls;
        if (!disableCls) return false;  //没有设置不可用样式字段，不可用无效
        var disabled = record.raw.disabled;
        return (disabled == "disabled" || disabled == true || disabled == "1" || disabled == "True"); //不可选
    },
    onExpand: function () {
        var me = this, value = me.getValue();
        if (value && value != "") {
            me.setValue(value);
        }
    }
});

