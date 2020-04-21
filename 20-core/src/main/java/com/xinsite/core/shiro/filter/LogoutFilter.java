package com.xinsite.core.shiro.filter;

import com.xinsite.core.model.user.LoginUser;
import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.network.IpUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.utils.log.LogUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;

/**
 * 退出过滤器
 *
 * @author zhangxiaxin
 */
public class LogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {
    private static final Logger log = LoggerFactory.getLogger(LogoutFilter.class);

    /**
     * 退出后重定向的地址
     */
    private String loginUrl;

    private Cache<String, Deque<Serializable>> cache;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        try {
            Subject subject = getSubject(request, response);
            //获取退出后重定向到的地址
            String redirectUrl = getRedirectUrl(request, response, subject);
            try {
                LoginUser loginUser = ShiroUtils.getShiroUser();
                if (loginUser != null) {
                    // 记录用户退出日志
                    String loginIp = IpUtils.getRemoteAddr(ServletUtils.getRequest());
                    LogUtils.addLogByLogin("退出", loginUser.getLoginName(), loginIp);
                    cache.remove(loginUser.getUserId() + ""); // 清理缓存
                }

                subject.logout(); // 退出登录
            } catch (SessionException ise) {
                log.error("退出失败", ise);
            }
            issueRedirect(request, response, redirectUrl); //重定向
        } catch (Exception e) {
            log.error("注销时遇到会话异常。可以安全地忽略这一点.", e);
        }
        return false;
    }

    /**
     * 退出跳转URL
     */
    @Override
    protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
        String url = getLoginUrl();
        if (StringUtils.isNotEmpty(url)) return url;
        return super.getRedirectUrl(request, response, subject);
    }

    // 设置Cache的key的前缀
    public void setCacheManager(CacheManager cacheManager) {
        // 必须和ehcache缓存配置中的缓存name一致
        this.cache = cacheManager.getCache(ShiroConstant.LOGIN_KICKOUT_CACHE);
    }
}
