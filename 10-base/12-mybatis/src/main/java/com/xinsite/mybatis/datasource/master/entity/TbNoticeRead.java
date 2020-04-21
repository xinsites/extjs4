package com.xinsite.mybatis.datasource.master.entity;

import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2019-10-29
 */ 
public class TbNoticeRead implements Serializable {

	private int idleaf;//通知Id

	private int userId;//用户Id

	private int isread;//是否已读；0：未读；1：已读；


	public void setIdleaf(int idleaf){
		this.idleaf=idleaf;
	}

	public int getIdleaf(){
		return idleaf;
	}

	public void setUserId(int userId){
		this.userId=userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setIsread(int isread){
		this.isread=isread;
	}

	public int getIsread(){
		return isread;
	}

}
