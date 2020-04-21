package com.xinsite.core.shiro.filter;

import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.common.response.ReturnMap;
import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.codec.EncodeUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.enums.KickoutEnum;
import com.xinsite.core.enums.OnlineStatus;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.shiro.session.OnlineSession;
import com.xinsite.core.shiro.session.OnlineSessionDAO;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.mybatis.helper.DataSource;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求认证过滤器
 */
public class SessionFilter extends AccessControlFilter {
    // 登录地址
    @Value("${shiro.user.loginUrl}")
    private
    String loginUrl;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        DataSource.clearDataSource();  //确保新的请求前是主数据源
        String sessionId = ShiroUtils.getSessionId();
//        System.out.println("sessionid=" + sessionId);
        if (subject == null || StringUtils.isEmpty(sessionId)) {
            return false;
        }
        LoginUser loginUser = ShiroUtils.getShiroUser();
        Session session = onlineSessionDAO.readSession(sessionId);
        if (session != null && session instanceof OnlineSession) {
            OnlineSession onlineSession = (OnlineSession) session;
            request.setAttribute(ShiroConstant.ONLINE_SESSION, onlineSession);
            //首次访问或者记住我登录
            if (onlineSession.getUserId() == 0 && loginUser != null) {
                onlineSession.setUserId(loginUser.getUserId());
                onlineSession.setDeptName(loginUser.getDeptName());
                onlineSession.markAttributeChanged();
            }
            if (onlineSession.getStatus() == OnlineStatus.离线) {
                return false;
            }
        }
        if (loginUser != null) {
            if (userCacheService.isChangeUserInfo(loginUser)) {
                ShiroUtils.changePrincipal(loginUser);
            } else if (!subject.isAuthenticated() && subject.isRemembered()) {
                //不是认证登录，是记住我登录的,头一次登录都会加载一次
                if (StringUtils.isEmpty(loginUser.getChangeUserFlag())) {
                    loginUser.setChangeUserFlag("loaded");
                    ShiroUtils.changePrincipal(loginUser);
                }
            }
        }
        if (UserUtils.isStop()) return false;
        return subject.isAuthenticated() || subject.isRemembered();
    }

    /**
     * 当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了
     * 拒绝访问的原因可能有超时、权限不够、需要登录验证等等
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String sessionId = ShiroUtils.getSessionId();
        if (ServletUtils.isAjaxRequest((HttpServletRequest) request)) {
            //ajax的session超时处理
            if (StringUtils.isEmpty(sessionId) || ShiroUtils.isSessionOut()) {
                response.getWriter().print(ReturnMap.getSessionOut());
            } else {
                Session session = onlineSessionDAO.readSession(sessionId);
                String off_msg = StringUtils.EMPTY;
                if (session != null && session.getAttribute("off_msg") != null) {
                    off_msg = session.getAttribute("off_msg").toString();
                }
                if (UserUtils.isStop()) off_msg = MessageUtils.message("user.name.stop");
                response.getWriter().print(ReturnMap.getReLoginFail(off_msg));
            }
        } else {
            this.saveRequestAndRedirectToLogin(request, response);
        }
        ShiroUtils.shiroLogout();
        return false;
    }

    // 跳转到登录页
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        String str = StringUtils.EMPTY;
        String sessionId = ShiroUtils.getSessionId();
        if (StringUtils.isEmpty(sessionId) || ShiroUtils.isSessionOut()) {
            str = "?type=" + KickoutEnum.Session丢失.getCode();
        } else {
            Session session = onlineSessionDAO.readSession(sessionId);
            String off_msg = StringUtils.EMPTY;
            if (session != null && session.getAttribute("off_msg") != null) {
                off_msg = session.getAttribute("off_msg").toString();
            }
            if (UserUtils.isStop()) off_msg = MessageUtils.message("user.name.stop");
            if (StringUtils.isNotEmpty(off_msg)) {
                off_msg = EncodeUtils.encodeUrl(off_msg);
                str = "?off_msg=" + EncodeUtils.encodeUrl(off_msg);
            }
        }
        WebUtils.issueRedirect(request, response, loginUrl + str);
    }

    @Override
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setOnlineSessionDAO(OnlineSessionDAO onlineSessionDAO) {
        this.onlineSessionDAO = onlineSessionDAO;
    }

    public void setChangeService(UserCacheService userCacheService) {
        this.userCacheService = userCacheService;
    }
}
