package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.SysUser;
import com.xinsite.mybatis.datasource.master.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
    * 查询用户集合
    * @return 用户集合
    */
    public List<SysUser> getSysUserList(Map<String, Object> params) {
        return sysUserMapper.getSysUserList(params);
    }

    /**
    * 新增用户
    */
    public void addSysUser(SysUser sysUser) {
        sysUserMapper.addSysUser(sysUser);
    }

    /**
    * 修改用户
    */
    public void updateSysUserById(SysUser sysUser) {
        sysUserMapper.updateSysUserById(sysUser);
    }

    /**
    * 删除用户
    */
    public void deleteSysUserById(int Primarykey) {
        try {
            sysUserMapper.deleteSysUserById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SysUser dealUpdateBean(SysUser sysUser, Map<String, Object> params) {
        sysUser = new SysUser();
        return sysUser;
    }

    public SysUser dealCreateBean(Map<String, Object> params) {
        SysUser sysUser = new SysUser();
        return sysUser;
    }

    /**
    * 查询用户
    * @return 用户
    */
    public SysUser getSysUserById(int Primarykey) {
        return sysUserMapper.getSysUserById(Primarykey);
    }
}
