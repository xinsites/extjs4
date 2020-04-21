package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.system.*;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.dbhelper.DBFunction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-13
 * object name:系统管理->角色管理
 */

@RestController
@RequestMapping(value = "system/role")
public class RoleController extends BaseController {
    @Autowired
    private UserCacheService userCacheService;

    // 获取角色管理树
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:role:tree")
    public String tree(HttpServletRequest request) {
        int node = getParaValue(request, "node", 0);
        try {
            JsonArray array = BLL_Role.getRoleTree(UserUtils.getOrgId());
            BLL_Role.setUserNum(array, "id");
            return JsonTree.getTreeJsonByPid(array, node + "", "", "");
        } catch (Exception ex) {
            LogError.write("获取角色管理树", LogEnum.Error, ex.toString());
        }
        return retGrid.getTreeFail();
    }

    // [角色管理]列表拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:role:sort")
    public String sort(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String sort_vals = getParaValue(request, "sort_vals", "");
            if (!StringUtils.isEmpty(sort_vals)) {
                BLL_Role.saveSort("role_id", sort_vals);
            }

            LogUtils.addOperateLog(item_id, "角色排序", "成功");
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("角色排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[角色管理]列表删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:role:del")
    public String delete(HttpServletRequest request) {
        String Ids = getParaValue(request, "ids", "0");
        int item_id = getParaValue(request, "item_id", 0);
        boolean success = false;
        try {
            Ids = StringUtils.joinAsFilter(Ids);
            if (!StringUtils.isEmpty(Ids)) {
                success = BLL_Role.deleteByIds("role_id", Ids);
            }

            if (success) {
                LogUtils.addOperateLog(item_id, "角色删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("角色删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[角色管理]信息新增/修改
    @RequestMapping(value = "save")
    @RequiresPermissions("system:role:save")
    public String save(HttpServletRequest request) {
        int role_id = getParaValue(request, "id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("org_id", UserUtils.getOrgId());                             //所属机构为当前登录用户机构
            ht.put("role_per_value", getParaValue(request, "role_per_value", ""));    //角色权限值
            ht.put("role_name", getParaValue(request, "text", ""));                   //角色名称
            ht.put("role_state", getParaValue(request, "role_state", ""));            //是否有效
            //ht.put("issys", Common.GetFromVal(Context, "issys", "");		    //是否公共
            ht.put("role_remark", getParaValue(request, "role_remark", ""));    //角色备注
            boolean isAdd = (role_id == 0);
            role_id = BLL_Role.saveInfo(ht, role_id);
            if (role_id > 0) {
                LogUtils.addOperateLog(item_id, "角色" + (isAdd ? "新增" : "修改"), "成功");
                return ret.getSuccessResult(role_id);
            } else {
                LogUtils.addOperateLog(item_id, "角色" + (isAdd ? "新增" : "修改"), "失败");
            }
        } catch (Exception ex) {
            LogError.write("角色新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [角色管理]角色用户列表
    @RequestMapping(value = "user/grid")
    @RequiresPermissions("system:role:member")
    public String roleUserGrid(HttpServletRequest request) {
        int role_id = getParaValue(request, "role_id", 0);
        int norole = getParaValue(request, "norole", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.isdel=0 and a1.org_id=" + UserUtils.getOrgId();
            pager.showColumns = "a1.user_id,a1.user_name,a1.login_name,a1.head_photo,a1.user_sex,a1.org_id,a1.role_id,a1.dept_id,a1.user_state,a1.issys,a1.Remark,a1.serialcode";
            pager.tables = "sys_user a1";
            if (role_id > 0) {
                pager.where += " and a1.role_id=" + role_id;
            } else if (norole == 1) {
                pager.where += " and (a1.role_id is null or a1.role_id=0)";
            }

            pager.loadPageGrid();
            BLL_Role.setGridRoleNames(pager.array, "role_id");
            BLL_Dept.setGridDeptText(pager.array, "dept_id");
            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("角色用户列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // 获取指定类型成员
    @RequestMapping(value = "user/sel")
    public String users(HttpServletRequest request) {
        int role_id = getParaValue(request, "role_id", 0);
        try {
            List<Integer> list = BLL_User.getRoleUserIds(role_id, UserUtils.getOrgId());
            return ret.getSuccessResult("user_ids", StringUtils.joinAsList(list));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [角色管理]角色用户批量分配
    @RequestMapping(value = "user/save")
    @RequiresPermissions("system:role:member")
    public String roleUserSave(HttpServletRequest request) {
        String IdVal = getParaValue(request, "IdVal", "");
        int role_id = getParaValue(request, "role_id", 0);
        try {
            if (IdVal.length() > 0 && role_id > 0) {
                BLL_Role.saveRoleUser(role_id, IdVal);
                String[] Items = IdVal.split(";");
                for (String item : Items) {
                    String[] arr = item.split(":");
                    if (arr.length == 2) {
                        int user_id = NumberUtils.strToInt(arr[0]);
                        userCacheService.changeUserInfoFlag(user_id);
                        ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                    }
                }
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("角色用户保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[角色管理]用户角色(添加、删除)
    @RequestMapping(value = "user/setup")
    @RequiresPermissions("system:role:member")
    public String roleUserDel(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        int role_id = getParaValue(request, "role_id", 0);
        try {
            if (user_id > 0) {
                Map ht = new HashMap();
                if (role_id > 0)
                    ht.put("role_id", role_id);
                else
                    ht.put("role_id", null);
                DBFunction.updateByTbName(ht, "sys_user", "user_id=" + user_id);
                userCacheService.changeUserInfoFlag(user_id);
                ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("用户角色配置", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

}


