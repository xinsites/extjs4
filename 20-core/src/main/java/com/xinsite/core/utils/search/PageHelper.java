package com.xinsite.core.utils.search;

import com.google.gson.JsonArray;
import com.xinsite.common.enums.system.ManTypeEnum;
import com.xinsite.common.enums.system.MemberEnum;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.enums.FieldTagEnum;
import com.xinsite.core.model.search.SearchDataModel;
import com.xinsite.core.model.search.SearchModel;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.common.enums.ErrorEnum;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.DataPerEnum;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.common.uitls.web.RequestUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.bll.system.*;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.dal.bean.Keys;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.dal.dbhelper.DBPager;
import com.xinsite.core.utils.log.LogError;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 页面查询(分页及全查)
 * create by zhangxiaxin
 */
public class PageHelper {
    public String querySql;          //查询语句，计算拼接，特殊情况自定义
    public String countSql;          //总记录数语句，计算拼接，特殊情况自定义
    public String showColumns;       //显示列表，querySql未赋值，此值必需设置
    public String tables;            //查询表，querySql未赋值，此值必需设置
    public String where;             //默认查询条件，根据情况设置
    public List<DBParameter> ls;     //查询条件，参数设置，根据情况使用
    public String orderBy;           //排序字段，页面传值或者根据情况设置
    public List<SearchModel> searchs; //页面高级查询条件，页面传值
    public String linkType;          //高级查询拼接方式，and、or
    public String searchsWhere;      //高级查询条件，拼接的where
    public int pageStart;            //起始页从0开始，页面传值
    public int pageSize;             //每页大小，页面传值
    public int itemId;               //栏目Id，sys_menu(item_id)
    public boolean isDataPer = true; //列表数据权限开关，默认打开，特殊情况自定义
    public String dataPerWhere;      //数据权限的where条件，栏目有数据权限的
    public String primaryKey;        //主表主键字段
    public HttpServletRequest request;

    public long recordCount;        //查询记录总数
    public JsonArray array = null;  //查询结果

    public PageHelper() {
        this.where = StringUtils.EMPTY;
        this.querySql = StringUtils.EMPTY;
        this.countSql = StringUtils.EMPTY;
    }

    /**
     * 该查询模板是否有效
     */
    public boolean isValid() {
        return !StringUtils.isEmpty(showColumns) && !StringUtils.isEmpty(tables);
    }

    /**
     * 该查询是否有记录
     */
    public boolean isRecord() {
        return array != null && array.size() > 0;
    }

    /**
     * 树形列表查询，是否需要根据结果集再查一次
     * 树形列表带条件查询时，需要将结果集的父目录带上
     */
    public boolean isReSearch() {
        return !SearchUtils.isEmptySearchs(searchs) && isRecord();
    }

    /**
     * 树形列表查询，是否逐级加载
     * 树形列表不带条件查询时，并指定否逐级加载
     */
    public boolean isLevelLoad(boolean level_load) {
        return SearchUtils.isEmptySearchs(searchs) && level_load;
    }

    /**
     * 添加DBParameter
     */
    public void addPara(String key, Object value) {
        if (ls == null) ls = new ArrayList<DBParameter>();
        ls.add(new DBParameter(key, value));
    }

    /**
     * 添加DBParameter
     */
    public void addPara(DBParameter parameter) {
        if (ls == null) ls = new ArrayList<DBParameter>();
        ls.add(parameter);
    }

    /**
     * 清空查询条件，另附where条件，一般用在树查询(找父结点)
     */
    public PageHelper clear() {
        if (ls != null) ls.clear();
        if (searchs != null) searchs.clear();
        querySql = StringUtils.EMPTY;
        countSql = StringUtils.EMPTY;
        where = StringUtils.EMPTY;        //默认的条件
        searchsWhere = StringUtils.EMPTY; //searchs拼接的条件
        dataPerWhere = StringUtils.EMPTY; //dataPer拼接的条件
        return this;
    }

    public DBParameter[] getParameter() {
        if (ls != null) {
            DBParameter[] parameters = new DBParameter[ls.size()];
            return ls.toArray(parameters);
        } else {
            return new DBParameter[0];
        }
    }

