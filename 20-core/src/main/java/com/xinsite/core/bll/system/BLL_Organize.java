package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.List;
import java.util.Map;

/**
 * 单位组织：机构管理
 * create by zhangxiaxin
 */
public class BLL_Organize {

    /**
     * 获取排序号
     */
    public static int getSerialCode(int pid) {
        String str = "ifnull(max(serialcode),0)+1";
        String strSql = StringUtils.format("select {0} from sys_organize where pid={1}", str, pid);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 保存排序
     */
    public static boolean saveSort(List<Map> list) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_organize set pid={0},serialcode={1} where org_id={2};";
        for (Map ht : list) {
            sb.append(StringUtils.format(sql, ht.get("pid"), ht.get("index"), ht.get("id")));
        }
        return DBFunction.executeNonQuery(sb.toString()) > 0;
    }

    /**
     * 删除列表
     */
    public static boolean deleteByIds(String id, int idsVal) throws Exception {
        String strSql = StringUtils.format("update sys_organize set isdel=1 where {0} in({1})", id, idsVal);
        return DBFunction.executeNonQuery(strSql) > 0;
    }

    /**
     * 保存信息
     */
    public static int saveInfo(Map<String, Object> ht, int org_id, int pid) throws Exception {
        if (org_id == 0) {
            ht.put("pid", pid);
            ht.put("isdel", 0);
            ht.put("create_time", DateUtils.getDateTime());
            ht.put("serialcode", BLL_Organize.getSerialCode(pid));
            org_id = DBFunction.insertByTbName(ht, "sys_organize");
        } else {
            if (!DBFunction.updateByTbName(ht, "sys_organize", "org_id=" + org_id))
                org_id = 0;
        }
        return org_id;
    }

    /**
     * 查询列表中 列表获取文本值
     */
    public static void setGridOrgText(JsonArray array, String field_name) throws Exception {
        if (array == null || array.size() == 0) return;
        if (!GsonUtils.getArrayFields(array).contains(field_name)) return;
        String sql = "select org_id,company_name,serialcode from sys_organize order by serialcode";
        JsonArray code = DBFunction.executeJsonArray(sql);

        String add_field = field_name + "_text";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String values = StringUtils.joinAsFilter(GsonUtils.tryParse(dr, field_name, ""));
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "org_id", values);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (sb.length() != 0) sb.append(",");
                sb.append(GsonUtils.tryParse(r, "company_name", ""));
            }
            dr.addProperty(field_name, sb.toString());
        }
    }

    /**
     * 设置是否有管理员
     */
    public static void setManagers(JsonArray dt) throws Exception {
        if (dt == null && dt.size() > 0) return;
        String sql = "select u1.org_id,u1.user_id from sys_user u1,sys_role r1 where u1.role_id=r1.role_id\n" +
                "and u1.isdel=0 and u1.user_state=1 and r1.issys=1 and r1.isdel=0";
        JsonArray array = DBFunction.executeJsonArray(sql);

        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            int org_id = GsonUtils.tryParse(dr, "id", 0);
            JsonArray drs = GsonUtils.getWhereArrayByIds(array, "org_id", org_id);
            dr.addProperty("ismanagers", drs.size());
        }
    }

    /**
     * 获取机构简称
     */
    public static String getShortName(int org_id) {
        String QuerySql = "select ShortName from sys_organize where org_id=" + org_id;
        String obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj;
    }

    /**
     * 获取机构名称
     */
    public static String getCompanyName(int org_id) {
        String QuerySql = "select company_name from sys_organize where org_id=" + org_id;
        String obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj;
    }
}








