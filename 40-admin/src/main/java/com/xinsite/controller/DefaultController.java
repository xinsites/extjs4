package com.xinsite.controller;

import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.mybatis.datasource.master.service.TbNoticeService;
import com.xinsite.mybatis.datasource.master.service.TbObjectAttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class DefaultController extends BaseController {
    @Autowired
    private TbNoticeService tbNoticeService;
    @Autowired
    private TbObjectAttService tbObjectAttService;

    @Value("${product.version}")
    private String version;

    @Value("${server.servlet.context-path}")
    private String context_path;

    /**
     * 登录成功去主页面
     */
    @GetMapping("main")
    public ModelAndView toMain(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.addObject("version", SysConfigCache.getFileLoadVersion());
        model.addObject("bottom_text", "© 2019 All Rights Reserved. <a>ZhangXiaXin</a>");
        model.addObject("fileType", Global.getConfig("config.upload_filetype"));
        model.addObject("fileMaxSize", Global.getMaxFileSizeMB());
        model.addObject("pageSize", SysConfigCache.getPageSize());
        model.addObject("productVersion", version);
        model.addObject("contextPath", context_path);
        try {
            LoginUser loginUser = UserUtils.getLoginUser();
            model.addObject("UserInfo", java.net.URLEncoder.encode(GsonUtils.toJson(loginUser), "utf-8"));
            model.addObject("UserName", loginUser.getUserName());
        } catch (UnsupportedEncodingException ex) {
            LogError.write("登录成功", LogEnum.Error, ex.toString());
        }
        model.setViewName("main");
        return model;
    }

    /**
     * 登录成功去主页面
     */
    @GetMapping("index")
    public String toIndex(Model model, HttpServletRequest request) {
        model.addAttribute("productVersion", version);
        model.addAttribute("version", SysConfigCache.getFileLoadVersion());
        return "index";
    }

    /**
     * 在线计算相关提醒信息
     */
    @ResponseBody
    @RequestMapping(value = "main/reminds", method = RequestMethod.POST)
    public String getReminds(HttpServletRequest request) {
        try {
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    @GetMapping("error/400")
    public String error_400(Model model) {
        return "error/400";
    }

    @GetMapping("error/403")
    public String error_403(Model model) {
        model.addAttribute("error", "权限不足");
//        return "redirect:error/403.html";
        return "error/403";
    }

    @GetMapping("error/500")
    public String error_500(Model model) {
        return "error/500";
    }

}
