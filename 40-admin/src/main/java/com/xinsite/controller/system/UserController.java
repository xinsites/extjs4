package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonGrid;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.model.system.PowerSearchModel;
import com.xinsite.core.bll.permission.BLL_PowerInfo;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.bll.system.*;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.utils.FileWebUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-08-26
 * object name:系统管理->用户管理
 */

@RestController
@RequestMapping(value = "system/user")
public class UserController extends BaseController {
    @Autowired
    private UserCacheService userCacheService;

    // [用户管理]信息查询列表
    @RequestMapping(value = "grid")
    @RequiresPermissions("system:user:grid")
    public String grid(HttpServletRequest request) {
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.isdel=0 and a1.org_id=" + UserUtils.getOrgId();
            pager.showColumns = "a1.user_id,a1.user_name,a1.login_name,a1.head_photo,a1.user_sex,a1.org_id,a1.role_id,a1.dept_id,a1.user_state,a1.issys,a1.remark,a1.serialcode";
            pager.tables = "sys_user a1";
            pager.loadPageGrid("a1.serialcode desc,a1.create_time desc");

            BLL_Role.setGridRoleNames(pager.array, "role_id");
            BLL_Dept.setGridDeptText(pager.array, "dept_id");
            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("用户信息查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //[用户管理]编辑单元格存储
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:user:save")
    public String editing(HttpServletRequest request) {
        int user_id = getParaValue(request, "Id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (user_id > 0 && !StringUtils.isEmpty(field)) {
                if (field.equalsIgnoreCase("text")) field = "user_name";
                Map ht = new HashMap();
                ht.put(field, value);
                DBFunction.updateByTbName(ht, "sys_user", "user_id=" + user_id);
                userCacheService.changeUserInfoFlag(user_id);
                LogUtils.addOperateLog(item_id, "用户单元格编辑", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("用户单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }


    // [用户管理]列表拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:user:sort")
    public String sort(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String sort_vals = getParaValue(request, "sort_vals", "");
            if (!StringUtils.isEmpty(sort_vals)) {
                BLL_User.saveDesignTableSort("user_id", sort_vals);
            }

            LogUtils.addOperateLog(item_id, "用户排序", "成功");
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("用户排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[用户管理]列表删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:user:del")
    public String delete(HttpServletRequest request) {
        String user_ids = getParaValue(request, "ids", "0");
        int item_id = getParaValue(request, "item_id", 0);
        boolean success = false;
        try {
            user_ids = StringUtils.joinAsFilter(user_ids);
            if (!StringUtils.isEmpty(user_ids)) {
                success = BLL_User.deleteByIds("user_id", user_ids);
            }
            if (success) {
                LogUtils.addOperateLog(item_id, "用户删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("用户删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [用户管理]获取信息
    @RequestMapping(value = "info")
    public String info(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        try {
            if (user_id > 0) {
                int leader = BLL_Leaders.getUserLeader(user_id);
                String sql = StringUtils.format("select *,{0} as leader from sys_user where user_id={1}", leader, user_id);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "user");
            }
        } catch (Exception ex) {
            LogError.write("用户获取信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[用户管理]登录名是否存在
    @RequestMapping(value = "isexist")
    public String isexist(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        String login_name = getParaValue(request, "login_name", "");

        try {
            boolean bl = BLL_User.isExistLoginName(user_id, login_name);
            return ret.getSuccessResult("isexist", bl ? 1 : 0);
        } catch (Exception ex) {
            LogError.write("登录名是否存在", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[用户管理]用户重置密码
    @RequestMapping(value = "resetpwd")
    @RequiresPermissions("system:user:reset_pwd")
    public String resetpwd(HttpServletRequest request) {
        String user_ids = getParaValue(request, "ids", "0");
        int item_id = getParaValue(request, "item_id", 0);
        boolean success = false;
        try {
            user_ids = StringUtils.joinAsFilter(user_ids);
            if (!StringUtils.isEmpty(user_ids)) {
                success = BLL_PassWord.setUserPassword(user_ids, "reset_password");
            }

            if (success) {
                LogUtils.addOperateLog(item_id, "用户重置密码", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("用户重置密码", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 权限树形目录获取
    @RequestMapping(value = "pertree")
    public String pertree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        int user_id = getParaValue(request, "user_id", 0);
        int self = getParaValue(request, "self", 0);  //只包含配置栏目
        String per_data = getParaValue(request, "per_data", "");
        PowerSearchModel powerModel = new PowerSearchModel();
        try {
            String sql_where = " and a.pid=" + node;
            if (!StringUtils.isEmpty(per_data)) {
                powerModel = GsonUtils.getBean(per_data, PowerSearchModel.class);
            }
            JsonArray dt = BLL_UserPower.getUserPerItemTree(sql_where, user_id, self, powerModel);
            BLL_PowerInfo.setCheckGroup(dt);
            return JsonTree.getTreeJson(dt, "checked:false");
        } catch (Exception ex) {
            LogError.write("权限树目录获取", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // 用户权限获取
    @RequestMapping(value = "perinfo")
    public String perinfo(HttpServletRequest request) {
        int cur_userid = getParaValue(request, "cur_userid", 0); //当前选择用户
        String per_data = getParaValue(request, "per_data", "");
        PowerSearchModel powerModel = new PowerSearchModel();
        try {
            if (!StringUtils.isEmpty(per_data)) {
                powerModel = GsonUtils.getBean(per_data, PowerSearchModel.class);
            }
            if (powerModel.role_per == 1 && BLL_User.isSuperAdminer(cur_userid)) {
                return ret.getSuccessResult("isSuperAdminer", true);
            }
            JsonArray dt = BLL_UserPower.getUserPermission(cur_userid, powerModel);
            if (dt != null) return "{success:true,data:" + JsonGrid.getGridJson(dt) + "}";
            return "{success:true,data:[]}";
        } catch (Exception ex) {
            LogError.write("用户权限获取", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[用户管理]信息新增/修改
    @RequestMapping(value = "save")
    @RequiresPermissions("system:user:save")
    public String save(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        int role_id = getParaValue(request, "role_id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String login_name = getParaValue(request, "login_name", "");    //登录名
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("org_id", UserUtils.getOrgId());                         //所属机构为当前登录用户机构
            ht.put("user_name", getParaValue(request, "user_name", ""));    //用户姓名
            ht.put("dept_id", getParaValue(request, "dept_id", ""));        //用户部门
            ht.put("login_name", login_name);    //登录名
            ht.put("role_id", getParaValue(request, "role_id", "null"));    //用户角色
            ht.put("birthday", getParaValue(request, "birthday", ""));      //出生日期
            ht.put("user_sex", getParaValue(request, "user_sex", ""));      //性别
            ht.put("post_id", getParaValue(request, "post_id", "null"));    //用户职位
            ht.put("email", getParaValue(request, "email", ""));            //电子邮箱
            ht.put("phone", getParaValue(request, "phone", ""));            //手机号码
            ht.put("oicq", getParaValue(request, "oicq", ""));              //QQ
            ht.put("workphone", getParaValue(request, "workphone", ""));    //工作手机号
            ht.put("subtelephone", getParaValue(request, "subtelephone", "")); //分机号
            ht.put("wechat", getParaValue(request, "wechat", ""));              //微信
            ht.put("user_state", getParaValue(request, "user_state", "1"));     //启用状态
            ht.put("remark", getParaValue(request, "remark", ""));              //备注
            if (BLL_User.isExistLoginName(user_id, login_name)) {
                return ret.getFailResult("该登录名已经存在！");
            } else {
                boolean isAdd = (user_id == 0);
                int re_role_id = BLL_User.getFieldByUser(user_id, "role_id", 0);
                user_id = BLL_User.saveInfo(ht, user_id);
                if (user_id > 0) {
                    if (!isAdd) {
                        userCacheService.changeUserInfoFlag(user_id);
                        if (role_id != re_role_id) { //角色改变，权限重新认证
                            ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                        }
                    } else {
                        BLL_PassWord.setUserPassword(user_id + "", "add_password");
                    }
                    LogUtils.addOperateLog(item_id, "用户" + (isAdd ? "新增" : "修改"), "成功");
                    return ret.getSuccessResult(user_id);
                } else {
                    LogUtils.addOperateLog(item_id, "用户" + (isAdd ? "新增" : "修改"), "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("用户新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 下拉列表（全部数据）
    @RequestMapping(value = "combo")
    public String combo(HttpServletRequest request) {
        String query = getParaValue(request, "query", "").trim();
        int dept_id = getParaValue(request, "dept_id", 0);
        try {
            String where = StringUtils.EMPTY;
            List<DBParameter> ls = new ArrayList<>();
            if (!StringUtils.isEmpty(query)) {
                where += " and (a1.user_name like @user_name or a1.login_name like @login_name)";
                ls.add(new DBParameter("@user_name", "%" + query + "%"));
                ls.add(new DBParameter("@login_name", "%" + query + "%"));
            }
            if (dept_id > 0) {
                where += " and dept_id=@dept_id";
                ls.add(new DBParameter("@dept_id", dept_id));
            }
            JsonArray dt = BLL_DataSource.getArray("ds.sys.user", where, ls);
            return retGrid.getGridJson(dt, dt.size(), "id,name,login_name,disabled");
        } catch (Exception ex) {
            LogError.write("用户下拉列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //region==========个人中心==========
    //[用户管理]用户基本信息修改
    @RequestMapping(value = "center/baseinfo")
    public String saveUserBaseInfo(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String login_name = getParaValue(request, "login_name", "");    //登录名
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("user_name", getParaValue(request, "user_name", ""));    //用户姓名
            ht.put("login_name", login_name);    //登录名
            ht.put("birthday", getParaValue(request, "birthday", ""));    //出生日期
            ht.put("user_sex", getParaValue(request, "user_sex", ""));        //性别

            if (BLL_User.isExistLoginName(UserUtils.getUserId(), login_name)) {
                return ret.getFailResult("该登录名已经存在！");
            } else {
                int User_Id = BLL_User.saveInfo(ht, UserUtils.getUserId());
                if (User_Id > 0) {
                    LoginUser loginUser = UserUtils.getLoginUser();
                    loginUser.setUserName(ht.get("user_name").toString());
                    loginUser.setLoginName(ht.get("login_name").toString());
                    //ShiroUtils.runAsPrincipal(loginUser);  //多个用户登录无效
                    userCacheService.changeUserInfoFlag(loginUser.getUserId());
                    LogUtils.addOperateLog(item_id, "用户基本信息修改", "成功");
                    return ret.getSuccessResult(User_Id);
                } else {
                    LogUtils.addOperateLog(item_id, "用户基本信息修改", "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("用户基本信息修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[用户管理]用户联系方式修改
    @RequestMapping(value = "center/linkinfo")
    public String saveUserLinkInfo(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("email", getParaValue(request, "email", ""));    //电子邮箱
            ht.put("phone", getParaValue(request, "phone", ""));    //个人手机号
            ht.put("workphone", getParaValue(request, "workphone", ""));       //工作手机号
            ht.put("subtelephone", getParaValue(request, "subtelephone", "")); //分机号
            ht.put("oicq", getParaValue(request, "oicq", ""));        //QQ
            ht.put("wechat", getParaValue(request, "wechat", ""));        //微信

            int User_Id = BLL_User.saveInfo(ht, UserUtils.getUserId());
            if (User_Id > 0) {
                LogUtils.addOperateLog(item_id, "用户联系方式修改", "成功");
                return ret.getSuccessResult(User_Id);
            } else {
                LogUtils.addOperateLog(item_id, "用户联系方式修改", "失败");
            }
        } catch (Exception ex) {
            LogError.write("用户联系方式修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[用户管理]用户头像修改
    @RequestMapping(value = "center/savephoto")
    public String saveUserPhoto(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String VisualPath = FileWebUtils.saveFile(request, ".jpg|.gif|.bmp|.png", 1024 * 1024);
            if (!StringUtils.isEmpty(VisualPath)) {
                Map ht = new HashMap();
                ht.put("modify_time", DateUtils.getDateTime());
                ht.put("head_photo", VisualPath);

                int User_Id = BLL_User.saveInfo(ht, UserUtils.getUserId());
                if (User_Id > 0) {
                    LoginUser loginUser = UserUtils.getLoginUser();
                    loginUser.setHeadPhoto(VisualPath);
                    //ShiroUtils.runAsPrincipal(loginUser);
                    userCacheService.changeUserInfoFlag(loginUser.getUserId());
                    LogUtils.addOperateLog(item_id, "用户头像修改", "成功");
                    ret.clear();
                    ret.addMap("id", User_Id);
                    ret.addMap("msg", java.net.URLEncoder.encode(VisualPath, "utf-8"));
                    return ret.getSuccessResult();
                } else {
                    LogUtils.addOperateLog(item_id, "用户头像修改", "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("用户头像修改", LogEnum.Error, ex.toString());
            return ret.getFailResult(ex.getMessage());
        }
        return ret.getFailResult();
    }

    //[用户管理]用户密码修改
    @RequestMapping(value = "center/modpwd")
    public String saveUserPassword(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String level_old_password = getParaValue(request, "level_old_password", "");
        try {
            Map ht = new HashMap();
            if (!BLL_PassWord.validatePassword(UserUtils.getUserId(), level_old_password)) {
                return ret.getFailResult("旧密码输入不正确！");
            } else {
                String level_password = getParaValue(request, "level_password", "");
                if (StringUtils.isEmpty(level_password)) return ret.getFailResult();
                String pwd_salt = IdGenerate.buildUUID();  //重新更改密码盐
                String new_password = BLL_PassWord.getUserPassword(level_password, pwd_salt);
                ht.put("modify_time", DateUtils.getDateTime());
                ht.put("pwd_salt", pwd_salt);         //重置密码盐
                ht.put("password", new_password);     //新密码

                int User_Id = BLL_User.saveInfo(ht, UserUtils.getUserId());
                if (User_Id > 0) {
                    LoginUser loginUser = UserUtils.getLoginUser();
                    //ShiroUtils.runAsPrincipal(loginUser);
                    userCacheService.changeUserInfoFlag(loginUser.getUserId());
                    LogUtils.addOperateLog(item_id, "用户密码修改", "成功");
                    return ret.getSuccessResult(User_Id);
                } else {
                    LogUtils.addOperateLog(item_id, "用户密码修改", "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("用户密码修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [用户管理]获取用户日志
    @RequestMapping(value = "center/loggrid")
    public String getUserLogGrid(HttpServletRequest request) {
        int log_type = getParaValue(request, "log_type", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = StringUtils.format(" and a1.isdel=0 and a1.user_id={0}", UserUtils.getUserId());
            pager.showColumns = "a1.log_id,a1.log_fun,a1.log_ip,a1.action_type,a1.log_message,a1.log_result,a1.create_time,a1.user_id,a2.user_name,a1.serialcode";
            pager.tables = "sys_log a1 left join sys_user a2 on a1.user_id=a2.user_id";
            if (log_type > 0) pager.where += " and a1.log_type=" + log_type;
            pager.loadPageGrid();

            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("用户日志查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // [用户管理]获取用户意见建议
    @RequestMapping(value = "center/forumgrid")
    public String getUserForumGrid(HttpServletRequest request) {
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.isdel=0";
            pager.showColumns = "a1.idleaf,a1.title,a1.forum_type,a1.create_time,a1.isread,a1.create_uid,a1.serialcode";
            pager.tables = "tb_forum a1";
            pager.where += " and a1.create_uid=" + UserUtils.getUserId();
            pager.loadPageGrid("a1.serialcode desc,a1.create_time desc");

            pager.addTextUserOrDept(); //列表加用户或者部门名称
            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("[意见建议]查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }
    //endregion

    // 根据用户ID，获取用户名称
    @RequestMapping(value = "user_name")
    public String userName(HttpServletRequest request) {
        String Ids = getParaValue(request, "ids", "0");
        try {
            return ret.getSuccessResult("texts", BLL_User.getUserNames(Ids));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
}


