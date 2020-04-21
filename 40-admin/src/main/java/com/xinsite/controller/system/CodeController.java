package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.core.utils.build.EnumUtils;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.DataTypeEnum;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.ObjectUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.system.BLL_Code;
import com.xinsite.core.bll.system.BLL_CodeType;
import com.xinsite.core.bll.system.BLL_DataShow;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * create by zhangxiaxin
 * create time: 2019-08-24
 * 系统管理->字典管理->编码管理
 */

@RestController
@RequestMapping(value = "system/code")
public class CodeController extends BaseController {

    //region 编码管理
    // 获取编码表树形列表
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:code:tree")
    public String codeTree(HttpServletRequest request) {
        int codetype_id = getParaValue(request, "codetype_id", 0);
        int isshow = getParaValue(request, "isshow", 0);
        String text = getParaValue(request, "text", "");
        try {
            String sql_where = " and codetype_id=" + codetype_id;
            String data_key = BLL_CodeType.getDataKey(codetype_id);
            if (isshow == 1) {
                String not_ids = BLL_DataShow.getNotShowIds(DataTypeEnum.编码表.getValue(), data_key);
                if (!StringUtils.isEmpty(not_ids)) {
                    sql_where += String.format(" and a.id not in(%s)", not_ids); //只包含可见数据源
                }
            }

            List<DBParameter> ls = new ArrayList<>();
            if (!StringUtils.isEmpty(text)) {
                sql_where += " and text like @text";
                ls.add(new DBParameter("@text", "%" + text + "%"));
            }
            JsonArray dt = BLL_Code.getCodeTree(ls, sql_where);
            if (!StringUtils.isEmpty(text) && dt.size() > 0) { //有查询条件
                String ids = TreeUtils.getTreeTableAllPids(dt, "sys_code", "id", "pid");
                sql_where = StringUtils.format(" and a.id in({0})", ids);
                dt = BLL_Code.getCodeTree(ls, sql_where);
            }
            BLL_DataShow.setEnabled(dt, DataTypeEnum.编码表.getValue(), data_key);
            return JsonTree.getTreeJsonByPid(dt, "0");  //一次性加载完的json方法
        } catch (Exception ex) {
            LogError.write("编码表树形列表", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //编码编辑单元格存储
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:code:save")
    public String codeEditing(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        String data_key = getParaValue(request, "data_key", "");
        int item_id = getParaValue(request, "item_id", 0);

        try {
            if (StringUtils.isEmpty(data_key)) {
                return ret.getFailResult("该编码数据源未标识！");
            }
            if (id > 0 && !StringUtils.isEmpty(field)) {
                BLL_DataShow.save(DataTypeEnum.编码表.getValue(), data_key, id, field, value);
                LogUtils.addOperateLog(item_id, "单元格编辑", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("编码表单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //删除编码值
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:code:del")
    public String codeDel(HttpServletRequest request) {
        int code_id = getParaValue(request, "Id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String type_name = BLL_CodeType.getCodeTypeName(code_id);
            if (StringUtils.isNotEmpty(type_name)) {
                return ret.getFailResult(String.format("“%s”编码不可以删除，请标识不可选或者不显示！", type_name));
            }
            String sql = StringUtils.format("update sys_code set isdel=1 where id={0}", code_id);
            if (DBFunction.executeNonQuery(sql) > 0) {
                LogUtils.addOperateLog(item_id, "编码表删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("编码表删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //保存编码排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:code:sort")
    public String codeSort(HttpServletRequest request) {
        String sort_vals = getParaValue(request, "sort_vals", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (!StringUtils.isEmpty(sort_vals)) {
                String[] Items = sort_vals.split(";");
                List<Map> list = new ArrayList<>();
                for (String item : Items) {
                    String[] arr = item.split(":");
                    if (arr.length == 3) {
                        Map ht = new HashMap();
                        ht.put("id", NumberUtils.strToInt(arr[0]));
                        ht.put("pid", NumberUtils.strToInt(arr[1]));
                        ht.put("index", NumberUtils.strToInt(arr[2]));
                        list.add(ht);
                    }
                }
                if (BLL_Code.saveCodeSort(list)) {
                    LogUtils.addOperateLog(item_id, "编码表排序", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("编码表排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //获取信息
    @RequestMapping(value = "info")
    public String codeInfo(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        try {
            if (id > 0) {
                String sql = StringUtils.format("select * from sys_code where id={0}", id);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "code");
            }
        } catch (Exception ex) {
            LogError.write("编码表信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[编码管理]信息新增/修改
    @RequestMapping(value = "save")
    @RequiresPermissions("system:code:save")
    public String codeSave(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        int pid = getParaValue(request, "pid", 0);
        int codetype_id = getParaValue(request, "codetype_id", 0);
        int item_id = getParaValue(request, "item_id", 0);

        try {
            String data_key = BLL_CodeType.getDataKey(codetype_id);
            if (StringUtils.isEmpty(data_key)) {
                return ret.getFailResult("该编码类型源未标识！");
            }

            Map ht = new HashMap();
            ht.put("text", getParaValue(request, "text", ""));
            ht.put("value", getParaValue(request, "value", ""));
            ht.put("expanded", getParaValue(request, "expand", ""));
            ht.put("remark", getParaValue(request, "remark", ""));    //备注
            ht.put("enabled", getParaValue(request, "enabled", 0));
            ht.put("isshow", getParaValue(request, "isshow", 0));

            if (id == 0) {
                ht.put("pid", pid);
                ht.put("isdel", 0);
                ht.put("issys", 0);
                ht.put("codetype_id", codetype_id);
                ht.put("serialcode", BLL_Code.getSerialCode(pid));
            }
            id = BLL_Code.saveCode(ht, id, data_key);
            if (id > 0) {
                LogUtils.addOperateLog(item_id, "编码保存", "成功");
                return ret.getSuccessResult(id);
            } else {
                LogUtils.addOperateLog(item_id, "编码保存", "失败");
            }
        } catch (AppException ex) {
            return ret.clear().addMap("code", ex.getCode()).addMap("msg", ex.getError()).getFailResult();
        } catch (Exception ex) {
            LogError.write("编码表新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion

    //Enum代码生成
    @RequestMapping(value = "build/enums")
    public String buildEnums(HttpServletRequest request) {
        String file_name = getParaValue(request, "file_name", "");
        String explain = getParaValue(request, "explain", "");
        String store_datas = getParaValue(request, "store_datas", "");
        try {
            if (!StringUtils.isEmpty(file_name) && !StringUtils.isEmpty(store_datas)) {
                String buildDir = EnumUtils.generateEnums(file_name, explain, store_datas);

                file_name = String.format("%s.java", file_name);
                ret.clear().addMap("filepath", buildDir + file_name).addMap("filename", file_name);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("Enum代码生成", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //region 编码表查询
    // 获取编码值文本
    @RequestMapping(value = "codetext")
    public String codeText(HttpServletRequest request) {
        String ids = getParaValue(request, "ids", "0");
        String id_field = getParaValue(request, "id_field", "id");

        try {
            String texts = "";
            if (!StringUtils.isEmpty(ids)) {
                if (id_field.equals("id")) {
                    texts = BLL_Code.getComboBoxText(ids);
                } else {
                    String data_key = getParaValue(request, "data_key", "");
                    texts = BLL_Code.getComboBoxTextByVals(data_key, ids);
                }
                return ret.getSuccessResult("texts", texts);
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 下拉列表（全部数据）
    @RequestMapping(value = "codecombo")
    public String codeCombo(HttpServletRequest request) {
        int codetype_id = getParaValue(request, "codetype_id", 0);
        String data_key = getParaValue(request, "data_key", "");
        String add_key = getParaValue(request, "add_key", "");  //附加Key,特殊不可选设置
        String query = getParaValue(request, "query", "").trim();
        String disableds = getParaValue(request, "disableds", "").trim(); //不能选择的数据，页面传值
        String noshows = getParaValue(request, "noshows", "").trim();     //不能显示的数据，页面传值
        try {
            if (codetype_id > 0 && StringUtils.isEmpty(data_key))
                data_key = BLL_CodeType.getDataKey(codetype_id);
            else if (codetype_id == 0 && !StringUtils.isEmpty(data_key))
                codetype_id = BLL_CodeType.getCodeTypeId(data_key);
            else return retGrid.getFailResult();

            if (!StringUtils.isEmpty(add_key)) data_key += "." + add_key;
            JsonArray show = BLL_DataShow.getDataShow(DataTypeEnum.编码表.getValue(), data_key);
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and isdel=0 and codetype_id=" + codetype_id;
            String not_ids = BLL_DataShow.getNotShowIds(show);
            if (!StringUtils.isEmpty(noshows)) {
                if (!StringUtils.isEmpty(not_ids)) not_ids += "," + noshows;
                else not_ids = noshows;
            }
            if (!StringUtils.isEmpty(not_ids)) {
                pager.where += String.format(" and id not in(%s)", not_ids); //只包含可见数据源
            }
            if (!StringUtils.isEmpty(query)) {
                pager.where += " and text like @text";
                pager.addPara(new DBParameter("@text", "%" + query + "%"));
            }
            pager.showColumns = "value id,text name,'' disabled,id data_id";
            pager.tables = "sys_code";
            JsonArray array = pager.getAllGrid("serialcode");
            BLL_DataShow.setGridDisableds(show, disableds);
            BLL_DataShow.setDisabled(array, show);
            return retGrid.getGridJson(array, array.size(), "id,name,disabled");
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // 下拉树列表(逐层加载数据)
    @RequestMapping(value = "codetree")
    public String codeLayerTree(HttpServletRequest request) {
        int node = getParaValue(request, "node", 0);
        int codetype_id = getParaValue(request, "codetype_id", 0);
        String data_key = getParaValue(request, "data_key", "");
        String add_key = getParaValue(request, "add_key", "");  //附加Key,特殊不可选设置
        boolean isCheck = ObjectUtils.toBoolean(getParaValue(request, "isCheck", "false"));
        String query = getParaValue(request, "query", "").trim();
        String disableds = getParaValue(request, "disableds", "").trim(); //不能选择的数据，页面传值
        String noshows = getParaValue(request, "noshows", "").trim();     //不能显示的数据，页面传值
        try {
            if (codetype_id > 0 && StringUtils.isEmpty(data_key))
                data_key = BLL_CodeType.getDataKey(codetype_id);
            else if (codetype_id == 0 && !StringUtils.isEmpty(data_key))
                codetype_id = BLL_CodeType.getCodeTypeId(data_key);
            else return retGrid.getFailResult();

            if (!StringUtils.isEmpty(add_key)) data_key += "." + add_key;
            JsonArray show = BLL_DataShow.getDataShow(DataTypeEnum.编码表.getValue(), data_key);
            String sql_where = StringUtils.format(" and codetype_id={0} and Pid={1}", codetype_id, node);
            if (!StringUtils.isEmpty(query))
                sql_where = StringUtils.format(" and codetype_id={0}", codetype_id); //有查询条件变全部加载

            String not_ids = BLL_DataShow.getNotShowIds(show);
            if (!StringUtils.isEmpty(noshows)) {
                if (!StringUtils.isEmpty(not_ids)) not_ids += "," + noshows;
                else not_ids = noshows;
            }
            if (!StringUtils.isEmpty(not_ids)) {
                sql_where += String.format(" and b.id not in(%s)", not_ids); //只包含可见数据源
            }

            List<DBParameter> ls = new ArrayList<>();
            if (!StringUtils.isEmpty(query)) {
                sql_where += " and text like @text";
                ls.add(new DBParameter("@text", "%" + query + "%"));
            }
            JsonArray dt = BLL_Code.getTreeComboBox(node, ls, sql_where);
            if (!StringUtils.isEmpty(query) && dt.size() > 0) { //有查询条件
                String ids = TreeUtils.getTreeTableAllPids(dt, "sys_code", "id", "pid");
                sql_where = StringUtils.format(" and b.id in({0})", ids);
                dt = BLL_Code.getTreeComboBox(node, ls, sql_where);
            }
            BLL_DataShow.setGridDisableds(show, disableds);
            BLL_DataShow.setDisabled(dt, show);

            String otherAttr = "";
            if (isCheck) otherAttr = "checked:false";

            if (!StringUtils.isEmpty(query)) //全部加载
                return JsonTree.getTreeJsonByPid(dt, node + "", otherAttr, otherAttr);
            else
                return JsonTree.getTreeJson(dt, otherAttr);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // 下拉树列表(全部数据)
    @RequestMapping(value = "codealltree")
    public String codeAllTree(HttpServletRequest request) {
        int node = getParaValue(request, "node", 0);
        int codetype_id = getParaValue(request, "codetype_id", 0);
        String data_key = getParaValue(request, "data_key", "");
        String add_key = getParaValue(request, "add_key", "");  //附加Key,特殊不可选设置
        boolean isCheck = ObjectUtils.toBoolean(getParaValue(request, "isCheck", "false"));
        String query = getParaValue(request, "query", "").trim();
        String disableds = getParaValue(request, "disableds", "").trim(); //不能选择的数据，页面传值
        String noshows = getParaValue(request, "noshows", "").trim();     //不能显示的数据，页面传值
        try {
            if (codetype_id > 0 && StringUtils.isEmpty(data_key))
                data_key = BLL_CodeType.getDataKey(codetype_id);
            else if (codetype_id == 0 && !StringUtils.isEmpty(data_key))
                codetype_id = BLL_CodeType.getCodeTypeId(data_key);
            else return retGrid.getFailResult();

            if (!StringUtils.isEmpty(add_key)) data_key += "." + add_key;
            JsonArray show = BLL_DataShow.getDataShow(DataTypeEnum.编码表.getValue(), data_key);

            String sql_where = " and codetype_id=" + codetype_id;
            List<DBParameter> ls = new ArrayList<>();
            if (!StringUtils.isEmpty(query)) {
                sql_where += " and text like @text";
                ls.add(new DBParameter("@text", "%" + query + "%"));
            }
            String not_ids = BLL_DataShow.getNotShowIds(show);
            if (!StringUtils.isEmpty(noshows)) {
                if (!StringUtils.isEmpty(not_ids)) not_ids += "," + noshows;
                else not_ids = noshows;
            }
            if (!StringUtils.isEmpty(not_ids)) {
                sql_where += String.format(" and b.id not in(%s)", not_ids); //只包含可见数据源
            }
            JsonArray dt = BLL_Code.getTreeComboBox(ls, sql_where);
            if (!StringUtils.isEmpty(query) && dt.size() > 0) { //有查询条件
                String ids = TreeUtils.getTreeTableAllPids(dt, "sys_code", "id", "pid");
                sql_where = StringUtils.format(" and b.id in({0})", ids);
                dt = BLL_Code.getTreeComboBox(ls, sql_where);
            }
            BLL_DataShow.setGridDisableds(show, disableds);
            BLL_DataShow.setDisabled(dt, show);

            String otherAttr = "";
            if (isCheck) otherAttr = "checked:false";
            return JsonTree.getTreeJsonByPid(dt, node + "", otherAttr, otherAttr);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }
    //endregion
}



