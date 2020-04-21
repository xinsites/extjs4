Ext.define("Ext.ux.UploadPanel.FileModel", {
    extend: "Ext.data.Model",
    fields: ['id', 'name', 'type', 'size', 'percent', 'status', 'fileName', 'attach_id']
});

Ext.define('Ext.ux.UploadPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.uploadpanel',
    xtype: 'uploadPanel',
    //autoDestroy: true,
    height: 300, //width: 700, 
    forceFit: true,
    isRemoveHandler: true,
    listeners: {
        edit: function (editor, e) {
            var record = e.record;
            var ischange = e.originalValue + "" != e.value + "";
            var attach_id = record.get("attach_id");
            if (ischange && attach_id > 0) {
                Ext.Ajax.request({
                    method: "POST", url: "info/file/rename",
                    params: {id: attach_id, name: e.value},
                    success: function (response, options) {
                        var txt = Ext.JSON.decode(response.responseText);
                        if (!txt.success)
                            record.set("fileName", e.originalValue);
                        else
                            record.set('name', e.value);
                        record.commit();
                    },
                    failure: function (response, options) {
                        record.set("fileName", e.originalValue);
                        record.commit();
                    }
                });
            }
        }
    },
    columns: [
        {xtype: 'rownumberer'},
        {text: '文件名', width: 100, dataIndex: 'name', hideable: false, hidden: true},
        {
            text: '文件名', width: 230, dataIndex: 'fileName',
            editor: {
                allowBlank: false, minLength: 2, maxLength: 50,
                xtype: 'textfield', selectOnFocus: false  //点击编辑框后，变成全选状态
            }
        }, //自定义文件名
        {text: '类型', width: 70, dataIndex: 'type'},
        {
            text: '大小', width: 90, dataIndex: 'size', renderer: function (v) {
                return Ext.util.Format.fileSize(v);
            }
        },
        {
            text: '进度', width: 130, dataIndex: 'percent', renderer: function (v) {
                var stml =
                    '<div>' +
                    '<div style="border:1px solid #008000;height:10px;width:115px;margin:2px 0px 1px 0px;float:left;">' +
                    '<div style="float:left;background:#FFCC66;width:' + v + '%;height:8px;"><div></div></div>' +
                    '</div>' +
                    //'<div style="text-align:center;float:right;width:40px;margin:3px 0px 1px 0px;height:10px;font-size:12px;">{3}%</div>'+
                    '</div>';
                return stml;
            }
        },
        {
            text: '状态', width: 80, dataIndex: 'status', renderer: function (v) {
                var status;
                if (v == -1) {
                    status = "等待上传";
                } else if (v == -2) {
                    status = "上传中...";
                } else if (v == -3) {
                    status = "<div style='color:red;'>上传失败</div>";
                } else if (v == -4) {
                    status = "上传成功";
                } else if (v == -5) {
                    status = "停止上传";
                }
                return status;
            }
        },
        {
            xtype: 'actioncolumn',
            width: 50, hideable: false,
            items: [{
                icon: 'images/icons/delete.gif',
                tooltip: '删除',
                handler: function (grid, rowIndex, colIndex) {
                    //var id = grid.store.getAt(rowIndex).get('id');
                    grid.store.removeAt(rowIndex);
                }
            }]
        },
        {text: 'attach_id', dataIndex: 'AttachID', hideable: false, hidden: true}
    ],
    plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],
    //    store: Ext.create("Ext.data.Store", {
    //        model: "Ext.ux.UploadPanel.FileModel",
    //        storeId: "fileItems"
    //    }),
    addFileBtnText: '选择文件',
    uploadBtnText: '上传',
    removeBtnText: '移除所有',
    cancelBtnText: '取消上传',
    debug: false,
    file_size_limit: 100, //MB,单个文件上传大小限制
    file_types: '*.*',  //上传文件类型限制， 如*.jpg;*.png;*.bmp;*.gif
    file_types_description: '所有文件', //指定在文件选取窗口中显示的文件类型描述，起一个提示和说明的作用
    file_upload_limit: 50,  //上传文件个数限制，默认最多上传50
    file_queue_limit: 0,  //指定文件上传队列里最多能同时存放多少个文件。当超过了这个数目后只有当队列里有文件上传成功、上传出错或被取消上传后，等同数量的其他文件才可以被添加进来
    post_params: {},
    upload_url: 'test.do',
    flash_url: "javascript/extjs4/swfUpload/swfupload.swf",
    flash9_url: "javascript/extjs4/swfUpload/swfupload_fp9.swf",
    initComponent: function () {
        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    itemId: 'addFileBtn',
                    iconCls: 'icon_upload_add',
                    //id: '_btn_for_swf_',
                    text: this.addFileBtnText
                }, {xtype: 'tbseparator'}, {
                    xtype: 'button',
                    itemId: 'uploadBtn',
                    iconCls: 'icon_upload_up',
                    text: this.uploadBtnText,
                    scope: this,
                    handler: this.onUpload
                }, {xtype: 'tbseparator'}, {
                    xtype: 'button',
                    itemId: 'removeBtn',
                    iconCls: 'icon_upload_trash',
                    text: this.removeBtnText,
                    scope: this,
                    handler: function () {
                        this.onRemove("removeBtn");
                    }
                }, {xtype: 'tbseparator'}, {
                    xtype: 'button',
                    itemId: 'cancelBtn',
                    iconCls: 'icon_upload_cancel',
                    disabled: true,
                    text: this.cancelBtnText,
                    scope: this,
                    handler: this.onCancelUpload
                }
            ]
        }];
        var me = this;
        this.store = Ext.create('Ext.data.JsonStore', {
            autoLoad: false, removeAt: function (rowIndex) {
                me.removeAt(rowIndex, me)
            },
            fields: ['id', 'name', 'type', 'size', 'percent', 'status', 'fileName', 'attach_id']
        });
        this.callParent();
        this.down('button[itemId=addFileBtn]').on({
            afterrender: function (btn) {
                var config = this.getSWFConfig(btn);
                this.swfupload = new SWFUpload(config);
                if (Ext.get(this.swfupload.movieName)) {
                    Ext.get(this.swfupload.movieName).setStyle({
                        position: 'absolute',
                        top: 0,
                        left: -2
                    });
                }
            },
            scope: this,
            buffer: 300
        });
    },
    getSWFConfig: function (btn) {
        var me = this;
        var placeHolderId = Ext.id();
        var em = btn.getEl().child('em');
        if (em == null) {
            em = Ext.get(btn.getId() + '-btnWrap');
        }
        em.setStyle({
            position: 'relative',
            display: 'block'
        });
        em.createChild({
            tag: 'div',
            id: placeHolderId
        });
        if (me.post_params) {
            var attr = [];
            Object.keys(me.post_params).forEach(function (key) {
                attr[attr.length] = key + "=" + me.post_params[key];
            });
            me.upload_url = me.upload_url + "?" + attr.join("&");
        }

        return {
            debug: me.debug,
            flash_url: me.flash_url,
            flash9_url: me.flash9_url,
            upload_url: me.upload_url,
            post_params: me.post_params || {savePath: 'upload\\'},
            file_size_limit: (me.file_size_limit * 1024),
            file_types: me.file_types,
            file_types_description: me.file_types_description,
            file_upload_limit: me.file_upload_limit,
            file_queue_limit: me.file_queue_limit,
            button_width: em.getWidth(),
            button_height: em.getHeight(),
            button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
            button_cursor: SWFUpload.CURSOR.HAND,
            button_placeholder_id: placeHolderId,
            custom_settings: {
                scope_handler: me
            },
            swfupload_preload_handler: me.swfupload_preload_handler,
            file_queue_error_handler: me.file_queue_error_handler,
            swfupload_load_failed_handler: me.swfupload_load_failed_handler,
            upload_start_handler: me.upload_start_handler,
            upload_progress_handler: me.upload_progress_handler,
            upload_error_handler: me.upload_error_handler,
            upload_success_handler: me.upload_success_handler,
            upload_complete_handler: me.upload_complete_handler,
            file_queued_handler: me.file_queued_handler/*,
			file_dialog_complete_handler : me.file_dialog_complete_handler*/
        };
    },
    swfupload_preload_handler: function () {
        if (!this.support.loading) {
            Ext.defer(function () {
                Ext.Msg.show({
                    title: '提示',
                    msg: '浏览器Flash Player版本太低,不能使用该上传功能！',
                    width: 250,
                    icon: Ext.Msg.ERROR,
                    buttons: Ext.Msg.OK
                });
            }, 100);
            return false;
        }
    },
    file_queue_error_handler: function (file, errorCode, message) {
        var me = this.settings.custom_settings.scope_handler;
        switch (errorCode) {
            case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
                msg('上传文件列表数量超限, 最多同时上传 ' + me.file_upload_limit + '个文件！');
                break;
            case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
                msg('文件大小超过限制, 单个文件最大 ' + me.file_size_limit + 'M！');
                break;
            case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
                msg('该文件大小为0,不能选择！');
                break;
            case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
                msg('该文件类型不允许上传, 只能上传 ' + me.file_types + '类型的文件！');
                break;
        }

        function msg(info) {
            Ext.Msg.show({
                title: '提示',
                msg: info,
                width: 300,
                bodyStyle: "padding:2px 10px;",
                icon: Ext.Msg.WARNING,
                buttons: Ext.Msg.OK
            });
        }
    },
    swfupload_load_failed_handler: function () {
        Ext.Msg.show({
            title: '提示',
            msg: 'SWFUpload加载失败！',
            width: 180,
            icon: Ext.Msg.ERROR,
            buttons: Ext.Msg.OK
        });
    },
    upload_start_handler: function (file) {
        var me = this.settings.custom_settings.scope_handler;
        me.down('#cancelBtn').setDisabled(false);
        var rec = me.store.getById(file.id);
        this.setFilePostName(encodeURIComponent(rec.get('fileName')));
    },
    upload_progress_handler: function (file, bytesLoaded, bytesTotal) {
        var me = this.settings.custom_settings.scope_handler;
        var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
        percent = percent == 100 ? 99 : percent;
        var rec = me.store.getById(file.id);
        rec.set('percent', percent);
        rec.set('status', file.filestatus);
        rec.commit();
    },
    upload_error_handler: function (file, errorCode, message) {
        var me = this.settings.custom_settings.scope_handler;
        var rec = me.store.getById(file.id);
        rec.set('percent', 0);
        rec.set('status', file.filestatus);
        rec.commit();
    },
    upload_success_handler: function (file, serverData, responseReceived) {
        var me = this.settings.custom_settings.scope_handler;
        var rec = me.store.getById(file.id);
        var resp = Ext.JSON.decode(serverData);
        if (resp.success) {
            rec.set('percent', 100);
            rec.set('status', file.filestatus);
            rec.set('attach_id', resp.attach_id);

            var originalValue = rec.get("name");
            var value = rec.get("fileName");
            var ischange = originalValue + "" != value + "";
            var attach_id = resp.attach_id;
            if (ischange && attach_id > 0) {
                Ext.Ajax.request({
                    method: "POST", url: "info/file/rename",
                    params: {id: attach_id, name: value},
                    success: function (response, options) {
                        var txt = Ext.JSON.decode(response.responseText);
                        if (!txt.success)
                            rec.set("fileName", originalValue);
                        else
                            rec.set("name", value);
                        rec.commit();
                    },
                    failure: function (response, options) {
                        rec.set("fileName", e.originalValue);
                        rec.commit();
                    }
                });
            }

        } else {
            rec.set('percent', 0);
            rec.set('status', SWFUpload.FILE_STATUS.ERROR);
        }
        rec.commit();
        if (this.getStats().files_queued > 0 && this.uploadStopped == false) {
            this.startUpload();
        } else {
            me.showBtn(me, true);
        }
    },
    upload_complete_handler: function (file) {

    },
    file_queued_handler: function (file) {
        var me = this.settings.custom_settings.scope_handler;
        //var grid = Ext.getCmp(me.getId());
        me.store.add({
            id: file.id,
            name: file.name,
            fileName: file.name,
            size: file.size,
            type: file.type,
            status: file.filestatus,
            percent: 0,
            attach_id: 0
        });
    },
    onUpload: function () {
        var me = this, ds = this.store;
        if (me.swfupload && ds.getCount() > 0) {
            if (this.swfupload.getStats().files_queued > 0) {
                this.showBtn(this, false);
                this.swfupload.uploadStopped = false;
                this.swfupload.startUpload();
            }
        }
    },
    showBtn: function (me, bl) {
        me.down('#addFileBtn').setDisabled(!bl);
        me.down('#uploadBtn').setDisabled(!bl);
        me.down('#removeBtn').setDisabled(!bl);
        me.down('#cancelBtn').setDisabled(bl);
        if (bl) {
            me.down('actioncolumn').show();
        } else {
            me.down('actioncolumn').hide();
        }
    },
    removeHandler: function (attach_id) {
        //attach_id=0:表示删除所有
        var Ids = [], store = this.store;
        if (attach_id == 0) {
            store.each(function (record) {
                if (record.get("attach_id") > 0)
                    Ids[Ids.length] = record.get("attach_id");
            });
        }
        else Ids[Ids.length] = attach_id;
        if (Ids.length > 0) {
            Ext.Ajax.request({
                method: "POST", url: "info/file/delete",
                params: {ids: Ids.join(",")},
                success: function (response, options) {
                },
                failure: function (response, options) {
                }
            });
        }
    },
    onRemove: function (type) {
        var ds = this.store;
        for (var i = 0; i < ds.getCount(); i++) {
            var record = ds.getAt(i);
            var file_id = record.get('id');
            this.swfupload.cancelUpload(file_id, false);
        }
        if (type) this.removeHandler(0);
        ds.removeAll();
        this.swfupload.uploadStopped = false;
    },
    removeAt: function (rowIndex, me) {
        var ds = me.store;
        var file_id = ds.getAt(rowIndex).get('id');
        var attach_id = ds.getAt(rowIndex).get('attach_id');
        if (me.isRemoveHandler && attach_id > 0) {
            me.removeHandler(attach_id);
        }
        me.swfupload.cancelUpload(file_id, false);
        ds.remove(ds.getAt(rowIndex));
    },
    beforeDestroy: function () {
        var me = this;
        me.store.removeAll();
        Ext.destroy(
            me.placeholder,
            me.ghostPanel
        );
        me.callParent();
    },
    destroy: function () {
        var me = this;
        var toolbar = me.getDockedItems('toolbar[dock="top"]');
        if (toolbar && toolbar.length > 0) {
            toolbar[0].items.each(function (item, index, length) {
                this.destroy();
            });
        }
        me.onRemove();
        me.swfupload.destroy();
    },
    onCancelUpload: function () {
        if (this.swfupload) {
            this.swfupload.uploadStopped = true;
            this.swfupload.stopUpload();
            this.showBtn(this, true);
        }
    }
});