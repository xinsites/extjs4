package com.xinsite.core.bll.system;

import com.xinsite.common.enums.system.ManTypeEnum;
import com.xinsite.common.enums.system.MemberEnum;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * 部门、机构、流程角色等成员管理
 * create by zhangxiaxin
 */
public class BLL_Member {

    /**
     * 成员保存(用户领导)
     */
    public static boolean saveUserLeader(String table_name, int man_type, String IdVal) {
        DBFunction.startTransaction();
        try {
            table_name = table_name.toLowerCase();
            for (String item : IdVal.split(",")) {
                int user_id = NumberUtils.strToInt(item);
                if (user_id == 0) continue;
                if (!BLL_Member.isMember(table_name, man_type, user_id, user_id)) {
                    Map ht = new HashMap();
                    ht.put("table_name", table_name);
                    ht.put("table_id", user_id);
                    ht.put("user_id", user_id);
                    ht.put("man_type", man_type);
                    DBFunction.insertByTbName(ht, "sys_member");
                }
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return true;
    }


    /**
     * 成员保存
     */
    public static boolean saveMemberUser(String table_name, int table_id, int man_type, String IdVal) {
        if (table_name.equals(MemberEnum.用户领导.getValue()) && man_type == ManTypeEnum.领导.getIndex()) {
            return BLL_Member.saveUserLeader(table_name, man_type, IdVal);
        }
        DBFunction.startTransaction();
        try {
            table_name = table_name.toLowerCase();
            List<DBParameter> ls = new ArrayList<>();
            ls.add(new DBParameter("@table_name", "=", table_name));
            String strSql = StringUtils.format("delete from sys_member where table_name=@table_name and table_id={0} and man_type={1}", table_id, man_type);
            DBFunction.executeNonQuery(strSql, DBParameter.getParameter(ls));

            if (table_name.equals(MemberEnum.用户领导.getValue())
                    && man_type == ManTypeEnum.成员.getIndex() && StringUtils.isNotEmpty(IdVal)) {  //设置的是用户领导成员
                //删除下属成员所属的所有可能领导，确保一个用户一个直属领导
                strSql = StringUtils.format("delete from sys_member where table_name=@table_name and man_type={0} and user_id in({1})", man_type, IdVal);
                DBFunction.executeNonQuery(strSql, DBParameter.getParameter(ls));
            }
            for (String item : IdVal.split(",")) {
                int user_id = NumberUtils.strToInt(item);
                if (user_id == 0) continue;
                Map ht = new HashMap();
                ht.put("table_name", table_name);
                ht.put("table_id", table_id);
                ht.put("user_id", user_id);
                ht.put("man_type", man_type);
                DBFunction.insertByTbName(ht, "sys_member");
            }
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return true;
    }

    /**
     * 项目组用户移除
     */
    public static void removeMemberUser(String table_name, int table_id) throws Exception {
        if (table_id > 0) {
            BLL_Member.removeMemberUser(table_name, table_id, 0, 0);
        }
    }

    /**
     * 项目组用户移除
     */
    public static void removeMemberUser(String table_name, int table_id, int user_id) throws Exception {
        List<DBParameter> ls = new ArrayList<>();
        table_name = table_name.toLowerCase();
        ls.add(new DBParameter("@table_name", "=", table_name));
        String strSql = StringUtils.format("delete from sys_member where table_name=@table_name and table_id={0} and user_id={1}", table_id, user_id);
        DBFunction.executeNonQuery(strSql, DBParameter.getParameter(ls));
    }

    /**
     * 项目组用户移除
     */
    public static void removeMemberUser(String table_name, int table_id, int user_id, int man_type) throws Exception {
        List<DBParameter> ls = new ArrayList<>();
        table_name = table_name.toLowerCase();
        ls.add(new DBParameter("@table_name", "=", table_name));
        String strSql = "delete from sys_member where table_name=@table_name";
        if (table_id > 0) {
            ls.add(new DBParameter("@table_id", "=", table_id));
            strSql += " and table_id=@table_id";
        }
        if (user_id > 0) {
            ls.add(new DBParameter("@user_id", "=", user_id));
            strSql += " and user_id=@user_id";
        }
        if (man_type > 0) {
            ls.add(new DBParameter("@man_type", "=", man_type));
            strSql += " and man_type=@man_type";
        }
        DBFunction.executeNonQuery(strSql, DBParameter.getParameter(ls));
    }

    /**
     * 查询成员 获取成员值
     * <param name="dt">数据填充列表</param>
     * <param name="Primary_Key">数据列表主键字段</param>
     * <param name="table_name">成员表对应数据表名</param>
     * <param name="man_type">成员类型</param>
     * <param name="field_name">数据列表要加的字段</param>
     */
    public static void setGridUserNames(JsonArray array, String Primary_Key, String table_name, int man_type, String field_name) throws Exception {
        String sql = "select b1.table_id,a1.user_id,a1.user_name,a1.serialcode from sys_user a1,sys_member b1 where a1.isdel=0\n" +
                "        and a1.user_id = b1.User_Id and b1.table_name = @table_name and b1.man_type = @man_type order by a1.serialcode";
        List<DBParameter> ls = new ArrayList<>();
        table_name = table_name.toLowerCase();
        ls.add(new DBParameter("@table_name", "=", table_name));
        ls.add(new DBParameter("@man_type", "=", man_type));
        JsonArray code = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));

        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            String values = StringUtils.joinAsFilter(dr.get(Primary_Key).getAsString());
            if (StringUtils.isEmpty(values)) continue;

            JsonArray drs = GsonUtils.getWhereArrayByIds(code, "table_id", values);
            StringBuilder id = new StringBuilder();
            StringBuilder text = new StringBuilder();

            for (int j = 0; j < drs.size(); j++) {
                JsonObject r = GsonUtils.getObject(drs, j);
                if (id.length() != 0) id.append(",");
                id.append(GsonUtils.tryParse(r, "user_id", ""));

                if (text.length() != 0) text.append(",");
                text.append(GsonUtils.tryParse(r, "user_name", ""));
            }

            dr.addProperty(field_name, id.toString());
            dr.addProperty(field_name + "_text", text.toString());
        }
    }

