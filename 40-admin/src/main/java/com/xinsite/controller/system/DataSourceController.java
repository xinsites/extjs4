package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.DataTypeEnum;
import com.xinsite.common.uitls.codec.AesUtils;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.ObjectUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.system.BLL_DataShow;
import com.xinsite.core.bll.system.BLL_DataSource;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-02
 * 系统管理->字典管理->系统数据源
 */

@RestController
@RequestMapping(value = "system/ds")
public class DataSourceController extends BaseController {

    //region 系统数据源管理
    //系统数据源树形菜单
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:datasource:tree")
    public String tree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        try {
            String sql_where = " and PId=" + node;
            JsonArray dtALL = BLL_DataSource.getDataSourceTree(sql_where);
            return JsonTree.getTreeJson(dtALL, "");
        } catch (Exception ex) {
            LogError.write("数据源树目录", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // 系统数据源排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:datasource:sort")
    public String sort(HttpServletRequest request) {
        String sort_vals = getParaValue(request, "sort_vals", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (!StringUtils.isEmpty(sort_vals)) {
                String[] Items = sort_vals.split(";");
                List<Hashtable> list = new ArrayList<>();
                for (String item : Items) {
                    String[] arr = item.split(":");
                    if (arr.length == 3) {
                        Hashtable<String, Object> ht = new Hashtable();
                        ht.put("id", NumberUtils.strToInt(arr[0]));
                        ht.put("pid", NumberUtils.strToInt(arr[1]));
                        ht.put("serialcode", NumberUtils.strToInt(arr[2]));
                        list.add(ht);
                    }
                }
                if (BLL_DataSource.saveDataSourceSort(list)) {
                    LogUtils.addOperateLog(item_id, "数据源排序", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("数据源排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion

    //region 数据源数据
    //数据源单元格编辑
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:datasource:mod")
    public String editing(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        String data_key = getParaValue(request, "data_key", "");
        String add_key = getParaValue(request, "add_key", "");  //附加Key,特殊不可选设置

        try {
            if (StringUtils.isEmpty(data_key)) {
                return ret.getFailResult("该系统数据源未标识！");
            }
            if (Id > 0 && !StringUtils.isEmpty(field)) {
                if (!StringUtils.isEmpty(add_key)) data_key += "." + add_key;
                BLL_DataShow.save(DataTypeEnum.系统数据源.getValue(), data_key, Id, field, value);
                LogUtils.addOperateLog(item_id, "单元格编辑", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("数据源单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //获取系统数据源数据
    @RequestMapping(value = "storedata")
    public String storeData(HttpServletRequest request) {
        String data_key = getParaValue(request, "data_key", "");
        String add_key = getParaValue(request, "add_key", "");  //附加Key,特殊不可选设置
        String disableds = getParaValue(request, "disableds", "").trim(); //不能选择的数据，页面传值
        String noshows = getParaValue(request, "noshows", "").trim();     //不能显示的数据，页面传值
        String data_type = getParaValue(request, "data_type", "combo");
        try {
            if (StringUtils.isEmpty(data_key)) return "[]";
            JsonObject source = BLL_DataSource.getDataSourceByKey(data_key);
            if (source != null) {
                if (StringUtils.isEmpty(data_type))
                    data_type = GsonUtils.tryParse(source, "data_type");  //tree：下拉树；combo：下拉列表
                String query_sql = GsonUtils.tryParse(source, "query_sql");   //查询语句
                String primary_key = GsonUtils.tryParse(source, "primary_key");
                String query_field = GsonUtils.tryParse(source, "query_field");
                data_key = GsonUtils.tryParse(source, "data_key");

                if (!StringUtils.isEmpty(query_sql)) {
                    query_sql = query_sql.replace("{org_id}", UserUtils.getOrgId() + "");
                    String queryText = getParaValue(request, "query", "").trim();         //查询关键字
                    List<DBParameter> ls = new ArrayList<>();
                    String sql_where = SearchUtils.addWhereName(ls, query_field, queryText);

                    int isshow = getParaValue(request, "isshow", 0);
                    //SysData:显示系统数据源栏中列表; InputForm：表单输入框配置中的系统数据源列表；为空：下拉列表数据
                    String queryType = getParaValue(request, "queryType", "");
                    boolean only_show = false;  //是否只包含可见数据源
                    if (queryType.equalsIgnoreCase("SysData") && isshow == 1) only_show = true;
                    else if (!queryType.equalsIgnoreCase("SysData")) only_show = true;

                    if (!StringUtils.isEmpty(add_key)) data_key += "." + add_key;

                    JsonArray show = BLL_DataShow.getDataShow(DataTypeEnum.系统数据源.getValue(), data_key);
                    if (only_show) {
                        String not_ids = BLL_DataShow.getNotShowIds(show);
                        if (!StringUtils.isEmpty(noshows)) {
                            if (!StringUtils.isEmpty(not_ids)) not_ids += "," + noshows;
                            else not_ids = noshows;
                        }
                        if (!StringUtils.isEmpty(not_ids)) {
                            sql_where += String.format(" and a1.%s not in(%s)", primary_key, not_ids); //只包含可见数据源
                        }
                    }
                    BLL_DataShow.setGridDisableds(show, disableds);
                    if (data_type.equalsIgnoreCase("combo"))
                        return getStoreComboData(query_sql, show, data_key, sql_where, ls, queryType);
                    else
                        return getStoreTreeData(request, show, data_key, source, query_sql, sql_where, ls, queryType);
                }
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        if (data_type.equalsIgnoreCase("combo"))
            return retGrid.getFailResult();
        return "[]";
    }

    public String getStoreComboData(String query_sql, JsonArray show, String data_key, String sql_where, List<DBParameter> ls, String queryType) {
        try {
            if (!StringUtils.isEmpty(query_sql)) {
                JsonArray dt = SearchUtils.searchAll(query_sql.replace("{where}", sql_where), ls);
                if (queryType.equalsIgnoreCase("SysData")) {  //系统数据源栏目请求数据
                    BLL_DataShow.setEnabled(dt, DataTypeEnum.系统数据源.getValue(), data_key);
                    return JsonTree.getTreeJsonByPid(dt, "0");
                } else if (queryType.equalsIgnoreCase("InputForm")) {  //表单输入框定义栏目中，系统数据源数据请求
                    return retGrid.getGridJson(dt, dt.size(), "id,value,text,name");
                } else {  //各应用下拉列表请求数据
                    BLL_DataShow.setDisabled(dt, show);
                    return retGrid.getGridJson(dt, dt.size(), "id,value,name,disabled");
                }
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    public String getStoreTreeData(HttpServletRequest request, JsonArray show, String data_key, JsonObject source, String query_sql, String sql_where, List<DBParameter> ls, String queryType) {
        try {
            if (!StringUtils.isEmpty(query_sql)) {
                int node = getParaValue(request, "node", 0);  //树加载的结点
                String data_page = GsonUtils.tryParse(source, "data_page"); //下拉树时：all[加载全部]；level[逐级加载]，下拉列表：都加载全部
                String queryText = getParaValue(request, "query", "").trim();         //查询关键字
                String primary_key = GsonUtils.tryParse(source, "primary_key");
                String parent_field = GsonUtils.tryParse(source, "parent_field");

                boolean level_load = StringUtils.isEmpty(queryText) && data_page.equalsIgnoreCase("level"); //是否逐级加载
                if (level_load) { //逐级加载
                    sql_where += StringUtils.format(" and a1.{0}={1}", parent_field, node);
                }

                JsonArray dt = SearchUtils.searchAll(query_sql.replace("{where}", sql_where), ls);
                if (!StringUtils.isEmpty(queryText) && dt != null && dt.size() > 0) { //有关键字查询条件
                    String table_name = GsonUtils.tryParse(source, "table_name");
                    String ids = TreeUtils.getTreeTableAllPids(dt, "id", "pid", table_name, primary_key, parent_field);
                    sql_where = StringUtils.format(" and a1.{0} in({1})", primary_key, ids);
                    dt = SearchUtils.searchAll(query_sql.replace("{where}", sql_where), null);
                }

                String otherAttr = "";
                if (StringUtils.isEmpty(queryType)) {
                    boolean isCheck = ObjectUtils.toBoolean(getParaValue(request, "isCheck", "false")); //是否多选
                    if (!StringUtils.isEmpty(queryText)) otherAttr = "expanded:true";
                    if (isCheck) {
                        if (!StringUtils.isEmpty(otherAttr))
                            otherAttr += ",checked:false";
                        else
                            otherAttr += "checked:false";
                    }
                    BLL_DataShow.setDisabled(dt, show);  //各应用下拉列表请求数据
                }
                if (queryType.equalsIgnoreCase("SysData")) {  //系统数据源栏目请求数据
                    BLL_DataShow.setEnabled(dt, DataTypeEnum.系统数据源.getValue(), data_key);
                }
                if (level_load) { //逐级加载
                    return JsonTree.getTreeJson(dt, otherAttr); //（逐层加载）
                } else {
                    return JsonTree.getTreeJsonByPid(dt, node + "", otherAttr, otherAttr);
                }
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //endregion

    //region 系统数据源统一获取数据
    // 获取系统数据源文本值
    @RequestMapping(value = "text")
    public String text(HttpServletRequest request) {
        String data_key = getParaValue(request, "data_key", ""); //系统数据源表主键值
        String Ids = getParaValue(request, "ids", ""); //数据源表记录的表主键值
        try {
            if (!StringUtils.isEmpty(data_key) && !StringUtils.isEmpty(Ids)) {
                String texts = "";
                texts = BLL_DataSource.getSystemDataText(data_key, Ids);
                return ret.getSuccessResult("texts", texts);
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion

    //region MyBatis数据源
    //获取数据源所有数据库表
    @RequestMapping(value = "mybatis/tables")
    public String mybatisTables(HttpServletRequest request) {
        String db_key = getParaValue(request, "db_key", "");
        String db_name = getParaValue(request, "db_name", "");
        try {
            if (StringUtils.isNotEmpty(db_key) && StringUtils.isNotEmpty(db_name)) {
                JsonArray array = BLL_DataSource.getTableNames(db_key, db_name);
                return retGrid.getGridJson(array);
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //生成MyBatis数据源
    @RequestMapping(value = "mybatis/source")
    public String mybatisSource(HttpServletRequest request) {
        String db_key = getParaValue(request, "db_key", "");
        String db_name = getParaValue(request, "db_name", "");
        String db_tables = getParaValue(request, "db_tables", "");
        try {
            if (StringUtils.isNotEmpty(db_key) && StringUtils.isNotEmpty(db_name) && StringUtils.isNotEmpty(db_tables)) {
                Map map = BLL_DataSource.getMyBatisSource(db_key, db_name, db_tables);
                String mybatis_source = GsonUtils.toJson(map);
                //content = AesUtils.decode(content);
                return ret.getSuccessResult("data", AesUtils.encode(mybatis_source));
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion

    //region 生成选择框数据源
    @RequestMapping(value = "select/source")
    public String selectSource(HttpServletRequest request) {
        String store_datas = getParaValue(request, "store_datas", "");
        try {
            if (StringUtils.isNotEmpty(store_datas)) {
                return ret.getSuccessResult("data", AesUtils.encode(store_datas));
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion
}




