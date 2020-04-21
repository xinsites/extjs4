/*
* @Author: zhangxiaxin
* @Date: 2019-10-16
* @Mark: 公共的Js方法
*/

//设置panel中item_ids控件是否可用
function setBtnDisabledByIds(panel, item_ids, disabled) {
    if (panel && item_ids) {
        Ext.Array.each(item_ids.split(","), function (item_id, index) {
            if (item_id) {
                var item = panel.queryById(item_id);
                if (item) item.setDisabled(disabled);
            }
        });
    }
}

//设置panel中item_ids控件是否显示
function setBtnVisibleByIds(panel, item_ids, visible) {
    if (panel && item_ids) {
        Ext.Array.each(item_ids.split(","), function (item_id, index) {
            if (item_id) {
                var item = panel.queryById(item_id);
                if (item) item.setVisible(visible);
            }
        });
    }
}

//设置form中item_ids输入框是否可空
function setFieldAllowBlank(form, item_ids, allowBlank) {
    if (form && item_ids) {
        Ext.Array.each(item_ids.split(","), function (item_id, index) {
            if (item_id) {
                var field = form.queryById(item_id);
                if (field) field.allowBlank = allowBlank;
            }
        });
    }
}

//设置form中item_ids输入框是否可空
function setFieldReadOnly(form, item_ids, state) {
    if (form && item_ids) {
        var readonly = state;
        if (!readonly) readonly = false;
        Ext.Array.each(item_ids.split(","), function (item_id, index) {
            if (item_id) {
                var field = form.queryById(item_id);
                if (field) field.setReadOnly(state);
            }
        });
    }
    if (form) {
        var editors = form.query('[isFormField]');
        for (var i = 0; i < editors.length; i++) {
            editors[i].setReadOnly(true);
        }
    }
}

//设置form中item_ids输入框是否可用与可空
function setDisabledAndBlank(form, item_ids, state) {
    if (form && item_ids) {
        setBtnDisabledByIds(form, item_ids, state);
        setFieldAllowBlank(form, item_ids, state);
    }
}

//获取form中item_id输入框的值
function getFormFieldValue(panel, item_id, val) {
    var value = val;
    var control = panel.queryById(item_id);
    if (control) value = control.getValue();
    return value;
}

function getFormFieldValues(form, item_ids, val) {
    var vals = [];
    if (form && item_ids) {
        Ext.Array.each(item_ids.split(","), function (item_id, index) {
            vals[vals.length] = getFormFieldValue(form, item_id, val);
        });
    }
    return vals;
}

function getEditorFieldValue(editor) {
    if (!editor) return "";
    var xtype = editor.xtype;
    var format = editor.format;
    var value = editor.getValue();
    if (xtype == "datefield" || xtype == "datetimefield" || xtype == "timefield") {
        value = Ext.util.Format.date(value, format);
    } else if (xtype == "checkboxgroup" || xtype == "radiogroup") {
        var boxs = editor.getChecked(), ids = [], texts = [];
        for (var i = 0; i < boxs.length; i++) {
            ids[ids.length] = boxs[i].inputValue;
            texts[texts.length] = boxs[i].boxLabel;
        }
        value = ids.join(",");
    }
    return value;
}

function getEditorFieldLabel(editor, columns) {
    var fieldLabel = editor.fieldLabel;
    if (!fieldLabel && columns instanceof Array && editor.name) {
        for (var i = 0; i < columns.length; i++) {
            if (columns[i].dataIndex == editor.name) {
                fieldLabel = columns[i].text;
                break;
            }
        }
    }
    if (fieldLabel) fieldLabel = fieldLabel + "：";
    else fieldLabel = "";
    return fieldLabel;
}

//record拷贝指定字段
function recordCopyFields(record, target, fields) {
    if (record && target && fields) {
        Ext.Array.each(fields.split(","), function (field, index) {
            var str = target.get(field);
            if (typeof (str) != 'undefined') record.set(field, str);
        });
    }
}

//Ext隐藏加载框
function extBodyUnmask() {
    Ext.getBody().unmask();
}

