package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.enums.FieldTagEnum;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出Excel相关
 * create by zhangxiaxin
 */
public class BLL_Excel {

    /**
     * 用户设置的导出字段
     */
    public static int getExportFieldCounts(int userId, int oid) {
        String sql = "select count(1) from tb_export_excel where isexport=1 and user_id={0} and oid={1}";
        sql = StringUtils.format(sql, userId, oid);
        return NumberUtils.strToInt(DBFunction.executeScalar(sql));
    }

    /**
     * 对象导出字段列表获取
     */
    public static JsonArray getExportFieldGrid(int userId, int oid, int isexport, int show_table) throws Exception {
        String sql = "select a.fid,b.table_explain,a.field_explain,a.field_name,\n" +
                " ifnull(e.serialcode,a.serialcode) serialcode,ifnull(e.isexport,0) isexport,d.xtype_name\n" +
                " from tb_gen_field a left join view_gen_xtype d on a.xtype=d.xtype\n" +
                " left join tb_export_excel e on e.user_id=%d and e.oid=%d and e.fid=a.fid,\n" +
                " tb_gen_table b where a.tid=b.tid and b.oid=%d and (a.isdefine=1 or a.iscolumns=1)\n" +
                " and a.field_tag!='primary_key'  and b.tb_relation in('主表','1对1') \n" +
                " and a.data_type not in('text','longtext','varchar(max)','nvarchar(max)')";

        if (isexport == 1) sql += " and ifnull(e.isexport,0)=1";
        sql += " order by 5";
        sql = String.format(sql, userId, oid, oid);

        //用户未设置过导出Excel列表
        if (BLL_Excel.getExportFieldCounts(userId, oid) == 0) {
            sql = "select a.fid,b.table_explain,a.field_explain,a.field_name, \n" +
                    " a.serialcode,a.iscolumns isexport,d.xtype_name \n" +
                    " from tb_gen_field a left join view_gen_xtype d on a.xtype=d.xtype, \n" +
                    "  tb_gen_table b where a.tid=b.tid and b.oid=%d and (a.isdefine=1 or a.iscolumns=1) \n" +
                    "   and a.field_tag!='primary_key'  and b.tb_relation in('主表','1对1') \n" +
                    "   and a.data_type not in('text','longtext','varchar(max)','nvarchar(max)')";

            if (isexport == 1) sql += " and a.iscolumns=1";
            sql += " order by a.serialcode";
            sql = String.format(sql, oid);
        }
        return DBFunction.executeJsonArray(sql);
    }

