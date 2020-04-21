package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.system.ManTypeEnum;
import com.xinsite.common.enums.system.MemberEnum;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.info.BLL_Extend_Info;
import com.xinsite.common.enums.system.ExtendEnum;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理：下属成员管理
 * create by zhangxiaxin
 */
public class BLL_Leaders {


    /**
     * 根据用户ID，获取用户直属领导
     */
    public static int getUserLeader(int user_id) throws Exception {
        String sql = "select table_id from sys_member where table_name=@table_name and user_id=@user_id";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", "=", MemberEnum.用户领导.getValue()));
        ls.add(new DBParameter("@user_id", "=", user_id));
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
        if (array.size() > 0) {
            JsonObject dr = GsonUtils.getObject(array, 0);
            return GsonUtils.tryParse(dr, "table_id", 0);
        }
        return 0;
    }

}
