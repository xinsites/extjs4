package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.system.*;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.BLL_Common;
import com.xinsite.core.utils.TreeUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.search.SearchUtils;
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
 * create time: 2019-09-13
 * object name:系统管理->机构管理
 */

@RestController
@RequestMapping(value = "system/org")
public class OrganizeController extends BaseController {

    // [机构管理]信息查询列表
    @RequestMapping(value = "tree")
    @RequiresPermissions("system:org:tree")
    public String tree(HttpServletRequest request) {
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.isdel=0";
            pager.showColumns = "a1.org_id id,a1.pid,a1.company_name text,a1.nature,a1.short_name,a1.build_time,a1.leader,a1.address,a1.serialcode,a1.remark";
            pager.tables = "sys_organize a1";

            if (!SearchUtils.isEmptySearchs(pager.searchs)) pager.showColumns = "a1.org_id,a1.pid";
            JsonArray array = pager.getAllGrid("a1.pid,a1.serialcode");
            if (pager.isReSearch()) { //带父目录查询
                pager.clear();
                String ids = TreeUtils.getTreeTableAllPids(array, "sys_organize", "org_id", "pid");
                pager.where += StringUtils.format(" and a1.org_id in({0})", ids);
                pager.showColumns = "a1.org_id id,a1.pid,a1.company_name text,a1.nature,a1.short_name,a1.build_time,a1.leader,a1.address,a1.serialcode,a1.remark";
                array = pager.getAllGrid("a1.pid,a1.serialcode");
            }
            //下拉框选择值时替换文本
            BLL_Common.setGridListCodeText(array, "nature", "company.type", "combobox");
            BLL_User.setGridUserNames(array, "leader");
            BLL_Organize.setManagers(array);
            String otherAttr = "iconCls:'jgjszIcon',expanded:true";
            return JsonTree.getTreeJsonByPid(array, "0", otherAttr, otherAttr);
        } catch (Exception ex) {
            LogError.write("组织机构信息查询", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    //[机构管理]编辑单元格存储
    @RequestMapping(value = "editing")
    @RequiresPermissions("system:org:save")
    public String editing(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (Id > 0 && !StringUtils.isEmpty(field)) {
                if (field.equalsIgnoreCase("text")) field = "company_name";
                Map ht = new HashMap();
                ht.put(field, value);
                DBFunction.updateByTbName(ht, "sys_organize", "org_id=" + Id);

                LogUtils.addOperateLog(item_id, "组织机构单元格编辑", "成功");
            }
        } catch (Exception ex) {
            LogError.write("组织机构单元格编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [机构管理]列表拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("system:org:sort")
    public String sort(HttpServletRequest request) {
        String sort_vals = getParaValue(request, "sort_vals", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (!sort_vals.equals("")) {
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
                if (BLL_Organize.saveSort(list)) {
                    LogUtils.addOperateLog(item_id, "组织机构排序", "成功");
                }
            }
        } catch (Exception ex) {
            LogError.write("组织机构排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[机构管理]列表删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("system:org:del")
    public String delete(HttpServletRequest request) {
        int Id = getParaValue(request, "Id", 0);
        int item_id = getParaValue(request, "item_id", 0);

        try {
            boolean success = BLL_Organize.deleteByIds("org_id", Id);
            if (success) {
                LogUtils.addOperateLog(item_id, "组织机构删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("组织机构删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [机构管理]获取信息
    @RequestMapping(value = "info")
    public String info(HttpServletRequest request) {
        int org_id = getParaValue(request, "org_id", 0);
        try {
            if (org_id > 0) {
                String sql = StringUtils.format("select * from sys_organize where org_id={0}", org_id);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "organize");
            }
        } catch (Exception ex) {
            LogError.write("组织机构获取信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[机构管理]信息新增/修改
    @RequestMapping(value = "save")
    @RequiresPermissions("system:org:save")
    public String save(HttpServletRequest request) {
        int org_id = getParaValue(request, "id", 0);
        int pid = getParaValue(request, "pid", 0);
        int item_id = getParaValue(request, "item_id", 0);

        try {
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("company_name", getParaValue(request, "company_name", ""));    //公司名称
            ht.put("nature", getParaValue(request, "nature", ""));    //公司性质
            ht.put("short_name", getParaValue(request, "short_name", ""));    //公司简称
            ht.put("build_time", getParaValue(request, "build_time", ""));    //成立时间
            ht.put("siteurl", getParaValue(request, "siteurl", ""));    //公司官网
            ht.put("email", getParaValue(request, "email", ""));    //电子邮箱
            ht.put("phone", getParaValue(request, "phone", ""));    //电话
            ht.put("fax", getParaValue(request, "fax", ""));    //传真
            ht.put("leader", getParaValue(request, "leader", ""));    //负责人
            ht.put("postal_code", getParaValue(request, "postal_code", ""));    //邮编
            ht.put("province_id", getParaValue(request, "province_id", ""));    //所在省
            ht.put("city_id", getParaValue(request, "city_id", ""));    //所在市
            ht.put("county_id", getParaValue(request, "county_id", ""));    //所在县
            ht.put("address", getParaValue(request, "address", ""));    //详细地址
            ht.put("remark", getParaValue(request, "remark", ""));    //备注
            boolean isAdd = (org_id == 0);
            if (isAdd) ht.put("create_uid", UserUtils.getUserId());

            org_id = BLL_Organize.saveInfo(ht, org_id, pid);
            if (org_id > 0) {
                LogUtils.addOperateLog(item_id, "组织机构" + (isAdd ? "新增" : "修改"), "成功");
                return ret.getSuccessResult(org_id);
            } else {
                LogUtils.addOperateLog(item_id, "组织机构" + (isAdd ? "新增" : "修改"), "失败");
            }
        } catch (Exception ex) {
            LogError.write("组织机构新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [机构管理]管理员用户列表
    @RequestMapping(value = "managergrid")
    @RequiresPermissions("system:org:manager")
    public String managerGrid(HttpServletRequest request) {
        int org_id = getParaValue(request, "org_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.role_id=r1.role_id and a1.isdel=0 and r1.issys=1 and r1.isdel=0 and a1.org_id=" + org_id;
            pager.showColumns = "a1.user_id,a1.user_name,a1.login_name,a1.head_photo,a1.user_sex,a1.org_id,a1.role_id,a1.user_state,a1.issys,a1.remark,a1.serialcode";
            pager.tables = "sys_user a1,sys_role r1";

            pager.loadPageGrid();
            BLL_Role.setGridRoleNames(pager.array, "role_id");

            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("管理员用户列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //[机构管理]管理员新增/修改
    @RequestMapping(value = "managersave")
    @RequiresPermissions("system:org:manager")
    public String managerSave(HttpServletRequest request) {
        int user_id = getParaValue(request, "user_id", 0);
        int org_id = getParaValue(request, "org_id", 0);
        int ResertPwd = getParaValue(request, "ResertPwd", 0);
        int item_id = getParaValue(request, "item_id", 0);

        try {
            String login_name = getParaValue(request, "login_name", "");    //登录名
            Map ht = new HashMap();
            ht.put("modify_time", DateUtils.getDateTime());
            ht.put("user_name", getParaValue(request, "user_name", ""));    //用户姓名
            ht.put("user_state", getParaValue(request, "user_state", "1"));
            ht.put("login_name", login_name); //登录名

            BLL_Config.checkOrganizeConfig(org_id);
            if (user_id == 0) {
                ht.put("role_id", BLL_Role.getManagerRoleId(org_id));
                ht.put("create_time", DateUtils.getDateTime());
                ht.put("org_id", org_id);         //所属机构
            } else {
                if (ResertPwd == 1) { //修改时重置密码
                    String password = BLL_Config.getConfigValue(org_id, "reset_password", "111111");
                    String level_password = BLL_PassWord.getLevelPassword(password);
                    String pwd_salt = IdGenerate.buildUUID();  //重新更改密码盐
                    String user_password = BLL_PassWord.getUserPassword(level_password, pwd_salt);
                    ht.put("pwd_salt", pwd_salt);         //重置密码盐
                    ht.put("password", user_password);    //重置密码
                }
            }
            if (BLL_User.isExistLoginName(user_id, login_name)) {
                return ret.getFailResult("该登录名已经存在！");
            } else {
                boolean isAdd = (user_id == 0);
                user_id = BLL_User.saveInfo(ht, user_id);
                if (user_id > 0) {
                    if (isAdd) {
                        BLL_PassWord.setUserPassword(user_id + "", "add_password");
                    } else if (ResertPwd == 1) {
                        ShiroUtils.kickoutUser(user_id, MessageUtils.message("user.password.reset"));  //重置密码用户下线
                    }

                    LogUtils.addOperateLog(item_id, "管理员" + (isAdd ? "新增" : "修改"), "成功");
                    return ret.getSuccessResult(user_id);
                } else {
                    LogUtils.addOperateLog(item_id, "管理员" + (isAdd ? "新增" : "修改"), "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("管理员新增/修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
}