    /**
     * 对象导出字段列表保存
     */
    public static boolean saveExportFieldGrid(int user_id, int oid, String saveVal) throws Exception {
        DBFunction.startTransaction();
        try {
            String del_sql = "delete from tb_export_excel where user_id=@user_id and oid=@oid";  //删除原有的配置
            List<DBParameter> ls = new ArrayList<>();
            ls.add(new DBParameter("@user_id", user_id));
            ls.add(new DBParameter("@oid", oid));
            DBFunction.executeNonQuery(del_sql, DBParameter.getParameter(ls));

            String[] Items = saveVal.split(";");
            for (String item : Items) {
                String[] arr = item.split(":");
                Map ht = new HashMap();
                ht.put("user_id", user_id);
                ht.put("oid", oid);
                ht.put("fid", arr[0]);
                ht.put("serialcode", arr[1]);
                ht.put("isexport", arr[2]);
                DBFunction.insertByTbName(ht, "tb_export_excel");
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
     * 清空导出列表配置
     */
    public static void clearExportFieldGrid(int user_id, int oid) throws Exception {
        String del_sql = "delete from tb_export_excel where user_id=@user_id and oid=@oid";  //删除原有的配置
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@user_id", user_id));
        ls.add(new DBParameter("@oid", oid));
        DBFunction.executeNonQuery(del_sql, DBParameter.getParameter(ls));
    }

    /**
     * 获取导出Excel用户配置列表
     */
    public static Map getExportFieldCofing(int userId, int oid) throws Exception {
        String sql = "select a.fid,a.tid,concat(b.table_name,a.extend_suf) table_name,a.field_name,a.field_tag,a.field_explain,b.tb_relation,\n" +
                "ifnull(e.serialcode,a.serialcode) serialcode from tb_gen_field a left join tb_export_excel e \n" +
                "on e.user_id=%d and e.oid=%d and e.fid=a.fid,tb_gen_table b where a.tid=b.tid and b.oid=%d \n" +
                "and (a.isdefine=1 or a.iscolumns=1) and a.field_tag!='primary_key'  and b.tb_relation in('主表','1对1') \n" +
                "and ifnull(e.isexport,0)=1 and a.data_type not in('text','longtext','varchar(max)','nvarchar(max)') order by 8";
        sql = String.format(sql, userId, oid, oid);

        //用户未设置过导出Excel列表
        if (BLL_Excel.getExportFieldCounts(userId, oid) == 0) {
            sql = "select a.fid,a.tid,concat(b.table_name,a.extend_suf) table_name,a.field_name,a.field_tag,a.field_explain,b.tb_relation,a.serialcode\n" +
                    " from tb_gen_field a,tb_gen_table b where a.tid=b.tid and b.oid=%d and (a.isdefine=1 or a.iscolumns=1) \n" +
                    " and a.field_tag!='primary_key'  and b.tb_relation in('主表','1对1') and a.iscolumns=1\n" +
                    " and a.data_type not in('text','longtext','varchar(max)','nvarchar(max)') order by a.serialcode";
            sql = String.format(sql, oid);
        }
        JsonArray dt = DBFunction.executeJsonArray(sql);
        if (dt != null && dt.size() > 0) {
            Map<String, String> tables = new HashMap<>();
            int count = 1, main_tid = 0;
            for (int i = 0; i < dt.size(); i++) {
                JsonObject dr = GsonUtils.getObject(dt, i);
                String tb_relation = GsonUtils.tryParse(dr, "tb_relation", "");
                if (tb_relation.equals("主表")) {
                    main_tid = GsonUtils.tryParse(dr, "tid", 0);
                    tables.put(GsonUtils.tryParse(dr, "table_name", ""), "a" + count++);
                    break;
                }
            }
            for (int i = 0; i < dt.size(); i++) {
                JsonObject dr = GsonUtils.getObject(dt, i);
                String tb_relation = GsonUtils.tryParse(dr, "tb_relation", "");
                String table_name = GsonUtils.tryParse(dr, "table_name", "");
                if (tb_relation.equals("主表")) continue;
                if (tables.containsKey(table_name)) continue;
                tables.put(table_name, "a" + count++);
            }
            String primary_key = BLL_Design.getFieldName(main_tid, FieldTagEnum.主键.getValue(), "idleaf");
            String field_isdel = BLL_Design.getFieldName(main_tid, FieldTagEnum.删除标识.getValue(), "");

            StringBuilder show_fields = new StringBuilder("a1.idleaf");
            StringBuilder link_table = new StringBuilder();
            StringBuilder where = new StringBuilder();
            if (StringUtils.isNotEmpty(field_isdel))
                where.append(String.format(" and a1.%s=0", field_isdel));

            for (String key : tables.keySet()) {
                if (!StringUtils.isEmpty(link_table.toString())) link_table.append(" left join ");
                link_table.append(key).append(" ").append(tables.get(key));

                if (!tables.get(key).equals("a1")) {
                    link_table.append(StringUtils.format(" on a1.{1}={0}.{1}", tables.get(key), primary_key));
                }
            }
            for (int i = 0; i < dt.size(); i++) {
                JsonObject dr = GsonUtils.getObject(dt, i);
                String table_name = GsonUtils.tryParse(dr, "table_name", "");
                String alias_name = tables.get(table_name);

                String field_name = GsonUtils.tryParse(dr, "field_name", "");
                String field_tag = GsonUtils.tryParse(dr, "field_tag", "");
                if (field_tag.equals(FieldTagEnum.创建时间.getValue())
                        || field_tag.equals(FieldTagEnum.修改时间.getValue())) {
                    show_fields.append(StringUtils.format(",date_format({0}.{1},'%Y-%m-%d %H:%i:%s') {1}", alias_name, field_name));
                } else {
                    show_fields.append(",").append(alias_name).append(".").append(field_name);
                }
            }
            String sql_showfields = show_fields.toString().toLowerCase();
            sql_showfields = sql_showfields.replace("a1.idleaf,", "");

            Map ht1 = new HashMap();
            ht1.put("searchgrid_showfields", sql_showfields);
            ht1.put("searchgrid_tables", link_table.toString());
            ht1.put("searchgrid_where", where.toString());
            ht1.put("excel_fields", dt);
            return ht1;
        }
        return null;
    }
}
