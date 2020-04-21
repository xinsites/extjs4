package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.SysUserOnline;
import com.xinsite.mybatis.datasource.master.mapper.SysUserOnlineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-11-17
 */
@Service
public class SysUserOnlineService {

    @Autowired
    private SysUserOnlineMapper sysUserOnlineMapper;

    /**
    * 查询在线用户记录集合
    * @return 在线用户记录集合
    */
    public List<SysUserOnline> getSysUserOnlineList(Map<String, Object> params) {
        return sysUserOnlineMapper.getSysUserOnlineList(params);
    }

    /**
    * 新增在线用户记录
    */
    public void addSysUserOnline(SysUserOnline sysUserOnline) {
        sysUserOnlineMapper.addSysUserOnline(sysUserOnline);
    }

    /**
    * 修改在线用户记录
    */
    public void updateSysUserOnlineById(SysUserOnline sysUserOnline) {
        sysUserOnlineMapper.updateSysUserOnlineById(sysUserOnline);
    }

    /**
     * 保存在线用户记录
     */
    public void saveSysUserOnline(SysUserOnline sysUserOnline) {
        sysUserOnlineMapper.saveSysUserOnline(sysUserOnline);
    }

    /**
    * 删除在线用户记录
    */
    public void deleteSysUserOnlineById(String Primarykey) {
        try {
            sysUserOnlineMapper.deleteSysUserOnlineById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除在线用户记录
     */
    public void batchDeleteOnline(Date expiredDate) {
        try {
            sysUserOnlineMapper.batchDeleteOnline(expiredDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SysUserOnline dealUpdateBean(SysUserOnline sysUserOnline, Map<String, Object> params) {
        sysUserOnline = new SysUserOnline();
        return sysUserOnline;
    }

    public SysUserOnline dealCreateBean(Map<String, Object> params) {
        SysUserOnline sysUserOnline = new SysUserOnline();
        return sysUserOnline;
    }

    /**
    * 查询在线用户记录
    * @return 在线用户记录
    */
    public SysUserOnline getSysUserOnlineById(String Primarykey) {
        return sysUserOnlineMapper.getSysUserOnlineById(Primarykey);
    }
}
