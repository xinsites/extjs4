package com.xinsite.core.shiro.service;

import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.network.AddressUtils;
import com.xinsite.core.enums.OnlineStatus;
import com.xinsite.core.shiro.session.OnlineSession;
import com.xinsite.common.uitls.TaskUtils;
import com.xinsite.mybatis.datasource.master.entity.SysUserOnline;
import com.xinsite.mybatis.datasource.master.service.SysUserOnlineService;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

/**
 * 在线用户相关操作
 */
@Component
public class OnlineService {
    @Autowired
    private SysUserOnlineService onlineService;

    /**
     * 获取会话信息
     */
    public Session getSession(Serializable sessionId) {
        SysUserOnline userOnline = onlineService.getSysUserOnlineById(String.valueOf(sessionId));
        if (userOnline == null) return null;
        return createSession(userOnline);
    }

    /**
     * 删除在线用户
     */
    public void deleteOnline(OnlineSession onlineSession) {
        if (onlineService != null) {
            onlineSession.setStatus(OnlineStatus.离线);
            onlineService.deleteSysUserOnlineById(String.valueOf(onlineSession.getId()));
        }
    }

    /**
     * 删除在线用户
     */
    public void deleteOnline(Serializable sessionId) {
        if (onlineService != null && sessionId != null) {
            onlineService.deleteSysUserOnlineById(String.valueOf(sessionId));
        }
    }

    /**
     * 删除过期在线用户session
     * 页面有30秒的定时请求，过期时间可以定为3分钟以内
     */
    public void batchDeleteOnline() {
        Date expiredDate = DateUtils.addMinutes(new Date(), -3);
        onlineService.batchDeleteOnline(expiredDate);
    }

    /**
     * 同步session到数据库在线用户
     */
    public void insertOnline(OnlineSession session) {
        TaskUtils.getInstance().execute(new TimerTask() {
            @Override
            public void run() {
                SysUserOnline online = new SysUserOnline();
                online.setSessionId(String.valueOf(session.getId()));
                online.setUserId(session.getUserId());
                online.setDeptName(session.getDeptName());
                online.setStartTimestamp(session.getStartTimestamp());
                online.setLastAccessTime(session.getLastAccessTime());
                online.setTimeOut(session.getTimeout());
                online.setIpAddress(session.getHost());
                online.setLoginLocation(AddressUtils.getRealAddressByIP(session.getHost()));
                online.setBrowser(session.getBrowser());
                online.setVersion(session.getVersion());
                online.setDevice(session.getDevice());
                online.setStatus(session.getStatus().getValue());
                onlineService.saveSysUserOnline(online);
            }
        });
    }


    /**
     * 根据数据库在线用户记录创建session
     */
    public Session createSession(SysUserOnline userOnline) {
        OnlineSession onlineSession = new OnlineSession();
        if (userOnline != null) {
            onlineSession.setId(userOnline.getSessionId());
            onlineSession.setUserId(userOnline.getUserId());
            onlineSession.setHost(userOnline.getIpAddress());
            onlineSession.setBrowser(userOnline.getBrowser());
            onlineSession.setDevice(userOnline.getDevice());
            onlineSession.setDeptName(userOnline.getDeptName());
            onlineSession.setUserId(userOnline.getUserId());
            onlineSession.setVersion(userOnline.getVersion());
            onlineSession.setStartTimestamp(userOnline.getStartTimestamp());
            onlineSession.setLastAccessTime(userOnline.getLastAccessTime());
            onlineSession.setTimeout(userOnline.getTimeOut());
        }
        return onlineSession;
    }

}
