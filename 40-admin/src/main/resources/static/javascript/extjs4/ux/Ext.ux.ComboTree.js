/**
 * 插件下拉列表树
 */
Ext.define('Ext.ux.TreePicker', {
    extend: 'Ext.form.field.Picker',
    xtype: 'treepicker',
    uses: ['Ext.tree.Panel'],
    multiSelect: false,  //默认单选
    selectMode: 'all',  //选择模式：只选叶子:leaf；只选父结点:parent；选择所有：all；只选复选框：check；
    queryMode: 'local',  //远程数据：remote[store不能为空]；本地数据：local[root不能为空]
    cascade: 'both',    //级联方式:1.child子级联;2.parent,父级联,3,both全部级联, 4.空不级联选择
    disableCls: "disabled",  //不可用选项字段，字段值为不可用样式.disabled，必须先定义好，该值不存在或者为空表示可以选择
    ajaxUrl: "system/code/codetext", //修改窗口，存值时获取文本值接口
    treeid: "",
    enableKeyEvents: true,
    triggerCls: Ext.baseCSSPrefix + 'form-arrow-trigger',
    config: {
        store: null,
        displayField: "text",
        columns: null,
        selectOnTab: true,
        minPickerWidth: null,
        singleExpand: false, //如果每个分支只有1个节点可能展开,默认false
        pickerResizable: true,
        maxPickerHeight: 400,
        pickerEmptyText: ""
        //minPickerHeight: 100
    },
    editable: false,
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
        me.addEvents('select');
        me.mon(me.store, {
            scope: me,
            load: me.onLoad,
            update: me.onUpdate
        });
        if (me.multiSelect) me.selectMode = 'check';
        me.store.on({
            'load': function (view, record) {
                if (me.multiSelect) {
                    if (record.get('checked') == null) {
                        me.setCheckedValue();
                    }
                }
            }
        });
        var preDate = new Date();
        me.on({
            'render': function (tree, eOpts) {
                if (me.multiSelect && me.queryMode == "local") {
                    if (me.value && me.value != "") {
                        var texts = [], ids = me.value.split(",");
                        if (ids.length > 0) {
                            me.getTexts(me.store.getRootNode(), ids, texts);
                            if (texts.length > 0) me.setRawValue(texts.join(","));
                            else me.setRawValue(me.value);
                        }
                    }
                }

                var store = tree.store;
                if (me.queryMode == "local") {
                    setTimeout(function () {
                        me.initDisabled(me.getPicker().getView(), me.getPicker().getRootNode());
                    }, 50);
                }
                store.on("expand", function (record) {
                    if (record.get('checked') == true) {
                        me.onCheckChange(record, true);
                    }
                    me.initDisabled(me.getPicker().getView(), record);
                });
                store.on("collapse", function (record) {
                    me.initDisabled(me.getPicker().getView(), record);
                });
                store.on("beforeload", function (store, record) {
                    //preDate = new Date();
                });
                store.on("load", function (store, record) {
                    if (me.value && !me.getRawValue()) {
                        if (me.store.rawValue) me.setRawValue(me.store.rawValue);
                        else me.value = "";
                    }
                    nodeExpand(record);
                });
            },
            'expand': function (picker, e) {
                setTimeout(function () {
                    if (me.editable) me.inputEl.focus();
                    me.initDisabled(me.getPicker().getView(), me.getPicker().getRootNode());
                }, 10);
            },
            'focus': function (picker, e, obj) {
                $("#" + me.getInputId()).focus(function () {
                    $("#" + me.getPicker().getId()).css("z-index", "29514");
                });
            },
            'keydown': function (picker, e, obj) {
                $("#" + me.getPicker().getId()).css("z-index", "29514");
            },
            'specialkey': function (picker, e, obj) {
                if (e.getKey() == e.BACKSPACE) {
                    if (me.editable) {
                        me.value = "";
                        me.setRawValue("");
                        searchPicker();
                    }
                } else if (e.getKey() == e.ENTER) {
                    if (me.editable) searchPicker();
                }
            },
            'keyup': function (picker, e, obj) {
                if (me.getRawValue().length > 0) {
                    if (preDate && ((new Date()).getTime() - preDate.getTime() < 2000)) return; //查询间隔时间不能小于2秒
                    preDate = new Date();
                    searchPicker();
                }
            }
        });

        function nodeExpand(record) {
            Ext.Array.each(record.childNodes, function (record, index) {
                if (record.raw.expand == "true") {
                    if (!record.get('leaf') && !record.isExpanded()) {
                        setTimeout(function () {
                            record.expand(false);
                        }, 50);
                    }
                    nodeExpand(record);
                }
            });
        }

        function searchPicker() {
            if (me.editable && me.queryMode != "local") {
                setTimeout(function () {
                    //me.initDisabled(me.getPicker().getView(), me.getPicker().getRootNode());
                    //if (me.value) me.getTexts(me.store.getRootNode(), me.value.split(","), []);
                    me.store.proxy.extraParams.query = me.getRawValue().replace(/%/g, '/%').replace(/_/g, '/_');
                    me.getPicker().getRootNode().removeAll(false);
                    me.store.load();
                    me.expand();
                }, 600);
                setTimeout(function () {
                    me.inputEl.focus();
                    me.initDisabled(me.getPicker().getView(), me.getPicker().getRootNode());
                }, 710);
            }
        }
    },
    createPicker: function () {
        //var task = new Ext.util.DelayedTask();
        var me = this;
        var pickerEmptyText = me.pickerEmptyText;
        var picker = new Ext.tree.Panel({
            shrinkWrapDock: 2,
            store: me.store,
            floating: true,
            rootVisible: false,
            //border: true,
            style: 'border:solid 1px #E1E1E1;background-color:#fff; border-width:0px 1px 1px 1px;z-index:19512;',
            displayField: me.displayField,
            hideHeaders: me.hideHeaders,
            columns: me.columns,
            minHeight: me.minPickerHeight,
            minWidth: me.minPickerWidth,
            maxHeight: me.maxPickerHeight,
            columns: me.columns,
            emptyText: pickerEmptyText,
            bodyCls: 'empty_parent',
            manageHeight: false,
            shadow: false,
            resizable: me.pickerResizable,
            listeners: {
                scope: me,
                itemclick: me.onItemClick,
                checkchange: me.onCheckChange
            },
            viewConfig: {
                deferEmptyText: false,
                listeners: {
                    scope: me,
                    render: me.onViewRender
                }
            }
        });

        view = picker.getView();
        if (Ext.isIE9 && Ext.isStrict) {
            view.on({
                scope: me,
                highlightitem: me.repaintPickerView,
                unhighlightitem: me.repaintPickerView,
                afteritemexpand: me.repaintPickerView,
                afteritemcollapse: me.repaintPickerView
            });
        }
        return picker;
    },
    onViewRender: function (view) {
        view.getEl().on('keypress', this.onPickerKeypress, this);
    },
    repaintPickerView: function () {
        var style = this.picker.getView().getEl().dom.style;
        // can't use Element.repaint because it contains a setTimeout, which results in a flicker effect
        style.display = style.display;
    },
    alignPicker: function () {
        var me = this, picker;
        if (me.isExpanded) {
            picker = me.getPicker();
            if (me.matchFieldWidth) {
                // Auto the height (it will be constrained by max height)
                picker.setWidth(me.bodyEl.getWidth());
            }
            if (picker.isFloating()) {
                me.doAlign();
            }
        }
    },
    getTexts: function (rootNode, ids, texts) {
        var me = this;
        var childNodes = rootNode.childNodes;
        for (var i = 0; i < childNodes.length; i++) {
            var rec = childNodes[i];
            for (var j = 0; j < ids.length; j++) {
                if (rec.get("id") == ids[j] || rec.get("text") == ids[j]) {
                    texts[texts.length] = rec.get("text");
                    me.store.rawValue = texts.join(",");
                    rec.set("checked", true);
                    ids.splice(j, 1);
                    break;
                }
            }
            if (ids && ids.length > 0)
                me.getTexts(rec, ids, texts);
            else
                break;
        }
    },
    onCheckChange: function (record, checked) {  //处理级联操作
        var me = this, cascade = me.cascade, disabled;
        if (me.multiSelect) {
            if (checked == true) {
                if (cascade == 'both' || cascade == 'child' || cascade == 'parent') {
                    if (cascade == 'child' || cascade == 'both') {
                        if (!record.get("leaf") && checked) record.cascadeBy(function (record) {
                            disabled = me.isDisabled(record);
                            if (record.get('checked') != null && !disabled) {
                                record.set('checked', true);
                                me.fireEvent("cascadechange", record);
                            }
                        });
                    }
                    if (cascade == 'parent' || cascade == 'both') {
                        pNode = record.parentNode;
                        for (; pNode != null; pNode = pNode.parentNode) {
                            disabled = me.isDisabled(pNode);
                            if (pNode.get('checked') != null && !disabled) {
                                pNode.set("checked", true);
                                me.fireEvent("cascadechange", pNode);
                            }
                        }
                    }
                }
            } else if (checked == false) {
                if (cascade == 'both' || cascade == 'child' || cascade == 'parent') {
                    if (cascade == 'child' || cascade == 'both') {
                        if (!record.get("leaf") && !checked) record.cascadeBy(function (record) {
                            disabled = me.isDisabled(record);
                            if (record.get('checked') != null && !disabled) {
                                record.set('checked', false);
                                me.fireEvent("cascadechange", record);
                            }
                        });
                    }
                }
            }
        }
        me.fireEvent("checkchange", record);
    },
    isDisabled: function (record) {
        var disableCls = this.disableCls;
        if (!disableCls) return false;  //没有设置不可用样式字段，不可用无效
        var disabled = record.raw.disabled;
        return (disabled == "disabled" || disabled == true || disabled == "1" || disabled == "True"); //不可选
    },
    setTreeDisabled: function (treeview, record, disabled) {
        var checkbox, nodeui;
        if (treeview) {
            try {
                record.set("disabled", disabled);
                //alert(treeview.getNode(record));
                var element = treeview.getNode(record);
                if (element) {
                    nodeui = element.firstChild.firstChild;
                    checkbox = nodeui.getElementsByTagName('input')[0];
                    nodeui.className = nodeui.className.replace(' treenode-disable', '') + (disabled ? ' treenode-disable' : "");
                    if (checkbox) {
                        checkbox.disabled = disabled;
                        checkbox.className = checkbox.className.replace(' treenode-disable', '') + (disabled ? ' treenode-disable' : "");
                    }
                }
            } catch (e) {
                //alert(e.message)
            }
        }
    },
    initDisabled: function (treeview, rootNode) {  //循环遍历结点，设置可用状态
        var childNodes = rootNode.childNodes, me = this;
        var disabled = me.isDisabled(rootNode);
        if (disabled) me.setTreeDisabled(treeview, rootNode, true);
        for (var i = 0; i < childNodes.length; i++) {
            disabled = me.isDisabled(childNodes[i]);
            if (disabled) me.setTreeDisabled(treeview, childNodes[i], true);
            me.initDisabled(treeview, childNodes[i]);
        }
    },
    onItemClick: function (view, record, node, rowIndex, e) {  //下拉树单击操作
        var me = this, disabled;
        disabled = me.isDisabled(record);
        if (me.multiSelect) {
            if (record.get('checked') != null) {
                if (!disabled) {
                    record.set("checked", !record.get('checked'));
                    if (!e.getTarget("input"))  //点击的不是复选框，需要级联操作
                        me.onCheckChange(record, record.get('checked'));
                    me.setCheckedValue();
                }
            }
            if (!record.get('leaf')) { //不是叶子结点
                if (!record.isExpanded()) {
                    view.expand(record, false);
                }
            }
        } else if (me.selectMode == 'parent') {  //单选，只能选择父结点
            if (!record.get('leaf')) {        //点击的是父结点
                if (!disabled) me.selectItem(record);
            }
        } else if (me.selectMode == 'leaf') {  //单选，只能选择叶子结点
            if (record.get('leaf')) {        //点击的是叶子结点
                if (!disabled) me.selectItem(record);
            } else {
                if (record.isExpanded()) {
                    view.collapse(record, false);
                } else {
                    view.expand(record, false);
                }
            }
        } else if (me.selectMode == 'check') {  //单选，只能选复选框
            if (record.get('checked') == null) {
                if (!record.get('leaf')) { //不是叶子结点
                    if (record.isExpanded()) {
                        view.collapse(record);
                    } else {
                        view.expand(record, false);
                    }
                }
            } else {
                if (!disabled) {
                    me.clearChecked();
                    record.set("checked", true);
                    me.selectItem(record);
                }
            }
        } else {
            if (!disabled) me.selectItem(record);
        }
        if (me.itemclick) me.itemclick(me, view, record, node, rowIndex, e);
    },
    setCheckedValue: function (record) { //设置所有选中的结点
        var me = this;
        var checkNodes = me.getPicker().getChecked();
        var ids = [], texts = [];
        for (var i = 0; i < checkNodes.length; i++) {
            ids[ids.length] = checkNodes[i].data.id;
            texts[texts.length] = checkNodes[i].data.text;
        }
        me.value = ids.join(",");
        me.setRawValue(texts.join(","));
    },
    onPickerKeypress: function (e, el) {
        var key = e.getKey();
        if (key === e.ENTER || (key === e.TAB && this.selectOnTab)) {
            this.selectItem(this.picker.getSelectionModel().getSelection()[0]);
        }
    },
    selectItem: function (record) {
        var me = this;
        me.store.loaded = true;  //标记不加载下拉树
        me.loadOriginalValue = true; //标记原始值
        me.setValue(record.getId());
        me.picker.hide();
        me.inputEl.focus();
        me.fireEvent('select', me, record);
        me.collapse();
        me.store.loaded = false;  //恢复加载下拉树
    },
    onExpand: function () {
        var me = this,
            picker = me.picker,
            store = picker.store,
            value = me.value,
            node;
        if (!me.store.loading) {
            if (me.selectMode == 'check') {  //只能选复选框
                me.clearChecked(); //如果是选择框取消所有选择
                var attr_ids = value.split(",");
                for (var i = 0; i < attr_ids.length; i++) {
                    var id = attr_ids[i];
                    var pnode = me.store.getNodeById(id); //没有找到这个结点
                    if (pnode) {
                        if (pnode.get('checked') != null) {
                            pnode.set("checked", true);
                        } else if (me.multiSelect) {
                            pnode.set("checked", true);
                        }
                        picker.selectPath(pnode.getPath());
                    }
                }
            } else {
                node = store.getNodeById(value);
                if (!node) {
                    node = store.getRootNode();
                }
                picker.selectPath(node.getPath());
            }
        } else {
            me.clearChecked();
        }

        Ext.defer(function () {
            picker.getView().focus();
        }, 1);
    },
    setInitValue: function (ids, texts) {
        var me = this, record;
        var attr_ids = ids.split(",");
        var attr_texts = texts.split(",");
        if (attr_ids.length == attr_texts.length) {
            this.clearChecked(); //如果是选择框取消所有选择
            for (var i = 0; i < attr_ids.length; i++) {
                var id = attr_ids[i];
                var text = attr_texts[i];
                var pnode = me.store.getNodeById(id); //没有找到这个结点
                if (Ext.isEmpty(pnode)) {
                    pnode = me.store.getRootNode();
                    var newnode = {'id': id, 'text': text, leaf: true}
                    if (me.multiSelect) newnode.checked = true;
                    pnode.appendChild(newnode); //添加子节点  
                }
                else if (pnode.get('checked') != null) {
                    pnode.set("checked", true);
                }
                else if (me.multiSelect) {
                    pnode.set("checked", true);
                }
            }
            if (attr_ids.length == 1) me.setValue(ids);
            else {
                if (me.multiSelect) this.setCheckedValue();
                else {
                    me.value = ids;
                    me.setRawValue(texts);
                }
            }
        }
    },
    setValue: function (value) {
        var me = this, record;
        me.value = value;
        if (!me.loadOriginalValue && !me.originalValue) {
            me.originalValue = value;
        }
        if (me.queryMode == "remote" && !me.store.loaded) {
            var data_key = me.store.proxy.extraParams.data_key;
            if (data_key && data_key.indexOf("ds.") == 0)
                me.ajaxUrl = "system/ds/text";
            if (value && me.ajaxUrl && data_key) {
                me.store.loaded = true;
                setTimeout(function () {
                    Ext.Ajax.request({
                        method: "POST", url: me.ajaxUrl,
                        params: {data_key: data_key, ids: value},
                        success: function (response, options) {
                            var txt = Ext.JSON.decode(response.responseText);
                            if (txt.success) {
                                me.value = value;
                                if (!me.loadOriginalValue && !me.originalValue) {
                                    me.originalValue = value;
                                }
                                if (!me.getRawValue()) {
                                    me.setValue(value);
                                    me.setRawValue(txt.texts);
                                }
                                me.store.loaded = false;
                            }
                        }
                    });
                }, 200);
                return me;
            }
        }
        //record = value ? me.store.getNodeById(value) : me.store.getRootNode();
        if (value) {
            if (me.inputEl) me.inputEl.removeCls("x-form-empty-field");
            if (me.selectMode == 'check') {  //只能选复选框
                var attr_ids = value.split(",");
                var attr_texts = [];
                for (var i = 0; i < attr_ids.length; i++) {
                    var id = attr_ids[i];
                    record = me.store.getNodeById(id);
                    attr_texts[i] = record ? record.get(me.displayField) : '';
                }
                me.setRawValue(attr_texts.join(","));
            } else {
                record = me.store.getNodeById(value);
                me.setRawValue(record ? record.get(me.displayField) : '');
            }
        } else {
            me.setRawValue('');
        }
        return me;
    },
    reset: function () {
        var me = this;
        me.setValue(me.originalValue ? me.originalValue : "");
    },
    getSubmitValue: function () {
        return this.value;
    },
    getValue: function () {
        return this.value;
    },
    clearChecked: function () {
        var checkNodes = this.getPicker().getChecked();
        for (var i = 0; i < checkNodes.length; i++) {
            checkNodes[i].set("checked", false);
        }
    },
    clearValue: function () {
        var me = this;
        me.value = "";
        me.setRawValue("");
    },
    onLoad: function () {
        var value = this.value;
        this.originalValue = value;
        if (value) {
            this.setValue(value);
            //this.setRawValue(this.value);
        }
    },
    onUpdate: function (store, rec, type, modifiedFieldNames) {
        var display = this.displayField;

        if (type === 'edit' && modifiedFieldNames && Ext.Array.contains(modifiedFieldNames, display) && this.value === rec.getId()) {
            this.setRawValue(rec.get(display));
        }
    }
});

