package com.xinsite.mybatis.datasource.viceone.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2019-09-04
 */ 
public class SysUser implements Serializable {

	private int UserId;
	private String UserName;
	private String LoginName;
	private String Password;
	private String Sex;
	private String Birthday;
	private String EMail;
	private String Phone;
	private String workPhone;
	private String SubTelephone;
	private String Photo;
	private String OICQ;
	private String WeChat;
	private int UserState;
	private String Remark;
	private int org_id;
	private int role_id;
	private int dept_id;
	private int PostId;
	private int serialcode;
	private Date create_time;
	private Date modify_time;
	private int isdel;
	private int issys;
	private int enabled;
	private int isShow;
	private String workDate;

	public void setUserId(int UserId){
		this.UserId=UserId;
	}

	public int getUserId(){
		return UserId;
	}

	public void setUserName(String UserName){
		this.UserName=UserName;
	}

	public String getUserName(){
		return UserName;
	}

	public void setLoginName(String LoginName){
		this.LoginName=LoginName;
	}

	public String getLoginName(){
		return LoginName;
	}

	public void setPassword(String Password){
		this.Password=Password;
	}

	public String getPassword(){
		return Password;
	}

	public void setSex(String Sex){
		this.Sex=Sex;
	}

	public String getSex(){
		return Sex;
	}

	public void setBirthday(String Birthday){
		this.Birthday=Birthday;
	}

	public String getBirthday(){
		return Birthday;
	}

	public void setEMail(String EMail){
		this.EMail=EMail;
	}

	public String getEMail(){
		return EMail;
	}

	public void setPhone(String Phone){
		this.Phone=Phone;
	}

	public String getPhone(){
		return Phone;
	}

	public void setWorkPhone(String workPhone){
		this.workPhone=workPhone;
	}

	public String getWorkPhone(){
		return workPhone;
	}

	public void setSubTelephone(String SubTelephone){
		this.SubTelephone=SubTelephone;
	}

	public String getSubTelephone(){
		return SubTelephone;
	}

	public void setPhoto(String Photo){
		this.Photo=Photo;
	}

	public String getPhoto(){
		return Photo;
	}

	public void setOICQ(String OICQ){
		this.OICQ=OICQ;
	}

	public String getOICQ(){
		return OICQ;
	}

	public void setWeChat(String WeChat){
		this.WeChat=WeChat;
	}

	public String getWeChat(){
		return WeChat;
	}

	public void setUserState(int UserState){
		this.UserState=UserState;
	}

	public int getUserState(){
		return UserState;
	}

	public void setRemark(String Remark){
		this.Remark=Remark;
	}

	public String getRemark(){
		return Remark;
	}

	public void setOrgId(int org_id){
		this.org_id=org_id;
	}

	public int getOrgId(){
		return org_id;
	}

	public void setRoleId(int role_id){
		this.role_id=role_id;
	}

	public int getRoleId(){
		return role_id;
	}

	public void setDeptId(int dept_id){
		this.dept_id=dept_id;
	}

	public int getDeptId(){
		return dept_id;
	}

	public void setPostId(int PostId){
		this.PostId=PostId;
	}

	public int getPostId(){
		return PostId;
	}

	public void setSerialCode(int serialcode){
		this.serialcode=serialcode;
	}

	public int getSerialCode(){
		return serialcode;
	}

	public void setCreateTime(Date create_time){
		this.create_time=create_time;
	}

	public Date getCreateTime(){
		return create_time;
	}

	public void setInputTime(Date modify_time){
		this.modify_time=modify_time;
	}

	public Date getInputTime(){
		return modify_time;
	}

	public void setIsDel(int isdel){
		this.isdel=isdel;
	}

	public int getIsDel(){
		return isdel;
	}

	public void setIsSys(int issys){
		this.issys=issys;
	}

	public int getIsSys(){
		return issys;
	}

	public void setEnabled(int enabled){
		this.enabled=enabled;
	}

	public int getEnabled(){
		return enabled;
	}

	public void setIsShow(int isShow){
		this.isShow=isShow;
	}

	public int getIsShow(){
		return isShow;
	}

	public void setWorkDate(String workDate){
		this.workDate=workDate;
	}

	public String getWorkDate(){
		return workDate;
	}

}
