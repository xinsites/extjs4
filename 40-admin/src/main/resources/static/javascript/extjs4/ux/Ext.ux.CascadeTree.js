/**
* 插件级联选择树，已被treepicker替代
*/
Ext.define('Ext.ux.CascadeTree', {
    extend: 'Ext.tree.Panel',
    xtype: 'cascadetree',
    singleClickExpand: false, //是否单击展开，默认双击展开
    multiSelect: true,   //默认多选
    clickType: 'all',    //单击选择方式,1.all：单击任意地方可作选择;2.checked：单击复选框才能选择;3.leaf：单击复选框或者叶子结点任意地方才能选择;4.item:配合cascade使用，单击checked可以选择，不级联,单击item级联
    queryMode: 'local',  //远程数据：remote[store不能为空]；本地数据：local[root不能为空]
    cascade: '',         //级联方式:1.child子级联;2.parent,父级联,3.both全部级联, 4.空不级联选择
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
        //        me.store.on({
        //            'load': function (view, record) {
        //            }
        //        });
        me.on({
            'render': me.onTreeRender,
            //'checkchange': me.onCheckChange,
            'itemclick': me.onItemClick
        });
    },
    onTreeRender: function (tree, eOpts) {
        var me = this, store = tree.store;
        if (me.queryMode == "local") {
            setTimeout(function () {
                tree.initDisabled(tree.getView(), tree.getRootNode());
            }, 50);
        }
        store.on("expand", function (record) {
            if (record.get('checked') == true) {
                me.onCheckChange(record, true);
            }
            // if (!record.get("init_disabled")) {
            //     record.set("init_disabled", true);
            //     me.initDisabled(tree.getView(), record);
            // }
            me.initDisabled(tree.getView(), record);
        });
        store.on("collapse", function (record) {
            me.initDisabled(tree.getView(), record);
        });
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
                        var pNode = record.parentNode;
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
    onItemClick: function (tree, record, node, rowIndex, e) {
        var me = this, disabled;
        disabled = me.isDisabled(record);
        if (!disabled) { //可用
            if (me.clickType != "disabled") {
                var el = e.getTarget("input");  //当前点击的是checkbox
                var click_checkbox = false;
                if (el) click_checkbox = el.type == "button";
                if (!click_checkbox && me.clickType == "checked") {
                    //单击复选框才能选择，本次不是单击的复选框
                }
                else if (me.clickType == "leaf" && !click_checkbox && !record.get('leaf')) {
                    //单击复选框或者叶子结点任意地方才能选择
                }
                else if (me.clickType == "item" && click_checkbox && me.cascade != '' && me.multiSelect) {
                    //配合cascade使用，单击checked可以选择，不级联,单击item级联
                    if (record.get('checked') != null) {
                        record.set("checked", !record.get('checked'));
                    }
                }
                else {
                    if (me.multiSelect) {
                        if (record.get('checked') != null) {
                            record.set("checked", !record.get('checked'));
                            if (me.cascade != '') //需要级联操作
                                me.onCheckChange(record, record.get('checked'));
                        }
                    }
                    else {  //单选
                        var checked = !record.get('checked') ? true : false;
                        me.clearChecked();
                        record.set("checked", checked);
                    }
                }
            }
        }
        if (me.singleClickExpand && !record.get('leaf')) {  //单击打开
            var expand = record.get('expanded')
            if (record.isExpanded()) {
                if (me.cascade == '' && record.get('checked') == null)  //不级联选择并且没有复选框，可以收缩
                    tree.collapse(record);
            }
            else {
                tree.expand(record, true);
            }
        }
    },
    isDisabled: function (record) {
        var disabled = record.raw.disabled;
        return (disabled == true || disabled == "1" || disabled == "True" || disabled == "disabled"); //不可用
    },
    setDisabled: function (treeview, record, disabled) {  //设置结点是否可用
        var checkbox, nodeui;
        if (treeview) {
            try {
                record.set("disabled", disabled);
                nodeui = treeview.getNode(record).firstChild.firstChild;
                checkbox = nodeui.getElementsByTagName('input')[0];
                nodeui.className = nodeui.className.replace(' treenode-disable', '') + (disabled ? ' treenode-disable' : "");
                if (checkbox) {
                    checkbox.disabled = disabled;
                    checkbox.className = checkbox.className.replace(' treenode-disable', '') + (disabled ? ' treenode-disable' : "");
                }
            } catch (e) { }
        }
    },
    initDisabled: function (treeview, rootNode) {  //循环遍历结点，设置可用状态
        var childNodes = rootNode.childNodes, me = this;
        var disabled = me.isDisabled(rootNode);
        if (disabled) me.setDisabled(treeview, rootNode, true);
        for (var i = 0; i < childNodes.length; i++) {
            disabled = me.isDisabled(childNodes[i]);
            if (disabled) me.setDisabled(treeview, childNodes[i], true);
            me.initDisabled(treeview, childNodes[i]);
        }
    },
    setChecked: function (ids, checked) {

    },
    onExpand: function () {

    },
    setValue: function (value) {
        var me = this, record;
        me.value = value;
        return me;
    },
    getSubmitValue: function () {
        return this.value;
    },
    getValue: function () {
        return this.value;
    },
    clearChecked: function () {
        var checkNodes = this.getChecked();
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
        if (value) {
            this.setValue(value);
        }
    }
});
