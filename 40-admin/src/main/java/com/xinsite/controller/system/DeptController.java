package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.annotation.NoRepeatSubmit;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.ManTypeEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.BLL_Common;
import com.xinsite.core.bll.system.BLL_Dept;
import com.xinsite.core.bll.system.BLL_Member;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.core.utils.search.SearchUtils;
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
 * object name:系统管理->部门管理
 */

@RestController
@RequestMapping(value = "system/dept")
public class DeptController extends BaseController {

    // [部门管理]信息查询列表
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:dept:tree")
    public String tree(HttpServletRequest request) {
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.isdel=0 and a1.org_id=" + UserUtils.getOrgId();
            pager.showColumns = "a1.dept_id id,a1.pid,a1.dept_name text,a1.dept_code,a1.dept_type,a1.dept_phone,a1.serialcode,a1.dept_remark";
            pager.tables = "sys_dept a1";

            if (!SearchUtils.isEmptySearchs(pager.searchs)) pager.showColumns = "a1.dept_id,a1.pid";
            JsonArray array = pager.getAllGrid("a1.pid,a1.serialcode");

            if (pager.isReSearch()) { //带父目录查询
                pager.clear();
                String ids = TreeUtils.getTreeTableAllPids(array, "sys_dept", "dept_id", "pid");
                pager.where += StringUtils.format(" and a1.dept_id in({0})", ids);
                pager.showColumns = "a1.dept_id id,a1.pid,a1.dept_name text,a1.dept_code,a1.dept_type,a1.dept_phone,a1.serialcode,a1.dept_remark";
                array = pager.getAllGrid("a1.pid,a1.serialcode");
            }
            BLL_Common.setGridListCodeText(array, "dept_type", "dept.type", "combobox");
            BLL_Member.setGridUserNames(array, "id", "sys_dept", ManTypeEnum.领导.getIndex(), "dept_leader");
            BLL_Member.setGridUserNames(array, "id", "sys_dept", ManTypeEnum.负责人.getIndex(), "dept_person");

            String otherAttr = "iconCls:'jgjszIcon',expanded:true";
            return JsonTree.getTreeJsonByPid(array, "0", otherAttr, otherAttr);
        } catch (Exception ex) {
            LogError.write("部门信息列表", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // [部门管理]列表拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:dept:sort")
    public String sort(HttpServletRequest request) {
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
                if (BLL_Dept.saveSort(list)) {
                    LogUtils.addOperateLog(item_id, "部门排序", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("部门排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //部门编辑单元格存储
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:dept:save")
    public String editing(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (Id > 0 && !StringUtils.isEmpty(field)) {
                if (field.equalsIgnoreCase("text")) field = "dept_name";
                Map ht = new HashMap();
                ht.put(field, value);
                DBFunction.updateByTbName(ht, "sys_dept", "dept_id=" + Id);

                LogUtils.addOperateLog(item_id, "部门单元格编辑", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("部门单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[部门管理]列表删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:dept:del")
    public String delete(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            boolean success = BLL_Dept.deleteByIds("dept_id", Id);
            if (success) {
                LogUtils.addOperateLog(item_id, "部门删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("部门删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [部门管理]获取信息
    @RequestMapping(value = "info")
    public String info(HttpServletRequest request) {
        int dept_id = getParaValue(request, "dept_id", 0);
        try {
            if (dept_id > 0) {
                String sql = StringUtils.format("select * from sys_dept where dept_id={0}", dept_id);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "dept");
            }
        } catch (Exception ex) {
            LogError.write("部门获取信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[部门管理]信息新增/修改
    @RequestMapping(value = "save")
    @NoRepeatSubmit(params = true)
    @RequiresPermissions("system:dept:save")
    public String save(HttpServletRequest request) {
        int dept_id = getParaValue(request, "id", 0);
        int pid = getParaValue(request, "pid", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("org_id", UserUtils.getOrgId());        //所属机构为当前登录用户机构
            ht.put("dept_name", getParaValue(request, "dept_name", ""));    //部门名称
            ht.put("dept_code", getParaValue(request, "dept_code", ""));    //部门编号
            ht.put("dept_type", getParaValue(request, "dept_type", ""));    //部门性质
            ht.put("dept_short_name", getParaValue(request, "dept_short_name", ""));    //部门简称
            ht.put("dept_phone", getParaValue(request, "dept_phone", ""));    //电话号码
            ht.put("dept_fax", getParaValue(request, "dept_fax", ""));    //传真
            ht.put("dept_remark", getParaValue(request, "dept_remark", ""));    //备注
            boolean isAdd = (dept_id == 0);
            dept_id = BLL_Dept.saveInfo(ht, dept_id, pid);
            if (dept_id > 0) {
                LogUtils.addOperateLog(item_id, "部门" + (isAdd ? "新增" : "修改"), "成功");
                return ret.getSuccessResult(dept_id);
            } else {
                LogUtils.addOperateLog(item_id, "部门" + (isAdd ? "新增" : "修改"), "失败");
            }
        } catch (Exception ex) {
            LogError.write("部门新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

}




