package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.SysCode;
import com.xinsite.mybatis.datasource.master.mapper.SysCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by 系统管理员
 * create time: 2020-02-21
 */
@Service
public class SysCodeService {

    @Autowired
    private SysCodeMapper sysCodeMapper;

    /**
    * 查询编码表集合
    * @return 编码表集合
    */
    public List<SysCode> getSysCodeList(Map<String, Object> params) {
        return sysCodeMapper.getSysCodeList(params);
    }

    /**
    * 新增编码表
    */
    public void addSysCode(SysCode sysCode) {
        sysCodeMapper.addSysCode(sysCode);
    }

    /**
    * 修改编码表
    */
    public void updateSysCodeById(SysCode sysCode) {
        sysCodeMapper.updateSysCodeById(sysCode);
    }

    /**
    * 删除编码表
    */
    public void deleteSysCodeById(int Primarykey) {
        try {
            sysCodeMapper.deleteSysCodeById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SysCode dealUpdateBean(SysCode sysCode, Map<String, Object> params) {
        sysCode = new SysCode();
        return sysCode;
    }

    public SysCode dealCreateBean(Map<String, Object> params) {
        SysCode sysCode = new SysCode();
        return sysCode;
    }

    /**
    * 查询编码表
    * @return 编码表
    */
    public SysCode getSysCodeById(int Primarykey) {
        return sysCodeMapper.getSysCodeById(Primarykey);
    }
}
