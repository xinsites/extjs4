package com.xinsite.core.shiro.session;

import com.xinsite.core.enums.OnlineStatus;
import org.apache.shiro.session.mgt.SimpleSession;

/**
 * 在线用户会话属性
 */
public class OnlineSession extends SimpleSession {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private int userId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 登录IP地址
     */
    private String host;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 浏览器版本号
     */
    private String version;

    /**
     * 操作系统
     */
    private String device;

    /**
     * 在线状态
     */
    private OnlineStatus status = OnlineStatus.在线;

    /**
     * 属性是否改变 优化session数据同步
     */
    private transient boolean attributeChanged = false;

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineStatus status) {
        this.status = status;
    }

    public void markAttributeChanged() {
        this.attributeChanged = true;
    }

    public void resetAttributeChanged() {
        this.attributeChanged = false;
    }

    public boolean isAttributeChanged() {
        return attributeChanged;
    }

}
