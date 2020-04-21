package com.xinsite.mybatis.datasource.viceone.service;

import com.xinsite.mybatis.datasource.viceone.mapper.SysUserMapper;
import com.xinsite.mybatis.datasource.viceone.entity.SysUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-04
 */
@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
    * 查询集合
    * @return 集合
    */
    public List<SysUser> getSysUserList(Map<String, Object> params) {
        return sysUserMapper.getSysUserList(params);
    }

    /**
    * 新增
    */
    public void addSysUser(SysUser sysUser) {
        sysUserMapper.addSysUser(sysUser);
    }

    /**
    * 修改
    */
    public void updateSysUserById(SysUser sysUser) {
        sysUserMapper.updateSysUserById(sysUser);
    }

    /**
    * 删除
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
    * 查询
    * @return 
    */
    public SysUser getSysUserById(int Primarykey) {
        return sysUserMapper.getSysUserById(Primarykey);
    }
}
