/*
* @Author: zhangxiaxin
* @Date: 2019-10-16
* @Mark: Ext各对象方法扩展(验证、自定义验证)
*/

Ext.apply(Ext.form.VTypes, {
    daterange: function (val, field) {
        //        if(val !="") val=val.substr(0,10);
        var date = field.parseDate(val);
        if (!date) {
            return false;
        }
        if (field.startDateField) {
            var start = Ext.getCmp(field.startDateField);
            if (!start.maxValue || (date.getTime() != start.maxValue.getTime())) {
                start.setMaxValue(date);
                start.validate();
            }
        }
        else if (field.endDateField) {
            var end = Ext.getCmp(field.endDateField);
            if (!end.minValue || (date.getTime() != end.minValue.getTime())) {
                end.setMinValue(date);
                end.validate();
            }
        }
        return true;
    },
    manNumConfirm2: function (val, field) {      //val指这里的文本框值，field指这个文本框组件
        if (field.confirmTo) {  //confirmTo是我们自定义的配置参数，一般用来保存另外的组件的id值
            var preManNum = Ext.get(field.confirmTo).getValue().trim();  //取得confirmTo的那个value的值
            val = val.trim();
            if (preManNum == '' || preManNum == '只能填写正整数') return true;
            if (val == '' || preManNum == '只能填写正整数') return true;
            return strToFloat(val) >= strToFloat(preManNum);
        }
        return true;
    },
    manNumConfirm: function (val, field) {      //val指这里的文本框值，field指这个文本框组件
        if (field.confirmTo) {  //confirmTo是我们自定义的配置参数，一般用来保存另外的组件的id值
            var preManNum = Ext.get(field.confirmTo).getValue().trim();  //取得confirmTo的那个value的值
            val = val.trim();
            if (preManNum == '' || preManNum == '只能填写正整数') return true;
            if (val == '' || preManNum == '只能填写正整数') return true;
            return strToInt(val) >= strToInt(preManNum);
        }
        return true;
    },
    dataConfirm2: function (val, field) {      //val指这里的文本框值，field指这个文本框组件
        if (field.confirmTo) {  //confirmTo是我们自定义的配置参数，一般用来保存另外的组件的id值
            var preDate = Ext.get(field.confirmTo).getValue().trim();  //取得confirmTo的那个value的值
            val = val.trim();
            if (preDate == '' || preDate == '<无>') return true;
            if (val == '' || val == '<无>') return true;
            return (val <= preDate);
        }
        return true;
    },
    dataConfirm: function (val, field) {      //val指这里的文本框值，field指这个文本框组件
        if (field.confirmTo) {  //confirmTo是我们自定义的配置参数，一般用来保存另外的组件的id值
            var preDate = Ext.get(field.confirmTo).getValue().trim();  //取得confirmTo的那个value的值
            val = val.trim();
            if (preDate == '' || preDate == '<无>') return true;
            if (val == '' || val == '<无>') return true;
            return (val >= preDate);
        }
        return true;
    },
    dataToday: function (val, field) {      //大于等于今天的日期校验
        if (val != "") {
            return val >= $("#H_SysDate").val();
        }
        return true;
    },
    integer: function (val, field) {      //正整数校验
        if (val != "") {
            return val > 0;
        }
        return true;
    }
});

