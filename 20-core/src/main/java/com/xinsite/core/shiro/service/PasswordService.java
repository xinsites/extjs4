package com.xinsite.core.shiro.service;

import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.codec.Md5Utils;
import com.xinsite.common.uitls.codec.RSAUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.core.bll.system.BLL_PassWord;
import com.xinsite.core.model.user.LoginTimes;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.utils.ValidCodeUtils;
import com.xinsite.mybatis.datasource.master.entity.SysUser;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * 登录密码检查
 *
 * @author zhangxiaxin
 */
@Component
public class PasswordService {
    @Autowired
    private CacheManager cacheManager;

    private Cache<Integer, LoginTimes> loginErrorsCache;

    @PostConstruct
    public void init() {
        loginErrorsCache = cacheManager.getCache(ShiroConstant.LOGIN_ERRORS_CACHE);
    }

    /**
     * 用户登录密码检查
     *
     * @param in_password 用户输入密码
     */
    public void validate(SysUser user, String in_password) {
        LoginTimes loginTimes = loginErrorsCache.get(user.getUserId());
        int max_login_errors = SysConfigCache.getLoginErrors(user.getOrgId());
        if (max_login_errors > 0) {
            if (loginTimes == null) loginTimes = new LoginTimes();
            int login_locked = SysConfigCache.getLoginLocked(user.getOrgId());
            if (login_locked > 60) login_locked = 60; //最长锁定60分钟, 缓存最长60
            long past_minutes = DateUtils.pastMinutes(loginTimes.getLastLoginTime());
            if (past_minutes > login_locked) loginTimes = new LoginTimes();

            if (loginTimes.getErrorCount().incrementAndGet() > max_login_errors) {
                String error_msg = MessageUtils.message("user.password.retry.limit", max_login_errors, login_locked);
                throw new AuthenticationException(error_msg);
            }
        }

        if (!matchesRSA(user, in_password)) {
            String error_msg = MessageUtils.message("user.password.not.match");
            if (max_login_errors > 0 && loginTimes != null) {
                error_msg = MessageUtils.message("user.password.retry.error", loginTimes.getErrorCount());
                loginTimes.setLastLoginTime(new Date());
                loginErrorsCache.put(user.getUserId(), loginTimes);
            }
            throw new AuthenticationException(error_msg);
        } else {
            clearLoginRecordCache(user.getUserId());
        }
    }

    public boolean matchesRSA(SysUser user, String in_password) {
        String private_Key = ValidCodeUtils.getPrivateKey();
        String de_password = RSAUtils.decrypt(in_password, private_Key);
        String code = ValidCodeUtils.getValidateCode().toLowerCase();
        if (!de_password.endsWith(code)) return false;
        String level_password = de_password.substring(0, de_password.length() - code.length());  //用户输入的第一层密码
        String user_password = BLL_PassWord.getUserPassword(level_password, user.getPwdSalt());
        return user_password.equals(user.getPassword());
    }

    public boolean matches(SysUser user, String in_password) {
        return in_password.equals(encryptPassword(user));
    }

    public String encryptPassword(SysUser user) {
        String server_pwd = Md5Utils.md5(user.getPassword() + ValidCodeUtils.getValidateCode().toLowerCase());
        return server_pwd;  //服务端密码
    }

    public void clearLoginRecordCache(int user_id) {
        loginErrorsCache.remove(user_id);
    }

}
