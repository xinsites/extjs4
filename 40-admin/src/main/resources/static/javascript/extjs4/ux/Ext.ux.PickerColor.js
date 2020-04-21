/**
* 插件下拉颜色选择框
*/
Ext.define('Ext.ux.PickerColor', {
    extend: 'Ext.form.field.Picker',
    requires: ['Ext.picker.Color'],
    //    alternateClassName: 'Ext.ux.ChooseColor',
    alias: ['widget.pickercolor'],
    //    defaultPickerConfig: {
    //        emptyText: '',
    //        width: 144,
    //        height: 100
    //    },
    initComponent: function () {
        var me = this;
        //注册事件
        me.addEvents(
            'select'
        );
        me.callParent();
    },
    createPicker: function () {
        var me = this,
            picker,
            opts = Ext.apply({
                pickerField: me,
                floating: true,
                hidden: true,
                ownerCt: me.ownerCt,
                value: me.value
            },
            me.pickerConfig,
            me.defaultPickerConfig
            );

        picker = me.picker = Ext.create('Ext.picker.Color', opts);
        //picker.colors = ["000000", "333333", "666666", "999999", "CCCCCC", "FFFFFF", "00CC00", "00CC33", "33CC00", "33CC33", "66CC00", "66CC33", "00CC66", "33CC66", "00FF00", "00FF33", "00FF66", "33FF00", "66FF00", "33FF33", "33FF66", "66FF33", "66FF66", "99FF00", "99FF33", "99FF66", "99FF99", "CCFF66", "99CC00", "CCFF99", "99CC66", "669933", "339933", "009933", "339900", "007326", "336600", "336633", "003300", "006633", "009966", "339966", "669966", "66CC66", "66CC99", "33CC99", "99CC99", "00FF99", "33FF99", "CCFFCC", "99FFCC", "66FFCC", "99FFFF", "66FFFF", "00FFFF", "33FFFF", "33FFCC", "00FFCC", "33CCCC", "00CCCC", "66CCCC", "00CC99", "339999", "009999", "006666", "FFFF66", "FFFF33", "FFFF00", "FFFF99", "FF9966", "FFCC00", "CCCC66", "CCCC33", "CCCC00", "999933", "999900", "999966", "666633", "666600", "333300", "663300", "996633", "996600", "CC9933", "FFCC66", "FFCC99", "FF9933", "FF9900", "FF6600", "CC9966", "000033", "000066", "003366", "333366", "003399", "333399", "3300CC", "0033CC", "006699", "0000FF", "3300FF", "3333FF", "0033FF", "0066FF", "3366FF", "0066CC", "666699", "3366CC", "6666FF", "336699", "0099CC", "6699CC", "3399CC", "0099FF", "6699FF", "3399FF", "00CCFF", "33CCFF", "66CCFF", "99CCFF", "CCFFFF", "99CCCC", "669999", "336666", "003333", "330066", "330099", "6600CC", "6600FF", "6633CC", "6633FF", "CCCCFF", "660099", "660066", "663399", "9900CC", "993399", "9933CC", "9900FF", "9933FF", "996699", "9966CC", "9966FF", "663366", "CC00FF", "CC66CC", "CC99FF", "CC33FF", "CC66FF", "FF99FF", "330033", "660033", "990066", "CC0099", "CC3399", "CC6699", "FF0099", "FF3399", "FF33CC", "FF00CC", "FF33FF", "FF00FF", "FF66CC", "FF99CC", "FFCCFF", "660000", "990033", "990000", "993333", "CC3333", "CC6666", "CC6633", "CC6600", "CC3300", "993300", "663333", "FF0066", "FF3366", "FF6666", "FF6699", "FF9999", "FFCCCC", "330000", "CC9999", "FF6633", "FF3300", "FF0033", "FF3333", "FF0000", "CC3366", "CC0066", "CC0033"],
        me.mon(picker, {
            select: me.select,
            scope: me
        });

        return picker;
    },
    alignPicker: function () {
        var me = this, picker;
        if (me.isExpanded) {
            picker = me.getPicker();
            picker.setHeight(picker.getHeight());
            picker.setWidth(picker.getWidth());

            //            picker.setWidth(me.bodyEl.getWidth());
            if (picker.isFloating()) {
                me.doAlign();
            }
        }
    },
    setValue: function (value) {
        var me = this;
        value = value || "";
        me.displayTplData = value;
        me.setRawValue(value);
        me.value = value;
        me.getPicker().select(value)
        return me;
    },
    getValue: function () {
        var me = this;
        return me.value;
    },
    getSubmitValue: function () {
        return this.getValue();
    },
    select: function (cp, color, eOpts) {
        var me = this;
        me.setRawValue("#" + cp.getValue());
        me.value = cp.getValue();
        me.setFieldStyle("color: #" + me.value + ";");
        //        me.setFieldStyle("color: #3366FF;");
        me.collapse();
        me.fireEvent('itemClick', me, cp, color, eOpts); //触发事件
    }
});

