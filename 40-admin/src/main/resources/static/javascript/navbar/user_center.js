Ext.define('javascript.navbar.user_center', {
    extend: ''
});

//用户中心面板
function createPanel_UserCenter(treenode) {
    var store = Ext.create('Ext.data.TreeStore', {
        root: {
            text: "根结点",
            children: [{
                id: 10001, text: '基本信息', leaf: true
            }, {
                id: 10002, text: '联系方式', leaf: true
            }, {
                id: 10003, text: '我的头像', leaf: true
            }, {
                id: 10004, text: '修改密码', leaf: true
            }, {
                id: 10005, text: '用户日志', leaf: true
            }]
        }
    });
    var tree = Ext.create('Ext.tree.Panel', {
        store: store,
        width: 260, height: 400,
        useArrows: true,
        rowLines: true, hideHeaders: true,
        autoScroll: true, rootVisible: false,
        singleExpand: true, //如果每个分支只有1个节点可能展开,默认false
        viewConfig: {
            getRowClass: function () {
                return 'log_tree_panel_row_height';
            }
        },
        listeners: {
            afterrender: function () {
                var record = this.getStore().getNodeById('10001');
                this.getSelectionModel().select(record)
            },
            "itemclick": function (treeview, record, item, index, e) {
                panel.setTitle(record.raw.text);
                //var card = panel.queryById("card-" + record.raw.id);
                panel.getLayout().setActiveItem("card-" + record.raw.id);

                if (record.raw.id == 10002) {
                    GetUserInfo(panel.queryById("form-" + record.raw.id));
                } else if (record.raw.id == 10003) {
                    var form = panel.queryById("form-" + record.raw.id);
                    if (form) form.getForm().reset();
                    var imageShow_box = panel.down('box[itemId=imageShow_box]');    //预览的图片框对象
                    if (imageShow_box) imageShow_box.getEl().dom.src = userinfo.headPhoto;
                } else if (record.raw.id == 10005) {
                    var grid = panel.queryById('main_grid_log');
                    if (grid && grid.store) grid.store.load();
                }
            }
        }
    });

    var panel = Ext.create('Ext.panel.Panel', {
        title: '基本信息',
        border: false,
        layout: {
            type: 'card',
            deferredRender: true
        },
        defaults: {
            border: false // 应用到所有子面板
        },
        items: [{
            itemId: 'card-10001',
            bodyStyle: 'padding:15px',
            items: [createUserBaseInfoForm()]
        }, {
            itemId: 'card-10002',
            bodyStyle: 'padding:15px',
            items: [createUserContactForm()]
        }, {
            itemId: 'card-10003',
            bodyStyle: 'padding:15px',
            items: [createUserHeaderForm()]
        }, {
            itemId: 'card-10004',
            bodyStyle: 'padding:15px',
            items: [createUserPasswordForm()]
        }, {
            itemId: 'card-10005', layout: 'fit',
            items: [createUserLogPanel()]
        }]
    });

    Ext.define('model_user_center', {
        extend: 'Ext.data.Model',
        idProperty: 'user_id',
        fields: [{"name": "user_id", "type": "int", "text": "主键"},
            {"name": "user_name", "type": "string", "text": "用户姓名"},
            {"name": "login_name", "type": "string", "text": "登录名"},
            {"name": "head_photo", "type": "string", "text": "头像"},
            {"name": "user_sex", "type": "string", "text": "性别"},
            {"name": "org_id", "type": "string", "text": "所属机构"},
            {"name": "org_id_text", "type": "string", "text": "所属机构_文本值"},
            {"name": "role_id", "type": "string", "text": "用户角色"},
            {"name": "role_id_text", "type": "string", "text": "用户角色_文本值"},
            {"name": "dept_id", "type": "string", "text": "用户部门"},
            {"name": "dept_id_text", "type": "string", "text": "用户部门_文本值"},
            {"name": "leader", "type": "string", "text": "直属领导"},
            {"name": "user_state", "type": "bool", "text": "启用状态"},
            {"name": "birthday", "type": "string", "text": "出生日期"},
            {"name": "email", "type": "string", "text": "电子邮箱"},
            {"name": "phone", "type": "string", "text": "个人手机号"},
            {"name": "post_id", "type": "string", "text": "用户职位"},
            {"name": "post_id_text", "type": "string", "text": "用户职位_文本值"},
            {"name": "remark", "type": "string", "text": "备注"},
            {"name": "oicq", "type": "string", "text": "QQ"},
            {"name": "wechat", "type": "string", "text": "微信"},
            {"name": "workphone", "type": "string", "text": "工作手机号"},
            {"name": "subtelephone", "type": "string", "text": "分机号"},
            {"name": "serialcode", "type": "int", "text": "排序号"},
            {"name": "issys", "type": "int", "text": "是否自带账户"}]
    });

    var UserInfo = null;

    function GetUserInfo(form) {
        if (!form) return;
        if (UserInfo == null) {
            Ext.Ajax.request({
                method: "POST", url: path_url.system.user.info,
                params: {user_id: userinfo.userId},
                success: function (response, options) {
                    Ext.getBody().unmask();
                    var resp = Ext.JSON.decode(response.responseText);
                    if (resp.success) {
                        UserInfo = Ext.create("model_user_center", resp["user"]["data"]);
                        designFormOriginalValue(form, UserInfo);
                    }
                },
                failure: function (response, options) {
                    Ext.getBody().unmask();
                }
            });
        } else {
            designFormOriginalValue(form, UserInfo);
        }
    }

    //用户基本信息
    function createUserBaseInfoForm() {
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '10 15 10',
            autoScroll: true,
            itemId: 'form-10001',
            layout: {type: 'vbox', align: 'stretch'},
            defaults: {width: 450},
            fieldDefaults: {labelAlign: 'right', msgTarget: 'side', labelWidth: 80},
            dockedItems: [{
                xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
                items: [{xtype: 'tbspacer', width: 90}, {
                    itemId: "btn_save_form",
                    text: '保存', minWidth: 70,
                    handler: function (btn, pressed) {
                        if (form.isValid()) {
                            form.submit({
                                method: "POST", url: "system/user/center/baseinfo",
                                waitMsg: '正在保存，请稍等...', submitEmptyText: false,
                                params: {},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        Ext.alert.msg('提示', "保存成功!");
                                        var values = form.getForm().getValues(false);
                                        userinfo.loginName = values.login_name;
                                        userinfo.userName = values.user_name;
                                        $("#lbl_UserName").html(userinfo.userName);
                                    }
                                    else Ext.alert.msg('提示', "保存失败!");
                                },
                                failure: function (form, action) {
                                    Ext.alert.msg('提示', "保存失败!");
                                }
                            });
                        } else {
                            Ext.alert.msg('无效输入', '请输入正确的信息!');
                        }
                    }
                }, '-', {
                    text: '重置', minWidth: 70,
                    handler: function (btn, pressed) {
                        form.getForm().reset();
                    }
                }]
            }],
            listeners: {
                afterrender: function (form, e) {
                    GetUserInfo(form);
                }
            },
            items: [{
                maxWidth: 450,
                xtype: 'textfield',
                name: 'login_name',
                itemId: 'login_name',
                allowBlank: false,
                fieldLabel: '登录名',
                minLength: 2, maxLength: 20,
                flex: 1, msgTarget: 'under',
                regex: /^[a-z|A-Z|0-9|_]+$/,
                regexText: '请输入字母数字下划线',
                textValid: true, readOnly: true,
                validator: function (value) {
                    if (value == "") return true;
                    return this.textValid;
                },
                listeners: {
                    'change': function (textfield, newValue, oldValue) {
                        isExistLoginName(textfield);
                    },
                    'blur': function (textfield, the, oldValue) {
                        isExistLoginName(textfield);
                    }
                }
            }, {
                maxWidth: 450,
                style: 'margin-top:6px;',
                xtype: 'textfield',
                name: 'user_name',
                itemId: 'user_name',
                fieldLabel: '用户姓名',
                allowBlank: false,
                maxLength: 15
            }, {
                maxWidth: 450,
                style: 'margin-top:6px;',
                xtype: 'datefield',
                name: 'birthday',
                itemId: 'birthday',
                fieldLabel: '出生日期',
                allowBlank: true,
                format: 'Y-m-d',
                editable: false
            }, {
                xtype: 'radiogroup',
                name: 'user_sex',
                itemId: 'user_sex',
                fieldLabel: '性别',
                allowBlank: false,
                items: [{"boxLabel": "男", "name": "user_sex", "inputValue": "男", "checked": true}, {
                    "boxLabel": "女",
                    "name": "user_sex",
                    "inputValue": "女"
                }],
                columns: [50, 50]
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:6px;margin-bottom:15px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                maxWidth: 450,
                xtype: 'treepicker',
                name: 'dept_id',
                itemId: 'dept_id',
                fieldLabel: '所在部门',
                multiSelect: false,
                queryMode: 'remote',
                selectMode: 'all',
                readOnly: true,
                store: getSysDataTreeStore('ds.sys.dept', false),
                editable: false
            }, {
                maxWidth: 450,
                xtype: 'combobox',
                name: 'role_id',
                itemId: 'role_id',
                fieldLabel: '用户角色',
                valueField: 'id',
                displayField: 'name',
                queryMode: 'remote',
                readOnly: true,
                store: getSysDataComboStore('ds.sys.role'), //, {id: 1, name: "超级管理员"}
                editable: false,
                maxLength: 50
            }, {
                maxWidth: 450,
                xtype: 'combobox',
                name: 'post_id',
                itemId: 'post_id',
                fieldLabel: '用户职位',
                valueField: 'id',
                displayField: 'name',
                queryMode: 'remote',
                readOnly: true,
                store: getCodeComboStore("work.post"),
                editable: false,
                maxLength: 50
            }, {
                maxWidth: 450,
                xtype: 'combobox',
                name: 'leader',
                itemId: 'leader',
                fieldLabel: '直属领导',
                valueField: 'id', displayField: 'name',
                store: getSysDataComboStore('ds.sys.user'),
                minChars: 0, queryDelay: 300,
                readOnly: true,
                queryMode: 'remote'
            }]
        });

        //用户登录名是否存在
        function isExistLoginName(textfield) {
            if (textfield.getValue()) {
                Ext.Ajax.request({
                    method: "POST", url: path_url.system.user.isexist,
                    params: {user_id: userinfo.userId, login_name: textfield.getValue()},
                    success: function (response, options) {
                        textfield.textValid = true;
                        var resp = Ext.JSON.decode(response.responseText);
                        if (resp.success && resp.isexist == 1) {
                            textfield.textValid = '该登录名已经存在';
                        }
                        textfield.validate();
                    },
                    failure: function (response, options) {
                        textfield.textValid = true;
                    }
                });
            }
        }

        return form;
    }

    //用户联系方式
    function createUserContactForm() {
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '10 15 10',
            autoScroll: true,
            itemId: 'form-10002',
            layout: {type: 'vbox', align: 'stretch'},
            defaults: {width: 450},
            fieldDefaults: {labelAlign: 'right', msgTarget: 'side', labelWidth: 80},
            dockedItems: [{
                xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
                items: [{xtype: 'tbspacer', width: 90}, {
                    itemId: "btn_save_form",
                    text: '保存', minWidth: 70,
                    handler: function (btn, pressed) {
                        if (form.isValid()) {
                            form.submit({
                                method: "POST", url: "system/user/center/linkinfo",
                                waitMsg: '正在保存，请稍等...', submitEmptyText: false,
                                params: {},
                                success: function (basic_form, action) {
                                    var flag = action.result.success;
                                    if (flag) {
                                        Ext.alert.msg('提示', "保存成功!");
                                    }
                                    else Ext.alert.msg('提示', "保存失败!");
                                },
                                failure: function (form, action) {
                                    Ext.alert.msg('提示', "保存失败!");
                                }
                            });
                        } else {
                            Ext.alert.msg('无效输入', '请输入正确的信息!');
                        }
                    }
                }, '-', {
                    text: '重置', minWidth: 70,
                    handler: function (btn, pressed) {
                        form.getForm().reset();
                    }
                }]
            }],
            items: [{
                maxWidth: 450,
                xtype: 'textfield',
                name: 'email',
                itemId: 'email',
                fieldLabel: '电子邮箱',
                allowBlank: true,
                maxLength: 50,
                vtype: 'email'
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:6px;margin-bottom:10px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'phone',
                itemId: 'phone',
                fieldLabel: '个人手机号',
                allowBlank: true,
                maxLength: 15
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'workphone',
                itemId: 'workphone',
                fieldLabel: '工作手机号',
                allowBlank: true,
                maxLength: 15
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'subtelephone',
                itemId: 'subtelephone',
                fieldLabel: '分机号',
                allowBlank: true,
                maxLength: 15
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:6px;margin-bottom:10px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'oicq',
                itemId: 'oicq',
                fieldLabel: 'QQ',
                allowBlank: true,
                maxLength: 20
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'wechat',
                itemId: 'wechat',
                fieldLabel: '微信',
                allowBlank: true,
                maxLength: 20
            }]
        });
        return form;
    }

    //用户头像
    function createUserHeaderForm() {
        var fileupload = Ext.create('Ext.form.field.File', {
            buttonOnly: true,
            hideLabel: true,
            buttonText: "选择头像",
            emptyText: '请选择一张照片(.jpg|.gif|.bmp|.png)',
            regex: /^.*?\.(jpg|gif|bmp|png)$/,
            regexText: "只能上传jpg、gif、bmp、png类型的文件",
            listeners: {
                'change': function (file, path) {
                    if (!file.isValid()) Ext.alert.msg('提示', '只能上传jpg、gif、bmp、png类型的文件！');
                    else {
                        if (path != null && !Ext.isEmpty(path)) {
                            var imageShow_box = form.down('box[itemId=imageShow_box]');    //预览的图片框对象
                            if (imageShow_box) {
                                var url = "file://" + path;
                                var imageShow_box_dom = imageShow_box.getEl().dom;
                                if (Ext.isIE) {//IE浏览器
                                    //imageShow_box_dom.src = Ext.BLANK_IMAGE_URL; // 覆盖原来的图片   
                                    imageShow_box_dom.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = url;
                                } else {
                                    var fileimg = file.fileInputEl.dom.files[0];
                                    imageShow_box_dom.src = window.URL.createObjectURL(fileimg);
                                }
                            }
                        }
                    }
                }
            }
        });
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '10 15 10',
            autoScroll: true,
            itemId: 'form-10003',
            layout: {type: 'vbox', align: 'stretch'},
            defaults: {width: 450},
            fieldDefaults: {labelAlign: 'right', msgTarget: 'side', labelWidth: 40},
            dockedItems: [{
                xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
                items: [{xtype: 'tbspacer', width: 50}, fileupload,
                    '-', {
                        itemId: "btn_save_form",
                        text: '保存', minWidth: 70,
                        handler: function (btn, pressed) {
                            if (fileupload.getValue()) {
                                form.submit({
                                    method: "POST", url: "system/user/center/savephoto",
                                    waitMsg: '正在上传头像，请稍等...', submitEmptyText: false,
                                    params: {},
                                    success: function (basic_form, action) {
                                        var flag = action.result.success;
                                        if (flag) {
                                            userinfo.headPhoto = decodeURIComponent(action.result.msg);
                                            $("#user_header_photo").attr("src", userinfo.headPhoto);
                                            Ext.alert.msg('提示', "保存成功!");
                                        }
                                    },
                                    failure: function (form, action) {
                                        showMsgBySubmit(action, "保存失败！");
                                    }
                                });
                            } else {
                                Ext.alert.msg('无效输入', '请选择一个用户头像!');
                            }
                        }
                    }, '-', {
                        text: '重置', minWidth: 70,
                        handler: function (btn, pressed) {
                            form.getForm().reset();
                            var imageShow_box = form.down('box[itemId=imageShow_box]');    //预览的图片框对象
                            if (imageShow_box) imageShow_box.getEl().dom.src = userinfo.headPhoto;
                        }
                    }]
            }],
            items: [{
                xtype: 'box',
                maxWidth: 100,
                maxHeight: 100,
                margin: '20 0 0 110',
                itemId: 'imageShow_box',
                autoEl: {
                    width: 100,
                    height: 100,
                    tag: 'img',
                    type: 'image',
                    src: 'images/default_avatar3.png',
                    onerror: "this.src='images/default_avatar3.png'",
                    style: 'filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale);width:160px;height:160px;text-align:center;border-radius: 100px;',
                    complete: 'off'
                }
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:20px;margin-bottom:0px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                xtype: 'displayfield',
                fieldLabel: ' ',
                labelSeparator: "",
                style: 'margin-bottom:5px',
                value: '<span style="color:blue;">建议上传图片尺寸为100x100、200x200，大小不超过1.0M</span>'
            }]
        });
        return form;
    }

    //用户修改密码
    function createUserPasswordForm() {
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: '10 15 10',
            autoScroll: true,
            itemId: 'form-10004',
            layout: {type: 'vbox', align: 'stretch'},
            defaults: {width: 450},
            fieldDefaults: {labelAlign: 'right', msgTarget: 'under', labelWidth: 80},
            dockedItems: [{
                xtype: 'toolbar', dock: 'bottom', ui: 'footer', layout: {pack: 'left'},
                items: [{xtype: 'tbspacer', width: 90}, {
                    itemId: "btn_save_form",
                    text: '保存', minWidth: 70,
                    handler: function (btn, pressed) {
                        if (form.isValid()) {
                            var old_password = MD5(form.queryById("old_password").getValue());
                            var level_old_password = MD5(old_password.toUpperCase()).toLowerCase();
                            var new_password = MD5(form.queryById("new_password").getValue());
                            var level_password = MD5(new_password.toUpperCase()).toLowerCase();
                            Ext.Ajax.request({
                                method: "POST", url: "system/user/center/modpwd",
                                waitMsg: '正在保存，请稍等...', submitEmptyText: false,
                                params: {
                                    level_password: level_password,
                                    level_old_password: level_old_password
                                },
                                success: function (response, options) {
                                    var resp = Ext.JSON.decode(response.responseText);
                                    if (resp.success) {
                                        form.getForm().reset();
                                        Ext.alert.msg('提示', "密码修改成功!");
                                    } else {
                                        showMsgByJson(resp, "保存失败!");
                                    }
                                },
                                failure: function (response, options) {
                                    showMsgByResponse(response, "保存失败!");
                                }
                            });
                        } else {
                            Ext.alert.msg('无效输入', '请输入正确的信息!');
                        }
                    }
                }, '-', {
                    text: '重置', minWidth: 70,
                    handler: function (btn, pressed) {
                        form.getForm().reset();
                    }
                }]
            }],
            items: [{
                maxWidth: 450,
                xtype: 'textfield',
                name: 'old_password',
                itemId: 'old_password',
                fieldLabel: '旧密码',
                allowBlank: false,
                maxLength: 20,
                inputType: "password"
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:6px;margin-bottom:10px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'new_password',
                itemId: 'new_password',
                fieldLabel: '新密码',
                inputType: "password",
                allowBlank: false,
                maxLength: 20,
                listeners: {
                    'blur': function (textfield, e) {
                        form.queryById("new_password2").validate();
                    }
                }
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:6px;margin-bottom:10px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }, {
                maxWidth: 450,
                xtype: 'textfield',
                name: 'new_password2',
                itemId: 'new_password2',
                fieldLabel: '重复新密码',
                inputType: "password",
                allowBlank: false,
                maxLength: 20,
                textValid: true,
                validator: function (value) {
                    if (value == "") return true;
                    if (form.queryById("new_password").getValue() == value) return true;
                    return '两次输入密码不正确！';
                }
            }, {
                xtype: 'box', border: false, layout: "fit", style: 'margin-top:6px;margin-bottom:10px',
                html: '<div style="background:#F6F6F6;width:98%;height:1px;"></div>'
            }]
        });
        return form;
    }

    //用户日志
    function createUserLogPanel() {
        Ext.define('model_usercenter_log', {
            extend: 'Ext.data.Model',
            idProperty: 'log_id',
            fields: [{"name": "log_id", "type": "int", "text": "主键"},
                {"name": "create_time", "type": "date", "text": "创建时间"},
                {"name": "user_id", "type": "int", "text": "创建用户"},
                {"name": "user_name", "type": "string", "text": "创建用户"},
                {"name": "log_ip", "type": "string", "text": "IP地址"},
                {"name": "log_fun", "type": "string", "text": "功能位置"},
                {"name": "action_type", "type": "string", "text": "操作类型"},
                {"name": "log_result", "type": "string", "text": "执行结果"},
                {"name": "log_message", "type": "string", "text": "日志信息描述"}]
        });
        var itemid = "user_log";
        var pageSize = getGridPageSize(itemid);
        var store = Ext.create('Ext.data.Store', {
            remoteSort: true, autoLoad: true, //是否自动加载
            proxy: {
                type: 'ajax', url: "system/user/center/loggrid",
                extraParams: {item_id: itemid},
                reader: {
                    type: 'json', root: 'root',
                    idProperty: 'log_id',
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
                property: 'serialcode',
                direction: 'desc'
            }, {
                property: 'create_time',
                direction: 'desc'
            }],
            pageSize: pageSize,
            model: 'model_usercenter_log'
        });
        var columns = [new Ext.grid.RowNumberer({width: 40, tdCls: 'blue'}),
            {text: 'log_id', width: 20, dataIndex: 'log_id', hideable: false, hidden: true}, {
                text: '操作时间',
                dataIndex: 'create_time',
                width: 145,
                fixed: true,
                align: 'left',
                sortable: true,
                renderer: Ext.util.Format.dateRenderer('Y-m-d H:i:s')
            }, {
                text: '操作用户',
                dataIndex: 'user_name',
                width: 140,
                fixed: true,
                align: 'left',
                sortable: true
            }, {
                text: 'IP地址',
                dataIndex: 'log_ip',
                width: 120,
                fixed: true,
                align: 'left',
                sortable: true
            }, {
                text: '系统功能',
                dataIndex: 'log_fun',
                width: 180,
                fixed: true,
                align: 'left',
                sortable: true
            }, {
                text: '操作类型',
                dataIndex: 'action_type',
                width: 80,
                fixed: true,
                align: 'center',
                sortable: true
            }, {
                text: '执行结果',
                dataIndex: 'log_result',
                width: 80,
                fixed: true,
                align: 'center',
                sortable: true,
                renderer: function (val, meta, rec) {
                    if (val == "成功")
                        return '<span style="font-weight:bold;color:green;">成功</span>';
                    else
                        return '<span style="font-weight:bold;color:red;">' + val + '</span>';
                }
            }, {
                text: '日志信息描述',
                dataIndex: 'log_message',
                width: 140,
                fixed: false,
                align: 'left',
                sortable: true
            }];
        var grid = Ext.create('Ext.grid.Panel', {
            itemId: "main_grid_log",
            store: store,
            multiSelect: true,
            forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
            viewConfig: {
                getRowClass: function () {
                    return 'custom-grid-row-height';
                },
                loadMask: true,
                loadingText: "数据加载中，请稍等..."
            },
            border: false,
            columns: columns,
            dockedItems: [{
                xtype: 'toolbar', dock: 'top',
                items: [
                    {
                        itemId: "linkType",
                        xtype: 'radiogroup',
                        columns: [88, 88, 88],
                        items: [{boxLabel: '登录日志', name: 'logType', inputValue: '1'},
                            {boxLabel: '访问日志', name: 'logType', inputValue: '2'},
                            {boxLabel: '操作日志', name: 'logType', inputValue: '3'}],
                        listeners: {
                            change: function (field, newValue, oldValue, e) {
                                grid.store.proxy.extraParams.log_type = field.getValue().logType;
                                grid.store.load();
                            }
                        }
                    },
                    '->', '<b>搜索:</b>',
                    {
                        xtype: 'textfield', width: 220, emptyText: "系统功能检索，请按enter键...",
                        listeners: {
                            specialkey: function (field, e) {
                                if (e.getKey() == Ext.EventObject.ENTER) {
                                    var search_all = grid.queryById("search_all");
                                    if (search_all) search_all.setChecked(true);
                                    designSearchByField(store, 'key_log', "log_fun", field.getValue().replace(/%/g, '/%').replace(/_/g, '/_'));
                                }
                            }
                        }
                    }, {
                        xtype: 'splitbutton', text: '高级搜索',
                        itemId: "btn_complexsearch",
                        iconCls: "icon_search", listeners: {
                            click: function () {
                                openSearchWin(treenode, grid);
                            }
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
                                            gridSearchByDate(store, 'key_log', 0);
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
                                            gridSearchByDate(store, 'key_log', 2);
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
                                            gridSearchByDate(store, 'key_log', 6);
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
                                            gridSearchByDate(store, 'key_log', 30);
                                        }
                                    }
                                },
                                {
                                    text: '全部',
                                    itemId: "search_all",
                                    checked: false,
                                    group: 'search-group',
                                    scope: this,
                                    listeners: {
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

        var logSearch;

        function openSearchWin(treenode, grid) {
            if (!logSearch) {
                var btn = grid.queryById("btn_complexsearch");
                var start_time = new Date();
                start_time.addWeeks(-1);
                var form = Ext.create('Ext.form.Panel', {
                    frame: false,
                    bodyPadding: "15 20 10 20",
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
                        xtype: 'fieldcontainer',
                        fieldLabel: '操作时间',
                        layout: 'hbox',
                        defaults: {
                            flex: 1,
                            editable: false,
                            hideLabel: true
                        },
                        defaultType: 'datefield',
                        items: [{
                            itemId: "createtime_s", name: 'createtime_s',
                            margin: '0 5 0 0', value: start_time,
                            format: 'Y-m-d', editable: false,
                            listeners: {
                                "select": function (field) {
                                    var last_date = field.up('form').down("#createtime_e");
                                    last_date.setMinValue(field.getValue());
                                    last_date.expand();
                                }
                            }
                        }, {
                            flex: 0, width: 20,
                            xtype: 'displayfield',
                            value: '至'
                        }, {
                            itemId: "createtime_e", name: 'createtime_e',
                            fieldLabel: 'End', value: new Date(),
                            format: 'Y-m-d', editable: false,
                            listeners: {
                                "select": function (field) {
                                    var first_date = field.up('form').down("#createtime_s");
                                    first_date.setMaxValue(field.getValue());
                                }
                            }
                        }]
                    }, {
                        xtype: 'textfield',
                        itemId: "log_ip", name: 'log_ip',
                        maxLength: 25,
                        fieldLabel: 'IP地址'
                    }, {
                        itemId: "log_result",
                        xtype: 'radiogroup',
                        name: 'log_result',
                        fieldLabel: '执行结果',
                        allowBlank: false,
                        items: [{"boxLabel": "成功", "name": "log_result", "inputValue": "成功"}, {
                            "boxLabel": "失败",
                            "name": "log_result",
                            "inputValue": "失败"
                        }],
                        columns: [70, 70],
                        value: '1'
                    }, {
                        itemId: "log_message",
                        xtype: 'textareafield',
                        name: 'log_message',
                        fieldLabel: '日志信息',
                        height: 80, rows: 3,
                        maxLength: 100,
                        flex: 1
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
                var win_config = {
                    title: '日志查询',
                    animateTarget: btn.getId(),
                    items: [form],
                    buttonAlign: "right",
                    buttons: ["->", {
                        text: '查询', minWidth: 70,
                        listeners: {
                            click: function () {
                                search();
                            }
                        }
                    }, {
                        text: '清空', minWidth: 70,
                        listeners: {
                            click: function () {
                                form.getForm().reset();
                                form.queryById('createtime_s').setValue("");
                                form.queryById('createtime_e').setValue("");
                                form.queryById('createtime_e').setMinValue("1991-01-01");
                                form.queryById('createtime_s').setMaxValue("2999-01-01");
                            }
                        }
                    }, {
                        text: '关闭', minWidth: 70,
                        handler: function () {
                            logSearch.close();
                        }
                    }]
                }
                logSearch = Ext.create('widget.window', Ext.apply(win_config, {
                    width: 460,
                    height: 300,
                    closable: true,
                    closeAction: 'hide',
                    plain: false,
                    modal: true,
                    layout: 'fit'
                }));
            }
            logSearch.show();

            function search() {
                var items = [];
                items[items.length] = {
                    field: "create_time",
                    operator: "between",
                    start: "createtime_s",
                    end: "createtime_e",
                    valType: "date"
                };
                items[items.length] = {item_id: "log_ip", operator: "like"};
                items[items.length] = {item_id: "log_result", operator: "=", valType: "int"};
                items[items.length] = {item_id: "log_message", operator: "like"};
                searchSingleTableReLoad(grid, designSearchByForm(form, items), "key_log");
                logSearch.close();
            }
        }

        return grid;
    }

    return Ext.create('Ext.panel.Panel', {
        layout: "border", border: false,
        items: [{
            title: "",
            region: 'west', split: {width: 5},
            width: 180, minWidth: 1, maxWidth: 500,
            layout: 'fit', items: [tree]
        }, {
            region: 'center',
            layout: 'fit',
            items: [panel]
        }]
    });
}