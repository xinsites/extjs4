package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.ObjectUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.bll.info.BLL_UploadFile;
import com.xinsite.core.bll.system.BLL_Menu;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-02
 * object name:系统管理->栏目管理
 */

@RestController
@RequestMapping(value = "system/item")
public class ItemController extends BaseController {

    //[栏目管理]信息查询列表
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:menu:tree")
    public String tree(HttpServletRequest request) {
        int node = getParaValue(request, "node", 0);
        try {
            List<DBParameter> ls = new ArrayList<>();
            PageHelper pager = SearchUtils.getPageHelper(request);
            String sql_where = BLL_Menu.getItemSearchWhere(pager.searchs, ls);
            if (StringUtils.isEmpty(sql_where)) {
                sql_where = " and a.isused=1 and a.pid=" + node;
                sql_where += StringUtils.format(" and (a.org_id=0 or a.org_id={0})", UserUtils.getOrgId());
                JsonArray array = BLL_Menu.getItemTree(sql_where, ls, true);
                return JsonTree.getTreeJson(array, "checked:false");
            } else {
                sql_where += StringUtils.format(" and (a.org_id=0 or a.org_id={0})", UserUtils.getOrgId());
                JsonArray array = BLL_Menu.getItemTree(sql_where, ls, false);
                if (array != null && array.size() > 0) {
                    ls.clear();
                    String ids = TreeUtils.getTreeTableAllPids(array, "id", "pid", "sys_menu", "item_id", "pid");
                    sql_where = StringUtils.format("and a.item_id in({0})", ids);
                    array = BLL_Menu.getItemTree(sql_where, ls, false);
                }
                return JsonTree.getTreeJsonByPid(array, "0", "checked:false", "checked:false");
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //[栏目管理]列表拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:item:sort")
    public String sort(HttpServletRequest request) {
        String sort_vals = getParaValue(request, "sort_vals", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (!StringUtils.isEmpty(sort_vals)) {
                String[] Items = StringUtils.split(sort_vals, ";");
                List<Map> list = new ArrayList<>();
                for (String item : Items) {
                    String[] arr = StringUtils.split(item, ":");
                    if (arr.length == 3) {
                        Map ht = new HashMap();
                        ht.put("id", NumberUtils.strToInt(arr[0]));
                        ht.put("pid", NumberUtils.strToInt(arr[1]));
                        ht.put("index", NumberUtils.strToInt(arr[2]));
                        list.add(ht);
                    }
                }
                if (list.size() > 0 && BLL_Menu.saveColumnsSort(list)) {
                    LogUtils.addOperateLog(item_id, "栏目排序", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //栏目编辑单元格存储
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:item:save")
    public String editing(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (Id > 0 && !StringUtils.isEmpty(field)) {
                if (field.equalsIgnoreCase("text")) field = "item_name";
                Map ht = new HashMap();
                ht.put(field, value);
                DBFunction.updateByTbName(ht, "sys_menu", "item_id=" + Id);
                LogUtils.addOperateLog(item_id, "栏目单元格编辑", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("栏目单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[栏目管理]列表删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:item:del")
    public String delete(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        boolean success = false;
        try {
            success = BLL_Menu.deleteByIds("item_id", Id);
            if (success) {
                LogUtils.addOperateLog(item_id, "栏目删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("栏目删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }


    // [栏目管理]获取信息
    @RequestMapping(value = "info")
    public String info(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (item_id > 0) {
                String sql = StringUtils.format("select *,item_id id,pid,item_name text,expanded expand from sys_menu where item_id={0}", item_id);

                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "item");
            }
        } catch (Exception ex) {
            LogError.write("栏目获取信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[栏目管理]信息新增/修改
    @RequestMapping(value = "save")
    @RequiresPermissions("system:item:save")
    public String save(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        int pid = getParaValue(request, "pid", 0);
        String per_value = getParaValue(request, "per_value", "null");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("item_name", getParaValue(request, "text", ""));                    //栏目名称
            ht.put("item_method", getParaValue(request, "item_method", ""));    //执行方法
            ht.put("item_type", getParaValue(request, "item_type", "method"));
            ht.put("open_type", getParaValue(request, "open_type", "null"));
            ht.put("per_value", per_value);
            ht.put("iconcls", getParaValue(request, "iconcls", ""));    //栏目图标
            ht.put("expanded", getParaValue(request, "expand", ""));    //是否展开
            ht.put("isused", getParaValue(request, "isused", 0));
            ht.put("ishistory", getParaValue(request, "ishistory", 0));
            ht.put("isdataper", getParaValue(request, "isdataper", 0));
            ht.put("isrecycle", getParaValue(request, "isrecycle", 0));
            ht.put("isfun", getParaValue(request, "isfun", 0));
            if (id == 0) {
                ht.put("pid", pid);
                ht.put("isdel", 0);
                ht.put("create_time", DateUtils.getDateTime());
                ht.put("item_sort", BLL_Menu.getSerialCode(pid));
            }
            boolean isAdd = (id == 0);

            String re_per_value = BLL_Menu.getFieldByMenu(id, "per_value", "");
            id = BLL_Menu.saveItemTableInfo(ht, id);
            if (id > 0) {
                if (!isAdd) {
                    per_value = getParaValue(request, "per_value", "");
                    if (!per_value.equals(re_per_value)) {
                        ShiroUtils.clearCachedAuthorizationInfo("role", 0);
                    }
                }
                LogUtils.addOperateLog(item_id, "栏目" + (isAdd ? "新增" : "修改"), "成功");
                if (BLL_Menu.isExistPerValue(id, per_value)) {
                    ret.addMap("msg", "请注意，该列表权限值已经存在！");
                }
                return ret.getSuccessResult(id);
            } else {
                LogUtils.addOperateLog(item_id, "栏目" + (isAdd ? "新增" : "修改"), "失败");
            }
        } catch (Exception ex) {
            LogError.write("栏目信息新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 根据栏目Id获取所有流程列表栏目Id
    @RequestMapping(value = "flowitem")
    public String flowItem(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String value = BLL_Design.getFlowGridItemIds(item_id);
            return ret.getSuccessResult("value", value);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 按条件获取栏目选择树
    @RequestMapping(value = "where/tree")
    public String getWhereItemTree(HttpServletRequest request) {
        String where = getParaValue(request, "where", "").toLowerCase().trim();
        if (!StringUtils.isEmpty(where) && where.indexOf("and") == -1) where = " and " + where;
        boolean isCheck = ObjectUtils.toBoolean(getParaValue(request, "isCheck", "false"));
        try {
            where = StringUtils.sqlFilter(where);
            JsonArray dtALL = BLL_Menu.getGridTreeItem(where);
            if (isCheck) {
                return JsonTree.getTreeJson(dtALL, "checked:false");
            } else {
                return JsonTree.getTreeJson(dtALL, "");
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //获取栏目允许上传的附件类型
    @RequestMapping(value = "filetype/info")
    public String itemFileType(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (item_id > 0) {
                ret.clear().addMap("uploadfile_type", BLL_UploadFile.getInfo(item_id));
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("获取上传的附件类型", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //保存栏目允许上传的附件类型
    @RequestMapping(value = "filetype/save")
    @RequiresPermissions("system:item:attach")
    public String uploadfileType(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String item_ids = getParaValue(request, "item_ids", "");
        String attach_type = getParaValue(request, "attach_type", "");
        try {
            if (BLL_UploadFile.saveInfo(item_ids, attach_type)) {
                LogUtils.addOperateLog(item_id, "保存栏目上传的附件类型", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("栏目上传的附件类型", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
}









