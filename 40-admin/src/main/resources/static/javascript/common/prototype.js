/*
* @Author: zhangxiaxin
* @Date: 2019-10-16
* @Mark: js各对象方法扩展
*/

// 对Date的扩展，将 Date 转化为指定格式的String   
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
// 例子：
// (new Date()).Format("yyyy-MM-dd HH:mm:ss.S") ==> 2006-07-02 08:09:04.423   
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18  
Date.prototype.Format = function (fmt) { //author: meizz   
    var o = {
        "M+": this.getMonth() + 1,                 //月份   
        "d+": this.getDate(),                    //日   
        "H+": this.getHours(),                   //小时   
        "m+": this.getMinutes(),                 //分   
        "s+": this.getSeconds(),                 //秒   
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度   
        "S": this.getMilliseconds()             //毫秒   
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
//var now = new Date();
//alert("现在时间" + now.Format("yyyy-MM-dd"));
//now.addDays(1); //当前时间加一天，如果参数为负数，则是一天前
//now.addWeeks(-1); //当前时间的一周前，如果参数为正数，则是一周后
Date.prototype.addDays = function (d) {
    this.setDate(this.getDate() + d);
};
Date.prototype.addWeeks = function (w) {
    this.addDays(w * 7);
};
Date.prototype.addMonths = function (m) {
    var d = this.getDate();
    this.setMonth(this.getMonth() + m);
    if (this.getDate() < d)
        this.setDate(0);
};
Date.prototype.addYears = function (y) {
    var m = this.getMonth();
    this.setFullYear(this.getFullYear() + y);
    if (m < this.getMonth()) {
        this.setDate(0);
    }
};

//求得日期相差的天数
Date.prototype.diff = function (date, type) {
    if (type == "days") //相差天数
        return (this.getTime() - date.getTime()) / (24 * 60 * 60 * 1000);
    else if (type == "hours") //相差小时数
        return (this.getTime() - date.getTime()) / (60 * 60 * 1000);
    else if (type == "minutes") //相差分钟数
        return (this.getTime() - date.getTime()) / (60 * 1000);
    else if (type == "seconds") //相差秒数
        return (this.getTime() - date.getTime()) / (1000);
    return (this.getTime() - date.getTime())  //相差毫秒
}

//数组删除指定值
Array.prototype.removeByValue = function (val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) {
            this.splice(i, 1);
            break;
        }
    }
}

//数组添加值
Array.prototype.appendByValue = function (val) {
    var exist = false;
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) {
            exist = true;
            break;
        }
    }
    if (!exist) this.push(val);
}

//JQuery 动态加载CSS与JS脚本文件
$.extend({
    includePath: '',
    app_name: "",  //extjs
    load_script_count: 0,
    include: function (file) {
        var files = typeof file == "string" ? [file] : file;
        for (var i = 0; i < files.length; i++) {
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            if (isCSS) {
                $("<link>").attr({
                    rel: "stylesheet",
                    type: "text/css",
                    href: $.app_name + name
                })
                    .appendTo("head");
            }
            else {
                jQuery.getScript($.app_name + name, function (data, status, jqxhr) {
                    $.load_script_count++;
                });
            }
        }
    },
    isLoadCompleted: function (count) {
        if ($.load_script_count >= count) {
            $.loadCompleted();
        }
        else {
            setTimeout(function () {
                $.isLoadCompleted(count);
            }, 100);
        }
    },
    loadCompleted: function () {
    }
});

String.space = function (len) {
    var t = [], i;
    for (i = 0; i < len; i++) {
        t.push(' ');
    }
    return t.join('');
};

String.prototype.format = function (args) {
    var result = this;
    if (arguments.length > 0) {
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                if (args[key] != undefined) {
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
        }
        else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    //var reg = new RegExp("({[" + i + "]})", "g");//这个在索引大于9时会有问题，谢谢何以笙箫的指出
                    var reg = new RegExp("({)" + i + "(})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
}