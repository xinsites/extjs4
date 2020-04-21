package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.LogTypeEnum;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.core.utils.log.LogError;

/**
 * 系统日志
 * create by zhangxiaxin
 */
public class BLL_Log {

    public static JsonObject getJsonObject(LogTypeEnum log_type) {
        JsonObject obj = new JsonObject();
        obj.addProperty("text", log_type.name());
        obj.addProperty("id", log_type.getIndex());
        obj.addProperty("pid", 0);
        obj.addProperty("leaf", true);
        return obj;
    }

    public static JsonArray GetLogTypeJsonArray() {
        JsonArray table = new JsonArray();
        table.add(getJsonObject(LogTypeEnum.登录日志));
        table.add(getJsonObject(LogTypeEnum.访问日志));
        table.add(getJsonObject(LogTypeEnum.操作日志));
        table.add(getJsonObject(LogTypeEnum.异常日志));
        return table;
    }

    /**
     * 删除列表
     */
    public static boolean deleteByIds(String id, String idsVal) {
        boolean result = false;
        DBFunction.startTransaction();
        try {
            idsVal = StringUtils.joinAsFilter(idsVal);
            String strSql = StringUtils.format("update sys_log set isdel=1 where {0} in({1})", id, idsVal);
            result = DBFunction.executeNonQuery(strSql) > 0;
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return result;
    }

    /**
     * 清空日志记录(只保留多少天的日志记录)
     */
    public static boolean clearLogRecords(int days, int log_type) {
        DBFunction.startTransaction();
        try {
            if (days == 0) days = -1;
            String strSql = "delete from sys_log where (org_id=0 or org_id={0}) and DATEDIFF(now(), create_time)>{1}";
            if (log_type > 0) strSql += " and log_type=" + log_type;
            DBFunction.executeNonQuery(StringUtils.format(strSql, UserUtils.getOrgId(), days));
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return true;
    }

}








