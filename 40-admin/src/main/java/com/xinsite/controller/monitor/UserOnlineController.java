package com.xinsite.controller.monitor;

import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.enums.OnlineStatus;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.DBParameter;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * create by zhangxiaxin
 * create time: 2019-11-22
 * object name: 在线用户
 */

@RestController
@RequestMapping(value = "monitor/online")
public class UserOnlineController extends BaseController {

    //在线用户：查询列表
    @RequestMapping(value = "grid")
    @RequiresPermissions("monitor:online:grid")
    public String grid(HttpServletRequest request) {
        int itemid = getParaValue(request, "item_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            pager.where = " and a1.user_id=b1.user_id and a1.status=@status";
            pager.addPara(new DBParameter("@status", OnlineStatus.在线.getValue()));
            pager.showColumns = "a1.sessionId,b1.login_name,a1.ip_address,a1.login_location,a1.device,a1.browser,a1.version,a1.status,a1.start_timestamp,a1.last_access_time";
            pager.tables = "sys_user_online a1,sys_user b1";

            pager.loadPageGrid("a1.last_access_time desc");
            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("在线用户查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //在线用户：强制退出
    @RequestMapping(value = "kickout")
    @RequiresPermissions("monitor:online:kickout")
    public String kickout(HttpServletRequest request) {
        String sessionIds = getParaValue(request, "sessionIds", "");
        try {
            if (StringUtils.isNotEmpty(sessionIds)) {
                List<String> list = StringUtils.stringToList(sessionIds);
                if (list.contains(ShiroUtils.getSessionId()))
                    return ret.getFailResult("当前登陆用户不允许强退");

                ShiroUtils.kickoutUser(sessionIds, "您已经被管理员强制下线，请重新登录！");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("在线用户退出", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

}