//列表计量单位
Ext.apply(Ext.util.Format, {
    monetary: function (val, metaData) { //金额字段，金额后有单位
        if (val) {
            if (Ext.monetaryUnit && Ext.monetaryUnit != 1)
                val = val / Ext.monetaryUnit;
            if (!Ext.monetaryText) Ext.monetaryText = "";
            // 正数用蓝色显示，负数用红色显示  
            metaData.style = 'color:' + (val > 0 ? 'blue' : 'red') + ';float:right;';
            return Ext.util.Format.number(val, '0,000.00') + Ext.monetaryText;
            //            return '<span style="color:' + (val > 0 ? 'blue' : 'red')
            //                                            + ';">' + Ext.util.Format.number(val, '0,000.00')
            //                                            + Ext.monetary + '</span>';
        }
        else
            return ''; // 如果为0,则不显示  
    },
    chMoney: function (val, metaData) {  //金额字段，金额后无单位
        if (val) {
            if (Ext.monetaryUnit && Ext.monetaryUnit != 1)
                val = val / Ext.monetaryUnit;
            if (!Ext.monetaryText) Ext.monetaryText = "";
            // 正数用蓝色显示，负数用红色显示  
            metaData.style = 'color:' + (val > 0 ? 'blue' : 'red') + ';float:right;';
            return Ext.util.Format.number(val, '0,000.00');
        }
        else
            return ''; // 如果为0,则不显示  
    },
    cmbRenderer: function (val, metaData) {
        if (val) {
            metaData.style = 'color:' + (val > 0 ? 'blue' : 'red');
            return Ext.util.Format.number(val, '¥0,000.00');
        }
        else
            return ''; // 如果为0,则不显示  
    },
    // 日期  
    datetimeRenderer: function (val) {
        return '<span style="color:#a40;">'
            + Ext.util.Format.date(val, 'Y-m-d') + '</span>';
    },
    // 浮点型变量  
    floatRenderer: function (val, rd, model, row, col, store, gridview) {
        return '<span style="color:' + (val > 0 ? 'blue' : 'red')
            + ';float:right;">' + (val == 0 ? '' : val) + '</span>';
    },
    // 整型变量  
    intRenderer: function (val, rd, model, row, col, store, gridview) {
        return '<span style="color:' + (val > 0 ? 'blue' : 'red')
            + ';float:right;">' + (val == 0 ? '' : val) + '</span>';
    },
    // 百分比  
    percentRenderer: function (v, rd, model) {
        v = v * 100;
        var v1 = v > 100 ? 100 : v;
        v1 = v1 < 0 ? 0 : v1;
        var v2 = parseInt(v1 * 2.55).toString(16);
        if (v2.length == 1)
            v2 = '0' + v2;
        v = Ext.util.Format.number(v, '0');
        return Ext.String
            .format(
                '<div>'
                + '<div style="float:left;border:1px solid #008000;height:15px;width:100%;">'
                + '<div style="float:left;text-align:center;width:100%;color:blue;">{0}%</div>'
                + '<div style="background: #FAB2{2};width:{1}%;height:13px;"></div>'
                + '</div></div>', v, v1, v2);
    },
    // 对模块的namefields字段加粗显示  
    nameFieldRenderer: function (val, rd, model, row, col, store, gridview) {
        return ('<strong>' + val + '</strong>');
    }
});

//树结点不可用计算方法
Ext.apply(Ext.tree.Panel.prototype, {
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
            } catch (e) {
            }
        }
    },
    initDisabled: function (treeview, rootNode) {  //循环遍历结点，设置可用状态
        var childNodes = rootNode.childNodes, me = this;
        var disabled = rootNode.raw.disabled;
        if (disabled == true || disabled == "1" || disabled == "True")
            me.setDisabled(treeview, rootNode, true);
        for (var i = 0; i < childNodes.length; i++) {
            disabled = childNodes[i].raw.disabled;
            if (disabled == true || disabled == "1" || disabled == "True")
                me.setDisabled(treeview, childNodes[i], true);
            me.initDisabled(treeview, childNodes[i]);
        }
    }
});

