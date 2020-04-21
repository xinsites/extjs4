package com.xinsite.mybatis.datasource.master.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2020-03-20
 * object name: 系统用户表
 */ 
public class SysUser implements Serializable {

	private int userId;          //用户Id

	private String userName;     //用户姓名

	private String loginName;    //登录名

	private String pwdSalt;      //密码盐，设置成唯一

	private String password;     //密码

	private String userSex;      //用户性别

	private String birthday;     //出生日期

	private String email;        //电子邮箱

	private String phone;        //个人手机号

	private String workphone;    //工作手机号

	private String subtelephone; //分机号

	private String headPhoto;    //头像地址

	private String oicq;         //QQ

	private String wechat;       //微信

	private int userState;       //启用状态

	private String remark;       //备注

	private int orgId;           //机构号

	private int roleId;          //用户角色

	private int deptId;          //用户部门

	private int postId;          //用户职位

	private int serialcode;      //排序号

	private Date createTime;     //创建时间

	private Date modifyTime;     //修改时间

	private int isdel;           //是否删除，0：未删除；1：删除

	private int issys;           //是否系统内置，1：是不能删除


	public void setUserId(int userId){
		this.userId=userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setUserName(String userName){
		this.userName=userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setLoginName(String loginName){
		this.loginName=loginName;
	}

	public String getLoginName(){
		return loginName;
	}

	public void setPwdSalt(String pwdSalt){
		this.pwdSalt=pwdSalt;
	}

	public String getPwdSalt(){
		return pwdSalt;
	}

	public void setPassword(String password){
		this.password=password;
	}

	public String getPassword(){
		return password;
	}

	public void setUserSex(String userSex){
		this.userSex=userSex;
	}

	public String getUserSex(){
		return userSex;
	}

	public void setBirthday(String birthday){
		this.birthday=birthday;
	}

	public String getBirthday(){
		return birthday;
	}

	public void setEmail(String email){
		this.email=email;
	}

	public String getEmail(){
		return email;
	}

	public void setPhone(String phone){
		this.phone=phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setWorkphone(String workphone){
		this.workphone=workphone;
	}

	public String getWorkphone(){
		return workphone;
	}

	public void setSubtelephone(String subtelephone){
		this.subtelephone=subtelephone;
	}

	public String getSubtelephone(){
		return subtelephone;
	}

	public void setHeadPhoto(String headPhoto){
		this.headPhoto=headPhoto;
	}

	public String getHeadPhoto(){
		return headPhoto;
	}

	public void setOicq(String oicq){
		this.oicq=oicq;
	}

	public String getOicq(){
		return oicq;
	}

	public void setWechat(String wechat){
		this.wechat=wechat;
	}

	public String getWechat(){
		return wechat;
	}

	public void setUserState(int userState){
		this.userState=userState;
	}

	public int getUserState(){
		return userState;
	}

	public void setRemark(String remark){
		this.remark=remark;
	}

	public String getRemark(){
		return remark;
	}

	public void setOrgId(int orgId){
		this.orgId=orgId;
	}

	public int getOrgId(){
		return orgId;
	}

	public void setRoleId(int roleId){
		this.roleId=roleId;
	}

	public int getRoleId(){
		return roleId;
	}

	public void setDeptId(int deptId){
		this.deptId=deptId;
	}

	public int getDeptId(){
		return deptId;
	}

	public void setPostId(int postId){
		this.postId=postId;
	}

	public int getPostId(){
		return postId;
	}

	public void setSerialcode(int serialcode){
		this.serialcode=serialcode;
	}

	public int getSerialcode(){
		return serialcode;
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

	public void setIsdel(int isdel){
		this.isdel=isdel;
	}

	public int getIsdel(){
		return isdel;
	}

	public void setIssys(int issys){
		this.issys=issys;
	}

	public int getIssys(){
		return issys;
	}

}
