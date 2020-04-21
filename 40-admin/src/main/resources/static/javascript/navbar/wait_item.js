Ext.define('javascript.navbar.wait_item', {
    extend: ''
});

//待办任务列表
function createPanel_WaitItem(treenode) {
    Ext.define('model_navbar_handle', {
        extend: 'Ext.data.Model',
        idProperty: 'alloc_id',
        fields: [{"name": "alloc_id", "type": "int", "text": "主键"},
            {"name": "status_id", "type": "int", "text": "流程状态Id"},
            {"name": "idleaf", "type": "int", "text": "信息Id"},
            {"name": "item_id", "type": "int", "text": "栏目Id"},
            {"name": "title", "type": "string", "text": "任务标题"},
            {"name": "task_no", "type": "string", "text": "流程任务号"},
            {"name": "task_type", "type": "string", "text": "任务类型"},
            {"name": "user_id", "type": "int", "text": "申请人"},
            {"name": "task_userid", "type": "int", "text": "审批人"},
            {"name": "create_time", "type": "date", "text": "申请日期"}]
    });

    var itemid = treenode.raw.id;
    var pageSize = getGridPageSize(itemid);
    var store = Ext.create('Ext.data.Store', {
        remoteSort: true, autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "flow/myitem/waitgrid",
            extraParams: {item_id: itemid},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'alloc_id',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            'load': function (store, records) {
                if (store.getCount() == 0 && store.currentPage > 1) {
                    store.currentPage = 1;
                    store.load();
                }
            }
        },
        sorters: [{
            property: 'create_time',
            direction: 'desc'
        }],
        pageSize: pageSize,
        model: 'model_navbar_handle'
    });
    var columns = [new Ext.grid.RowNumberer({width: 40, tdCls: 'blue'}),
        {
            text: '任务名称',
            dataIndex: 'task_no',
            width: 150,
            fixed: false,
            align: 'left',
            sortable: false,
            renderer: function (value, mata, record) {
                if (userinfo.userId == record.get("task_userid"))
                    return "【" + record.get("task_type") + "申请_" + value + "】" + record.get("title");
                else
                    return "<span style='color:blue;'>【代理】</span>【" + record.get("task_type") + "申请_" + value + "】" + record.get("title");
            }
        }, {
            text: '申请日期',
            dataIndex: 'create_time',
            width: 160,
            fixed: true,
            align: 'left',
            sortable: true,
            renderer: Ext.util.Format.dateRenderer('Y-m-d H:i')
        }];

    var add_type = "first", first_index = 1;
    var grid = Ext.create('Ext.grid.Panel', {
        itemId: "main_grid",
        store: store,
        multiSelect: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            preserveScrollOnRefresh: true,
            loadingText: "数据加载中，请稍等..."
        },
        border: false,
        listeners: {
            'itemdblclick': function (gridview, rec, item, index) {
                onClickFlowGrid(itemid, rec.get("item_id"), rec.get("idleaf"), rec.get("status_id"), "", rec.get("alloc_id"));
            }
        },
        columns: columns,
        dockedItems: [{
            xtype: 'toolbar', dock: 'top',
            items: ['-', '<b>搜索:</b>',
                {
                    xtype: 'textfield', width: 220, emptyText: "标题关键字检索，请按enter键...",
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                                designSearchByField(store, 'simple_task_status', "title", field.getValue().replace(/%/g, '/%').replace(/_/g, '/_'));
                            }
                        }
                    }
                }, {
                    xtype: 'splitbutton', text: '高级搜索',
                    itemId: "btn_complexsearch",
                    iconCls: "icon_search",
                    handler: function (button, e) {
                        setTimeout(function () {
                            button.showMenu();
                        }, 70);
                    },
                    menu: {
                        items: [
                            {
                                text: '今天',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'simple_task_status', 0);
                                    }
                                }
                            },
                            {
                                text: '最近三天',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'simple_task_status', 2);
                                    }
                                }
                            },
                            {
                                text: '最近一周',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'simple_task_status', 6);
                                    }
                                }
                            },
                            {
                                text: '最近一月',
                                checked: false,
                                group: 'search-group',
                                scope: this,
                                listeners: {
                                    click: function () {
                                        gridSearchByDate(store, 'simple_task_status', 30);
                                    }
                                }
                            },
                            {
                                text: '全部', checked: false, group: 'search-group', scope: this, listeners: {
                                    click: function () {
                                        store.proxy.extraParams.searchdata = "";
                                        store.reload();
                                    }
                                }
                            }]
                    }
                }, "-"]
        }, {
            xtype: 'pagingtoolbar',
            dock: 'bottom', store: store,   // GridPanel使用相同的数据源
            displayInfo: true, itemId: "pagingtoolbar",
            plugins: Ext.create('Ext.ux.PagesizeSlider', {cookieName: "grid_" + itemid})
        }]
    });

    return Ext.create('Ext.panel.Panel', {
        layout: "fit", border: false,
        items: [grid]
    });
}