function getLenChar(char, len) {
    var str = "";
    for (var i = 0; i < len; i++)
        str += char;
    return str;
}

//下载上传的附件
function downLoadFile(value) {
    //alert(encodeURI(value));
    window.open("info/file/up/download?value=" + base64encode(encodeURI(value)));
}

//下载生成的附件
function downLoadBuildFile(value) {
    window.open("info/file/gen/download?value=" + base64encode(encodeURI(value)));
}

///获取Grid分页数
function getGridPageSize(cookieName) {
    cookieName += "";
    if (cookieName.indexOf("grid_") == -1)
        cookieName = "grid_" + cookieName; //cookieName是itemid
    var pageSize = getCookie(cookieName);
    if (pageSize == null) pageSize = defaultPageSize;
    if (pageSize == null) pageSize = 40;
    return pageSize;
}

///设置Grid分页数
function setGridPageSize(cookieName, pageSize) {
    setCookie(cookieName, pageSize);
    return pageSize;
}

//根据左边菜单树的record,获取ItemIds(功能权限的ItemIds)
function getItemIds(record) {
    var item_ids = "";
    try {
        item_ids = record.getProxy().extraParams.item_ids;
        if (!item_ids) item_ids = record.store.proxy.extraParams.item_ids;
    } catch (e) {
    }
    return item_ids;
}

function isExistsItemIds(itemIds, itemId) {
    var isExists = false;
    if (itemIds) {
        Ext.Array.each(itemIds.split(","), function (id, index) {
            if (itemId == id) {
                isExists = true;
                return false;
            }
        });
    }
    return isExists;
}


//grid点击后自动清空
function setItemClickFlag(val) {
    itemClickFlag = val;
    setTimeout(function () {
        itemClickFlag = "";
    }, 300);
}

//获取所有展开结点
function sutExpandedNode(node, idPaths) {
    if (node) {
        var records = node.childNodes;
        for (var i = 0; i < records.length; i++) {
            if (records[i].isExpanded()) {
                //records[i].collapse();
                //records[i].set("expanded", null);
                idPaths[idPaths.length] = records[i].getPath("id");
                sutExpandedNode(records[i], idPaths);
            }
        }
    }
}

//清理树结点及节点下所有结点
function removeTreeNode(node) {
    if (!node) {
        return;
    }
    while (node.hasChildNodes()) {
        removeTreeNode(node.firstChild);
        node.removeChild(node.firstChild);
    }
}

//树刷新结点保存展开、选择不变
function refreshTreeNode(tree) {
    var idPaths = [];
    sutExpandedNode(tree.getRootNode(), idPaths);
    var records = tree.getSelectionModel().getSelection();
    for (var i = 0, len = records.length; i < len; i++) {
        idPaths[idPaths.length] = records[i].getPath("id");
    }
    tree.getSelectionModel().deselectAll(true);
    tree.getStore().load({
        node: tree.getRootNode(), //刷新根节点
        callback: function () {
            for (var i = 0, len = idPaths.length; i < len; i++) {
                // var str=idPaths[i].split("/");
                // alert(str[str.length-1]);
                tree.expandPath(idPaths[i], 'id');
                //tree.selectPath(idPaths[i]);
            }
        }
    });
}

//允许上传的文件类型
function getUploadFileTypeDatas() {
    var data = [];
    if (fileUploadType) {
        var attrs = fileUploadType.split(";");
        attrs.forEach(function (item) {
            data[data.length] = {"id": item, "name": item};
        });
    }
    return data;
}

//Extjs允许上传的文件大小(M)
function getFileMaxSize() {
    if (!fileUploadSize) return 10;
    return fileUploadSize;
}

//Extjs上传控件获取允许上传的文件类型
function getUploadFileType() {
    if (!fileUploadType) {
        return "*.doc;*.docx;*.jpg;*.png;*.bmp;*.gif";
    }
    return fileUploadType;
}

function getFileName(filename) {
    if (filename) {
        var index1 = filename.lastIndexOf(".");
        var index2 = filename.length;
        var result = filename.substring(index1, index2);//后缀名
        //alert(filename.replace(result,result.toLowerCase()));
        return filename.replace(result, result.toLowerCase());
    }
    return filename;
}