    /**
     * 搜索分页查找
     */
    public void loadPageGrid(String... sorter) throws Exception {
        joinQuerySql();
        String sql = querySql + addDataPerWhere();

        if (StringUtils.isEmpty(orderBy)) {
            if (sorter.length > 0) {
                orderBy = sorter[0];
            }
            if (StringUtils.isEmpty(orderBy)) {
                orderBy = "a1.serialcode desc,a1.create_time desc";
            }
        }
        DBPager pagerUtils = new DBPager(sql, orderBy, pageStart, pageSize);
        try {
            pagerUtils.setParams(ls);
            recordCount = getAllCount();
            array = pagerUtils.getCurPageArray();
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
    }

    /**
     * 搜索查询全部
     */
    public JsonArray getAllGrid(String... sorter) {
        try {
            joinQuerySql();
            String sql = querySql + addDataPerWhere();
            if (StringUtils.isEmpty(orderBy)) {
                if (sorter.length > 0) {
                    orderBy = sorter[0];
                } else if ("".equals(orderBy)) {
                    orderBy = "a1.serialcode desc,a1.create_time desc";
                }
            }

            if (sql.toLowerCase().indexOf("order by") == -1) {
                if (!StringUtils.isEmpty(orderBy)) {
                    if (orderBy.toLowerCase().indexOf("order by") >= 0) sql += orderBy;
                    else sql += " order by " + orderBy;
                }
            }
            if (!StringUtils.isEmpty(sql)) {
                array = DBFunction.executeJsonArray(sql, getParameter());
                recordCount = array.size();
                return array;
            }
            recordCount = 0;
            return new JsonArray();
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return null;
        }
    }

    /**
     * 通用记录总数
     */
    public long getAllCount() {
        try {
            if (StringUtils.isEmpty(countSql)) {
                joinQuerySql();
            }
            if (StringUtils.isEmpty(countSql)) {
                throw new AppException("查询出错，countSql未定义：");
            }
            String sql = countSql + addDataPerWhere();
            String str = DBFunction.getSqlCount(sql, getParameter());
            return NumberUtils.strToLong(str);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return 0;
        }
    }

    /**
     * 查询列表加用户或者部门名称
     */
    public void addTextUserOrDept() throws Exception {
        if (!StringUtils.isEmpty(showColumns) && isRecord()) {
            for (String str : showColumns.split(",")) {
                String fieldname = str.toLowerCase();
                if (fieldname.indexOf("create_uid") >= 0) {
                    BLL_User.setGridUserNames(array, "create_uid");
                } else if (fieldname.indexOf("modify_uid") >= 0) {
                    BLL_User.setGridUserNames(array, "modify_uid");
                } else if (fieldname.indexOf("dept_id") >= 0) {
                    BLL_Dept.setGridDeptText(array, "dept_id");
                }
            }
        }
    }

    /**
     * Excel导出最大记录检查
     */
    public void validExcelMaxCount() throws AppException {
        int max_count = SysConfigCache.getExcelMaxCount();
        if (getAllCount() > max_count)
            throw new AppException(ErrorEnum.导出超出.getCode(), "导出超出最大记录：" + max_count);
    }

    /**
     * 计算拼接查询语句
     */
    private void joinQuerySql() throws AppException {
        if (StringUtils.isEmpty(where)) where = StringUtils.EMPTY;
        if (StringUtils.isEmpty(querySql)) {
            if (StringUtils.isEmpty(tables)) {
                throw new AppException("查询出错，tables未定义：");
            }
            querySql = StringUtils.format("select distinct PrimaryKey from {0} where 1=1 {1}", tables, where);
            countSql = StringUtils.format("select count(1) from {0} where 1=1 {1}", tables, where);
        } else if (!StringUtils.isEmpty(where)) {
            if (querySql.indexOf(where) == -1) {  //防止再次调用赋值两遍，这里有误差
                querySql += where;
            }
        }
        if (StringUtils.isEmpty(querySql)) {
            throw new AppException("查询出错，querySql未定义：");
        }
        if (StringUtils.isEmpty(countSql)) {
            if (querySql.indexOf("select distinct PrimaryKey") >= 0) {
                countSql = querySql.replace("select distinct PrimaryKey", "select count(1)");
            }
        }
        if (querySql.indexOf("distinct PrimaryKey") >= 0) {
            if (StringUtils.isEmpty(showColumns)) {
                throw new AppException("查询出错，showColumns未定义：");
            }
            querySql = querySql.replace("distinct PrimaryKey", showColumns);
        }
        joinSearchsWhere();
        if (!StringUtils.isEmpty(searchsWhere)) {
            if (querySql.indexOf(searchsWhere) == -1) {  //防止再次调用赋值两遍，这里有误差
                if (linkType.equalsIgnoreCase("and")) {
                    querySql += searchsWhere;
                    if (StringUtils.isNotEmpty(countSql))
                        countSql += searchsWhere;
                } else {
                    querySql += StringUtils.format(" and (1=0{0})", searchsWhere);
                    if (StringUtils.isNotEmpty(countSql))
                        countSql += StringUtils.format(" and (1=0{0})", searchsWhere);
                }
            }
        }
    }

    /**
     * 计算数据权限形成的where条件
     */
    public String addDataPerWhere(Keys... keys) throws Exception {
        if (!isDataPer) return StringUtils.EMPTY;
        if (dataPerWhere == null && itemId > 0) { //第一次计算
            if (request == null) request = ServletUtils.getRequest();
            LoginUser loginUser = UserUtils.getLoginUser();
            if (loginUser.isSuperAdminer()) return StringUtils.EMPTY;  //超级管理员不需要加数据权限

            boolean load_all = false; //未赋数据权限是否加载全部数据，默认否
            String alias = "a1";
            String field_dept = "dept_id";
            String field_user = "create_uid";
            String org_id = "org_id";
            for (Keys k : keys) {
                if (k.key.equalsIgnoreCase("alias")) alias = k.getValue();
                if (k.key.equalsIgnoreCase("org_id")) org_id = k.getValue();
                else if (k.key.equalsIgnoreCase("field_dept") && !StringUtils.isEmpty(k.getValue()))
                    field_dept = k.getValue();
                else if (k.key.equalsIgnoreCase("field_user") && !StringUtils.isEmpty(k.getValue()))
                    field_user = k.getValue();
                else if (k.key.equalsIgnoreCase("load_all") && k.getValue().equalsIgnoreCase("true"))
                    load_all = true;
            }

            int is_data_per = RequestUtils.getParaValue(request, "is_data_per", 0);
            if (is_data_per != 1) {
                is_data_per = BLL_Menu.getDataPermissionByItemId(itemId);
            }
            if (is_data_per == 1) { //栏目有数据权限
                dataPerWhere = StringUtils.EMPTY;
                int data_per = RequestUtils.getParaValue(request, "data_per", -1);  //-1是首次加载数据列表，点击栏目时数据权限还没赋值
                String data_ids = RequestUtils.getParaValue(request, "data_ids", "");
                if (data_per == -1) {  //从数据库获取
                    JsonArray dt = BLL_UserPower.getLoginItemPower(itemId, loginUser); //用户某栏目权限信息
                    if (dt != null && dt.size() > 0) {
                        data_per = NumberUtils.strToInt(GsonUtils.getObjectValue(dt, 0, "data_per"));
                        data_ids = GsonUtils.getObjectValue(dt, 0, "data_ids");
                    }
                }
                if (CommUtils.enumDataPerContains(data_per)) {
                    if (!StringUtils.isEmpty(alias)) {
                        org_id = alias + "." + org_id;
                        field_dept = alias + "." + field_dept;
                        field_user = alias + "." + field_user;
                    }
                    DataPerEnum dataPer = DataPerEnum.values()[data_per];
                    switch (dataPer) {
                        case 未设置:
                            if (!load_all) {  //只加载本人数据
                                dataPerWhere += StringUtils.format(" and {0}={1}", field_user, loginUser.getUserId());
                            }
                            break;
                        case 所在公司:
                            if (!StringUtils.isEmpty(org_id))
                                dataPerWhere += StringUtils.format(" and {0}={1}", org_id, loginUser.getOrgId());
                            break;
                        case 仅限本人:
                            dataPerWhere += StringUtils.format(" and {0}={1}", field_user, loginUser.getUserId());
                            break;
                        case 仅限本人及下属:
                            List<Integer> list = BLL_Member.getMemberUserIds(MemberEnum.用户领导.getValue(), loginUser.getUserId(), ManTypeEnum.成员.getIndex());
                            list.add(UserUtils.getUserId());
                            dataPerWhere += String.format(" and %s in(%s)", field_user, StringUtils.join(list, ","));
                            break;
                        case 所在部门:
                            dataPerWhere += StringUtils.format(" and ({0}={1} or {2}={3})", field_dept, loginUser.getDeptId(), field_user, loginUser.getUserId());
                            break;
                        case 自定义部门:
                            if (StringUtils.isEmpty(data_ids)) data_ids = "0";
                            dataPerWhere += StringUtils.format(" and ({0} in({1}) or {2}={3})", field_dept, data_ids, field_user, loginUser.getUserId());
                            break;
                        case 自定义用户:
                            if (StringUtils.isEmpty(data_ids)) data_ids = "0";
                            dataPerWhere += StringUtils.format(" and ({0} in({1}) or {2}={3})", field_user, data_ids, field_user, loginUser.getUserId());
                            break;
                    }
                }

                if (StringUtils.isEmpty(dataPerWhere) && !load_all) {  //只加载本人数据
                    dataPerWhere = StringUtils.EMPTY;
                    dataPerWhere += StringUtils.format(" and {0}={1}", field_user, loginUser.getUserId());
                }
            }
        }
        if (StringUtils.isEmpty(dataPerWhere)) {
            dataPerWhere = StringUtils.EMPTY;
        }

        return dataPerWhere;
    }

    /**
     * 计算高级查询searchs形成的where条件
     */
    public String joinSearchsWhere() {
        if (searchsWhere == null) { //第一次计算
            if (searchs != null && searchs.size() > 0) {
                Hashtable ht = getTablesBySearchs();
                if (ht.size() == 0) ht = getTablesByTables();
                StringBuilder list_where = new StringBuilder();
                if (ht.size() > 0) {
                    String alias_name = "a1";
                    for (SearchModel search : searchs) {
                        if (StringUtils.isEmpty(search.tableName)) continue;
                        search.tableName = search.tableName.toLowerCase();

                        if (ht.containsKey(search.tableName)) {
                            alias_name = ht.get(search.tableName).toString();
                        } else {
                            alias_name = "grid" + (searchs.indexOf(search) + 1);
                        }
                        alias_name += ".";

                        StringBuilder condition = new StringBuilder();
                        for (SearchDataModel field : search.datas) {
                            if (StringUtils.isEmpty(field.field)) continue;
                            if (StringUtils.isEmpty(field.operator)) continue;

                            //region 一个对象的所有条件
                            switch (field.operator) {
                                case "=":
                                case "!=":
                                case ">":
                                case ">=":
                                case "<":
                                case "<=":
                                    if (!StringUtils.isEmpty(field.value)) {
                                        condition.append(StringUtils.format(" {0} {3}{2}{1}@{2}", linkType, field.operator, field.field, alias_name));
                                        addPara(new DBParameter(field.field, field.operator, field.value));
                                    }
                                    break;
                                case "like":
                                    switch (field.fieldType) //textfield
                                    {
                                        case "textfield":
                                        case "textareafield":
                                        case "htmleditor":
                                        case "ueditor":
                                        case "kindeditor":
                                        case "datefield":
                                        case "datetimefield":
                                        case "timefield":
                                        case "trigger":
                                        case "panelpicker":
                                            if (!StringUtils.isEmpty(field.value)) {
                                                String[] strs = field.field.split(",");
                                                if (strs.length > 1) {
                                                    condition.append(" " + linkType + " ");
                                                    for (int index = 0; index < strs.length; index++) {
                                                        addPara(new DBParameter(strs[index], field.operator, "%" + field.value + "%"));
                                                        if (index == 0)
                                                            condition.append("(" + StringUtils.format(" {0}{1} like @{2}", alias_name, strs[index], strs[index]));
                                                        else
                                                            condition.append(" or " + StringUtils.format(" {0}{1} like @{2}", alias_name, strs[index], strs[index]));
                                                    }
                                                    condition.append(")");
                                                } else if (strs.length == 1) {
                                                    condition.append(StringUtils.format(" {0} {2}{1} like @{1}", linkType, field.field, alias_name));
                                                    addPara(new DBParameter(field.field, field.operator, "%" + field.value + "%"));
                                                }
                                            }
                                            break;
                                        case "checkboxgroup":    //复选组选择框，整型数组,分隔
                                        case "multitreepicker":  //下拉树选择框，整型数组,分隔
                                        case "multicombobox":    //下拉多选框，整型、字符型数组,分隔
                                            if (!StringUtils.isEmpty(field.value)) {
                                                String[] strs = field.value.split(",");
                                                //String invalue = "";
                                                if (strs.length > 0) {
                                                    condition.append(" " + linkType + " ");
                                                    for (int index = 0; index < strs.length; index++) {
                                                        addPara(new DBParameter(field.field + index, "=", strs[index]));
                                                        if (index == 0)
                                                            condition.append("(" + StringUtils.format("find_in_set(@{2},{0}{1})", alias_name, field.field, field.field + index));
                                                        else
                                                            condition.append(" and " + StringUtils.format(" find_in_set(@{2},{0}{1})", alias_name, field.field, field.field + index));
                                                    }
                                                    condition.append(")");
                                                }
                                            }
                                            break;
                                    }
                                    break;
                                case "left_like":
                                    switch (field.fieldType) //textfield
                                    {
                                        case "textfield":
                                        case "textareafield":
                                        case "htmleditor":
                                        case "ueditor":
                                        case "kindeditor":
                                        case "datefield":
                                        case "datetimefield":
                                        case "timefield":
                                        case "trigger":
                                        case "panelpicker":
                                            condition.append(StringUtils.format(" {0} {2}{1} like @{1}", linkType, field.field, alias_name));
                                            addPara(new DBParameter(field.field, "like", "%" + field.value));
                                            break;
                                    }
                                    break;
                                case "right_like":
                                    switch (field.fieldType) //textfield
                                    {
                                        case "textfield":
                                        case "textareafield":
                                        case "htmleditor":
                                        case "ueditor":
                                        case "kindeditor":
                                        case "datefield":
                                        case "datetimefield":
                                        case "timefield":
                                        case "trigger":
                                        case "panelpicker":
                                            condition.append(StringUtils.format(" {0} {2}{1} like @{1}", linkType, field.field, alias_name));
                                            addPara(new DBParameter(field.field, "like", field.value + "%"));
                                            break;
                                    }
                                    break;
                                case "between":
                                    if (!StringUtils.isEmpty(field.value) && !StringUtils.isEmpty(field.value2)) {
                                        addPara(new DBParameter(field.field + "_1", field.operator, field.value));
                                        switch (field.fieldType) //textfield
                                        {
                                            case "datefield":
                                            case "datetimefield":
                                                addPara(new DBParameter(field.field + "_2", field.operator, field.value2 + " 23:59"));
                                                break;
                                            default:
                                                addPara(new DBParameter(field.field + "_2", field.operator, field.value2));
                                                break;
                                        }
                                        condition.append(StringUtils.format(" {0} {1} between @{2} and @{3}", linkType, alias_name + field.field, "" + field.field + "_1", "" + field.field + "_2"));
                                    } else if (!StringUtils.isEmpty(field.value)) {
                                        condition.append(StringUtils.format(" {0} {3}{2}{1}@{2}", linkType, ">=", field.field, alias_name));
                                        addPara(new DBParameter(field.field, field.operator, field.value));
                                    } else if (!StringUtils.isEmpty(field.value2)) {
                                        condition.append(StringUtils.format(" {0} {3}{2}{1}@{2}", linkType, "<=", field.field, alias_name));
                                        addPara(new DBParameter(field.field, field.operator, field.value2));
                                    }
                                    break;
                                case "is_null":
                                    condition.append(StringUtils.format(" {0} ({1} is null or length({1})=0)", linkType, alias_name + field.field));
                                    break;
                                case "is_not_null":
                                    condition.append(StringUtils.format(" {0} ({1} is not null or length({1})>0)", linkType, alias_name + field.field));
                                    break;
                                case "late":  //按时间，最近几天
                                    condition.append(StringUtils.format(" and datediff(now(), {1}create_time)<={0}", NumberUtils.strToInt(field.value), alias_name));
                                    break;
                            }
                            //endregion
                        }

                        if (ht.containsKey(search.tableName)) {
                            list_where.append(condition.toString());
                        } else if (condition.toString().length() > 0) {
                            if (StringUtils.isEmpty(primaryKey)) {
                                primaryKey = getPrimaryKey(ht);
                            }
                            if (StringUtils.isNotEmpty(primaryKey)) {
                                alias_name = alias_name.replace(".", "");
                                String str = " and exists(select 1 from {0} {1} where a1.primaryKey={1}.primaryKey {2}";
                                if (linkType.equalsIgnoreCase("or")) {
                                    str = " or exists(select 1 from {0} {1} where a1.primaryKey={1}.primaryKey and (1=0{2})";
                                }
                                str = StringUtils.replaceVal(str, "primaryKey", primaryKey);
                                list_where.append(StringUtils.format(str, search.tableName, alias_name, condition.toString()));
                                if (itemId > 0 && search.tableName.equalsIgnoreCase("tb_object_att")) {
                                    list_where.append(StringUtils.format(" and {0}.item_id={1}", alias_name, itemId));
                                }
                                list_where.append(")");
                            }
                        }
                    }
                }
                searchsWhere = list_where.toString();
            }
        }

        if (StringUtils.isEmpty(searchsWhere)) searchsWhere = StringUtils.EMPTY;
        return searchsWhere;
    }

    /**
     * 根据searchs获取主表主键字段
     */
    private String getPrimaryKey(Map<String, Object> ht) {
        String primaryKey = "";
        if (ht != null && ht.size() > 0) {
            for (String key : ht.keySet()) {
                String alias_name = ht.get(key).toString();
                if ("a1".equals(alias_name)) {
                    try {
                        JsonArray array = BLL_Design.getTableFields(key);
                        primaryKey = BLL_Design.getFieldName(array, FieldTagEnum.主键.getValue());
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return primaryKey;
    }

    /**
     * 根据searchs获取关联表的所有表
     */
    private Hashtable getTablesBySearchs() {
        Hashtable ht = new Hashtable();
        for (SearchModel search : searchs) {
            if (StringUtils.isEmpty(search.tableName)) continue;
            if (StringUtils.isEmpty(search.alias)) continue;
            String table_name = search.tableName;
            if (!ht.containsKey(table_name)) ht.put(table_name, search.alias);
        }
        return ht;
    }

    /**
     * 根据查询的tables获取关联表的所有表(计算有误差)
     */
    private Hashtable getTablesByTables() {
        Hashtable ht = new Hashtable();
        String tables = this.tables;
        if (StringUtils.isEmpty(tables) && StringUtils.isNotEmpty(querySql)) {
            tables = querySql.substring(querySql.indexOf("from"));
            tables = tables.substring(0, tables.indexOf("where"));
            tables = tables.replace("from", "").trim();
        }
        if (!StringUtils.isEmpty(tables)) {
            tables = tables.replaceAll("\n", " ");
            tables = tables.toLowerCase().trim();
            String[] strs = tables.split(",");
            for (String str : strs) {
                String[] temps = str.trim().split(" ");
                for (int i = 0; i < temps.length; i++) {
                    String table_name = temps[i].trim();
                    if (StringUtils.isEmpty(table_name)) continue;
                    if (table_name.indexOf("=") >= 0
                            || table_name.indexOf(">") >= 0
                            || table_name.indexOf("<") >= 0) continue;

                    if (table_name.equalsIgnoreCase("left")
                            || table_name.equalsIgnoreCase("join")
                            || table_name.equalsIgnoreCase("on")
                            || table_name.equalsIgnoreCase("and")
                            || table_name.equalsIgnoreCase("or"))
                        continue;

                    String alias_name = "";
                    for (int j = i + 1; j < temps.length; j++) {
                        i = j;
                        alias_name = temps[j].trim();
                        if (alias_name.equalsIgnoreCase("left") || alias_name.equalsIgnoreCase("on") || alias_name.equalsIgnoreCase(",")) {
                            alias_name = temps[j - 1];
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(alias_name)) alias_name = table_name;

                    if (table_name.indexOf("_") > 0) {
                        if (!ht.containsKey(table_name)) ht.put(table_name, alias_name);
                    } else if (table_name.length() > 2) {
                        if (!ht.containsKey(table_name)) ht.put(table_name, alias_name);
                    }
                }
            }
        }
        return ht;
    }
}
