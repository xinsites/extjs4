package com.xinsite.controller.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.bll.design.BLL_Search;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.DBParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * create by zhangxiaxin
 * create time: 2019-12-06
 * object name: 设计表高级查询
 */

@RestController
@RequestMapping(value = "design/search")
public class SearchController extends BaseController {

    // 获取查询对象的所有查询表
    @RequestMapping(value = "table/combo")
    public String table(HttpServletRequest request) {
        String table_key = getParaValue(request, "table_key", "");
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (oid > 0) {
                JsonArray dt = BLL_Search.getSearchTable(oid);
                return retGrid.getGridJson(dt, dt.size(), "id,name");
            } else {
                JsonArray dt = BLL_Search.getSearchTable(table_key);
                return retGrid.getGridJson(dt, dt.size(), "id,name");
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //获取所有查询操作符
    @RequestMapping(value = "operator")
    public String operator(HttpServletRequest request) {
        try {
            JsonArray dt = BLL_Search.getSearchOperator();
            JsonArray dt1 = BLL_Search.getSearchOperatorAll();
            Hashtable jsn = new Hashtable();
            for (int i = 0; i < dt.size(); i++) {
                JsonObject dr = GsonUtils.getObject(dt, i);
                String field_type = GsonUtils.tryParse(dr, "field_type", "");
                JsonArray drs = GsonUtils.getWhereArray(dt1, "field_type", field_type);
                List<Map> list = new ArrayList<>();
                for (int j = 0; j < drs.size(); j++) {
                    JsonObject r = GsonUtils.getObject(drs, j);
                    Map ht = new HashMap();
                    ht.put("id", r.get("id").getAsString());
                    ht.put("name", r.get("name").getAsString());
                    list.add(ht);
                }
                jsn.put(field_type, list);
            }
            return ret.getSuccessResult("data", GsonUtils.toJson(jsn));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 高级查询列表
    @RequestMapping(value = "grid")
    public String searchGrid(HttpServletRequest request) {
        String table_key = getParaValue(request, "table_key", "");
        int tid = getParaValue(request, "tid", 0);
        String fids = getParaValue(request, "fids", "");  //定制字段
        String field_explain = getParaValue(request, "field_explain", "");
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (oid > 0) {
                PageHelper pager = SearchUtils.getPageHelper(request);
                fids = StringUtils.joinAsFilter(fids);
                pager.addPara(new DBParameter("@oid", oid));
                pager.querySql = "select distinct PrimaryKey from tb_gen_field a,tb_gen_table b\n" +
                        " where a.tid=b.tid and b.oid=@oid and a.issearchfield=1\n";
                if (!StringUtils.isEmpty(field_explain)) {
                    pager.addPara(new DBParameter("@field_explain", "%" + field_explain + "%"));
                    pager.querySql += " and a.field_explain like @field_explain";
                }
                if (!StringUtils.isEmpty(fids)) {
                    pager.querySql += StringUtils.format(" and fid in({0})", fids);
                } else {
                    if (tid == 0) tid = BLL_Design.getTableId(oid);
                    pager.addPara(new DBParameter("@tid", tid));
                    pager.querySql += " and b.tid=@tid";
                }
                pager.showColumns = "a.fid,a.tid,b.table_explain tab_name,b.table_key,a.field_name,a.field_explain,a.xtype field_type,a.field_type value_type,'=' operator,'等于' operator_desc, '' fieldvalue, '' fieldvalue2, '' fieldvalue_text, '' fieldvalue2_text, editor_search editor";
                JsonArray array = pager.getAllGrid("a.tid,a.serialcode");

                List<Map> dt = GsonUtils.getList(array, Map.class);
                for (Map<String, Object> dr : dt) {
                    //有些字段类型，去掉默认操作符
                    String xtype = dr.get("field_type").toString();
                    switch (xtype) {
                        case "textfield":
                        case "textareafield":
                        case "htmleditor":
                        case "ueditor":
                        case "kindeditor":
                        case "checkboxgroup":
                        case "multitreepicker":
                        case "multicombobox":
                            dr.put("operator", "like");
                            dr.put("operator_desc", "包含");
                            break;
                        case "numberfield":
                        case "datefield":
                        case "datetimefield":
                        case "timefield":
                        case "my97date":
                            dr.put("operator", "between");
                            dr.put("operator_desc", "区间");
                    }
                }
                array = GsonUtils.getBean(GsonUtils.toJson(dt), JsonArray.class);
                return retGrid.getGridJson(array, pager.recordCount);
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //定制查询字段配置列表
    @RequestMapping(value = "config")
    public String config(HttpServletRequest request) {
        String table_key = getParaValue(request, "table_key", "");
        int tid = getParaValue(request, "tid", 0);
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (oid > 0) {
                PageHelper pager = SearchUtils.getPageHelper(request);
                pager.querySql = "select distinct PrimaryKey from tb_gen_field a,tb_gen_table b \n" +
                        " where a.tid=b.tid and b.oid=@oid and b.tid=@tid and a.isdefine=1 and a.issearchfield=1\n";
                pager.addPara(new DBParameter("@oid", oid));
                if (tid > 0) {
                    pager.addPara(new DBParameter("@tid", tid));
                } else {
                    pager.querySql = pager.querySql.replace(" and b.tid=@tid", "");
                }
                pager.showColumns = "a.fid,b.table_explain tab_name,a.field_name,a.field_explain,0 search_field";
                JsonArray array = pager.getAllGrid("a.tid,a.serialcode");

                return retGrid.getGridJson(array, array.size());
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

}


