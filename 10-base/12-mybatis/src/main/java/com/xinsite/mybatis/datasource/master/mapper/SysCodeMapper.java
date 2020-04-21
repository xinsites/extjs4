package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.SysCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by 系统管理员
 * create time: 2020-02-21
 */
@Service
public interface SysCodeMapper {
    /**
    * 查询编码表集合
    * @return 编码表集合
    */
    List<SysCode> getSysCodeList(Map<String, Object> params);

    /**
    * 新增编码表
    */
    void addSysCode(SysCode sysCode);

    /**
    * 修改编码表
    */
    void updateSysCodeById(SysCode sysCode);

    /**
    * 删除编码表
    */
    void deleteSysCodeById(int Primarykey);

    /**
    * 查询编码表
    * @return 编码表
    */
    SysCode getSysCodeById(int Primarykey);
}
