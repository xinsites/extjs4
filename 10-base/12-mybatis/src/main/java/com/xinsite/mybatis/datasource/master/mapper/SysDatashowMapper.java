package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.SysDatashow;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public interface SysDatashowMapper {
    /**
    * 查询编码表是否显示可用辅助表集合
    * @return 编码表是否显示可用辅助表集合
    */
    List<SysDatashow> getSysDatashowList(Map<String, Object> params);

    /**
    * 新增编码表是否显示可用辅助表
    */
    void addSysDatashow(SysDatashow sysDatashow);

    /**
    * 修改编码表是否显示可用辅助表
    */
    void updateSysDatashowById(SysDatashow sysDatashow);

    /**
    * 删除编码表是否显示可用辅助表
    */
    void deleteSysDatashowById(int id);

    /**
    * 查询编码表是否显示可用辅助表
    * @return 编码表是否显示可用辅助表
    */
    SysDatashow getSysDatashowById(int id);
}