//将用户设置的上传的文件类型转换成Extjs上传控件需要的格式
function convertFileType(uploadFileType) {
    if (uploadFileType) {
        var attrs = uploadFileType.split(",");
        return attrs.join(";");
    }
    return getUploadFileType();
}

/// 设置COOKIE键和值
function setCookie(name, value) {
    $.cookie(name, value);
}

/// 获取COOKIE值
function getCookie(name) {
    return $.cookie(name);
}

function createAMonetary(monetaryText, monetaryUnit, unitText, cookie) {
    $.cookie("monetaryText", monetaryText);
    $.cookie("monetaryUnit", monetaryUnit);
    $.cookie("unitText", unitText);
    Ext.monetaryText = monetaryText; // 跟在数值后面的金额单位文字,如 100.00万
    Ext.monetaryUnit = monetaryUnit; // 显示的数值需要除的分子
    Ext.unitText = unitText;  // 跟在字段后面的单位如 合同金额(万元)
    //    reLocation();
}

//显示js对象所有属性和方法的函数
function showObjProperty(Obj) {
    var PropertyList = '';
    var PropertyCount = 0;
    for (i in Obj) {
        if (Obj.i != null)
            PropertyList = PropertyList + i + '属性：' + Obj.i + '\r\n';
        else
            PropertyList = PropertyList + i + '方法\r\n';
    }
    alert(PropertyList);
}

function lookProperty(obj) {
    var Property = "";
    for (var i in obj) {
        Property += "属性：" + i + "\r\n";
    }
    alert(Property);
}


function strToInt(str) {
    if (!isNaN(str)) return parseInt(str);
    else return 0;
}

function strToFloat(str) {
    if (!isNaN(str)) return parseFloat(str);
    else return 0;
}

function IEVersion() {
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
    var isEdge = userAgent.indexOf("Edge") > -1 && !isIE; //判断是否IE的Edge浏览器
    var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
    if (isIE) {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        if (fIEVersion == 7) {
            return 7;
        } else if (fIEVersion == 8) {
            return 8;
        } else if (fIEVersion == 9) {
            return 9;
        } else if (fIEVersion == 10) {
            return 10;
        } else {
            return 6;//IE版本<=7
        }
    } else if (isEdge) {
        return 'edge';//edge
    } else if (isIE11) {
        return 11; //IE11
    } else {
        return -1;//不是ie浏览器
    }
}

function browserVersion() {
    return $.browser.version;
}

//合并两个json对象属性为一个对象
function mergeJson(json1, json2) {
    if (typeof json2 == 'undefined') return json1;
    return $.extend({}, json1, json2);
}

////日期格式化问题
function getFormatDate(value, format) {
    if (value instanceof Date) {
        return new Date(value).format(format);
    } else {
        return value;
    }
}

function focusFn(item) {
    try {
        item.el.dom.blur();
    } catch (e) {
    }
}

function addDate(date, days) {
    var d = new Date(date.replace(/-/g, "/"));
    d.setDate(d.getDate() + (days - 1));
    var m = d.getMonth() + 1;
    return d.getFullYear() + '-' + (m < 10 ? "0" : "") + m + '-' + (d.getDate() < 10 ? "0" : "") + d.getDate();
}

//随机生成n以内的数
function rndNum(n) {
    var rnd = 0;
    for (var i = 0; i < n; i++)
        rnd += Math.floor(Math.random() * 30);
    return rnd;
}

//数字前面自动补零
function prefixInteger(num, length) {
    return (Array(length).join("0") + num).slice(-length);
}

//转换成驼峰命名,initial首字母是否大写
function transferToCamel(str, initial) {
    var newStr = '';
    if (str) {
        var arr = str.split('_');//split是分隔字符串
        if (arr.length < 2) arr = str.split('-');
        for (var i = 0; i < arr.length; i++) {
            var s = arr[i];
            if (i == 0 && !initial) {
                newStr += s;
            } else {
                newStr += s.substr(0, 1).toLocaleUpperCase();
                newStr += s.substr(1, s.length - 1);
            }
        }
    }
    return newStr;
}



