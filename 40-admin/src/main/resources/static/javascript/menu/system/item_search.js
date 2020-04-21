Ext.define('javascript.menu.system.item_search', {
    extend: ''
});

//栏目高级查询窗口
function openItemSearchWin(treenode, grid) {
    var treeSearchWin = grid.treeSearchWin;
    if (!treeSearchWin) {
        var tree_store = Ext.create('Ext.data.TreeStore', {
            nodeParam: 'node', autoLoad: false,
            proxy: {
                type: 'ajax', url: path_url.system.item.tree,
                reader: {
                    totalProperty: 'totalProperty',
                    type: 'json', id: "id", root: 'root'
                },
                extraParams: {isCheck: true, where: "isRecycle=1"}
            },
            root: {text: '根节点', id: 0, expanded: true}
        });
        var btn = grid.queryById("btn_complexsearch");
        var form = Ext.create('Ext.form.Panel', {
            frame: false,
            bodyPadding: "20 20 10 20",
            fieldDefaults: {
                labelAlign: 'left',
                msgTarget: 'side',
                labelWidth: 70
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                itemId: "item_name",
                xtype: 'textfield',
                name: 'item_name',
                maxLength: 25,
                fieldLabel: '栏目名称'
            }, {
                xtype: 'treepicker',
                name: 'item_pid',
                itemId: 'item_pid',
                fieldLabel: '父栏目',
                multiSelect: false, editable: false,
                queryMode: 'remote', selectMode: 'parent',
                store: getSysDataTreeStore('ds.sys.item', false)
            }, {
                xtype: 'textfield',
                name: 'per_value',
                itemId: 'per_value',
                fieldLabel: '列表权限值',
                maxLength: 50
            }, {
                itemId: 'isdataper', name: 'isdataper',
                xtype: 'radiogroup', columns: [100, 100],
                fieldLabel: '  ', labelSeparator: '',
                items: [{"boxLabel": "有数据权限", "name": "isdataper", "inputValue": "1"}, {
                    "boxLabel": "无数据权限", "name": "isdataper", "inputValue": "0"
                }]
            }, {
                itemId: 'isfun', name: 'isfun',
                xtype: 'radiogroup', columns: [100, 100],
                fieldLabel: '  ', labelSeparator: '',
                items: [{"boxLabel": "有栏目功能", "name": "isfun", "inputValue": "1"}, {
                    "boxLabel": "无栏目功能", "name": "isfun", "inputValue": "0"
                }]
            }],
            listeners: {
                afterRender: function (thisForm, options) {
                    this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {
                        enter: search,
                        scope: this
                    });
                }
            }
        });

        function search() {
            var items = [];
            items[items.length] = {item_id: "item_name", operator: "like"};
            items[items.length] = "item_pid";
            items[items.length] = {item_id: "per_value", operator: "like"};
            items[items.length] = "isdataper";
            items[items.length] = "isfun";
            var datas = designSearchByForm(form, items);
            if (is_not_used)
                datas[datas.length] = getSearchByVal("is_not_used", "like", is_not_used, "checkbox", "int");

            searchSingleTableReLoad(grid, datas, "key_menu");
            treeSearchWin.close();
        }

        grid.store.on("load", function (treeview, record) {
            setTimeout(function () {
                if (grid.store.proxy.extraParams.searchdata) grid.expandAll();
            }, 100);
        });
        var is_not_used = "";
        treeSearchWin = Ext.create('widget.window', {
            title: '栏目查询',
            animateTarget: btn.getId(),
            items: [form],
            width: 420,
            height: 270,
            resizable: false,
            closable: true,
            closeAction: 'hide',
            plain: false,
            modal: true,
            layout: 'fit',
            buttonAlign: "right",
            buttons: [{
                xtype: 'checkbox',
                boxLabel: '只含无用栏目',
                labelWidth: 5, fieldLabel: ' ',
                labelSeparator: "", labelAlign: "right",
                listeners: {
                    'change': function (item, checked) {
                        is_not_used = checked ? "true" : "";
                    }
                }
            }, "->", {
                text: '查询', minWidth: 70,
                listeners: {
                    click: search
                }
            }, {
                text: '清空', minWidth: 70,
                listeners: {
                    click: function () {
                        form.getForm().reset();
                    }
                }
            }, {
                text: '关闭', minWidth: 70,
                handler: function () {
                    treeSearchWin.close();
                }
            }]
        });
    }
    grid.treeSearchWin = treeSearchWin;
    treeSearchWin.show();
}



