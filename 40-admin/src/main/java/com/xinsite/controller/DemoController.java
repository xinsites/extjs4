package com.xinsite.controller;

import com.xinsite.common.base.BaseController;
import com.xinsite.common.uitls.Global;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.mybatis.datasource.master.entity.SysUser;
import com.xinsite.mybatis.datasource.master.service.SysUserService;
import com.xinsite.mybatis.enums.Enums_DBKey;
import com.xinsite.mybatis.helper.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;

@Controller
@RequestMapping(value = "test")
public class DemoController extends BaseController {

    @Value("${product.productName}")
    private String productName2;

    @ResponseBody
    @RequestMapping("/show")
    public String print() {
        return "productName3=" + productName2;
    }

    @RequestMapping("/doMain")
    public String doMain() {
        return "doMain";
    }

    @Autowired
    private SysUserService userService;

    @Autowired
    private com.xinsite.mybatis.datasource.viceone.service.SysUserService userService2;

    @RequestMapping(value = "/Thymeleaf", method = RequestMethod.GET)
    public String Thymeleaf(Model model){
        model.addAttribute("uid","123456789");
        model.addAttribute("name","Jerry");
        return "main";
    }

    @RequestMapping("main")
    public ModelAndView main(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("msg", Global.getConfig("product.productName"));
        mv.addObject("name", "this a msg from HelloWorldController===");
        request.setAttribute("requestMessage", "springboot-request");
        request.getSession().setAttribute("sessionMessage", "springboot-session");
        request.getServletContext().setAttribute("applicationMessage", "springboot-application");
        mv.setViewName("main");
        return mv;
    }

    @RequestMapping("/hello")
    public ModelAndView hello() {
        ModelAndView mv = new ModelAndView();
        String str = "";

        mv.addObject("msg", "this a msg from HelloWorldController===" + str);
        mv.setViewName("doMain");
        return mv;
    }

    @ResponseBody
    @RequestMapping("/showinfo")
    public String showinfo(HttpServletRequest request, String user_name) {
        String username2 = getParaString("user_name");
        String password = getParaString("passwordww");
        String username3 = request.getParameter("user_name");
        String password2 = request.getParameter("password");
        LoginUser loginUser = UserUtils.getLoginUser();
        Connection conn = null;
        try {
            long maxId = DBFunction.getMaxId("sys_user", "user_id");

            DataSource.setDataSource(Enums_DBKey.viceone);
            maxId = DBFunction.getMaxId("wf_purchase", "idleaf");

            DataSource.setDataSource(Enums_DBKey.sqlserver);

            maxId = DBFunction.getMaxId("TB_Role", "role_id");

            DataSource.setDataSource(Enums_DBKey.master);
            SysUser user = userService.getSysUserById(1);

            DataSource.setDataSource(Enums_DBKey.viceone);
            com.xinsite.mybatis.datasource.viceone.entity.SysUser user2 = userService2.getSysUserById(4);
            maxId = DBFunction.getMaxId("sys_user", "user_id");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Shiro中Session是否超时=" + ShiroUtils.isSessionOut() + ",login_name=" + loginUser.getLoginName();
    }

}