//使树结点可以拖动到另一个树结点下做为该结点的子结点
Ext.override(Ext.tree.ViewDropZone, {
    getPosition: function (e, node) {
        var view = this.view,
            record = view.getRecord(node),
            y = e.getPageY(),
            noAppend = record.isLeaf(),
            noBelow = false,
            region = Ext.fly(node).getRegion(),
            fragment;

        if (record.isRoot()) {
            return 'append';
        }

        if (this.appendOnly) {
            return noAppend ? false : 'append';
        }
        if (typeof view.plugins.allowLeafInserts == 'boolean') this.allowLeafInserts = view.plugins.allowLeafInserts;
        if (!this.allowParentInsert) {
            noBelow = this.allowLeafInserts || (record.hasChildNodes() && record.isExpanded());
        }

        fragment = (region.bottom - region.top) / (noAppend ? 2 : 3);
        if (y >= region.top && y < (region.top + fragment)) {
            return 'before';
        }
        else if (!noBelow && (noAppend || (y >= (region.bottom - fragment) && y <= region.bottom))) {
            return 'after';
        }
        else {
            return 'append';
        }
    },
    handleNodeDrop: function (data, targetNode, position) {
        var me = this,
            view = me.view,
            parentNode = targetNode.parentNode,
            store = view.getStore(),
            recordDomNodes = [],
            records, i, len,
            insertionMethod, argList,
            needTargetExpand,
            transferData,
            processDrop;
        if (data.copy) {
            records = data.records;
            data.records = [];
            for (i = 0, len = records.length; i < len; i++) {
                data.records.push(Ext.apply({}, records[i].data));
            }
        }
        me.cancelExpand();
        if (typeof view.plugins.allowLeafInserts == 'boolean') this.allowLeafInserts = view.plugins.allowLeafInserts;
        if (position == 'before') {
            insertionMethod = parentNode.insertBefore;
            argList = [null, targetNode];
            targetNode = parentNode;
        }
        else if (position == 'after') {
            if (targetNode.nextSibling) {
                insertionMethod = parentNode.insertBefore;
                argList = [null, targetNode.nextSibling];
            }
            else {
                insertionMethod = parentNode.appendChild;
                argList = [null];
            }
            targetNode = parentNode;
        }
        else {
            if (this.allowLeafInserts) {
                if (targetNode.get('leaf')) {
                    targetNode.set('leaf', false);
                    targetNode.set('expanded', true);
                }
            }
            if (!targetNode.isExpanded()) {
                needTargetExpand = true;
            }
            insertionMethod = targetNode.appendChild;
            argList = [null];
        }

        transferData = function () {
            var node;
            for (i = 0, len = data.records.length; i < len; i++) {
                argList[0] = data.records[i];
                node = insertionMethod.apply(targetNode, argList);

                if (Ext.enableFx && me.dropHighlight) {
                    recordDomNodes.push(view.getNode(node));
                }
            }
            if (Ext.enableFx && me.dropHighlight) {
                Ext.Array.forEach(recordDomNodes, function (n) {
                    if (n) {
                        Ext.fly(n.firstChild ? n.firstChild : n).highlight(me.dropHighlightColor);
                    }
                });
            }
        };
        if (needTargetExpand) {
            targetNode.expand(false, transferData);
        }
        else if (targetNode.isLoading()) {
            targetNode.on({
                expand: transferData,
                delay: 1,
                single: true
            });
        }
        else {
            transferData();
        }
    }
});

Ext.override(Ext.tree.plugin.TreeViewDragDrop, {
    allowLeafInserts: true,
    onViewRender: function (view) {
        var me = this;
        if (me.enableDrag) {
            if (me.containerScroll) {
                scrollEl = view.getEl();
            }
            me.dragZone = Ext.create('Ext.tree.ViewDragZone', {
                view: view,
                allowLeafInserts: me.allowLeafInserts,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText,
                displayField: me.displayField,
                repairHighlightColor: me.nodeHighlightColor,
                repairHighlight: me.nodeHighlightOnRepair,
                scrollEl: scrollEl
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('Ext.tree.ViewDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                allowContainerDrops: me.allowContainerDrops,
                appendOnly: me.appendOnly,
                allowLeafInserts: me.allowLeafInserts,
                allowParentInserts: me.allowParentInserts,
                expandDelay: me.expandDelay,
                dropHighlightColor: me.nodeHighlightColor,
                dropHighlight: me.nodeHighlightOnDrop
            });
        }
    }
});

//用RowEditing编辑行时，保存按钮变灰
//填写框修改后无效
Ext.override(Ext.grid.RowEditor, {
    addFieldsForColumn: function (column, initial) {
        var me = this, i, length, field;
        if (Ext.isArray(column)) {
            for (i = 0, length = column.length; i < length; i++) {
                me.addFieldsForColumn(column[i], initial);
            }
            return;
        }
        if (column.getEditor) {
            field = column.getEditor(null, {
                xtype: 'displayfield',
                getModelData: function () {
                    return null;
                }
            });
            if (column.align === 'right') {
                field.fieldStyle = 'text-align:right';
            }
            if (column.xtype === 'actioncolumn') {
                field.fieldCls += ' ' + Ext.baseCSSPrefix + 'form-action-col-field';
            }
            if (me.isVisible() && me.context) {
                if (field.is('displayfield')) {
                    me.renderColumnData(field, me.context.record, column);
                } else {
                    field.suspendEvents();
                    field.setValue(me.context.record.get(column.dataIndex));
                    field.resumeEvents();
                }
            }
            if (column.hidden) {
                me.onColumnHide(column);
            } else if (column.rendered && !initial) {
                me.onColumnShow(column);
            }
            // -- start edit
            me.mon(field, 'change', me.onFieldChange, me);
            // -- end edit
        }
    }
});