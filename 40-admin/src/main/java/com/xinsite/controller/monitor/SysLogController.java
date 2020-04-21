package com.xinsite.controller.monitor;

import com.google.gson.JsonArray;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.base.BaseController;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.system.BLL_Log;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.Keys;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * create by zhangxiaxin
 * create time: 2019-10-16
 * object name: 系统日志监控
 */

@RestController
@RequestMapping(value = "monitor/log")
public class SysLogController extends BaseController {

    // 日志类型树形目录
    @RequestMapping(value = "logtype")
    public String logType(HttpServletRequest request) {
        String node = getParaValue(request, "node", "0");
        try {
            JsonArray dtALL = BLL_Log.GetLogTypeJsonArray();
            return JsonTree.getTreeJson(dtALL, "");
        } catch (Exception ex) {
            LogError.write("日志类型树形目录", LogEnum.Error, ex.toString());
        }
        return "[]";
    }

    // [操作日志]信息查询列表
    @RequestMapping(value = "grid")
    @RequiresPermissions("monitor:log:grid")
    public String grid(HttpServletRequest request) {
        int log_type = getParaValue(request, "log_type", 0);
        int itemid = getParaValue(request, "item_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request, itemid);
            pager.where = String.format(" and a1.isdel=0 and (a1.org_id=%d or a1.org_id=0)", UserUtils.getOrgId());
            pager.showColumns = "a1.log_id,a1.log_fun,a1.log_ip,a1.action_type,a1.log_message,a1.log_result,a1.create_time,a1.user_id,ifnull(a2.user_name,a1.login_name) user_name,a1.serialcode";
            pager.tables = "sys_log a1 left join sys_user a2 on a1.user_id=a2.user_id";
            if (log_type > 0) pager.where += " and a1.log_type=" + log_type;
            pager.addDataPerWhere(Keys.getKey("load_all", "true"), Keys.getKey("field_user", "user_id")); //加数据权限

            if (!UserUtils.isSuperAdminer()) {
                pager.where += StringUtils.format(" and a2.role_id!={0}", Global.getConfig("config.super_role"));
            }
            pager.loadPageGrid();

            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("日志信息查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //[操作日志]清空日志记录
    @RequestMapping(value = "clear")
    @RequiresPermissions("monitor:log:clear")
    public String clear(HttpServletRequest request) {
        int days = getParaValue(request, "days", 0);
        int log_type = getParaValue(request, "log_type", 0);
        int item_id = getParaValue(request, "item_id", 0);

        try {
            BLL_Log.clearLogRecords(days, log_type);
            LogUtils.addOperateLog(item_id, "清空日志记录", "成功");
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("清空日志记录", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // [操作日志]生成导出的Excel
    @RequestMapping(value = "excel")
    @RequiresPermissions("monitor:log:excel")
    public String excel(HttpServletRequest request) {
//        String linkType = getParaValue(request, "linkType", "and");
//        int log_type = getParaValue(request, "log_type", 0);
//        String searchdata = getParaValue(request, "searchdata", "");
//        try {
//            String tempDir = "~/TempFiles/";
//            String filename = Context.Session.SessionID;
//            String MapUrl = HttpContext.Current.Server.MapPath(System.IO.Path.Combine(tempDir, filename + ".xls"));
//
//            String sql_where = StringUtils.format(" and a1.isdel=0 and (a1.org_id={0} or a1.org_id=0)", UserUtils.getOrgId());
//            pager.showColumns = "Convert(varchar(16),a1.create_time,120) '操作时间',a2.user_name '操作用户',a1.log_ip 'IP地址',a1.log_fun '系统功能',a1.action_type '操作类型',a1.log_result '执行结果',a1.log_message '日志信息描述'";
//            pager.tables = "sys_log a1 left join sys_user a2 on a1.user_id=a2.user_id";
//            if (log_type > 0) sql_where += " and a1.log_type=" + log_type;
//            sql_showfields = sql_showfields.Replace("a1.create_time", "Convert(varchar(10),a1.create_time,120) create_time");
//
//            List<DBParameter> ls = new ArrayList<>();
//            List<Searchs> searchs = null;
//            if (!StringUtils.isEmpty(searchdata))
//                searchs = searchdata.JSONStringToList < Searchs > ();
//            String sql = SqlUtils.getSqlBySearchs(sql_tables, searchs, ls, linkType, sql_where);
//            JsonArray searchDt = SqlUtils.SearchAll(sql, "a1.serialcode desc,a1.create_time desc", ls, sql_showfields);
//
//            HelperNPOI_Excel.Export(searchDt, "操作日志", MapUrl);
//            var jsn = new JObject();
//            jsn["success", true;
//            jsn["filepath", filename;
//            jsn["filename", "操作日志";
//            json = jsn.toString();
//        } catch (Exception ex) {
//            LogError.Write("日志导出Excel", Sys_LogType.Error, ex.toString());
//        }
        return ret.getFailResult();
    }

    // [操作日志]获取信息
    @RequestMapping(value = "info")
    public String info(HttpServletRequest request) {
        int log_id = getParaValue(request, "log_id", 0);
        try {
            if (log_id > 0) {
                String sql = StringUtils.format("select * from sys_log where log_id={0}", log_id);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "OperateLog");
            }
        } catch (Exception ex) {
            LogError.write("日志获取信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //[日志管理]设置访问日志
    @RequestMapping(value = "add")
    public String add(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        int log_id = 0;
        try {
            LogUtils.addLogByBrowse(item_id);
            return ret.getSuccessResult(log_id);
        } catch (Exception ex) {
            LogError.write("设置访问日志", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
}


