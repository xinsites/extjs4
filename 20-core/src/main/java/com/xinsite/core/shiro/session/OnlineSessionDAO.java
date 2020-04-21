package com.xinsite.core.shiro.session;

import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.core.shiro.service.OnlineService;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * 针对自定义的ShiroSession的db操作
 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO {
    /**
     * 同步session到数据库的周期 单位为毫秒（默认1分钟）
     */
    @Value("${shiro.session.dbSyncPeriod}")
    private int dbSyncPeriod;

    @Autowired
    private OnlineService onlineService;

    /**
     * 上次同步数据库的时间戳
     */
    private static final String ONLINE_LAST_DB = OnlineSessionDAO.class.getName() + ShiroConstant.ONLINE_LAST_DB;


    public OnlineSessionDAO() {
        super();
    }

    public OnlineSessionDAO(long expireTime) {
        super();
    }


    @Override
    public void update(Session session) throws UnknownSessionException {
        super.update(session);
    }

    /**
     * 根据会话ID获取会话
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        return onlineService.getSession(sessionId);
    }

    /**
     * 更新会话；如更新会话最后访问时间/停止会话/设置超时时间/设置移除属性等会调用
     */
    public void insertOnline(OnlineSession onlineSession) {
        Date lastTime = (Date) onlineSession.getAttribute(ONLINE_LAST_DB);
        if (lastTime != null) {
            long deltaTime = onlineSession.getLastAccessTime().getTime() - lastTime.getTime();
            if (deltaTime < dbSyncPeriod * 60 * 1000) return;  //时间差不足 无需同步
        }
        // 更新上次同步数据库时间
        onlineSession.setAttribute(ONLINE_LAST_DB, onlineSession.getLastAccessTime());
        onlineService.insertOnline(onlineSession);
    }

    /**
     * 当会话过期/停止（如用户退出时）属性等会调用
     */
    @Override
    protected void doDelete(Session session) {
        OnlineSession onlineSession = (OnlineSession) session;
        if (onlineSession == null) return;
        onlineService.deleteOnline(onlineSession);
    }

    public void delete(Serializable sessionId) {
        if (sessionId == null) return;
        onlineService.deleteOnline(sessionId);
    }
}
