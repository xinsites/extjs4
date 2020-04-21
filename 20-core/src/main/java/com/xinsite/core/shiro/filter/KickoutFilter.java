package com.xinsite.core.shiro.filter;

import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.common.response.ReturnMap;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.enums.KickoutEnum;
import com.xinsite.core.utils.user.UserUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 登录帐号踢出用户过滤器
 *
 * @author zhangxiaxin
 */
public class KickoutFilter extends AccessControlFilter {
    /**
     * 踢出后重定向的地址
     */
    private String kickoutUrl;

    private boolean kickoutAfter = false; //踢出最先登录者

    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        int maxSession = SysConfigCache.getMaxSession();
        if (!subject.isAuthenticated() && !subject.isRemembered() || maxSession < 1) {
            return true;  //如果没登录，或者无限制，直接进行之后的流程
        }

        Session session = subject.getSession();
        String userId = UserUtils.getUserId() + "";
        Serializable sessionId = session.getId();

        //读取缓存 没有就存入
        Deque<Serializable> deque = cache.get(userId);
        if (deque == null) deque = new LinkedList<Serializable>();

//        if (!subject.isAuthenticated() && subject.isRemembered()) { //不是认证登录，是记住我登录的
//            if (ShiroUtils.isSessionOut()) {
//                System.out.println("不是认证登录，是记住我登录的,超时sessionid=" + sessionId);
//                if (!ShiroUtils.reSetLoginUser())
//                    return isAjaxResponse(request, response, KickoutEnum.重新登录失败.getCode());
//            }
//        }

        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
            deque.push(sessionId);
            cache.put(userId, deque);
        }

//        if (AppCache.isChangeRolePer() && session.getAttribute("clearauth") != null) {
//            session.setAttribute("clearauth", true); //清理权限缓存
//        }

        //如果队列里的sessionId数超出最大会话数，开始踢人
        while (deque.size() > maxSession) {
            Serializable kickoutSessionId = null;
            if (kickoutAfter) {
                kickoutSessionId = deque.removeFirst(); //踢出最后登录者
            } else {
                kickoutSessionId = deque.removeLast();  //踢出最先登录者
            }
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    kickoutSession.setAttribute("kickout", true); //设置会话的kickout属性表示踢出了
                }
            } catch (Exception e) {//ignore exception
                //return isAjaxResponse(request, response, KickoutEnum.Session丢失.getCode());
            }
        }

        //如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute("kickout") != null) {
            subject.logout();
            saveRequest(request);
            return isAjaxResponse(request, response, KickoutEnum.重复登录.getCode());
        }

        return true;
    }

    private boolean isAjaxResponse(ServletRequest request, ServletResponse response, int kickout) throws IOException {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        if (ServletUtils.isAjaxRequest(httpRequest)) {
            response.getWriter().println(ReturnMap.getRepeatLogin());
        } else {
            WebUtils.issueRedirect(request, response, kickoutUrl + "?type=" + kickout);
        }
        return false;
    }

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // 设置Cache的key的前缀
    public void setCacheManager(CacheManager cacheManager) {
        // 必须和ehcache缓存配置中的缓存name一致
        this.cache = cacheManager.getCache(ShiroConstant.LOGIN_KICKOUT_CACHE);
    }

}
