package com.xinsite.mybatis.datasource.master.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2019-11-28
 * object name: 在线用户记录
 */ 
public class SysUserOnline implements Serializable {

	private String sessionId;//用户会话id

	private int userId;//登录账号

	private String deptName;//部门名称

	private String ipAddress;//登录IP地址

	private String loginLocation;//登录地点

	private String browser;//浏览器名称

	private String version;//浏览器版本号

	private String device;//操作系统

	private String status;//在线状态on_line在线off_line离线

	private Date startTimestamp;//session创建时间

	private Date lastAccessTime;//session最后访问时间

	private long timeOut;//超时时间，单位为毫秒


	public void setSessionId(String sessionId){
		this.sessionId=sessionId;
	}

	public String getSessionId(){
		return sessionId;
	}

	public void setUserId(int userId){
		this.userId=userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setDeptName(String deptName){
		this.deptName=deptName;
	}

	public String getDeptName(){
		return deptName;
	}

	public void setIpAddress(String ipAddress){
		this.ipAddress=ipAddress;
	}

	public String getIpAddress(){
		return ipAddress;
	}

	public void setLoginLocation(String loginLocation){
		this.loginLocation=loginLocation;
	}

	public String getLoginLocation(){
		return loginLocation;
	}

	public void setBrowser(String browser){
		this.browser=browser;
	}

	public String getBrowser(){
		return browser;
	}

	public void setVersion(String version){
		this.version=version;
	}

	public String getVersion(){
		return version;
	}

	public void setDevice(String device){
		this.device=device;
	}

	public String getDevice(){
		return device;
	}

	public void setStatus(String status){
		this.status=status;
	}

	public String getStatus(){
		return status;
	}

	public void setStartTimestamp(Date startTimestamp){
		this.startTimestamp=startTimestamp;
	}

	public Date getStartTimestamp(){
		return startTimestamp;
	}

	public void setLastAccessTime(Date lastAccessTime){
		this.lastAccessTime=lastAccessTime;
	}

	public Date getLastAccessTime(){
		return lastAccessTime;
	}

	public void setTimeOut(long timeOut){
		this.timeOut=timeOut;
	}

	public long getTimeOut(){
		return timeOut;
	}

}
