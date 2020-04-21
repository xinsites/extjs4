package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.SysDatashow;
import com.xinsite.mybatis.datasource.master.mapper.SysDatashowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public class SysDatashowService {

    @Autowired
    private SysDatashowMapper sysDatashowMapper;

    /**
    * 查询编码表是否显示可用辅助表集合
    * @return 编码表是否显示可用辅助表集合
    */
    public List<SysDatashow> getSysDatashowList(Map<String, Object> params) {
        return sysDatashowMapper.getSysDatashowList(params);
    }

    /**
    * 新增编码表是否显示可用辅助表
    */
    public void addSysDatashow(SysDatashow sysDatashow) {
        sysDatashowMapper.addSysDatashow(sysDatashow);
    }

    /**
    * 修改编码表是否显示可用辅助表
    */
    public void updateSysDatashowById(SysDatashow sysDatashow) {
        sysDatashowMapper.updateSysDatashowById(sysDatashow);
    }

    /**
    * 删除编码表是否显示可用辅助表
    */
    public void deleteSysDatashowById(int Primarykey) {
        try {
            sysDatashowMapper.deleteSysDatashowById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SysDatashow dealUpdateBean(SysDatashow sysDatashow, Map<String, Object> params) {
        sysDatashow = new SysDatashow();
        return sysDatashow;
    }

    public SysDatashow dealCreateBean(Map<String, Object> params) {
        SysDatashow sysDatashow = new SysDatashow();
        return sysDatashow;
    }

    /**
    * 查询编码表是否显示可用辅助表
    * @return 编码表是否显示可用辅助表
    */
    public SysDatashow getSysDatashowById(int Primarykey) {
        return sysDatashowMapper.getSysDatashowById(Primarykey);
    }
}
