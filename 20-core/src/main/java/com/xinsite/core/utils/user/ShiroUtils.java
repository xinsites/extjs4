package com.xinsite.core.utils.user;

import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.web.CookieUtils;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.shiro.realm.UserRealm;
import com.xinsite.common.uitls.TaskUtils;
import com.xinsite.mybatis.datasource.master.bll.BLL_SysUser;
import com.xinsite.mybatis.datasource.master.entity.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.util.TimerTask;

public class ShiroUtils {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * Shiro登录的Session
     */
    public static Session getSession(boolean create) {
        return getSubject().getSession(create);
    }

    /**
     * 是否拥有权限
     */
    public static boolean isPermittedAll(String... permissions) {
        return SecurityUtils.getSubject().isPermittedAll(permissions);
    }

    /**
     * Shiro登录的SessionId
     */
    public static String getSessionId() {
        Session session = getSession(false);
        if (session != null) return String.valueOf(session.getId());
        return StringUtils.EMPTY;
    }

    /**
     * Shiro中Session是否超时
     */
    public static boolean isSessionOut() {
        Session session = getSession(false);
        if (session == null) return true;
        return false;
    }

    /**
     * Shiro中获取登录用户
     */
    public static LoginUser getShiroUser() {
        return (LoginUser) getSubject().getPrincipal();
    }

    /**
     * Shiro是否记住我登录
     */
    public static boolean isRemembered() {
        return getSubject().isRemembered();
    }

    /**
     * Shiro退出登录
     */
    public static void shiroLogout() {
        Subject subject = getSubject();
        if (subject != null) subject.logout();  //退出登录
    }

    /**
     * 根据用户登录类型，是否在关闭浏览器记录sessionId
     */
    public static void setCookie(Boolean rememberme) {
        Session session = ShiroUtils.getSession(false);
        if (session != null) {
            if (ShiroUtils.isRemembered() || rememberme) { //记住我登录的
                int maxIdleTimeInMillis = 10 * 60 * 60; //10小时
                session.setTimeout(maxIdleTimeInMillis * 1000);
                CookieUtils.setCookie("JSESSIONID", session.getId().toString(), maxIdleTimeInMillis); //浏览器JSESSIONID保存10小时
            } else {
                CookieUtils.setCookie("JSESSIONID", session.getId().toString(), -1); //关闭浏览器,JSESSIONID就会消失
            }
        }
    }

    /**
     * 指定用户下线
     */
    public static void kickoutUser(final int user_id, final String off_msg) {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        final UserRealm realm = (UserRealm) rsm.getRealms().iterator().next();
        final String sessionId = ShiroUtils.getSessionId();
        TaskUtils.getInstance().execute(new TimerTask() {
            @Override
            public void run() {
                realm.kickoutUser(user_id, off_msg, sessionId);
            }
        });
    }

    /**
     * 指定sessionIds用户下线
     */
    public static void kickoutUser(final String sessionIds, final String off_msg) {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        final UserRealm realm = (UserRealm) rsm.getRealms().iterator().next();
        realm.kickoutUser(sessionIds, off_msg);
    }

    /**
     * 清空权限认证，重新查询
     */
    public static void clearCachedAuthorizationInfo(final String tb_type, final int tb_id) {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        final UserRealm realm = (UserRealm) rsm.getRealms().iterator().next();
        TaskUtils.getInstance().execute(new TimerTask() {
            @Override
            public void run() {
                if (tb_type.equals("user"))
                    realm.clearCachedAuthorizationInfo(tb_id);
                else
                    realm.clearAllCachedAuthorizationInfo();
            }
        });
    }

    /**
     * 登录用户变更用户信息
     */
    public static void changePrincipal(LoginUser loginUser) {
        SysUser user = BLL_SysUser.getSysUserById(loginUser.getUserId());
        LoginUser login_user = UserUtils.getLoginUser(user);
        loginUser.setUserState(login_user.getUserState());
        login_user.setChangeUserFlag(loginUser.getChangeUserFlag());
        ShiroUtils.runAsPrincipal(login_user);
    }

    /**
     * 登录用户变更用户信息
     */
    public static void runAsPrincipal(LoginUser loginUser) {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(loginUser, realmName);
        subject.runAs(newPrincipalCollection); // 重新加载Principal
    }
}
