package com.xinsite.interceptor;

import com.xinsite.core.model.user.LoginUser;
import com.xinsite.common.constant.MyConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录检查拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 预处理回调方法，实现处理器的预处理（如检查登陆），第三个参数为响应的处理器，自定义Controller
     * 返回值：true表示继续流程（如调用下一个拦截器或处理器）；false表示流程中断（如登录检查失败），不会继续调用其他的拦截器或处理器，此时我们需要通过response来产生响应；
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginUser user = (LoginUser) request.getSession().getAttribute(MyConstant.SESSION_KEY_USER);
        if (user == null) { //未登录，给出错误信息
            request.setAttribute("msg", "未登录或者已过期，请再登录");
            //request.getRequestDispatcher("/login.html").forward(request, response);
            request.getServletContext().getRequestDispatcher("/").forward(request, response);  //绝对路径
//            if (contextPath == null) contextPath = "";
//            response.sendRedirect(contextPath + "/login.html");  //改变地址栏的URL
            return false;
        } else {
            return true;
        }
    }

    /**
     * 后处理回调方法，实现处理器的后处理（但在渲染视图之前），此时我们可以通过modelAndView（模型和视图对象）对模型数据进行处理或对视图进行处理，modelAndView也可能为null。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 整个请求处理完毕回调方法，即在视图渲染完毕时回调，如性能监控中我们可以在此记录结束时间并输出消耗时间，还可以进行一些资源清理，类似于try-catch-finally中的finally，但仅调用处理器执行链中
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
