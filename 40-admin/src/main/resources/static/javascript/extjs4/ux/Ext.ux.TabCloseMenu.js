// //关闭TabPanel标签
// var TabMenu = null;
// Ext.ux.TabCloseMenu = function () {
//     var tabs, ctxItem;
//     this.init = function (tp) {
//         tabs = tp;
//         tabs.on('contextmenu', onContextMenu);
//     }
//     function onContextMenu(ts, item, me) {
//         if (TabMenu == null) {
//             TabMenu = new Ext.menu.Menu([{
//                 id: tabs.id + '-close',
//                 text: '关闭当前标签',
//                 iconCls: "icon_close_tab",
//                 handler: function () {
//                     tabs.remove(ctxItem);
//                 }
//             }, {
//                 id: tabs.id + '-close-others',
//                 text: '除此之外全部关闭',
//                 iconCls: "icon_close_all",
//                 handler: function () {
//                     tabs.items.each(function (item) {
//                         if (item.closable && item != ctxItem) {
//                             tabs.remove(item);
//                         }
//                     });
//                 }
//             }]);
//         }
//         ctxItem = item;
//         var items = TabMenu.items;
//         items.get(tabs.id + '-close').setDisabled(!item.closable);
//         var disableOthers = true;
//         if (tabs.items.length > 1) {
//             if (item.id == "Div1") disableOthers = false;
//             else {
//                 tabs.items.each(function () {
//                     if (this != item && this.closable) {
//                         disableOthers = false;
//                         return false;
//                     }
//                 });
//             }
//             items.get(tabs.id + '-close-others').setDisabled(disableOthers);
//             TabMenu.showAt(me.getXY());
//         }
//     }
// };

Ext.define('Ext.ux.TabCloseMenu', {
    alias: 'plugin.tabclosemenu',
    mixins: {observable: 'Ext.util.Observable'},
    closeTabText: '关闭标签',
    showCloseOthers: true,
    closeOthersTabsText: '关闭其他标签',
    showCloseAll: true,
    closeAllTabsText: '关闭全部标签',
    showFixedTab: true,
    RefreshTabPage: '刷新面板页面',  //面板页面的grid,itemId值main_grid、main_grid1、main_grid2...
    FixedTabText: '固定标签到面板',  //面板有ColId值就可以使用
    CancelFixedTabText: '取消固定标签',
    extraItemsHead: null,
    extraItemsTail: null,
    item: null,
    constructor: function (config) {
        this.addEvents(
            'aftermenu',
            'beforemenu');
        this.mixins.observable.constructor.call(this, config);
    },
    init: function (tabpanel) {
        this.tabPanel = tabpanel;
        this.tabBar = tabpanel.down("tabbar");

        this.mon(this.tabPanel, {
            scope: this,
            afterlayout: this.onAfterLayout,
            single: true
        });
    },
    onAfterLayout: function () {
        this.mon(this.tabBar.el, {
            scope: this,
            contextmenu: this.onContextMenu,
            delegate: '.x-tab'
        });
    },
    onBeforeDestroy: function () {
        Ext.destroy(this.menu);
        this.callParent(arguments);
    },
    onContextMenu: function (event, target) {
        var me = this,
            disableAll = true,
            disableOthers = true,
            tab = me.tabBar.getChildByElement(target),
            index = me.tabBar.items.indexOf(tab);
        me.item = me.tabPanel.getComponent(index);
        var menu = me.createMenu();
        //alert(this.item.title);
        menu.child('*[text="' + me.closeTabText + '"]').setDisabled(!me.item.closable);

        var tab_fixed = menu.queryById("tab_fixed");
        if (tab_fixed) {
            if (!me.item.closable) {
                tab_fixed.setDisabled(true);
            } else {
                tab_fixed.setDisabled(!me.item.item_id);
            }
            if (!me.item.closable && me.item.item_id > 0) {
                tab_fixed.setDisabled(false);
                tab_fixed.setText(me.CancelFixedTabText);
            }
            else tab_fixed.setText(me.FixedTabText);
        }

        if (me.showCloseAll || me.showCloseOthers) {
            me.tabPanel.items.each(function (item) {
                if (item.closable) {
                    disableAll = false;
                    if (item != me.item) {
                        disableOthers = false;
                        return false;
                    }
                }
                return true;
            });

            if (me.showCloseAll) {
                menu.child('*[text="' + me.closeAllTabsText + '"]').setDisabled(disableAll);
            }

            if (me.showCloseOthers) {
                menu.child('*[text="' + me.closeOthersTabsText + '"]').setDisabled(disableOthers);
            }
        }

        event.preventDefault();
        me.fireEvent('beforemenu', menu, me.item, me);

        menu.showAt(event.getXY());
    },
    createMenu: function () {
        var me = this;
        if (!me.menu) {
            var items = [{
                text: me.closeTabText,
                scope: me,
                handler: me.onClose
            }];

            if (me.showCloseAll || me.showCloseOthers) {
                items.push('-');
            }

            if (me.showCloseOthers) {
                items.push({
                    text: me.closeOthersTabsText,
                    scope: me,
                    handler: me.onCloseOthers
                });
            }

            if (me.showCloseAll) {
                items.push({
                    text: me.closeAllTabsText,
                    scope: me,
                    handler: me.onCloseAll
                });
            }
            items.push('-');
            items.push({
                itemId: "tab_refresh",
                text: me.RefreshTabPage,
                scope: me,
                handler: me.onRefresh
            });
            if (me.showFixedTab) {
                items.push('-');
                items.push({
                    itemId: "tab_fixed",
                    text: me.FixedTabText,
                    scope: me,
                    handler: me.onFixed
                });
            }

            if (me.extraItemsHead) {
                items = me.extraItemsHead.concat(items);
            }

            if (me.extraItemsTail) {
                items = items.concat(me.extraItemsTail);
            }

            me.menu = Ext.create('Ext.menu.Menu', {
                items: items,
                listeners: {
                    hide: me.onHideMenu,
                    scope: me
                }
            });
        }

        return me.menu;
    },
    onHideMenu: function () {
        var me = this;
        setTimeout(function () {
            me.item = null;
            me.fireEvent('aftermenu', me.menu, me);
        }, 100);
        //        var task = new Ext.util.DelayedTask(function () {
        //            alert(me.item.title);
        //        }).delay(100);
        //        task.delay(100);
    },
    onClose: function () {
        this.tabPanel.remove(this.item);
    },
    onCloseOthers: function () {
        this.doClose(true);
    },
    onCloseAll: function () {
        this.doClose(false);
    },
    doClose: function (excludeActive) {
        var items = [];

        this.tabPanel.items.each(function (item) {
            if (item.closable) {
                if (!excludeActive || item != this.item) {
                    items.push(item);
                }
            }
        }, this);

        Ext.each(items, function (item) {
            this.tabPanel.remove(item);
        }, this);
    },
    onFixed: function () {
        if (typeof setFixedTabPanel == "function" && this.item.item_id) {
            setFixedTabPanel(this.item);
        }
    },
    onRefresh: function () {
        if (rightTabPanel) rightTabPanel.setActiveTab(this.item);
        if (typeof refreshFixedTabPanel == "function") {
            refreshFixedTabPanel(this.item);
        }
    }
}); 
