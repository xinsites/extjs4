package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.PerEnum;
import com.xinsite.common.uitls.extjs.JsonGrid;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.base.BaseController;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.model.system.PowerSaveModel;
import com.xinsite.core.bll.permission.BLL_PowerInfo;
import com.xinsite.core.bll.permission.BLL_Permission;
import com.xinsite.core.bll.system.BLL_Menu;
import com.xinsite.common.enums.system.ItemEnum;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zhangxiaxin
 * create time: 2019-09-13
 * object name:系统管理->权限管理
 */

@RestController
@RequestMapping(value = "system/power")
public class PowerController extends BaseController {

    @Autowired
    private UserCacheService userCacheService;

    // 权限树形目录获取
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:power:tree")
    public String GetPermissionTree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        String tb_type = getParaValue(request, "tb_type", "user");
        int org_id = getParaValue(request, "org_id", 0);  //组织机构管理员分配
        int tb_id = getParaValue(request, "tb_id", 0);
        int self = getParaValue(request, "self", 0);  //1:只包含配置栏目

        if (org_id == 0) org_id = UserUtils.getOrgId();
        try {
            List<DBParameter> ls = new ArrayList<>();
            PageHelper pager = SearchUtils.getPageHelper(request);
            String sql_where = BLL_Menu.getItemSearchWhere(pager.searchs, ls);
            int tbType = BLL_PowerInfo.getObjType(tb_type);
            if (StringUtils.isEmpty(sql_where)) {
                sql_where = " and a.pid=" + node;
                sql_where += StringUtils.format(" and (a.org_id=0 or a.org_id={0})", org_id);
                JsonArray array = BLL_PowerInfo.getItemTree(sql_where, org_id, tbType, tb_id, self);
                BLL_PowerInfo.setCheckGroup(array);
                return JsonTree.getTreeJson(array, "checked:false");
            } else {
                sql_where += StringUtils.format(" and (a.org_id=0 or a.org_id={0})", org_id);
                JsonArray array = BLL_PowerInfo.getAllItemTree(sql_where, tbType, tb_id, self, ls);
                if (array != null && array.size() > 0) {
                    ls.clear();
                    String ids = TreeUtils.getTreeTableAllPids(array, "id", "pid", "sys_menu", "item_id", "pid");
                    sql_where = StringUtils.format("and a.item_id in({0})", ids);
                    array = BLL_PowerInfo.getAllItemTree(sql_where, tbType, tb_id, self, ls);
                    BLL_PowerInfo.setCheckGroup(array);
                }
                return JsonTree.getTreeJsonByPid(array, "0", "checked:false", "checked:false");
            }
        } catch (Exception ex) {
            LogError.write("权限树目录获取", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // 用户角色权限获取
    @RequestMapping(value = "info")
    public String GetPermissionInfo(HttpServletRequest request) {
        int tb_id = getParaValue(request, "tb_id", 0);
        String tb_type = getParaValue(request, "tb_type", "user");
        try {
            JsonArray dt = BLL_PowerInfo.getPermissionInfo(BLL_PowerInfo.getObjType(tb_type), tb_id);
            return "{success:true,data:" + JsonGrid.getGridJson(dt) + "}";
        } catch (Exception ex) {
            LogError.write((tb_type.equalsIgnoreCase("user") ? "用户" : "角色") + "权限获取", LogEnum.Error, ex.toString());
        }
        return "{success:false,data:[]}";
    }

    //实时权限保存
    @RequestMapping(value = "realsave")
    @RequiresPermissions("system:per:save")
    public String realSave(HttpServletRequest request) {
        int tb_id = getParaValue(request, "tb_id", 0);
        String tb_type = getParaValue(request, "tb_type", "user");
        String power_info = getParaValue(request, "power_info", "");

        try {
            //System.Threading.Thread.Sleep(1000);
            if (!StringUtils.isEmpty(power_info)) {
                power_info = StringUtils.unescape(power_info);
                List<PowerSaveModel> list = GsonUtils.getList(power_info, PowerSaveModel.class);
                if (BLL_Permission.saveRealPermission(BLL_PowerInfo.getObjType(tb_type), tb_id, list)) {
                    JsonArray dt = BLL_PowerInfo.getPermissionInfo(BLL_PowerInfo.getObjType(tb_type), tb_id);
                    LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "实时权限保存", "成功");
                    ShiroUtils.clearCachedAuthorizationInfo(tb_type, tb_id);
                    return "{success:true,data:" + JsonGrid.getGridJson(dt) + "}";
                }
            }
        } catch (Exception ex) {
            LogError.write("实时权限保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //按钮权限保存
    @RequestMapping(value = "btnsave")
    @RequiresPermissions("system:per:save")
    public String btnSave(HttpServletRequest request) {
        int tb_id = getParaValue(request, "tb_id", 0);
        String tb_type = getParaValue(request, "tb_type", "user");
        String power_info = getParaValue(request, "power_info", "");
        String item_ids = getParaValue(request, "item_ids", "");  //打开的没有被选中的栏目
        try {
            //System.Threading.Thread.Sleep(1000);
            if (!StringUtils.isEmpty(power_info)) {
                power_info = StringUtils.unescape(power_info);
                List<PowerSaveModel> list = GsonUtils.getList(power_info, PowerSaveModel.class);
                if (BLL_Permission.saveBtnPermission(BLL_PowerInfo.getObjType(tb_type), tb_id, list, item_ids)) {
                    JsonArray dt = BLL_PowerInfo.getPermissionInfo(BLL_PowerInfo.getObjType(tb_type), tb_id);
                    LogUtils.addOperateLog(ItemEnum.权限管理.getId(), (tb_type.equalsIgnoreCase("user") ? "用户" : "角色") + "权限保存", "成功");
                    ShiroUtils.clearCachedAuthorizationInfo(tb_type, tb_id);
                    return "{success:true,data:" + JsonGrid.getGridJson(dt) + "}";
                }
            }
        } catch (Exception ex) {
            LogError.write("权限保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //按钮权限清空
    @RequestMapping(value = "clear")
    @RequiresPermissions("system:per:clear")
    public String clear(HttpServletRequest request) {
        int tb_id = getParaValue(request, "tb_id", 0);
        String tb_type = getParaValue(request, "tb_type", "user");
        try {
            if (BLL_Permission.clearAllPermission(BLL_PowerInfo.getObjType(tb_type), tb_id)) {
                LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "权限清空", "成功");
                ShiroUtils.clearCachedAuthorizationInfo(tb_type, tb_id);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("权限清空", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //数据权限保存
    @RequestMapping(value = "datasave")
    @RequiresPermissions("system:per:data")
    public String dataSave(HttpServletRequest request) {
        int tb_id = getParaValue(request, "tb_id", 0);
        String tb_type = getParaValue(request, "tb_type", "user");
        int data_per = getParaValue(request, "data_per", 0);
        String data_ids = getParaValue(request, "data_ids", "");
        String power_info = getParaValue(request, "power_info", "");

        try {
            //System.Threading.Thread.Sleep(1000);
            if (!StringUtils.isEmpty(power_info)) {
                power_info = StringUtils.unescape(power_info);
                List<PowerSaveModel> list = GsonUtils.getList(power_info, PowerSaveModel.class);
                if (BLL_Permission.saveDataPermission(BLL_PowerInfo.getObjType(tb_type), tb_id, data_per, data_ids, list)) {
                    JsonArray dt = BLL_PowerInfo.getPermissionInfo(BLL_PowerInfo.getObjType(tb_type), tb_id);
                    LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "数据权限保存", "成功");
                    return "{success:true,data:" + JsonGrid.getGridJson(dt) + "}";
                }
            }
        } catch (Exception ex) {
            LogError.write("数据权限保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //region==========剔除权限管理==========
    // 剔除权限查询列表
    @RequestMapping(value = "remove/tree")
    @RequiresPermissions("system:per:remove")
    public String removeTree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        int user_id = getParaValue(request, "user_id", 0);
        //String expand = getParaValue(request, "expand", "false");
        try {
            String sql_where = " and a.pid=" + node;
            JsonArray dt = BLL_PowerInfo.getRemoveItemTree(user_id);
            BLL_PowerInfo.setCheckGroup(dt);
            return JsonTree.getTreeJsonByPid(dt, node + ""); //全部加载
        } catch (Exception ex) {
            LogError.write("剔除权限栏目获取", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //剔除权限新增
    @RequestMapping(value = "remove/add")
    @RequiresPermissions("system:per:remove")
    public String removeAdd(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        String item_ids = getParaValue(request, "item_ids", "");

        try {
            if (user_id > 0 && !StringUtils.isEmpty(item_ids)) {
                if (BLL_Permission.saveRemovePermission(user_id, item_ids)) {
                    LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "剔除权限新增", "成功");
                    ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("剔除权限新增", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 剔除权限新增时，栏目树获取
    @RequestMapping(value = "remove/seltree")
    @RequiresPermissions("system:per:remove")
    public String removeSelTree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        int user_id = getParaValue(request, "user_id", 0);

        try {
            String sql_where = " and a.pid=" + node;
            sql_where += StringUtils.format(" and (a.org_id=0 or a.org_id={0})", UserUtils.getOrgId());
            JsonArray dt = BLL_PowerInfo.selRemoveItemTree(sql_where, user_id);
            return JsonTree.getTreeJson(dt, "checked:false");
        } catch (Exception ex) {
            LogError.write("剔除新增栏目获取", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //剔除权限单元格编辑
    @RequestMapping(value = "remove/editing")
    @RequiresPermissions("system:per:remove")
    public String removeEditing(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");

        try {
            if (user_id > 0 && item_id > 0 && !StringUtils.isEmpty(field)) {
                if (BLL_Permission.editingRemovePermission(user_id, item_id, field, value)) {
                    if (field.equals("del_item")) userCacheService.clearUserDeleteItemIds(user_id);
                    LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "权限剔除编辑", "成功");
                    ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("权限剔除编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //剔除权限删除
    @RequestMapping(value = "remove/delete")
    @RequiresPermissions("system:per:remove")
    public String removeDelete(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (BLL_Permission.deletePermission(PerEnum.剔除权限.getIndex(), user_id, item_id)) {
                userCacheService.clearUserDeleteItemIds(user_id);
                LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "剔除权限删除", "成功");
                ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("剔除权限删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //按钮剔除权限清空
    @RequestMapping(value = "remove/clear")
    @RequiresPermissions("system:per:remove")
    public String removeClear(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        try {
            if (BLL_Permission.clearAllPermission(PerEnum.剔除权限.getIndex(), user_id)) {
                userCacheService.clearUserDeleteItemIds(user_id);
                LogUtils.addOperateLog(ItemEnum.权限管理.getId(), "剔除权限清空", "成功");
                ShiroUtils.clearCachedAuthorizationInfo("user", user_id);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("剔除权限清空", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //endregion

    //region==========权限用户查看==========
    ///菜单栏目树
    @RequestMapping(value = "item/tree")
    public String itemTree(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        try {
            String Condition = " and a.pid=" + node;
            JsonArray array = BLL_PowerInfo.getItemTree(Condition);
            return JsonTree.getTreeJson(array);
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    @RequestMapping(value = "user/power")
    public String userPower(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (item_id > 0) {
                PageHelper pager = SearchUtils.getPageHelper(request);
                pager.where = " and (u1.item_id>0 or r1.item_id>0 or a1.role_id=1) and a1.isdel=0 and a1.org_id=" + UserUtils.getOrgId();
                pager.where += " \n and not exists(select 1 from sys_power_menu b1 where b1.item_id=@item_id and b1.tb_type=@tb_type3 and b1.del_item=1 and b1.tb_id=a1.user_id)";
                pager.showColumns = "a1.user_id,a1.role_id,a1.user_name,a1.login_name,a1.head_photo, \n" +
                        "(select group_concat(distinct c1.fun_id separator ',') from sys_menu_fun c1,sys_power_fun d1\n" +
                        " where c1.isdel=0 and c1.fun_id=d1.fun_id and (d1.pm_id=u1.pm_id or d1.pm_id=r1.pm_id)\n" +
                        " and not exists(select 1 from sys_power_fun df where df.pm_id=d.pm_id and df.fun_id=c1.fun_id)) fun_ids,\n" +
                        "case when u1.data_per>0 then u1.data_per else r1.data_per end data_per, \n" +
                        "case when u1.data_per>0 then u1.data_ids else r1.data_ids end data_ids ";
                pager.tables = "sys_user a1\n" +
                        "left join sys_power_menu u1 on u1.item_id=@item_id and u1.isdel=0 and u1.tb_type=@tb_type1 and u1.tb_id=a1.user_id\n" +
                        "left join sys_power_menu r1 on r1.item_id=@item_id and r1.isdel=0 and r1.tb_type=@tb_type2 and r1.tb_id=a1.role_id\n" +
                        "left join sys_power_menu d on d.item_id=@item_id and d.isdel=0 and d.tb_type=@tb_type3 and d.tb_id=a1.user_id ";

                pager.addPara(new DBParameter("@item_id", item_id));
                pager.addPara(new DBParameter("@tb_type1", PerEnum.用户权限.getIndex()));
                pager.addPara(new DBParameter("@tb_type2", PerEnum.角色权限.getIndex()));
                pager.addPara(new DBParameter("@tb_type3", PerEnum.剔除权限.getIndex()));
                pager.loadPageGrid("a1.serialcode desc,a1.create_time desc");
                BLL_PowerInfo.setCheckGroup(pager.array, item_id);
                return retGrid.getGridJson(pager.array, pager.recordCount);
            }
        } catch (Exception ex) {
            LogError.write("权限用户查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }
    //endregion
}


