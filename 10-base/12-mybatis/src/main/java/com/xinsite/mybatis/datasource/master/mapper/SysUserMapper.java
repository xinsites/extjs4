package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public interface SysUserMapper {
    /**
    * 查询用户集合
    * @return 用户集合
    */
    List<SysUser> getSysUserList(Map<String, Object> params);

    /**
    * 新增用户
    */
    void addSysUser(SysUser sysUser);

    /**
    * 修改用户
    */
    void updateSysUserById(SysUser sysUser);

    /**
    * 删除用户
    */
    void deleteSysUserById(int id);

    /**
    * 查询用户
    * @return 用户
    */
    SysUser getSysUserById(int id);
}
