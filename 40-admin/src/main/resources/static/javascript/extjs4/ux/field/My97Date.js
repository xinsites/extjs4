Ext.define('Ext.ux.form.field.My97Date', {
    extend: 'Ext.form.field.Text',
    alias: ['widget.my97date'],
    my97Config: {},
    initComponent: function () {
        var me = this;
        if (me.my97Config.dateIcon)
            me.fieldCls = 'x-form-field  x-form-date-trigger ' + me.my97Config.dateIcon;
        else
            me.fieldCls = 'x-form-field  x-form-date-trigger Wdate';

        me.callParent(arguments);
        me.on("render", function (field) {
            if (me.my97Config.onfocus && typeof me.my97Config.onfocus == "function") {
                $("#" + me.getId() + "-inputEl").bind("focus", function () {
                    me.my97Config.onfocus(me.my97Config);
                    me.setValue(me.getValue());
                });
            }
            field.getEl().on('click', function () {
                if (!field.readOnly) {
                    WdatePicker(Ext.apply(me.my97Config, {
                        el: me.getId() + "-inputEl",
                        autoPickDate: true,
                        onpicking: function (dq) {
//                        alert(dq.cal.getNewDateStr());
                            //在单元格编辑中，该事件慢于单元格编辑事件
                            if (me.record && me.field) {
                                var record = me.record;
                                record.set(me.field, dq.cal.getNewDateStr());
                                record.commit();
                            }
                            me.setValue(dq.cal.getNewDateStr());
                        }
                    }));
                } else {
                    $("#" + me.getId() + "-inputEl").blur();
                }
            });
        });
    },
    setReadOnly: function (readOnly) {
        var me = this;
        me.readOnly = readOnly;
    }
});