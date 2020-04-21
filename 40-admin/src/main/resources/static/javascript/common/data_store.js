/*
* @Author: zhangxiaxin
* @Date: 2019-10-16
* @Mark: 各下拉框(列表)的store
*/

//下拉列表请求编码表
function getCodeComboStore(data_key, first_item, disableds, noshows) {
    if (!first_item) first_item = {};
    if (!disableds) disableds = "";
    if (!noshows) noshows = "";
    return Ext.create('Ext.data.Store', {
        autoLoad: true, // 必须自动加载, 否则无在编辑的时候load
        proxy: {
            type: 'ajax', url: "system/code/codecombo",
            extraParams: {
                data_key: data_key,
                disableds: disableds,
                noshows: noshows,
                id: first_item.id, name: first_item.name
            },
            reader: {
                type: 'json', root: 'root',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            load: function (store, records) {
                try {
                    if (store.proxy.extraParams.name)
                        store.insert(0, [{'id': store.proxy.extraParams.id, 'name': store.proxy.extraParams.name}]);
                } catch (e) {
                }
            }
        },
        fields: [{"name": "id", "type": "string"}, {"name": "name", "type": "string"}, 'disabled']
    });
}

//下拉树请求编码表(逐层请求)
function getCodeLayerTreeStore(data_key, isCheck, disableds, noshows) {
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: true,
        proxy: {
            type: 'ajax', url: "system/code/codetree",
            reader: {type: 'json', id: "id"},
            extraParams: {
                data_key: data_key,
                isCheck: isCheck,
                disableds: disableds,
                noshows: noshows
            }
        },
        root: {text: '根节点', id: '0', expanded: true}
    });
    return store;
}

//下拉树请求编码表(一次全部请求)
function getCodeAllTreeStore(data_key, isCheck, disableds, noshows) {
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: true,
        proxy: {
            type: 'ajax', url: "system/code/codealltree",
            reader: {
                type: 'json', id: "id"
            },
            extraParams: {
                data_key: data_key,
                isCheck: isCheck,
                disableds: disableds,
                noshows: noshows
            }
        },
        root: {text: '根节点', id: '0', expanded: true}
    });
    return store;
}

//下拉列表请求系统数据源
function getSysDataComboStore(data_key, first_item, disableds, noshows) {
    if (!first_item) first_item = {};
    if (!disableds) disableds = "";
    if (!noshows) noshows = "";
    return Ext.create('Ext.data.Store', {
        autoLoad: true, // 必须自动加载, 否则无在编辑的时候load
        proxy: {
            type: 'ajax', url: path_url.system.ds.storedata,
            extraParams: {
                data_key: data_key,
                id: first_item.id,
                value: first_item.value,
                name: first_item.name,
                disableds: disableds,
                noshows: noshows
            },
            reader: {
                type: 'json', root: 'root',
                totalProperty: 'totalProperty'
            }
        },
        listeners: {
            load: function (store, records) {
                try {
                    if (store.proxy.extraParams.name)
                        store.insert(0, [{
                            'id': store.proxy.extraParams.id,
                            'value': store.proxy.extraParams.value,
                            'name': store.proxy.extraParams.name
                        }]);
                } catch (e) {
                }
            }
        },
        fields: [{"name": "id", "type": "string"}, {"name": "value", "type": "string"}, {
            "name": "name",
            "type": "string"
        }, 'disabled']
    });
}

//下拉树请求系统数据源
function getSysDataTreeStore(data_key, isCheck, disableds, noshows) {
    var store = Ext.create('Ext.data.TreeStore', {
        nodeParam: 'node', autoLoad: true,
        proxy: {
            type: 'ajax', url: path_url.system.ds.storedata,
            reader: {
                type: 'json', id: "id", isCheck: isCheck
            },
            extraParams: {
                data_type: "tree", data_key: data_key,
                isCheck: isCheck, disableds: disableds, noshows: noshows
            }
        },
        root: {text: '根节点', id: '0', expanded: true}
    });
    return store;
}

//选择省、市、区县下拉列表
function getAreaStore(pid, autoLoad) {
    return Ext.create('Ext.data.Store', {
        autoLoad: autoLoad,
        proxy: {
            type: 'ajax', url: "system/area/combo",
            extraParams: {pid: pid},
            reader: {
                type: 'json', root: 'root',
                totalProperty: 'totalProperty'
            }
        },
        fields: ['id', 'name']
    });
}

//重置省、市、区县下拉列表
function resetAreaStore(form, area_id, Value) {
    var combobox = form.queryById(area_id);
    if (combobox) {
        combobox.store.proxy.extraParams.pid = Value ? Value : -1;
        combobox.store.reload();
    }
}

//清空省、市、区县下拉列表
function clearAreaCombo(form, area_id) {
    var combobox = form.queryById(area_id);
    if (combobox) combobox.clearValue();
}

//子流程下拉列表(一次全部请求)
function getSubFlowStore(status_id, item_id) {
    return Ext.create('Ext.data.Store', {
        autoLoad: true,
        proxy: {
            type: 'ajax', url: path_url.flow.apply.combo.subflow,
            extraParams: {
                status_id: status_id, item_id: item_id
            },
            reader: {
                type: 'json', root: 'root',
                totalProperty: 'totalProperty'
            }
        },
        fields: ['id', 'name', 'disabled']
    });
}

//获取数据权限数组
function getDataPerArray() {
    var dataPer_column = [];
    for (var i = 0; i < dataPer_store.length; i++) {
        dataPer_column[dataPer_column.length] = [dataPer_store[i].id, dataPer_store[i].name];
    }
    return dataPer_column;
}