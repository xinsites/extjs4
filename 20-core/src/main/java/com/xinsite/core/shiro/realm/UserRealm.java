package com.xinsite.core.shiro.realm;

import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.enums.OnlineStatus;
import com.xinsite.core.shiro.service.PasswordService;
import com.xinsite.core.shiro.session.OnlineSession;
import com.xinsite.core.shiro.session.OnlineSessionDAO;
import com.xinsite.core.shiro.verify.CredentialsMatcher;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.mybatis.datasource.master.bll.BLL_SysUser;
import com.xinsite.mybatis.datasource.master.entity.SysUser;
import com.xinsite.mybatis.datasource.master.entity.SysUserOnline;
import com.xinsite.mybatis.datasource.master.service.SysUserOnlineService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    private PasswordService passwordService;

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;

    @Autowired
    private SysUserOnlineService onlineService;

    /**
     * 授权认证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        //从凭证中获得用户名
        LoginUser loginUser = UserUtils.getLoginUser();
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        SimpleAuthorizationInfo autho = new SimpleAuthorizationInfo();
        if (UserUtils.isSuperAdminer()) {
            autho.addRole("admin"); // 超级管理员拥有所有权限
            autho.addStringPermission("*:*:*");
        } else {
            autho.setRoles(BLL_UserPower.getUserRolePermissions(loginUser.getRoleId()));
            autho.setStringPermissions(BLL_UserPower.getUserFunPermissions(loginUser));
        }
//        autho.addRole("admin"); // 超级管理员拥有所有权限
//        autho.addStringPermission("*:*:*");
        return autho;
    }

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());
        SysUser user = BLL_SysUser.getSysUserByName(username);
//        if (user == null && verifyUtils.isEmail(username)) user = BLL_SysUser.getSysUserByEmail(username);
//        if (user == null && verifyUtils.isPhone(username)) user = BLL_SysUser.getSysUserByPhone(username);

        if (user != null) {
            passwordService.validate(user, password);
            if (user.getIsdel() == 1 && user.getUserId() != 1) {
                throw new AuthenticationException(MessageUtils.message("user.name.delete"));
            } else if (user.getUserState() == 0) {
                throw new AuthenticationException(MessageUtils.message("user.name.stop"));
            }
            LoginUser loginUser = UserUtils.getLoginUser(user);
            ShiroUtils.setCookie(upToken.isRememberMe());
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(loginUser, user.getPassword(), getName());
            return info;
        }
        throw new AuthenticationException(MessageUtils.message("user.not.exists"));
    }

    @PostConstruct
    public void initCredentialsMatcher() {
        //重写shiro的密码验证，让shiro用我自己的验证
        setCredentialsMatcher(new CredentialsMatcher());
    }

    /**
     * 指定用户下线
     */
    public void kickoutUser(int user_id, String off_msg, String sessionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", OnlineStatus.在线.getValue()); //所有在线用户
        List<SysUserOnline> onlines = onlineService.getSysUserOnlineList(params);
        for (SysUserOnline userOnline : onlines) {
            if (userOnline.getUserId() == user_id) {
                if (userOnline.getSessionId().equals(sessionId)) continue; //当前登录用户不退出
                OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(userOnline.getSessionId());
                onlineSession.setStatus(OnlineStatus.离线);
                onlineSession.setAttribute("off_msg", off_msg);
                onlineSessionDAO.update(onlineSession);
                userOnline.setStatus(OnlineStatus.离线.getValue());
                onlineService.saveSysUserOnline(userOnline);
            }
        }
    }

    /**
     * 指定sessionId的用户下线
     */
    public void kickoutUser(String session_ids, String off_msg) {
        if (StringUtils.isEmpty(session_ids)) return;
        Map<String, Object> params = new HashMap<>();
        params.put("status", OnlineStatus.在线.getValue()); //所有在线用户
        List<SysUserOnline> onlines = onlineService.getSysUserOnlineList(params);
        List<String> list = StringUtils.stringToList(session_ids);
        for (SysUserOnline userOnline : onlines) {
            if (list.contains(userOnline.getSessionId())) {
                OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(userOnline.getSessionId());
                onlineSession.setStatus(OnlineStatus.离线);
                onlineSession.setAttribute("off_msg", off_msg);
                onlineSessionDAO.update(onlineSession);
                userOnline.setStatus(OnlineStatus.离线.getValue());
                onlineService.saveSysUserOnline(userOnline);
            }
        }
    }

    /**
     * 清理指定用户权限缓存
     */
    public void clearCachedAuthorizationInfo(int user_id) {
        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null) {
            for (Object key : cache.keys()) {
                if (key instanceof PrincipalCollection) {
                    PrincipalCollection info = (PrincipalCollection) key;
                    LoginUser loginUser = (LoginUser) info.getPrimaryPrincipal();
                    if (loginUser.getUserId() == user_id) {
                        cache.remove(key);
                    }
                }
            }
        }
    }

    /**
     * 清理所有登录权限缓存
     */
    public void clearAllCachedAuthorizationInfo() {
        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null) {
            for (Object key : cache.keys()) {
                //System.out.println(key + ":" + key.toString());
                cache.remove(key);
            }
        }
    }
}
