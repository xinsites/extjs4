/*
* @Author: zhangxiaxin
* @Date: 2017-11-16
* @Mark: ExtJs框架共用的Js方法
*/

//扩展提示窗口
Ext.alert = function () {
    var msgCt, preMsg, preDate;

    function createBox(t, s) {
        return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
    }

    return {
        msg: function (title, format, interval) {
            if (!msgCt) {
                msgCt = Ext.DomHelper.insertFirst(document.body, {id: 'msg-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            if (!interval) interval = 2000;
            if (preDate && preMsg == s) {
                var date = new Date();
                if (date.getTime() - preDate.getTime() < interval) //毫秒时间
                    return;  //防止同一消息同时出现多条
            }
            var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
            m.hide();
            preDate = new Date();
            preMsg = s;
            m.slideIn('t').ghost("t", {delay: interval, remove: true});
        },

        init: function () {
            if (!msgCt) {
                msgCt = Ext.DomHelper.insertFirst(document.body, {id: 'msg-div'}, true);
            }
        }
    };
}();

Ext.onReady(Ext.alert.init, Ext.alert);
Ext.MessageBox.minWidth = 200;   //对话框弹出的最小宽度200
Ext.MessageBox.maxWidth = 360;   //对话框弹出的最大宽度360
Ext.Ajax.timeout = 1200000;     //1200秒,用于请求的超时时间，单位为毫秒(默认值为30000毫秒，即30秒)
Ext.MessageBox.TipTime = 3000;
Ext.onReady(function () {
    Ext.EventManager.on(Ext.isIE ? document : window, 'keydown', function (e, t) {
        if (e.getKey() == e.BACKSPACE && (t.disabled || t.readOnly)) {
            e.stopEvent();
        }
    });  //Ext的组件设置readOnly和disabled之后，按backspace页面后退的问题
    Ext.Ajax.on("requestcomplete", function (conn, r, o) {
        try {
            var resp = Ext.JSON.decode(r.responseText);
            if (resp.success == false) {
                //console.log("responseText：%s", r.responseText);
                if (resp.relogin) return returnLoginPage(resp.error_msg);
                if (resp.error_msg) setTimeout(function () {
                    errorBoxShow(resp.error_msg);
                }, 300);
                if (resp.error_url) top.window.location.href = resp.error_url;
            }
        } catch (e) {
        }
    });
});

function returnLoginPage(msg) {
    if (!isReturnLogin) {
        isReturnLogin = true;
        if (project_code) {
            $.cookie("re_u" + project_code, "");
            $.cookie("re_p" + project_code, "");
        }
        if (msg) alert(msg);
        top.window.location.href = "login.html";
    }
}

//FormPanel提交处理失败提示信息
function ajaxFailureTipMsg(form, action) {
    Ext.MessageBox.hide();
    if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
        errorBoxShow('Status:' + action.response.status + ': ' + action.response.statusText);
    } else if (action.failureType === Ext.form.Action.SERVER_INVALID) {
        if (typeof action.result.msg == 'undefined') errorBoxShow("操作处理失败！");
        else if (action.result.msg == '') errorBoxShow("操作处理失败！");
        else errorBoxShow(action.result.msg);
    }
}

//表单提交请求失败信息获取
function showMsgBySubmit(action, msg, apply_panel) {
    var message = msg;
    if (!message) message = '操作失败！';
    try {
        if (action && action.result && action.result.msg) {
            message = action.result.msg;
        } else if (apply_panel && action.result.code == 3) {  //特殊应用错误
            message += "请选择下一任务审批人！";
            var toolbar_select_dynauser = apply_panel.queryById('toolbar_select_dynauser');
            if (toolbar_select_dynauser) toolbar_select_dynauser.setVisible(true);
        }
        errorBoxShow(message);
    } catch (e) {
        errorBoxShow("操作失败！");
    }
}

//Ext.Ajax请求失败信息获取
function showMsgByJson(resp, msg, interval) {
    var message = msg;
    if (!message) message = '操作失败！';
    if (resp && resp.msg) message = resp.msg;
    var title = "错误提示";
    if (resp && resp.title) title = resp.title;

    if (interval)
        alterShow(title, message, interval);
    else
        top.messageBoxShow(title, message, 200, Ext.Msg.ERROR);
}

//Ext.Ajax请求失败信息获取
function showMsgByResponse(response, msg, interval) {
    var message = msg;
    if (!message) message = '操作失败！';
    var title = "错误提示";
    try {
        if (response && response.responseText) {
            var resp = Ext.JSON.decode(response.responseText);
            if (resp.msg) message = resp.msg;
        }
        if (response && response.title) title = response.title;
    } catch (e) {
        message = "操作失败！";
    }
    Ext.getBody().unmask();
    if (interval)
        alterShow(title, message, interval);
    else
        top.messageBoxShow(title, message, 200, Ext.Msg.ERROR);
}

//Ajax处理失败失败提示信息
function ajaxFailure(response) {
    Ext.MessageBox.hide();
    if (typeof response == 'string') errorBoxShow(response);
    else {
        try {
            errorBoxShow(Ext.JSON.decode(response.responseText).data.msg);
        } catch (e) {
            errorBoxShow("你的请求处理失败！");
        }
    }
}

function alterShow(title, message, interval) {
    if (!interval) Ext.alert.msg(title, message);
    else Ext.alert.msg(title, message, interval);
}

//type=Ext.MessageBox.INFO;Ext.MessageBox.QUESTION;Ext.MessageBox.WARNING;Ext.MessageBox.ERROR;
function messageBoxShow(title, msg, minWidth, type, callback) {
    Ext.MessageBox.minWidth = minWidth;
    Ext.MessageBox.show({title: title, msg: msg, buttons: Ext.Msg.OK, icon: type, fn: callback});
}

function errorBoxShow(msg) {
    top.messageBoxShow("错误提示", msg, 200, Ext.Msg.ERROR);
}

function alertBoxShow(msg, callback) {
    top.messageBoxShow("提示信息", msg, 260, Ext.Msg.INFO, callback);
}

function progressBoxShow(msg, progressText) {
    Ext.MessageBox.show({             //弹出效果
        title: '进度条显示',
        msg: msg,
        progressText: progressText,
        width: 300,
        wait: true,   //动态显示progress
        waitConfig: {interval: 400},  //0.6s进度条自动加载一定长度
        icon: Ext.MessageBox.INFO,   //弹出框内容前面的图标，取值为Ext.MessageBox.INFO，Ext.MessageBox.ERROR，Ext.MessageBox.WARNING，Ext.MessageBox.QUESTION
        animEl: 'saving'            //对话框弹出和关闭时的动画效果
    });
}

function showProess(msg, progressText) {
    top.progressBoxShow(msg, progressText);
}

function getNewLoadMask(id, text) {
    var mk = new Ext.LoadMask({
        target: id,
        msg: text,
        msgCls: "x-mask-loading",
        removeMask: true //完成后移除
    });
    return mk;
}

//自定义（Picket弹出框）
function definePickerFn(field) {
    var fields = ['id', 'xtype', 'xtype_name'];
    var store = Ext.create('Ext.data.Store', {
        autoLoad: true, //是否自动加载
        proxy: {
            type: 'ajax', url: "info/util/pickergrid",
            extraParams: {},
            reader: {
                type: 'json', root: 'root',
                idProperty: 'id',
                totalProperty: 'totalProperty'
            }
        },
        fields: fields
    });
    var columns = [
        {text: 'id', width: 20, fixed: true, dataIndex: 'stDepartid', hideable: false, hidden: true},
        {text: '类型', width: 80, sortable: false, dataIndex: 'xtype'},
        {text: '类型名称', width: 220, sortable: false, dataIndex: 'xtype_name'}
    ];
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        disableSelection: true, //设置为true，则禁用选择模型
        columnLines: true,
        forceFit: true, //设置为true，则强制列自适应成可用宽度。标题头部的尺寸首先根据配置来确定
        viewConfig: {
            getRowClass: function () {
                return 'custom-grid-row-height';
            },
            loadMask: true,
            loadingText: "数据加载中，请稍等..."
        },
        listeners: {
            'itemclick': function (gridview, record, item, index) {
                field.setValue(record.raw.xtype_name);
                field.collapse();
            }
        },
        columns: columns
    });
    return Ext.create('Ext.panel.Panel', {
        border: true,
        floating: true,
        height: 240,
        minWidth: 400,
        resizable: true,
        autoScroll: true,
        layout: 'fit',
        items: [grid]
    });
}

