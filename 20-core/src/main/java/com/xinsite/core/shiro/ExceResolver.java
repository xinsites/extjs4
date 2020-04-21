package com.xinsite.core.shiro;

import com.xinsite.common.enums.ErrorEnum;
import com.xinsite.common.uitls.web.http.ServletUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * spring mvc的统一异常处理类HandlerExceptionResolver
 * 主要解决setUnauthorizedUrl没有权限，无法跳转页面
 * 及Ajax请求时，没有权限的跳转
 */
public class ExceResolver implements HandlerExceptionResolver {
    // 权限认证失败地址
    @Value("${shiro.user.unauthorizedUrl}")
    private String unauthorizedUrl;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        //如果是shiro无权操作，因为shiro 在操作auno等一部分不进行转发至无权限url
        if (ex instanceof UnauthorizedException ||
                ex instanceof UnauthenticatedException) {
            if (ServletUtils.isAjaxRequest(request)) {
                String url = request.getRequestURI().toLowerCase();
                String msg = "操作权限不足.";
                if (url.endsWith("/grid") || url.endsWith("/tree")) msg = "无查询操作权限.";
                ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
                mv.addObject("success", false);
                mv.addObject("code", ErrorEnum.权限不足.getCode());
                //mv.addObject("error_url", "error/403");
                mv.addObject("error_msg", msg);
                return mv;
            } else {
                ModelAndView mv = new ModelAndView(unauthorizedUrl);
                return mv;
            }
        } else if (ServletUtils.isAjaxRequest(request)) {
            ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
            mv.addObject("success", "false");
            mv.addObject("error_msg", "操作出错");
            return mv;
        }
        ModelAndView mv = new ModelAndView("error/unusual");
        mv.addObject("errorMsg", ex.toString().replaceAll("\n", "<br/>"));
        return mv;
    }
}
