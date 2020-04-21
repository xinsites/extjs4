package com.xinsite.mybatis.datasource.master.bll;

import com.xinsite.dal.uitls.Utils_Context;
import com.xinsite.mybatis.datasource.master.entity.SysCode;
import com.xinsite.mybatis.datasource.master.service.SysCodeService;

/**
 * create by 系统管理员
 * create time: 2020-02-21
 */
public class BLL_SysCode {
    /**
    * 获取编码表
    */
    public static SysCode getSysCodeById(int Primarykey) {
        SysCodeService service = Utils_Context.getBean(SysCodeService.class);
        return service.getSysCodeById(Primarykey);
    }
}