    /**
     * 获取种类成员用户Id
     */
    public static List<Integer> getMemberUserIds(String table_name, int table_id, int man_type) throws Exception {
        String sql = "select b1.table_id,a1.user_id,a1.user_name,a1.serialcode from sys_user a1,sys_member b1 where a1.isdel=0\n" +
                "        and a1.user_id = b1.User_Id and b1.table_name=@table_name and b1.table_id=@table_id and b1.man_type=@man_type order by a1.serialcode";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", "=", table_name));
        ls.add(new DBParameter("@man_type", "=", man_type));
        if (table_id > 0) {
            ls.add(new DBParameter("@table_id", "=", table_id));
        } else {
            sql = sql.replace("and b1.table_id=@table_id ", "");
        }
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
        return ArrayUtils.listByField(array, "user_id", 0);
    }

    /**
     * 该种成员类型是否存在
     */
    public static boolean isMember(String table_name, int man_type, int user_id, int table_id) {
        String sql = "select count(1) from sys_member where table_name=@table_name and man_type=@man_type and user_id=@user_id";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", "=", table_name));
        ls.add(new DBParameter("@man_type", "=", man_type));
        ls.add(new DBParameter("@user_id", "=", user_id));
        if (table_id > 0) {
            sql += " and table_id=@table_id";
            ls.add(new DBParameter("@table_id", "=", table_id));
        }
        return NumberUtils.strToInt(DBFunction.executeScalar(sql, DBParameter.getParameter(ls))) > 0;
    }

    /**
     * 获取成员部门下属所有人员Id
     */
    public static List<Integer> getDeptUserIds(int user_id) throws Exception {
        List list = new ArrayList();
        String sql = "select a1.user_id,a1.user_name,a1.serialcode from sys_user a1 where a1.isdel=0\n" +
                "and a1.dept_id in(select table_id from sys_member where table_name=@table_name and user_id=@user_id)\n" +
                "order by a1.serialcode";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", "=", MemberEnum.部门管理.getValue()));
        ls.add(new DBParameter("@user_id", "=", user_id));
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
        return ArrayUtils.listByField(array, "user_id", 0);
    }

    /**
     * 获取该成员所在组中该种类所有成员
     * 比如用户在项目一与二组，则获取该二组的所有Man_Type人员
     */
    public static List<Integer> getManTypeMembersByUserId(String table_name, int man_type, int user_id) throws Exception {
        List list = new ArrayList();
        String sql = "select user_id from sys_member where table_name=@table_name and man_type=@man_type and table_id \n" +
                "in(select table_id from sys_member where table_name=@table_name and user_id=@user_id) ";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", "=", table_name));
        ls.add(new DBParameter("@man_type", "=", man_type));
        ls.add(new DBParameter("@user_id", "=", user_id));
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
        return ArrayUtils.listByField(array, "user_id", 0);
    }

    /**
     * 设置指定类型的成员
     */
    public static void setMemberNum(JsonArray dt, String field_table_id, String table_name, int man_type) throws Exception {
        if (dt == null || dt.size() == 0) return;
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", "=", table_name));
        ls.add(new DBParameter("@man_type", "=", man_type));
        String sql = "select table_id,count(user_id) member_num from sys_member where table_name=@table_name and man_type=@man_type group by table_id";
        JsonArray array = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));

        for (int i = 0; i < dt.size(); i++) {
            JsonObject dr = GsonUtils.getObject(dt, i);
            int id = GsonUtils.tryParse(dr, field_table_id, 0);
            JsonArray drs = GsonUtils.getWhereArray(array, "table_id", id);
            if (drs.size() > 0) {
                dr.addProperty("member_num", GsonUtils.getObjectValue(drs, 0, "member_num"));
            } else {
                dr.addProperty("member_num", 0);
            }
        }
    }

}








