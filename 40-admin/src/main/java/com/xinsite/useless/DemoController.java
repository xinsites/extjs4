//package com.xinsite.useless;
//
//import com.xinsite.build.test.test_util;
//import com.xinsite.core.base.BaseController;
//import com.xinsite.common.user.bean.LoginUser;
//import com.xinsite.core.bean.user.ShiroUser;
//import com.xinsite.core.constant.ShiroConstant;
//import com.xinsite.core.utils.Global;
//import com.xinsite.core.utils.ShiroUtils;
//import com.xinsite.dal.datasource.DataSourceHolder;
//import com.xinsite.dal.datasource.DynamicDataSource;
//import com.xinsite.dal.dbhelper.DBFunction;
//import com.xinsite.dal.enums.Enums_DBKey;
//import com.xinsite.uitl.service.ContextUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import java.sql.Connection;
//
//@Controller
//public class DemoController extends BaseController {
//
//    @Value("${product.productName}")
//    private String productName2;
//
//    @ResponseBody
//    @RequestMapping("/show")
//    public String print() {
//        return "productName3=" + productName2;
//    }
//
//    @RequestMapping("/doMain")
//    public String doMain() {
//        return "doMain";
//    }
//
//    @Autowired
//    private DynamicDataSource dataSource;

//    @Autowired
//    private SysUserService userService;
//
//    @Autowired
//    private com.xinsite.mybatis.datasource.viceone.service.SysUserService userService2;

//    @RequestMapping("main")
//    public ModelAndView main(HttpServletRequest request) {
//        ModelAndView mv = new ModelAndView();
//        mv.addObject("msg", Global.getConfig("xinsite.productName"));
//        mv.addObject("name", "this a msg from HelloWorldController===");
//        request.setAttribute("requestMessage", "springboot-request");
//        request.getSession().setAttribute("sessionMessage", "springboot-session");
//        request.getServletContext().setAttribute("applicationMessage", "springboot-application");
//        mv.setViewName("main");
//        return mv;
//    }
//
//    @RequestMapping("/hello")
//    public ModelAndView hello() {
//        ModelAndView mv = new ModelAndView();
//        String str = test_util.GetProps();
//
//        mv.addObject("msg", "this a msg from HelloWorldController===" + str);
//        mv.setViewName("doMain");
//        return mv;
//    }
//
//    @ResponseBody
//    @RequestMapping("/showinfo")
//    public String showinfo(HttpServletRequest request, String username) {
//        super.request = request;
//        String username2 = getParaString("username");
//        String password = getParaString("passwordww");
//        String username3 = request.getParameter("username");
//        String password2 = request.getParameter("password");
//        LoginUser loginUser = ShiroUtils.getLoginUser();
//        ShiroUser shiroUser = ShiroUtils.getShiroUser();
//        ShiroConstant.setMulti_max_session(2);
//        Connection conn = null;
//        try {
//            long maxId = DBFunction.GetMaxId("sys_user", "user_id");
//            conn = dataSource.getConnection();  //每次新的请求都是主数据库
//            DataSource.setDataSource(Enums_DBKey.viceone);
//            conn = dataSource.getConnection(); //改变后后面连接都是从库viceone
//            conn = dataSource.getConnection(); //数据库viceone
//            DataSource.setDataSource(Enums_DBKey.master);
//            conn = dataSource.getConnection(); //主数据库
//             DataSource.setDataSource(Enums_DBKey.viceone);
//                maxId=DBFunction.GetMaxId("wf_purchase","idleaf");
//
//                DataSource.setDataSource(Enums_DBKey.vicetwo);
//                maxId=DBFunction.GetMaxId("TB_User","user_id");
//                maxId=DBFunction.GetMaxId("TB_Role","role_id");
//            dataSource = ContextUtils.getBean(DynamicDataSource.class);
//            conn = dataSource.getConnection(); //主数据库
//            System.out.println("conn = " + conn);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "Shiro中Session是否超时=" + ShiroUtils.isSessionOut() + ",login_name=" + loginUser.getLoginName();
//    }

//        @ResponseBody
//        @RequestMapping("/showinfo")
//        public String showinfo(HttpServletRequest request, String username) {
//                super.request = request;
//                String username2 = getParaString("username");
//                String password = getParaString("passwordww");
//                String username3 = request.getParameter("username");
//                String password2 = request.getParameter("password");
//                LoginUser loginUser = ShiroUtils.getLoginUser();
//                ShiroUser shiroUser = ShiroUtils.getShiroUser();
//                ShiroConstant.setMulti_max_session(2);
//                Connection conn = null;
//                try {
//                long maxId = DBFunction.GetMaxId("sys_user", "user_id");
//
//                DataSource.setDataSource(Enums_DBKey.viceone);
//                maxId = DBFunction.GetMaxId("wf_purchase", "idleaf");
//
//                DataSource.setDataSource(Enums_DBKey.sqlserver);
//
//                maxId = DBFunction.GetMaxId("TB_Role", "role_id");
//
//                DataSource.setDataSource(Enums_DBKey.master);
//                SysUser user = userService.getSysUserById(1);
//
//                DataSource.setDataSource(Enums_DBKey.viceone);
//                com.xinsite.mybatis.datasource.viceone.entity.SysUser user2 = userService2.getSysUserById(4);
//                maxId = DBFunction.GetMaxId("sys_user", "user_id");
//
//                } catch (Exception e) {
//                e.printStackTrace();
//                }
//
//                return "Shiro中Session是否超时=" + ShiroUtils.isSessionOut() + ",login_name=" + loginUser.getLoginName();
//        }
//
//}
