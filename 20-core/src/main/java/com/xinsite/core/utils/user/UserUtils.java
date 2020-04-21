package com.xinsite.core.utils.user;

import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.network.IpUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.mybatis.datasource.master.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {

    /**
     * 获取Session用户
     */
    public static LoginUser getLoginUser() {
        LoginUser user = ShiroUtils.getShiroUser();
        if (user != null) return user;
        return new LoginUser();
    }

    /**
     * 登录用户是否停用
     */
    public static boolean isStop() {
        LoginUser user = ShiroUtils.getShiroUser();
        if (user != null) return user.getUserState() == 0;
        return false;
    }

    /**
     * 获取登录用户Id
     */
    public static int getUserId() {
        return UserUtils.getLoginUser().getUserId();
    }

    public static String getUserName() {
        return UserUtils.getLoginUser().getUserName();
    }

    public static int getOrgId() {
        return UserUtils.getLoginUser().getOrgId();
    }

    public static int getDeptId() {
        return UserUtils.getLoginUser().getDeptId();
    }

    public static int getRoleId() {
        return UserUtils.getLoginUser().getRoleId();
    }

    public static boolean isSuperAdminer() {
        return UserUtils.getLoginUser().isSuperAdminer();
    }

    public static String getLoginName() {
        return UserUtils.getLoginUser().getLoginName();
    }

    /**
     * 获取登录用户
     */
    public static LoginUser getLoginUser(SysUser sysUser) {
        LoginUser user = new LoginUser();
        user.setUserId(sysUser.getUserId());
        user.setUserName(sysUser.getUserName());
        user.setLoginName(sysUser.getLoginName());
        user.setOrgId(sysUser.getOrgId());
        user.setOrganizeName(DBFunction.getFieldNameById("company_name", "sys_organize", "org_id", sysUser.getOrgId()));
        user.setRoleId(sysUser.getRoleId());
        user.setRoleName(DBFunction.getFieldNameById("role_name", "sys_role", "role_id", sysUser.getRoleId()));
        user.setDeptId(sysUser.getDeptId());
        user.setDeptName(DBFunction.getFieldNameById("dept_name", "sys_dept", "dept_id", sysUser.getDeptId()));
//        user.setPostId(sysUser.getPostId());
//        user.setPostName(DBFunction.getFieldNameById("post_name", "sys_post", "post_id", sysUser.getPostId()));
        user.setUserState(sysUser.getUserState());
        user.setHeadPhoto(sysUser.getHeadPhoto());
        HttpServletRequest request = ServletUtils.getRequest();
        user.setLoginIp(IpUtils.getRemoteAddr(request));
        int super_role = Global.getInt("config.super_role");
        user.setSuperAdminer(sysUser.getRoleId() == super_role);
        return user;
    }
}
