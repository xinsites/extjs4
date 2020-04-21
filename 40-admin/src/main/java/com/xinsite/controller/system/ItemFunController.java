package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.system.BLL_Menu_Fun;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.search.SearchUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-02
 * object name:系统管理->功能管理/常用功能
 */

@RestController
@RequestMapping(value = "system/fun")
public class ItemFunController extends BaseController {

    // [功能管理]信息查询列表
    @RequestMapping(value = "grid")
    @RequiresPermissions("system:item:fun")
    public String grid(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String ids = getParaValue(request, "ids", "");
        int isdel = getParaValue(request, "isdel", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.item_id=" + item_id;
            if (isdel == 0) pager.where += " and a1.isdel=0";  //默认没有删除的
            if (!StringUtils.isEmpty(ids)) {
                ids = StringUtils.joinAsFilter(ids);
                pager.where = StringUtils.format(" where a1.fun_id in({0})", ids);
            }

            pager.showColumns = "a1.fun_id,a1.item_id,a1.name,a1.itemid,a1.per_value,a1.serialcode,a1.isdel";
            pager.tables = "sys_menu_fun a1";

            JsonArray array = pager.getAllGrid("a1.serialcode");
            return retGrid.getGridJson(array);
        } catch (Exception ex) {
            LogError.write("功能信息列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // [功能管理]列表拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:item:fun")
    public String sort(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String sort_vals = getParaValue(request, "sort_vals", "");
            if (!StringUtils.isEmpty(sort_vals)) {
                BLL_Menu_Fun.saveGridSort("fun_id", sort_vals);
            }
            if (item_id > 0) {
                LogUtils.addOperateLog(item_id, "栏目功能排序", "成功");
            }
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("功能信息排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[功能管理]列表删除行
    @RequestMapping(value = "deleteing")
    @RequiresPermissions("system:item:fun")
    public String deleteing(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String fun_ids = getParaValue(request, "fun_ids", "0");
        boolean success = false;
        try {
            fun_ids = StringUtils.joinAsFilter(fun_ids);
            if (!StringUtils.isEmpty(fun_ids)) {
                success = BLL_Menu_Fun.deleteByIds(fun_ids);
            }

            if (success) {
                if (item_id > 0) {
                    ShiroUtils.clearCachedAuthorizationInfo("role", 0);
                    LogUtils.addOperateLog(item_id, "栏目功能删除", "成功");
                }
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("功能信息删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[功能管理]列表恢复删除行
    @RequestMapping(value = "restore")
    @RequiresPermissions("system:item:fun")
    public String restore(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        int fun_id = getParaValue(request, "fun_id", 0);
        boolean success = false;
        try {
            if (fun_id > 0) {
                success = BLL_Menu_Fun.restoreById(fun_id);
            }
            if (success) {
                if (item_id > 0) {
                    ShiroUtils.clearCachedAuthorizationInfo("role", 0);
                    LogUtils.addOperateLog(item_id, "栏目功能删除恢复", "成功");
                }
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("功能恢复删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[功能管理]列表永久删除
    @RequestMapping(value = "deleted")
    @RequiresPermissions("system:item:fun")
    public String deleted(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        int fun_id = getParaValue(request, "fun_id", 0);
        boolean success = false;
        try {
            if (fun_id > 0) {
                success = BLL_Menu_Fun.deleteById(fun_id);
            }
            if (success) {
                if (item_id > 0) LogUtils.addOperateLog(item_id, "栏目功能永久删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("功能永久删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[功能管理]新增、修改行存储
    @RequestMapping(value = "rowediting")
    @RequiresPermissions("system:item:fun")
    public String rowediting(HttpServletRequest request) {
        int fun_id = getParaValue(request, "fun_id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        String itemid = getParaValue(request, "itemid", "");
        String per_value = getParaValue(request, "per_value", "");
        try {
            Map ht = new HashMap();
            ht.put("item_id", item_id);
            ht.put("itemid", itemid);
            ht.put("per_value", per_value);
            ht.put("name", getParaValue(request, "name", ""));
            ht.put("serialcode", getParaValue(request, "serialcode", 0));
            if (BLL_Menu_Fun.isExistItemId(fun_id, item_id, itemid)) {
                return ret.getFailResult("该ItemId已经存在！");
            } else {
                boolean isAdd = (fun_id == 0);
                fun_id = BLL_Menu_Fun.saveInfo(ht, fun_id);
                if (fun_id > 0) {
                    if (item_id > 0){
                        ShiroUtils.clearCachedAuthorizationInfo("role", 0);
                        LogUtils.addOperateLog(item_id, "栏目功能" + (isAdd ? "新增" : "修改"), "成功");
                    }

                    if (BLL_Menu_Fun.isExistPerValue(fun_id, per_value)) {
                        ret.addMap("msg", "请注意，该控制器权限值已经存在！");
                    }
                    return ret.getSuccessResult(fun_id);
                }
            }
        } catch (Exception ex) {
            LogError.write("功能信息新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[栏目管理]栏目功能批量保存
    @RequestMapping(value = "batch/save")
    @RequiresPermissions("system:item:setup")
    public String batchSave(HttpServletRequest request) {
        String item_ids = getParaValue(request, "item_ids", "");
        String pre_permission = getParaValue(request, "pre_permission", "");
        String fun_ids = getParaValue(request, "fun_ids", "");

        boolean success = false;
        try {
            success = BLL_Menu_Fun.saveItemFunInfo(item_ids, pre_permission, fun_ids, "替换");
            if (success) {
                ShiroUtils.clearCachedAuthorizationInfo("role", 0);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("栏目功能保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 下拉树列表(全部数据)
    @RequestMapping(value = "treecombo")
    public String treecombo(HttpServletRequest request) {
        try {
            JsonArray dtALL = BLL_Menu_Fun.getTreeComboBox();
            String otherAttr = "checked:false";
            return JsonTree.getTreeJsonByPid(dtALL, "0", otherAttr, otherAttr);
        } catch (Exception ex) {
            LogError.write("功能信息下拉树", LogEnum.Error, ex.toString());
        }
        return "[]";
    }
}










