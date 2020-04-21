package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.system.BLL_CodeType;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
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
 * create time: 2019-08-24
 * 系统管理->字典管理->编码类型
 */

@RestController
@RequestMapping(value = "system/code/type")
public class CodeTypeController extends BaseController {

    //region 编码类型管理
    // 编码类型树形菜单
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:code:tree")
    public String codeTypeTree(HttpServletRequest request) {
        String json = "[]";
        String node = getParaValue(request, "node", "0");
        try {
            String sql_where = " and PId=" + node;
            sql_where += StringUtils.format(" and (ispublic=1 or org_id={0})", UserUtils.getOrgId());
            JsonArray array = BLL_CodeType.getCodeTypeTree(sql_where);
            json = JsonTree.getTreeJson(array, "");
            json = json.replace("ispublic:\"1\"", "ispublic:\"1\",iconCls:\"icon_code_pub\"");
            return json;
        } catch (Exception ex) {
            LogError.write("编码类型树目录", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //保存编码类型排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:code:type:sort")
    public String codeTypeSort(HttpServletRequest request) {
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
                        ht.put("serialcode", NumberUtils.strToInt(arr[2]));
                        list.add(ht);
                    }
                }
                if (BLL_CodeType.saveCodeTypeSort(list)) {
                    LogUtils.addOperateLog(item_id, "编码类型排序", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("编码类型排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //获取信息
    @RequestMapping(value = "info")
    public String typeInfo(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        try {
            if (id > 0) {
                String sql = StringUtils.format("select * from sys_codetype where id={0}", id);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "code_type");
            }
        } catch (Exception ex) {
            LogError.write("编码类型信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //新增/编辑编码类型
    @RequestMapping(value = "save")
    @RequiresPermissions("system:code:type:save")
    public String codeTypeSave(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        int pid = getParaValue(request, "pid", 0);
        String data_key = getParaValue(request, "data_key", "");
        int item_id = getParaValue(request, "item_id", 0);

        try {
            Map ht = new HashMap();
            ht.put("name", getParaValue(request, "text", ""));
            ht.put("istree", getParaValue(request, "istree", 0));
            ht.put("ispublic", getParaValue(request, "ispublic", 0));
            ht.put("data_key", data_key);
            ht.put("expanded", getParaValue(request, "expand", "false"));
            if (BLL_CodeType.isExistDataKey(id, data_key)) {
                return ret.getFailResult("该数据源标识已经存在！");
            } else {
                if (id == 0) {
                    ht.put("pid", pid);
                    ht.put("org_id", UserUtils.getOrgId());
                    ht.put("serialcode", BLL_CodeType.getSerialCode(pid));
                    id = DBFunction.insertByTbName(ht, "sys_codetype");
                    LogUtils.addOperateLog(item_id, "编码类型新增", "成功");
                } else {
                    DBFunction.updateByTbName(ht, "sys_codetype", "Id=" + id);
                    LogUtils.addOperateLog(item_id, "编码类型修改", "成功");
                }
                return ret.getSuccessResult(id);
            }
        } catch (Exception ex) {
            LogError.write("编码类型新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //删除编码类型
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:code:type:del")
    public String codeTypeDel(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        int item_id = getParaValue(request, "item_id", 0);

        try {
            String sql = StringUtils.format("update sys_codetype set isdel=1 where Id={0}", Id);
            if (DBFunction.executeNonQuery(sql) > 0) {
                LogUtils.addOperateLog(item_id, "编码类型删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("编码类型删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion
}



