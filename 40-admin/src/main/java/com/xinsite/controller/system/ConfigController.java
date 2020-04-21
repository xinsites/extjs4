package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.system.BLL_Config;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.core.utils.search.SearchUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-08-24
 * 系统管理->系统参数配置
 */

@RestController
@RequestMapping(value = "system/config")
public class ConfigController extends BaseController {

    // [系统配置]-信息列表
    @RequestMapping(value = "grid")
    @RequiresPermissions("system:config:grid")
    public String grid(HttpServletRequest request) {
        int itemid = getParaValue(request, "item_id", 0);
        try {
            //JsonArray searchDt = BLL_Config.getConfig(UserUtils.getOrgId(), itemid);
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = StringUtils.format(" and a1.org_id={0} and a1.item_id={1}", UserUtils.getOrgId(), itemid);
            pager.showColumns = "a1.id,a1.config_key,a1.field_explain,a1.config_editor,a1.config_value,a1.config_text,a1.issys";
            pager.tables = "sys_config a1";

            JsonArray searchDt = pager.getAllGrid("a1.serialcode");
            return retGrid.getGridJson(searchDt, searchDt.size());
        } catch (Exception ex) {
            LogError.write("系统配置列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //[系统配置]-单元格存储
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:config:mod")
    public String editing(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        String config_key = getParaValue(request, "config_key", "");
        String value = getParaValue(request, "value", "");
        String text = getParaValue(request, "text", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (id > 0) {
                Map<String, Object> ht = new HashMap<>();
                ht.put("config_value", value);
                ht.put("config_text", text);
                ht.put("modify_time", DateUtils.getDateTime());
                if (BLL_Config.setConfigValue(ht, id, config_key)) {
                    LogUtils.addOperateLog(item_id, "系统配置编辑", "成功");
                    return ret.getSuccessResult();
                } else {
                    LogUtils.addOperateLog(item_id, "系统配置编辑", "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("系统配置单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[系统配置]新增
    @RequestMapping(value = "add")
    @RequiresPermissions("system:config:add")
    public String add(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String config_key = getParaValue(request, "config_key", "");
        String field_explain = getParaValue(request, "field_explain", "");
        String config_editor = getParaValue(request, "config_editor", "");
        try {
            if (item_id > 0) {
                int org_id = UserUtils.getOrgId();
                Map ht = new HashMap();
                ht.put("item_id", item_id);
                ht.put("org_id", org_id);
                ht.put("config_key", config_key);
                ht.put("field_explain", field_explain);
                ht.put("config_editor", config_editor);
                ht.put("serialcode", BLL_Config.getSerialCode(org_id, item_id));
                ht.put("modify_time", DateUtils.getDateTime());
                if (BLL_Config.isExistKey(org_id, item_id, config_key)) {
                    return ret.getFailResult("该变量名称已经存在！");
                } else {
                    int id = BLL_Config.insert(ht);
                    LogUtils.addOperateLog(item_id, "系统配置新增", id);
                    if (id > 0) {
                        return ret.getSuccessResult(id);
                    }
                }
            } else {
                return ret.getFailResult("菜单栏目Id获取不到！");
            }
        } catch (Exception ex) {
            LogError.write("系统配置新增", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[系统配置]删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:config:delete")
    public String delete(HttpServletRequest request) {
        int id = getParaValue(request, "id", 0);
        int itemid = getParaValue(request, "item_id", 0);
        boolean success = false;
        try {
            if (id > 0) {
                success = BLL_Config.deleteById(id);
                if (success) {
                    LogUtils.addOperateLog(itemid, "系统配置删除", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("系统配置删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

}




