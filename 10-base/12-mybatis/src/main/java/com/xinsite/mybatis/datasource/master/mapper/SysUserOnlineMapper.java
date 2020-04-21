package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.SysUserOnline;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-11-17
 */
@Service
public interface SysUserOnlineMapper {
    /**
    * 查询在线用户记录集合
    * @return 在线用户记录集合
    */
    List<SysUserOnline> getSysUserOnlineList(Map<String, Object> params);

    /**
    * 新增在线用户记录
    */
    void addSysUserOnline(SysUserOnline sysUserOnline);

    /**
    * 修改在线用户记录
    */
    void updateSysUserOnlineById(SysUserOnline sysUserOnline);

    /**
     * 保存在线用户记录
     */
    void saveSysUserOnline(SysUserOnline sysUserOnline);

    /**
    * 删除在线用户记录
    */
    void deleteSysUserOnlineById(String Primarykey);

    /**
     * 删除过期在线用户
     */
    void batchDeleteOnline(Date expiredDate);

    /**
    * 查询在线用户记录
    * @return 在线用户记录
    */
    SysUserOnline getSysUserOnlineById(String Primarykey);
}
