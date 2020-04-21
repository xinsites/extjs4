/**
 * 单选框组列
 */
Ext.define('Ext.grid.column.ColumnRadioGroup', {
    extend: Ext.grid.column.Column,
    alternateClassName: 'Ext.ux.ColumnRadioGroup',
    alias: 'widget.radiogroupcolumn',
    align: 'center',
    intputname: "intputname",
    groupRadioValue: [[1, "是"], [0, "否"]],
    enableChecked: true,
    radioGroupClass: "radio-img",  //IE8以上的默认使用radio-item，而radio-img是Extjs本身的复选框
    //    tdCls: Ext.baseCSSPrefix + 'grid-cell-radiocolumn',
    //    innerCls: Ext.baseCSSPrefix + 'grid-cell-inner-radiocolumn',
    constructor: function () {
        this.scope = this;
        this.callParent(arguments);
    },
    processEvent: function (type, view, cell, recordIndex, cellIndex, e, record, row) {
        var me = this,
            key = type === 'keypress' && e.getKey(),
            mouseup = type == 'mouseup';

        //Ext.alert.msg('提示', "mouseup " + mouseup);
        if (!me.disabled && (mouseup || (key == e.ENTER || key == e.SPACE))) {
            var dataIndex = me.dataIndex;
            //alert($(cell).find("input").length);
            if (record.store) {
                var item_ids = getItemIds(record);
                if (!isExistsByItemIds(item_ids, "btn_mod", "")) {
                    $(cell).find("input").unbind("click").click(function () {
                        $(this).attr('checked', !$(this).is(':checked'));
                    });
                } else {
                    setTimeout(function () {
                        record.originalValue = record.get(dataIndex);
                        $(cell).find("input").each(function () {
                            if ($(this).is(':checked')) {
                                record.set(dataIndex, $(this).attr('value'));
                                return;
                            }
                        });
                        //record.commit();
                        var curValue = record.get(dataIndex);
                        if (itemClickFlag == me.dataIndex && curValue != record.originalValue)
                            me.onCheckChange(view, cell, record, curValue);
                    }, 10);
                }
            }
            return !me.enableChecked;
        } else {
            return me.callParent(arguments);
        }
    },
    onCheckChange: function (view, cell, record, value) {

    },
    onEnable: function (silent) {
        var me = this;
        me.callParent(arguments);
        me.up('tablepanel').el.select('.' + Ext.baseCSSPrefix + 'grid-cell-' + me.id).removeCls(me.disabledCls);
        if (!silent) {
            me.fireEvent('enable', me);
        }
    },
    onDisable: function (silent) {
        var me = this;

        me.callParent(arguments);
        me.up('tablepanel').el.select('.' + Ext.baseCSSPrefix + 'grid-cell-' + me.id).addCls(me.disabledCls);
        if (!silent) {
            me.fireEvent('disable', me);
        }
    },
    getRadio: function (record, item, value, disabled, itemClass) {
        var me = this, checked = "", val = item[0];
        if (value == val) checked = "checked";
        var name = me.getId() + record.getId();

        if (itemClass) {
            var id = me.getId() + record.getId() + "_" + val;
            var str = Ext.String.format('id="{0}" name="{1}"', id, name);
            var label = '<div class="{0}"><input {1} type="radio" value="{2}" {3} {4} onclick="setItemClickFlag(\'{5}\')"/><label for="{7}">{6}</label></div>';
            return Ext.String.format(label, itemClass, str, item[0], checked, disabled, me.dataIndex, item[1], id);
        } else {
            var str = Ext.String.format('name="{0}"', name);
            var label = '<label><input {5} type="radio" value="{0}" {2} {3} onclick="setItemClickFlag(\'{4}\')"/>{1} </label>';
            return Ext.String.format(label, item[0], item[1], checked, disabled, me.dataIndex, str);
        }
    },
    renderer: function (value, meta, record) {
        if (value == "clear") return "";
        var me = this, attrs = [], disabled = "", itemClass = "";
        if (!me.groupRadioValue || me.groupRadioValue.length == 0) return "";
        if (!me.enableChecked) disabled = "disabled";
        var ie_v = 1;
        if (typeof IEVersion == "function") ie_v = IEVersion();
        if (ie_v > 8 || ie_v == -1) {
            var checkClass = me.radioGroupClass;
            if (checkClass != "radio-item" && checkClass != "radio-img") checkClass = "radio-item";
            itemClass = checkClass + (disabled != "" ? " " + checkClass + "-disabled" : "");
        }

        for (var i = 0; i < me.groupRadioValue.length; i++) {
            attrs[attrs.length] = me.getRadio(record, me.groupRadioValue[i], value, disabled, itemClass);
        }
        return attrs.join("");
    }
});