/**
 * 自定义下拉框(插件)
 */

Ext.define('Ext.ux.DefinePicker', {
    extend: Ext.form.field.Picker,
    alias: 'widget.definepicker',
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },
    createPicker: function () {
        var me = this, picker;
        if (typeof me.runFunction == "string") {
            //Ext.alert.msg('提示', '定义的运行函数名：' + me.runFunction);
            picker = eval(me.runFunction);
        }
        return picker;
    }
});