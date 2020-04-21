//共用请求链接
var path_url = {
    info: {
        util: {  //设计管理工具
            buildspell: "info/util/buildspell"          //生成汉字拼音
            , username: "info/util/username"            //获取用户名
            , rolename: "info/util/rolename"            //获取角色名
            , deptname: "info/util/deptname"            //获取部门名
            , item_level: "info/util/left/item/level"   //一级栏目
            , item_tree: "info/util/left/item/tree"     //栏目下的树形菜单
            , user_permit: "info/util/user/item/permit" //用户某栏目权限
            , item_info: "info/util/item/info"          //获取某个栏目信息
            , fixtab_save: "info/util/user/fixtab/save" //用户固定标签保存
            , fixtab_get: "info/util/user/fixtabs/get"  //用户设置的固定标签数
        }
    },
    system: {  //系统管理
        user: {
            info: "system/user/info"                //用户信息获取
            , isexist: "system/user/isexist"        //用户登录名是否存在
        },
        member: {
            grid: "system/member/grid"              //成员列表获取
            , delete: "system/member/delete"        //成员移除
            , deptusers: "system/member/deptusers"  //获取部门用户
            , save: "system/member/save"            //成员保存
        },
        item: {  //栏目管理
            flowitem: "system/item/flowitem"        //根据栏目Id获取所有流程列表栏目Id
            , tree: "system/item/where/tree"        //栏目树形目录获取
        },
        power: {  //权限管理
            tree: "system/power/tree"           //权限树形目录获取
            , info: "system/power/info"         //权限信息获取
            , realsave: "system/power/realsave"     //实时权限保存
            , btnsave: "system/power/btnsave"       //按钮权限保存
            , clear: "system/power/clear"           //按钮权限清空
            , datasave: "system/power/datasave"     //数据权限保存
            , item_tree: "system/power/item/tree"   //菜单栏目树
            , user_power: "system/power/user/power" //栏目用户权限查看
        },
        ds: {
            storedata: "system/ds/storedata"        //获取数据源入口(新加)
        }
    }
}


