Ext.define('Ext.ux.UEditor', {
    extend: 'Ext.form.FieldContainer',
    mixins: {
        field: Ext.form.field.Field
    },
    alias: 'widget.ueditor_', //xtype名称
    alternateClassName: 'Ext.form.UEditor',
    ue: null,
    initialized: false,
    initComponent: function () {
        var me = this;
        me.addEvents('initialize', 'change'); // 为ueditor添加一个初始化完成的事件
        //var id = me.id + '-ueditor';
        var id = me.id + '_' + Math.random().toString().substr(2);
        me.html = '<script id="' + id + '"  name="' + me.name + '"></script>';
        //调用当前方法的父类方法详见Ext.Base
        me.callParent(arguments);
        me.initField();
        me.on('render', function () {
            var width = me.getWidth() - 130;
            var height = me.height - 109;
            var config = {initialFrameWidth: width, initialFrameHeight: height};
            //me.ue = UE.getEditor(id, config);

            me.ue = UE.getEditor(id, Ext.apply(me.ueditorConfig, {
                initialFrameHeight: height,
                initialFrameWidth: '100%'
            }));
            me.ue.ready(function () {
                me.initialized = true;
                me.fireEvent('initialize', me);
                me.ue.addListener('contentChange', function () {
                    me.fireEvent('change', me);
                });
            });
        });
    },
    getValue: function () {
        var me = this,
            value = '';
        if (me.initialized) {
            value = me.ue.getContent();
        }
        me.value = value;
        return value;
    },
    setValue: function (value) {
        // alert(value);
        var me = this;
        if (value === null || value === undefined) {
            value = '';
        } else {
            me.isChanged = true;
        }
        if (me.initialized) {
            me.ue.setContent(value);
        }
        return me;
    },
    reset: function () {
        var me = this;
        if (me.initialized) {
            me.ue.setContent("");
        }
        return me;
    },
    getPlainTxt: function () {
        var me = this,
            value = '';
        if (me.initialized) {
            value = me.ue.getPlainTxt();
        }
        me.value = value;
        return value;
    },
    onDestroy: function () {
        var me = this;
        try {
            $("#" + me.id + "-bodyEl").find("div").remove();
            //$("#" + me.id).remove();
            me.ue.destroy();
        } catch (e) {
            // alert(e.name + ": " + e.message);
        }
    }
});


Ext.define('App.ux.UEditor', {
    extend: 'Ext.form.field.Text',
    alias: ['widget.ueditor'],
    //alternateClassName: 'App.form.UEditor',
    fieldSubTpl: [
        '<textarea id="{id}" {inputAttrTpl}',
        '<tpl if="name"> name="{name}"</tpl>',
        '<tpl if="rows"> rows="{rows}" </tpl>',
        '<tpl if="cols"> cols="{cols}" </tpl>',
        '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
        '<tpl if="size"> size="{size}"</tpl>',
        '<tpl if="maxLength !== undefined"> maxlength="{maxLength}"</tpl>',
        '<tpl if="readOnly"> readonly="readonly"</tpl>',
        '<tpl if="disabled"> disabled="disabled"</tpl>',
        '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
        //            ' class="{fieldCls} {inputCls}" ',
        '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
        ' autocomplete="off">\n',
        '<tpl if="value">{[Ext.util.Format.htmlEncode(values.value)]}</tpl>',
        '</textarea>',
        {
            disableFormats: true
        }

    ],
    ueditorConfig: {zIndex: 20000},
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },
    afterRender: function () {
        var me = this;
        me.callParent(arguments);
        if (!me.ue) {
            var id = me.getInputId() + '_' + Math.random().toString().substr(2);
            me.ue = UE.getEditor(me.getInputId(), Ext.apply(me.ueditorConfig, {
                initialFrameHeight: me.height || '300px',
                initialFrameWidth: '100%',
                zIndex: 20000
            }));
            me.ue.ready(function () {
                me.UEditorIsReady = true;
            });
            //这块 组件的父容器关闭的时候 需要销毁编辑器 否则第二次渲染的时候会出问题 可根据具体布局调整
            var win = me.up('window');
            if (win && win.closeAction == "hide") {
                win.on('beforehide', function () {
                    me.onDestroy();
                });
            } else {
                var panel = me.up('panel');
                if (panel && panel.closeAction == "hide") {
                    panel.on('beforehide', function () {
                        me.onDestroy();
                    });
                }
            }
        } else {
            me.ue.setContent(me.getValue());
        }
    },
    setValue: function (value) {
        var me = this;
        if (!me.ue) {
            me.setRawValue(me.valueToRaw(value));
        } else {
            me.ue.ready(function () {
                me.ue.setContent(value);
            });
        }
        me.callParent(arguments);
        return me.mixins.field.setValue.call(me, value);
    },
    getRawValue: function () {
        var me = this;
        if (me.UEditorIsReady && me.ue) {
            me.ue.sync(me.getInputId());
        }
        v = (me.inputEl ? me.inputEl.getValue() : Ext.value(me.rawValue, ''));
        me.rawValue = v;
        return v;
    },
    destroyUEditor: function () {
        var me = this;
        if (me.rendered) {
            try {
                me.ue.destroy();
                var dom = document.getElementById(me.id);
                if (dom) {
                    dom.parentNode.removeChild(dom);
                }
                //$("#" + me.id + "-bodyEl").find("div").remove();
                me.ue = null;
            } catch (e) {
            }
        }
    },
    onDestroy: function () {
        var me = this;
        me.callParent();
        me.destroyUEditor();
    }
});

