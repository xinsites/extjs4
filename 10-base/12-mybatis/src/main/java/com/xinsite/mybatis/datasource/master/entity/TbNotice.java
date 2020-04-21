package com.xinsite.mybatis.datasource.master.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2019-10-29
 */ 
public class TbNotice implements Serializable {

	private int idleaf;
	private String title;
	private int serialcode;
	private int position;
	private Date positionTime;
	private Date createTime;
	private Date modifyTime;
	private int createUid;
	private int modifyUid;
	private int itemId;
	private int orgId;
	private int deptId;
	private int isdel;
	private String grade;//等级

	private String type;//类型

	private String fbDate;//发布日期

	private String content;//内容

	private int viewCount;

	public void setIdleaf(int idleaf){
		this.idleaf=idleaf;
	}

	public int getIdleaf(){
		return idleaf;
	}

	public void setTitle(String title){
		this.title=title;
	}

	public String getTitle(){
		return title;
	}

	public void setSerialcode(int serialcode){
		this.serialcode=serialcode;
	}

	public int getSerialcode(){
		return serialcode;
	}

	public void setPosition(int position){
		this.position=position;
	}

	public int getPosition(){
		return position;
	}

	public void setPositionTime(Date positionTime){
		this.positionTime=positionTime;
	}

	public Date getPositionTime(){
		return positionTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getCreateTime(){
		return createTime;
	}

	public void setModifyTime(Date modifyTime){
		this.modifyTime=modifyTime;
	}

	public Date getModifyTime(){
		return modifyTime;
	}

	public void setCreateUid(int createUid){
		this.createUid=createUid;
	}

	public int getCreateUid(){
		return createUid;
	}

	public void setModifyUid(int modifyUid){
		this.modifyUid=modifyUid;
	}

	public int getModifyUid(){
		return modifyUid;
	}

	public void setItemId(int itemId){
		this.itemId=itemId;
	}

	public int getItemId(){
		return itemId;
	}

	public void setOrgId(int orgId){
		this.orgId=orgId;
	}

	public int getOrgId(){
		return orgId;
	}

	public void setDeptId(int deptId){
		this.deptId=deptId;
	}

	public int getDeptId(){
		return deptId;
	}

	public void setIsdel(int isdel){
		this.isdel=isdel;
	}

	public int getIsdel(){
		return isdel;
	}

	public void setGrade(String grade){
		this.grade=grade;
	}

	public String getGrade(){
		return grade;
	}

	public void setType(String type){
		this.type=type;
	}

	public String getType(){
		return type;
	}

	public void setFbDate(String fbDate){
		this.fbDate=fbDate;
	}

	public String getFbDate(){
		return fbDate;
	}

	public void setContent(String content){
		this.content=content;
	}

	public String getContent(){
		return content;
	}

	public void setViewCount(int viewCount){
		this.viewCount=viewCount;
	}

	public int getViewCount(){
		return viewCount;
	}

}
