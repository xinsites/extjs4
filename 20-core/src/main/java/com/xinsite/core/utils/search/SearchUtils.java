package com.xinsite.core.utils.search;

import com.google.gson.JsonArray;
import com.xinsite.common.enums.ErrorEnum;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.web.RequestUtils;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.model.search.OrderModel;
import com.xinsite.core.model.search.SearchModel;
import com.xinsite.core.model.search.SearchDataModel;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class SearchUtils {

    /**
     * 查询列表时，获取查询条件及相关参数
     * parameters:表示itemId
     */
    public static PageHelper getPageHelper(HttpServletRequest request, Object... parameters) throws AppException {
        PageHelper pager = new PageHelper();
        pager.request = request;
        if (parameters.length > 0) {
            pager.itemId = NumberUtils.strToInt(parameters[0]);
        }
        if (pager.itemId == 0) {
            pager.itemId = RequestUtils.getParaValue(request, "item_id", 0);
        }
        pager.linkType = RequestUtils.getParaValue(request, "linkType", "and").trim();
        if (!pager.linkType.equalsIgnoreCase("and")
                && !pager.linkType.equalsIgnoreCase("or")) pager.linkType = "and";

        String searchdata = RequestUtils.getParaValue(request, "searchdata", "");
        //searchdata = UtilString.unescape(searchdata);
        pager.searchs = GsonUtils.gsonToList(searchdata, SearchModel.class);
        if (pager.searchs != null && pager.searchs.size() > 0) {
            for (SearchModel search : pager.searchs) {
                if (!StringUtils.isEmpty(search.tableKey)) {
                    search.tableName = BLL_Design.getTableName(search.tableKey);
                }
                if (StringUtils.isEmpty(search.tableName))
                    throw new AppException(ErrorEnum.查询表未找到.getCode(), search.tableKey + "-数据表未配置");
            }
        }
        pager.pageStart = RequestUtils.getParaValue(request, "start", 0);
        pager.pageSize = RequestUtils.getParaValue(request, "limit", 0);
        if (pager.pageSize == 0) pager.pageSize = SysConfigCache.getPageSize();

        String Sorters = RequestUtils.getParaValue(request, "sort", "");
        if (!StringUtils.isEmpty(Sorters)) {
            try {
                List<OrderModel> list = GsonUtils.gsonToList(Sorters, OrderModel.class);
                if (list != null && list.size() > 0) {
                    pager.orderBy = StringUtils.EMPTY;
                    for (OrderModel en : list) {
                        if (!StringUtils.isEmpty(pager.orderBy)) pager.orderBy += ",";
                        pager.orderBy += StringUtils.format(" {0} {1}", en.property, en.direction);
                    }
                }
            } catch (Exception ex) {
                pager.orderBy = StringUtils.EMPTY;
            }
        }
        if (pager.orderBy == null) pager.orderBy = StringUtils.EMPTY;
        return pager;
    }

    public static boolean isEmptySearchs(List<SearchModel> searchs) {
        if (searchs.size() > 0) {
            for (SearchModel search : searchs) {
                for (SearchDataModel field : search.datas) {
                    if (!StringUtils.isEmpty(field.field) && !StringUtils.isEmpty(field.value))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * 搜索通用
     */
    public static JsonArray searchAll(String sql, String OrderBy, String ShowField) {
        return searchAll(sql, OrderBy, null, ShowField);
    }

    /**
     * 搜索通用
     */
    public static JsonArray searchAll(String sql, String OrderBy, List<DBParameter> ls, String ShowField) {
        try {
            if (sql.indexOf("distinct PrimaryKey") >= 0) sql = sql.replace("distinct PrimaryKey", ShowField);
            if (!StringUtils.isEmpty(OrderBy)) {
                if (OrderBy.toLowerCase().indexOf("order by") >= 0) sql += OrderBy;
                else sql += " order by " + OrderBy;
            }

            JsonArray dt;
            if (ls != null) {
                dt = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
            } else {
                dt = DBFunction.executeJsonArray(sql);
            }
            return dt;
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return null;
        }
    }

    /**
     * 搜索通用
     */
    public static JsonArray searchAll(String sql, List<DBParameter> ls) {
        try {
            JsonArray dt;
            if (ls != null) {
                dt = DBFunction.executeJsonArray(sql, DBParameter.getParameter(ls));
            } else {
                dt = DBFunction.executeJsonArray(sql);
            }
            return dt;
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return null;
        }
    }

    /**
     * 下拉列表添加关键字查询
     */
    public static String addWhereName(List<DBParameter> ls, String queryField, String queryText) {
        String sql_where = "";
        if (!StringUtils.isEmpty(queryText) && !StringUtils.isEmpty(queryField)) {
            String[] strs = queryField.split(",");
            if (strs.length == 1) {
                sql_where += StringUtils.format(" and {0} like @{0}", strs[0]);
                ls.add(new DBParameter("@" + strs[0], "%" + queryText + "%"));
            } else if (strs.length > 1) {
                sql_where += StringUtils.format(" and ({0} like @{0}", strs[0]);
                ls.add(new DBParameter("@" + strs[0], "%" + queryText + "%"));
                for (int i = 1; i < strs.length; i++) {
                    sql_where += StringUtils.format(" or {0} like @{0}", strs[i]);
                    ls.add(new DBParameter("@" + strs[i], "%" + queryText + "%"));
                }
                sql_where += ")";
            }
        }
        return sql_where;
    }
}

