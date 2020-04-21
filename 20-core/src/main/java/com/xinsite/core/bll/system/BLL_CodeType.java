package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 字典管理：编码类型
 * create by zhangxiaxin
 */
public class BLL_CodeType {
    /**
     * 根据条件获取编码类型
     */
    public static JsonArray getCodeTypeTree(String Condition) throws Exception {
        String sql = "select id,name text,pid,istree,data_key,ispublic,issys," +
                "case when b.expanded='true' then 'true' else null end expand," +
                "case when  (select count(1) from sys_codetype a where a.isdel=0 and a.Pid=b.Id)>0 then 'false' else 'true' end leaf\n" +
                "from sys_codetype b where isdel=0 {0} order by serialcode";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition));
    }

    public static JsonArray getAllCodeType(String xtype, String condition) throws Exception {
        String sql = "select data_key id,name from sys_codetype a where not exists (select 1 from sys_codetype b where a.id=b.pid) and istree=0 {0} order by pid,serialcode";
        if (xtype.equals("tree")) {
            sql = "select data_key id,name from sys_codetype a where Id not in(select pid from sys_codetype) and istree=1 {0} order by pid,serialcode";
        }
        return DBFunction.executeJsonArray(StringUtils.format(sql, condition));
    }

    /**
     * 保存编码类型排序
     */
    public static boolean saveCodeTypeSort(List<Map> list) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_codetype set Pid={0},serialcode={1} where Id={2};";
        for (Map ht : list) {
            sb.append(StringUtils.format(sql, ht.get("pid"), ht.get("serialcode"), ht.get("id")));
        }
        return DBFunction.executeNonQuery(sb.toString()) > 0;
    }

    /**
     * 获取排序号
     */
    public static int getSerialCode(int pid) {
        String str = "ifnull(max(serialcode),0)+1";
        String strSql = StringUtils.format("select {0} from sys_codetype where Pid={1}", str, pid);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * data_key是否存在
     */
    public static boolean isExistDataKey(int id, String data_key) throws Exception {
        String sql = "select id,data_key from sys_codetype where data_key=@data_key or id=" + id;
        JsonArray array = DBFunction.executeJsonArray(sql, new DBParameter("@data_key", "=", data_key));
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (GsonUtils.tryParse(dr, "data_key", "").equalsIgnoreCase(data_key)) {
                if (id == 0)
                    return true;
                else if (id != dr.get("id").getAsInt())
                    return true;
            }
        }
        return false;
    }

    /**
     * 获取data_key
     */
    public static String getDataKey(int id) {
        String QuerySql = "select data_key from sys_codetype where id=" + id;
        Object obj = DBFunction.executeScalar(QuerySql);
        String data_key = (obj == null ? "" : obj.toString());
        return data_key;
    }

    /**
     * 获取编码类型Id
     */
    public static int getCodeTypeId(String data_key) {
        if (StringUtils.isEmpty(data_key)) return 0;
        String strSql = "select id from sys_codetype where data_key=@data_key";
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql, new DBParameter("@data_key", "=", data_key)));
    }

    /**
     * 获取编码类型名称
     */
    public static String getCodeTypeName(int code_id) {
        String QuerySql = "select a1.name from sys_codetype a1,sys_code b1 where a1.id=b1.codetype_id and a1.code_deleted=1 and b1.id=" + code_id;
        Object obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj.toString();
    }

}







