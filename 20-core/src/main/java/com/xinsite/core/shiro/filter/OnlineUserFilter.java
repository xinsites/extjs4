package com.xinsite.core.shiro.filter;

import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.core.shiro.session.OnlineSession;
import com.xinsite.core.shiro.session.OnlineSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 在线用户过滤器
 */
public class OnlineUserFilter extends FormAuthenticationFilter {

    // 登录地址
    @Value("${shiro.user.loginUrl}")
    private String loginUrl;

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;

    @Autowired
    private SessionDAO sessionDAO;

    /**
     * 同步会话数据到DB 一次请求最多同步一次 防止过多处理 需要放到Shiro过滤器之前
     */
    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
        //Collection<Session> sessions = onlineSessionDAO.getActiveSessions();  //所有在线的用户
        OnlineSession session = (OnlineSession) request.getAttribute(ShiroConstant.ONLINE_SESSION);
        // session停止时间，如果stopTimestamp不为null，则代表已停止
        if (session != null && session.getUserId() != 0 && session.getStopTimestamp() == null) {
            onlineSessionDAO.insertOnline(session);
        }
        return true;
    }


}
