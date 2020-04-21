/*
* @Mark: js各常用变量
*/

//项目唯一编号
var project_code = "xinsite_demo";

//固定栏目标记
var fixed_item = {};

//项目缓存名称集合
var ck_names = {
    remember_user: "re_u" + project_code, //登录记住用户名
    remember_me: "re_m" + project_code,   //登录记住我
    js_loaded: "load_" + project_code    //缓存预加载js文件标记
}

//应用系统访问路径
var context_path = "";

//是否已经返回登录页面
var isReturnLogin = false;

//用户信息
var userinfo = {};

//单元格点击列区分
var itemClickFlag = "";

//RSA加密公钥
var publicKey = "";

//主页面右边TabPanel
var rightTabPanel = null;

//左边第一个菜单项的树(模拟手动点击菜单用)
var main_left_tree = null;

//右边TabPanel最多打开的tab数
var max_tabs_items = 15;

//项目文件上传公共类型、大小(M)
var fileUploadType = "", fileUploadSize = 1;

//在线计算相关信息Timeout
var t_onlinestatic;

//Extjs框架日期选择格式
var F_YMDHM = "Y-m-d H:i", F_YMDHMS = "Y-m-d H:i:s", F_YMD = "Y-m-d", F_YM = "Y-m";

//Grid列表默认页面大小
var defaultPageSize = 40;

//默认最小百度编辑工具栏
var mini_ueditor_toolbars = [
    'fullscreen', 'source', '|', 'undo', 'redo', '|',
    'bold', 'italic', 'underline', 'strikethrough', '|',
    'superscript', 'subscript', '|', 'forecolor', 'backcolor', '|',
    'removeformat', 'formatmatch', '|',
    'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
    'paragraph', 'fontfamily', 'fontsize', '|',
    'justifyleft', 'justifycenter', 'justifyright', '|',
    'link', 'unlink', '|',
    'emotion', 'insertimage', 'insertvideo', 'map', '|',
    'horizontal', 'print', 'preview', 'searchreplace', 'spechars'
];

//默认最小kindeditor编辑工具栏
var mini_kindeditor_toolbars = [
    'source', '|', 'undo', 'redo', '|',
    'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
    'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
    'insertunorderedlist', '|', 'emoticons', 'image', 'insertfile', 'link', '|', 'preview', 'print', '|', 'fullscreen'
];

//数据权限默认列表
var dataPer_store = [{"id": "0", "name": "===请选择==="}
    , {"id": "1", "name": "仅限本人"}
    , {"id": "2", "name": " 仅限本人及下属"}
    , {"id": "3", "name": "所在部门"}
    , {"id": "4", "name": "所在公司"}
    , {"id": "5", "name": "自定义部门"}
    , {"id": "6", "name": "自定义用户"}];




