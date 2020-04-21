package com.xinsite.controller.monitor;

import com.xinsite.common.base.BaseController;
import com.xinsite.core.utils.web.domain.Server;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * create by zhangxiaxin
 * create time: 2019-11-22
 * object name: 服务器监控
 */

@Controller
@RequestMapping(value = "monitor/server")
public class ServerController extends BaseController {

    /**
     * 登录成功去主页面
     */
    @GetMapping("index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.addObject("server", Server.getServerInfo());
        model.setViewName("monitor/server");
        return model;
    }

    @RequestMapping(value = "refresh")
    public String refresh(Model model, HttpServletRequest request) {
        boolean reload = getParaValue(request, "reload", false);
        String fragment = getParaValue(request, "fragment", "sysFile");
        Server.refreshServerInfo(reload);

        model.addAttribute("server", Server.getServerInfo());
        return "monitor/server::" + fragment;
    }

}
