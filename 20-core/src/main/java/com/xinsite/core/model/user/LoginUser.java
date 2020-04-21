package com.xinsite.core.model.user;


import com.xinsite.common.uitls.mapper.JsonMapper;

import java.io.Serializable;

/**
 * 用户登录成功用户实例
 * create by zhangxiaxin
 */
public class LoginUser implements Serializable {
    private int userId;  //当前登录用户Id
    private String userName;
    private String loginName;
    private int orgId;  // 当前登录用户所属机构(公司)
    private String organizeName;
    private int roleId; // 当前登录用户所属角色
    private String roleName;
    private int deptId; // 当前登录用户所属部门
    private String deptName;
    private int postId;
    private String postName;
    private int userState;
    private String headPhoto;
    private boolean superAdminer; //是否属于超级管理员
    private String loginIp; // 当前登录用户Ip地址

    /**
     * 用户信息更新标记
     */
    private String changeUserFlag;

    public LoginUser() {
    }

    public LoginUser(int userId, String loginName) {
        this.userId = userId;
        this.loginName = loginName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getOrganizeName() {
        return organizeName;
    }

    public void setOrganizeName(String organizeName) {
        this.organizeName = organizeName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserState() {
        return userState;
    }

    public void setUserState(int userState) {
        this.userState = userState;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public boolean isSuperAdminer() {
        return superAdminer;
    }

    public void setSuperAdminer(boolean superAdminer) {
        this.superAdminer = superAdminer;
    }

    public String getUserInfoJson() {
        return JsonMapper.toJson(this);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getChangeUserFlag() {
        return changeUserFlag;
    }

    public void setChangeUserFlag(String changeUserFlag) {
        this.changeUserFlag = changeUserFlag;
    }

}
