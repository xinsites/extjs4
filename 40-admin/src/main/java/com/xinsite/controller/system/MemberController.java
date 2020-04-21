package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.enums.system.MemberEnum;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.base.BaseController;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.system.BLL_Dept;
import com.xinsite.core.bll.system.BLL_Member;
import com.xinsite.core.bll.system.BLL_Role;
import com.xinsite.core.bll.system.BLL_User;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zhangxiaxin
 * create time: 2019-09-02
 * object name:系统管理->成员管理
 */

@RestController
@RequestMapping(value = "system/member")
public class MemberController extends BaseController {

    // [成员管理]成员表格列表
    @RequestMapping(value = "grid")
    public String grid(HttpServletRequest request) {
        String table_name = getParaValue(request, "table_name", "");
        int table_id = getParaValue(request, "table_id", 0);
        int man_type = getParaValue(request, "man_type", 0);
        int noleader = getParaValue(request, "noleader", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.user_id=b1.user_id and b1.table_name=@table_name and b1.man_type=@man_type";
            pager.showColumns = "a1.user_id,b1.table_id leader,a1.user_name,a1.login_name,a1.head_photo,a1.user_sex,a1.org_id,a1.role_id,a1.dept_id,a1.user_state,a1.issys,a1.Remark,a1.serialcode";
            pager.tables = "sys_user a1,sys_member b1";
            if (table_name.equals(MemberEnum.用户领导.getValue())) {
                pager.tables = "sys_user a1 left join sys_member b1 on 1=1" + pager.where;
                pager.where = "";
            }
            pager.where += " and a1.isdel=0 and a1.org_id=" + UserUtils.getOrgId();

            pager.addPara(new DBParameter("@table_name", table_name));
            if (table_id > 0) {
                pager.addPara(new DBParameter("@table_id", table_id));
                pager.where += " and b1.table_id=@table_id";
            } else if (noleader == 1) {
                pager.where += " and b1.table_id is null";
            }
            pager.addPara(new DBParameter("@man_type", man_type));
            pager.loadPageGrid();

            BLL_Role.setGridRoleNames(pager.array, "role_id");
            BLL_Dept.setGridDeptText(pager.array, "dept_id");
            if (table_name.equals(MemberEnum.用户领导.getValue())) {
                BLL_User.setGridUserNames(pager.array, "leader");
            }
            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("成员列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // 成员移除
    @RequestMapping(value = "delete")
    public String delete(HttpServletRequest request) {
        String table_name = getParaValue(request, "table_name", "");
        int table_id = getParaValue(request, "table_id", 0);
        int user_id = getParaValue(request, "user_id", 0);

        try {
            if (!table_name.equals("") && table_id > 0 && user_id > 0) {
                BLL_Member.removeMemberUser(table_name, table_id, user_id);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("成员移除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 获取部门所有用户信息(窗口成员列表)
    @RequestMapping(value = "deptusers")
    public String deptUsers(HttpServletRequest request) {
        int dept_id = getParaValue(request, "dept_id", 0);
        String query = getParaValue(request, "query", "");
        try {
            String sql_where = " and a.org_id=" + UserUtils.getOrgId();
            if (dept_id != 0) sql_where += " and a.dept_id=" + dept_id;

            List<DBParameter> ls = new ArrayList<>();
            if (!StringUtils.isEmpty(query)) {
                sql_where += " and (a.user_name like @user_name or a.login_name like @login_name)";
                ls.add(new DBParameter("@user_name", "%" + query + "%"));
                ls.add(new DBParameter("@login_name", "%" + query + "%"));
            }
            JsonArray dt = BLL_User.getDeptUserInfo(ls, sql_where);
            return JsonTree.getTreeJsonByPid(dt, "0");
        } catch (Exception ex) {
            LogError.write("获取部门用户信息", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // 获取指定类型成员
    @RequestMapping(value = "users")
    public String users(HttpServletRequest request) {
        String table_name = getParaValue(request, "table_name", "");
        int table_id = getParaValue(request, "table_id", 0);
        int man_type = getParaValue(request, "man_type", 0);
        try {
            List<Integer> list = BLL_Member.getMemberUserIds(table_name, table_id, man_type);
            return ret.getSuccessResult("user_ids", StringUtils.joinAsList(list));
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 成员保存(窗口成员选择)
    @RequestMapping(value = "save")
    @RequiresPermissions("system:member:save")
    public String save(HttpServletRequest request) {
        String table_name = getParaValue(request, "table_name", "");
        int table_id = getParaValue(request, "table_id", 0);
        int man_type = getParaValue(request, "man_type", 0);
        String IdVal = getParaValue(request, "IdVal", "");
        try {
            if (!table_name.equals("") && man_type > 0) {
                IdVal = StringUtils.joinAsFilter(IdVal);
                BLL_Member.saveMemberUser(table_name, table_id, man_type, IdVal);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("成员保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
}

