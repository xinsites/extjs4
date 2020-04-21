package com.xinsite.interceptor;

import com.xinsite.common.uitls.lang.ByteUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.TimeUtils;
import com.xinsite.common.uitls.network.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

/**
 * 日志拦截器
 */
public class LogInterceptor implements HandlerInterceptor {
    private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("LogInterceptor StartTime");
    private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long beginTime = System.currentTimeMillis();// 1、开始时间
        startTimeThreadLocal.set(beginTime);		// 线程绑定变量（该数据只有当前请求的线程可见）
        if (logger.isDebugEnabled()){
            logger.debug("开始计时: {}  URI: {}  IP: {}", new SimpleDateFormat("hh:mm:ss.SSS").format(beginTime), request.getRequestURI(), IpUtils.getRemoteAddr(request));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null){
            logger.info("ViewName: " + modelAndView.getViewName() + " <<<<<<<<< " + request.getRequestURI() + " >>>>>>>>> " + handler);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long beginTime = startTimeThreadLocal.get();// 得到线程绑定的局部变量（开始时间）
        long endTime = System.currentTimeMillis(); 	// 2、结束时间
        long executeTime = endTime - beginTime;	// 3、获取执行时间
        startTimeThreadLocal.remove(); // 用完之后销毁线程变量数据

        // 保存日志
//        LogUtils.saveLog(UserUtils.getUser(), request, handler, ex, null, null, executeTime);

        // 打印JVM信息。
        if (logger.isDebugEnabled()){
            Runtime runtime = Runtime.getRuntime();
            logger.debug("计时结束: {}  用时: {}  URI: {}  总内存: {}  已用内存: {}",
                    DateUtils.formatDate(endTime, "hh:mm:ss.SSS"), TimeUtils.formatDateAgo(executeTime), request.getRequestURI(),
                    ByteUtils.formatByteSize(runtime.totalMemory()), ByteUtils.formatByteSize(runtime.totalMemory()-runtime.freeMemory()));
        }

    }

}