//Ext.apply(Ext.data.Model.prototype, {
//    disabled: true,
//    getNodeUI: function (treeid) {
//        var me = this;
//        var checkbox;
//        var nodeui;
//        var tree = Ext.getCmp(treeid);
//        if (tree) {
//            nodeui = tree.getView().getNode(me).firstChild.firstChild;
//        } else {
//            Ext.ComponentManager.all.each(function (key, value) {
//                var Type = value.getXType();
//                if (Type == "treepanel" && value.getView().getNode(me)) {
//                    nodeui = value.getView().getNode(me).firstChild.firstChild;
//                }
//            });
//        }
//        checkbox = nodeui.getElementsByTagName('input')[0];
//        return [nodeui, checkbox];
//    },
//    getCheckbox: function (treeid) {
//        var checkbox = this.getNodeUI(treeid)[1];
//        //        for (var i = 0; i < this.getNodeUI(treeid).length; i++) {
//        //            alert(this.getNodeUI(treeid)[i].innerHTML); 
//        //        }
//        return {
//            disabled: checkbox ? checkbox.disabled : false,
//            disable: function () {
//                if (checkbox) {
//                    checkbox.disabled = true;
//                    checkbox.className = checkbox.className.replace('treenode-disable', '') + ' treenode-disable';
//                }
//            },
//            enable: function () {
//                if (checkbox) {
//                    checkbox.disabled = false;
//                    checkbox.className = checkbox.className.replace('treenode-disable', '');
//                }
//            }
//        }
//    },
//    disable: function (treeid) {
//        this.disabled = true;
//        var nodeui = this.getNodeUI(treeid)[0];
//        nodeui.className = nodeui.className.replace('treenode-disable', '') + ' treenode-disable';
//        //alert(nodeui.innerHTML); 
//        var checkbox = this.getNodeUI(treeid)[1];
//        if (checkbox) {
//            checkbox.disabled = true;
//            checkbox.className = checkbox.className.replace('treenode-disable', '') + ' treenode-disable';
//        }
//    },
//    enable: function (treeid) {
//        this.disabled = false;
//        var nodeui = this.getNodeUI(treeid)[0];
//        nodeui.className = nodeui.className.replace('treenode-disable', '');

//        var checkbox = this.getNodeUI(treeid)[1];
//        if (checkbox) {
//            checkbox.disabled = false;
//            checkbox.className = checkbox.className.replace('treenode-disable', '');
//        }
//    }
//});
