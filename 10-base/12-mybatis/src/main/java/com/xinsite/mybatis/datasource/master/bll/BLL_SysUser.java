package com.xinsite.mybatis.datasource.master.bll;

import com.xinsite.dal.uitls.Utils_Context;
import com.xinsite.mybatis.datasource.master.entity.SysUser;
import com.xinsite.mybatis.datasource.master.service.SysUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-08
 */
public class BLL_SysUser {
    /**
     * 获取用户
     */
    public static SysUser getSysUserById(int Primarykey) {
        SysUserService service = Utils_Context.getBean(SysUserService.class);
        return service.getSysUserById(Primarykey);
    }

    /**
     * 获取用户
     */
    public static SysUser getSysUserByName(String loginName) {
        SysUserService service = Utils_Context.getBean(SysUserService.class);
        Map params = new HashMap<String, Object>();
        params.put("loginName", loginName);
        List<SysUser> list = service.getSysUserList(params);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取用户
     */
    public static SysUser getSysUserByEmail(String loginName) {
        SysUserService service = Utils_Context.getBean(SysUserService.class);
        Map params = new HashMap<String, Object>();
        params.put("email", loginName);
        List<SysUser> list = service.getSysUserList(params);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取用户
     */
    public static SysUser getSysUserByPhone(String loginName) {
        SysUserService service = Utils_Context.getBean(SysUserService.class);
        Map params = new HashMap<String, Object>();
        params.put("phone", loginName);
        List<SysUser> list = service.getSysUserList(params);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
}
