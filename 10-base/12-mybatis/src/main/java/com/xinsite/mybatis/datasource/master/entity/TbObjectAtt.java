package com.xinsite.mybatis.datasource.master.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2019-10-29
 */ 
public class TbObjectAtt implements Serializable {

	private long attachId;//主键

	private int itemId;
	private int idleaf;
	private long serialcode;//排序号

	private String attachName;
	private String attachAdd;
	private String attachSize;
	private String attachType;
	private Date createTime;
	private int attachState;

	public void setAttachId(long attachId){
		this.attachId=attachId;
	}

	public long getAttachId(){
		return attachId;
	}

	public void setItemId(int itemId){
		this.itemId=itemId;
	}

	public int getItemId(){
		return itemId;
	}

	public void setIdleaf(int idleaf){
		this.idleaf=idleaf;
	}

	public int getIdleaf(){
		return idleaf;
	}

	public void setSerialcode(long serialcode){
		this.serialcode=serialcode;
	}

	public long getSerialcode(){
		return serialcode;
	}

	public void setAttachName(String attachName){
		this.attachName=attachName;
	}

	public String getAttachName(){
		return attachName;
	}

	public void setAttachAdd(String attachAdd){
		this.attachAdd=attachAdd;
	}

	public String getAttachAdd(){
		return attachAdd;
	}

	public void setAttachSize(String attachSize){
		this.attachSize=attachSize;
	}

	public String getAttachSize(){
		return attachSize;
	}

	public void setAttachType(String attachType){
		this.attachType=attachType;
	}

	public String getAttachType(){
		return attachType;
	}

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getCreateTime(){
		return createTime;
	}

	public void setAttachState(int attachState){
		this.attachState=attachState;
	}

	public int getAttachState(){
		return attachState;
	}

}
