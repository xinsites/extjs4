/**
* 自定义弹出框(插件)
*/
Ext.define('Ext.ux.DefineTrigger', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.definetrigger',
    onTriggerClick: function (field) {
        var me = this;
        if (me.runFunction) {
            //Ext.alert.msg('提示', '定义的运行函数名：' + me.runFunction);
            eval(me.runFunction);
        }
    }
});