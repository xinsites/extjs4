package com.xinsite.controller.info;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.text.PinyinUtils;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.bll.system.BLL_Dept;
import com.xinsite.core.bll.system.BLL_Menu;
import com.xinsite.core.bll.system.BLL_Role;
import com.xinsite.core.bll.system.BLL_User;
import com.xinsite.core.bll.info.BLL_Item_Fixed;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.core.utils.user.UserUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-08-06
 * object name: 工具
 */

@RestController
@RequestMapping(value = "info/util")
public class UtilController extends BaseController {

    // 自定义（Picket弹出框）列表
    @RequestMapping(value = "pickergrid")
    public String pickerGrid(HttpServletRequest request) {
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.querySql = "select distinct PrimaryKey from view_gen_xtype a where 1=1";
            pager.showColumns = "a.id,a.xtype,a.xtype_name";
            JsonArray array = pager.getAllGrid("a.serialcode");
            return retGrid.getGridJson(array, array.size());
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // 生成汉字拼音
    @RequestMapping(value = "buildspell")
    public String buildSpell(HttpServletRequest request) {
        String text = getParaValue(request, "text", "");
        String type = getParaValue(request, "type", "simple");
        String lower = getParaValue(request, "lower", "");

        try {
            String value = "";
            if (type.equalsIgnoreCase("simple"))
                value = PinyinUtils.getFirstSpell(text);
            else
                value = PinyinUtils.getFullSpell(text);

            if (lower.equals("true")) value = value.toLowerCase();

            return ret.getSuccessResult("value", value);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 根据用户ID，获取用户名称
    @RequestMapping(value = "username")
    public String userName(HttpServletRequest request) {
        String Ids = getParaValue(request, "ids", "0");
        try {
            return ret.getSuccessResult("texts", BLL_User.getUserNames(Ids));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 根据角色ID，获取角色名称
    @RequestMapping(value = "rolename")
    public String roleName(HttpServletRequest request) {
        int role_id = getParaValue(request, "ids", 0);
        try {
            return ret.getSuccessResult("texts", BLL_Role.getRoleName(role_id));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 根据部门ID，获取部门名称
    @RequestMapping(value = "deptname")
    public String deptName(HttpServletRequest request) {
        String Ids = getParaValue(request, "ids", "0");
        try {
            return ret.getSuccessResult("texts", BLL_Dept.getDeptNames(Ids));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //region 左边动态树管理 ctrl+alt+t
    //登录用户一级栏目菜单
    @RequestMapping(value = "left/item/level", produces = "application/json;charset=utf-8", method = {RequestMethod.POST})
    public String leftItemLevel(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            JsonArray array = BLL_UserPower.getLoginItems(" and a.pid=0", UserUtils.getLoginUser());
            if (array != null && array.size() > 0) {
                resultMap.put("success", true);
                resultMap.put("config", array);
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            resultMap.put("success", false);
        }
        return res.AjaxJson(resultMap);
    }

    ///登录用户一级栏目以下的菜单
    @RequestMapping(value = "left/item/tree")
    public String leftInfoTree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        try {
            String Condition = " and a.pid=" + node;
            JsonArray array = BLL_UserPower.getLoginItems(Condition, UserUtils.getLoginUser());
            return JsonTree.getTreeJson(array);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //登录用户某栏目权限信息
    @RequestMapping(value = "user/item/permit", method = {RequestMethod.POST})
    public String getUserItemPermission(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (item_id > 0) {
                JsonArray array = BLL_UserPower.getLoginItemPower(item_id, UserUtils.getLoginUser());
                return ret.getFormJson(array, "power");
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //获取某个栏目信息
    @RequestMapping(value = "item/info")
    public String getItemInfo(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (item_id > 0) {
                JsonArray array = BLL_Menu.getItems(item_id);
                return ret.getFormJson(array, "item");
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion


    //region 用户栏目标签操作
    //Fixed=true:固定标签；Fixed=false:取消固定标签；
    @RequestMapping(value = "user/fixtab/save")
    public String saveTabFixed(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String fixed = getParaValue(request, "fixed", "");
        try {
            if (fixed.equalsIgnoreCase("true")) {
                int max_tabs = SysConfigCache.getMaxFixedTabs();
                List<Integer> list = BLL_Item_Fixed.getFixedTabs();
                if (list.size() >= max_tabs) {
                    BLL_Item_Fixed.deleteFixedTabs(list);
                    return ret.getFailResult("固定选项卡最大设置数：" + max_tabs);
                }
            }
            if (BLL_Item_Fixed.saveInfo(UserUtils.getUserId(), item_id, fixed)) {
                return ret.clear().getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("设置固定标签", LogEnum.Error, ex.toString());
        }
        return ret.clear().getFailResult();
    }

    //获取登录用户设置的固定标签
    @RequestMapping(value = "user/fixtabs/get")
    public String getTabFixed(HttpServletRequest request) {
        try {
            JsonArray array = BLL_Item_Fixed.getSetFixedTab();
            return ret.getArrayJson(array, "tabs");
        } catch (Exception ex) {
            LogError.write("获取固定标签", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion
}


