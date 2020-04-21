/**
 * 复选框组列(每列复选框相同)
 */
Ext.define('Ext.grid.column.ColumnCheckGroup', {
    extend: Ext.grid.column.Column,
    alternateClassName: 'Ext.ux.ColumnCheckGroup',
    alias: 'widget.checkgroupcolumn',
    align: 'center',
    groupCheckValue: [[1, "是"], [0, "否"]], //每行复选框相同
    //groupCheckField: "",  //每行复选框不同，通过后端返回，优先级最大，列表值如：[[1, "是"], [0, "否"]]
    checkGroupClass: "check-img",  //IE8以上的默认使用check-item，而check-img是Extjs本身的复选框
    enableChecked: true,
    //    tdCls: Ext.baseCSSPrefix + 'grid-cell-checkcolumn',
    //    innerCls: Ext.baseCSSPrefix + 'grid-cell-inner-checkcolumn',
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
            var vals = [];
            if (record.store) {
                var item_ids = getItemIds(record);
                if (!isExistsByItemIds(item_ids, "btn_mod", "")) {
                    $(cell).find("input").unbind("click").click(function () {
                        $(this).attr('checked', !$(this).is(':checked'));
                    });
                } else {
                    setTimeout(function () {
                        $(cell).find("input").each(function () {
                            if ($(this).is(':checked'))
                                vals[vals.length] = $(this).attr('value');
                        });
                        record.set(dataIndex, vals.join(','));
                        //record.commit();

                        if (itemClickFlag == me.dataIndex)
                            me.onCheckChange(view, cell, record, record.get(dataIndex));
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
    updateData: function (cell, value) {
        var me = this;
        var dataIndex = me.dataIndex;
    },
    getCheckbox: function (record, item, vals, disabled, itemClass) {
        var me = this, checked = "", val = item[0];
        for (var j = 0; j < vals.length; j++) {
            if (vals[j] == val) {
                checked = "checked";
                break;
            }
        }
        if (itemClass) {
            var id = me.getId() + record.getId() + "_" + val;
            var label = '<div class="{0}"><input id="{1}" type="checkbox" value="{2}" {3} {4} onclick="setItemClickFlag(\'{5}\')"/><label for="{1}">{6}</label></div>';
            return Ext.String.format(label, itemClass, id, item[0], checked, disabled, me.dataIndex, item[1]);
        } else {
            var label = '<label><input type="checkbox" value="{0}" {2} {3} onclick="setItemClickFlag(\'{4}\')"/>{1} </label>';
            return Ext.String.format(label, item[0], item[1], checked, disabled, me.dataIndex);
        }
    },
    renderer: function (value, meta, record) {
        if (value == "clear") return "";
        var me = this, attrs = [], vals = [], disabled = "", itemClass = "";
        if (!me.groupCheckValue || me.groupCheckValue.length == 0) return "";
        if (!me.enableChecked) disabled = "disabled";
        var ie_v = IEVersion();
        if (ie_v > 8 || ie_v == -1) {
            var checkClass = me.checkGroupClass;
            if (checkClass != "check-item" && checkClass != "check-img") checkClass = "check-item";
            itemClass = checkClass + (disabled != "" ? " " + checkClass + "-disabled" : "");
        }

        if (value && value != "") vals = value.split(",");
        for (var i = 0; i < me.groupCheckValue.length; i++) {
            attrs[attrs.length] = me.getCheckbox(record, me.groupCheckValue[i], vals, disabled, itemClass);
        }
        return attrs.join("");
    }
});