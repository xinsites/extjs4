package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.DataTypeEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 字典管理：编码管理
 * create by zhangxiaxin
 */
public class BLL_Code {
    /**
     * 根据编码表树(逐层加载数据)
     */
    public static JsonArray getTreeComboBox(int Pid, List<DBParameter> ls, String Condition) throws Exception {
        String sql = "select id,text,'' disabled,\n" +
                "        case when b.expanded='true' then 'true' else null end expanded,\n" +
                "        case when (select count(1) from sys_code a where a.isdel=0 and a.pid=b.id)>0 then 'false' else 'true' end leaf\n" +
                "        from sys_code b where b.isdel=0 and Pid=%d%s order by b.serialcode";

        return DBFunction.executeJsonArray(String.format(sql, Pid, Condition), DBParameter.getParameter(ls));
    }

    public static JsonArray getAllCode(int codetype_id) throws Exception {
        String sql = "select id,value,text from sys_code where codetype_id=@codetype_id and isdel=0 order by pid,serialcode";
        return DBFunction.executeJsonArray(sql, new DBParameter("@codetype_id", "=", codetype_id));
    }

    public static JsonArray getAllTreeCode(int codetype_id) throws Exception {
        String sql = "select id,pid,text,\n" +
                "                    case when expanded='true' then 'true' else null end expanded\n" +
                "                    from sys_code where codetype_id=" + codetype_id + " and isdel=0 order by pid,serialcode";
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 根据编码表树(全部数据)
     */
    public static JsonArray getTreeComboBox(List<DBParameter> ls, String Condition) throws Exception {
        String sql = "select id,b.pid,text,'' disabled,\n" +
                "        case when b.expanded='true' then 'true' else null end expanded\n" +
                "        from sys_code b where b.isdel=0 {0} order by b.serialcode";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition), DBParameter.getParameter(ls));
    }

    /**
     * 保存编码表排序
     */
    public static boolean saveCodeSort(List<Map> list) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update sys_code set Pid={0},serialcode={1} where Id={2};";
        for (Map ht : list) {
            sb.append(StringUtils.format(sql, ht.get("pid"), ht.get("index"), ht.get("id")));
        }
        return DBFunction.executeNonQuery(sb.toString()) > 0;
    }

    public static String getComboBoxText(String Ids) throws Exception {
        Ids = StringUtils.joinAsFilter(Ids);
        if (StringUtils.isEmpty(Ids)) return "";
        String sql = "select id,text from sys_code where id in({0}) order by serialcode";
        JsonArray array = DBFunction.executeJsonArray(StringUtils.format(sql, Ids));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (sb.length() != 0) sb.append(",");
            sb.append(dr.get("text").getAsString());
        }
        return sb.toString();
    }

    public static String getComboBoxTextByVals(String data_key, String values) throws Exception {
        if (StringUtils.isEmpty(data_key) || StringUtils.isEmpty(values)) return "";
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@data_key", data_key));
        String[] strs = values.split(",");
        String invalue = "";
        for (int i = 0; i < strs.length; i++) {
            ls.add(new DBParameter("@Value" + i, strs[i]));
            if (invalue.equals("")) invalue = "@Value" + i;
            else invalue += "," + ("@Value" + i);
        }
        String sql = "select a1.id,a1.text from sys_code a1,sys_codetype b1 where a1.codetype_id=b1.id" +
                " and data_key=@data_key and value in({0}) order by a1.serialcode";
        JsonArray array = DBFunction.executeJsonArray(StringUtils.format(sql, invalue), DBParameter.getParameter(ls));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            if (sb.length() != 0) sb.append(",");
            sb.append(dr.get("text").getAsString());
        }
        return sb.toString();
    }

    public static JsonArray getCodeTree(List<DBParameter> ls, String Condition) throws Exception {
        String sql = "select a.id,a.pid,text,a.value,1 enabled,1 isshow,a.issys,a.codetype_id,b.Name type_name,\n" +
                "                    a.remark,case when a.expanded='true' then 'true' else null end expanded,a.expanded expand\n" +
                "                    from sys_code a,sys_codetype b  where a.codetype_id=b.Id and a.isdel=0 \n" +
                "                    {0} order by a.codetype_id,a.pid,a.serialcode";

        return DBFunction.executeJsonArray(StringUtils.format(sql, Condition), DBParameter.getParameter(ls));
    }

    /**
     * 获取排序号
     */
    public static int getSerialCode(int pid) {
        String str = "ifnull(max(serialcode),0)+1";
        String strSql = StringUtils.format("select {0} from sys_code where pid={1}", str, pid);
        return NumberUtils.strToInt(DBFunction.executeScalar(strSql));
    }

    /**
     * 获取指定编码类型的编码列表
     */
    public static JsonArray getCodeArray(String data_key) throws Exception {
        String sql = "select a1.id,a1.text,a1.value,a1.serialcode from sys_code a1,sys_codetype b1" +
                " where a1.codetype_id=b1.id and data_key=@data_key order by a1.serialcode";
        return DBFunction.executeJsonArray(sql, new DBParameter("@data_key", data_key));
    }

    /**
     * 成员编码
     */
    public static int saveCode(Map<String, Object> ht, int id, String data_key) {
        DBFunction.startTransaction();
        try {
            String enabled = "1";
            String isshow = "1";
            if (ht.containsKey("enabled")) {
                enabled = ht.get("enabled").toString();
                ht.remove("enabled");
            }
            if (ht.containsKey("isshow")) {
                isshow = ht.get("isshow").toString();
                ht.remove("isshow");
            }
            if (id == 0) {
                id = DBFunction.insertByTbName(ht, "sys_code");
            } else {
                DBFunction.updateByTbName(ht, "sys_code", "id=" + id);
            }
            String disabled = !"1".equals(enabled) ? "disabled" : "";
            long ds_id = 0L;
            ds_id = BLL_DataShow.save(DataTypeEnum.编码表.getValue(), data_key, id, "disabled", disabled);
            ds_id = BLL_DataShow.save(DataTypeEnum.编码表.getValue(), data_key, id, "isshow", isshow);
            BLL_DataShow.delete(ds_id, disabled, isshow);
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return 0;
        }
        return id;
    }

    public static String getTreeCodePathName(int code_id) throws Exception {
        String path_name = "";
        List<String> names = new ArrayList<>();
        String sql = "select id,pid,text from sys_code where id=";
        while (true) {
            JsonArray array = DBFunction.executeJsonArray(sql + code_id);
            if (array == null || array.size() == 0) break;
            JsonObject object = GsonUtils.getObject(array, 0);
            names.add(GsonUtils.tryParse(object, "text", ""));
            code_id = GsonUtils.tryParse(object, "pid", 0);
            if (code_id == 0) break;
        }
        if (names.size() > 0) {
            Collections.reverse(names);
            path_name = StringUtils.joinAsList(names, "/");
        }
        return path_name;
    }
}







