package com.xinsite.core.shiro.session;

import com.xinsite.common.uitls.network.IpUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.common.uitls.web.http.UserAgentUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.web.session.mgt.WebSessionContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义sessionFactory会话
 */
@Component
public class OnlineSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        OnlineSession session = new OnlineSession();
        if (initData != null && initData instanceof WebSessionContext) {
            WebSessionContext sessionContext = (WebSessionContext) initData;
            HttpServletRequest request = (HttpServletRequest) sessionContext.getServletRequest();
            if (request != null) {
                UserAgent userAgent = UserAgentUtils.getUserAgent(ServletUtils.getRequest());
                if (userAgent != null) {
                    //userAgent.getBrowserVersion().getVersion();
                    session.setHost(IpUtils.getRemoteAddr(request));
                    session.setBrowser(userAgent.getBrowser().getName());  // 获取客户端浏览器
                    session.setVersion(userAgent.getBrowserVersion().getVersion());  // 获取浏览器版本号
                    session.setDevice(userAgent.getOperatingSystem().getName()); // 获取客户端操作系统
                }
            }
        }
        return session;
    }
}
