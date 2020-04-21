package com.xinsite.mybatis.datasource.master.bll;

import com.xinsite.dal.uitls.Utils_Context;
import com.xinsite.mybatis.datasource.master.entity.SysDatashow;
import com.xinsite.mybatis.datasource.master.service.SysDatashowService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-23
 */
public class BLL_SysDatashow {
    /**
     * 获取
     */
    public static SysDatashow getSysDatashowById(int Primarykey) {
        SysDatashowService service = Utils_Context.getBean(SysDatashowService.class);
        return service.getSysDatashowById(Primarykey);
    }

    /**
     * 获取辅助表
     */
    public static SysDatashow getSysDatashow(String data_type, String data_key, int data_id) {
        SysDatashowService service = Utils_Context.getBean(SysDatashowService.class);
        Map params = new HashMap<String, Object>();
        params.put("dataType", data_type);
        params.put("dataKey", data_key);
        params.put("dataId", data_id);
        List<SysDatashow> list = service.getSysDatashowList(params);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
}
