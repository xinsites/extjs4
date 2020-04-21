package com.xinsite.mybatis.datasource.master.bll;

import com.xinsite.dal.uitls.Utils_Context;
import com.xinsite.mybatis.datasource.master.entity.SysUserOnline;
import com.xinsite.mybatis.datasource.master.service.SysUserOnlineService;

/**
 * create by zhangxiaxin
 * create time: 2019-11-17
 */
public class BLL_SysUserOnline {
    /**
    * 获取在线用户记录
    */
    public static SysUserOnline getSysUserOnlineById(String Primarykey) {
        SysUserOnlineService service = Utils_Context.getBean(SysUserOnlineService.class);
        return service.getSysUserOnlineById(Primarykey);
    }
}
