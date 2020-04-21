package com.xinsite.mybatis.datasource.viceone.mapper;

import com.xinsite.mybatis.datasource.viceone.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-04
 */
@Service
public interface SysUserMapper {
    /**
    * 查询集合
    * @return 集合
    */
    List<SysUser> getSysUserList(Map<String, Object> params);

    /**
    * 新增
    */
    void addSysUser(SysUser sysUser);

    /**
    * 修改
    */
    void updateSysUserById(SysUser sysUser);

    /**
    * 删除
    */
    void deleteSysUserById(int id);

    /**
    * 查询
    * @return 
    */
    SysUser getSysUserById(int id);
}