//打开帮助文档
function openHelpWindow(html, width, height, title) {
    if (html) {
        var window = Ext.create('Ext.window.Window', {
            title: title ? title : "帮助说明",
            width: width, height: height,
            resizable: true,
            closable: true,
            maximizable: true,
            iconCls: "icon_help",
            closeAction: 'destroy',  //destroy，hide
            modal: 'true',  // 弹出模态窗体  
            layout: 'fit',
            items: [{
                layout: "fit", border: false,
                bodyStyle: "padding:20px;",
                html: '',
                autoScroll: true,
                listeners: {
                    'render': function (panel, eOpts) {
                        var ajax = new XMLHttpRequest();
                        ajax.open("GET", html, true);
                        ajax.send();
                        ajax.onreadystatechange = function () {
                            if (ajax.readyState == 4) {
                                if (ajax.status == 200) {
                                    var htmlData = ajax.responseText;
                                    var bodyData = htmlData.substring(htmlData.indexOf("<body>") + 6, htmlData.lastIndexOf("</body>"));
                                    panel.body.update(bodyData);
                                }
                            }
                        };
                    }
                }
            }],
            listeners: {
                "show": function (window, eOpts) {

                }
            }
        });
        window.show();
    }
}

//打开文字说明窗口
function openTextWindow(title, explain) {
    var form = Ext.create('Ext.form.Panel', {
        autoScroll: true,
        bodyPadding: "15 20 10 20",
        defaultType: 'textfield',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [{
            xtype: 'textareafield',
            name: 'editor_form',
            height: 330,flex: 1,
            fieldLabel: '', value: explain
        }]
    });
    var editor_form = "";
    var window = Ext.create('Ext.window.Window', {
        title: title,
        width: 520,
        resizable: true,
        closable: true,
        closeAction: 'destroy',  //destroy，hide
        modal: 'true',  // 弹出模态窗体
        layout: "fit",
        items: [form],
        buttonAlign: "right",
        buttons: ["->", {
            text: "关闭",
            minWidth: 70,
            handler: function () {
                window.close();
            }
        }]
    });
    window.show();
}


