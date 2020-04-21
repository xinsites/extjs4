Ext.define('Ext.ux.form.field.UEditor', {
    extend: 'Ext.form.field.Text',
    alias: ['widget.ueditor'],
    isToolbar: true,  //是否有工具栏
    isAutoHidden: false,  //全屏时是否隐藏指定的pannel，这里是Bug，自行解决的，默认不隐藏，经测试弹出窗口中的编辑器不需要隐藏
    autoHiddenIds: ["body_west", "body_south", "body_north"], //默认隐藏指定的pannel主键
    //alternateClassName: 'Ext.form.UEditor',
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
    ueditorConfig: {},
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },
    afterRender: function () {
        var me = this;
        me.callParent(arguments);
        if (!me.ue) {
            if (me.isToolbar) {
                me.ue = UE.getEditor(me.getInputId(), Ext.apply({
                    zIndex: 2000,
                    initialFrameHeight: me.height || '300px',
                    initialFrameWidth: '100%',
                },  me.ueditorConfig));
            }
            else {
                me.ue = new UE.Editor(Ext.apply(me.ueditorConfig, {
                    initialFrameHeight: me.height || '300px',
                    initialFrameWidth: '100%'
                }));
                me.ue.render(me.getInputId(), {
                    autoFloatEnabled: false
                });
            }
            me.ue.ready(function () {
                me.UEditorIsReady = true;
                //                me.initialized = true;
                //                me.fireEvent('initialize', me);
                //                me.ue.ui.setFullScreen(true);
                //                me.ue.addListener('contentChange', function () {
                //                    me.fireEvent('change', me);
                //                });
                //                alert(me.isAutoHidden);
                me.ue.addListener('beforefullscreenchange', function (event, isFullScreen) { //全屏前
                    //                    me.ue.ui.setFullScreen(true);
                    if (me.isAutoHidden) {
                        var ids = me.autoHiddenIds, index;
                        if (isFullScreen) me.tempIds = [];  //记录本来是隐藏状态的panel
                        for (index in ids) {
                            var panel = Ext.getCmp(ids[index]);
                            if (panel) {
                                if (isFullScreen && !panel.isVisible()) {
                                    me.tempIds[me.tempIds.length] = ids[index];
                                    continue;
                                }
                                if (!isFullScreen) {  //退回全屏，要把已经隐藏的panel再显示出来
                                    if (me.tempIds.indexOf(ids[index]) >= 0) continue;
                                }
                                panel.setVisible(!isFullScreen);
                            }
                        }
                        //                        if (Ext.getCmp("body_west")) Ext.getCmp("body_west").setVisible(!isFullScreen);
                    }
                });
                me.ue.addListener('fullscreenchanged', function (event, isFullScreen) { //全屏后
                    if (!isFullScreen) {
                        me.up('panel').doLayout(); //me.up("panel").setTitle('数据视图');
                    }
                });
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
    getUEditor: function () {
        var me = this;
        return me.ue;
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
    reset: function () {
        var me = this;
        if (me.originalValue) {
            me.ue.setContent(me.originalValue);
        }
        else {
            me.ue.setContent("");
        }
    },
    getRawValue: function () {
        var me = this;
        if (me.UEditorIsReady) {
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

