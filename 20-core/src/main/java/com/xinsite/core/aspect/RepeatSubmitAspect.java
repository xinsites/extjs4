package com.xinsite.core.aspect;

import com.xinsite.common.annotation.NoRepeatSubmit;
import com.xinsite.common.response.ReturnMap;
import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.codec.Md5Utils;
import com.xinsite.common.uitls.json.JSON;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 判断就否重复提交，有两种注解方式
 * 一种是token方式，提交token进行判断
 * 一种是以提交值+锁定时间判断
 */
@Aspect
@Component
public class RepeatSubmitAspect {
    private static final Logger log = LoggerFactory.getLogger(RepeatSubmitAspect.class);

    private final String SESSION_KEY = "repeat_token";

    public final String SUBMIT_PARAMS = "submit_params";

    public final String SUBMIT_TIME = "submit_time";

    @Autowired
    private ReturnMap ret;  //添加返回值

    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoRepeatSubmit noRepeatSubmit) {
    }

    @Around("pointCut(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = getKey(ShiroUtils.getSessionId(), request.getServletPath());
        String msg_repeat = MessageUtils.message("user.repeat.submit");
        HttpSession session = request.getSession();
        if (noRepeatSubmit.params()) {
            int lockSeconds = noRepeatSubmit.lockTime();
            String nowParams = JSON.marshal(request.getParameterMap());
            if (isRepeatSubmit(session, token, nowParams, lockSeconds)) {
                return ret.getFailResult(msg_repeat);
            }
        } else {
            if (noRepeatSubmit.token()) {
                session.setAttribute(SESSION_KEY, token);  //在服务器使用session保存token(令牌)
            } else if (noRepeatSubmit.submit()) {
                if (isRepeatSubmit(request)) {
                    return ret.getFailResult(msg_repeat);
                }
            }
        }
        // 获取锁成功
        Object result;
        try {
            result = pjp.proceed();  // 执行进程
        } finally {
            if (noRepeatSubmit.submit()) {
                session.removeAttribute(SESSION_KEY);
            } else if (noRepeatSubmit.params()) {
                session.removeAttribute(SESSION_KEY);
                session.removeAttribute(SUBMIT_PARAMS);
                session.removeAttribute(SUBMIT_TIME);
            }
        }
        return result;
    }

    private String getKey(String path, String sessionId) {
        return Md5Utils.md5(path + sessionId) + path;
    }

    /**
     * 根据锁定时间判断是否重复提交
     *
     * @return true 用户重复提交了表单,false 用户没有重复提交表单
     */
    private boolean isRepeatSubmit(HttpSession session, String token, String nowParams, int lockSeconds) {
        //取出存储在Session中的token
        String server_token = (String) session.getAttribute(SESSION_KEY);
        String pre_params = (String) session.getAttribute(SUBMIT_PARAMS);
        Long pre_time = (Long) session.getAttribute(SUBMIT_TIME);
        if (server_token != null && pre_params != null && pre_time != null) {
            if (token.equals(server_token)) {
                long time1 = System.currentTimeMillis() - pre_time;
                if (time1 <= lockSeconds * 1000 && nowParams.equals(pre_params)) {
                    return true;
                }
            }
        }
        session.setAttribute(SESSION_KEY, token);
        session.setAttribute(SUBMIT_PARAMS, nowParams);
        session.setAttribute(SUBMIT_TIME, System.currentTimeMillis());
        return false;
    }

    /**
     * 判断客户端提交上来的令牌和服务器端生成的令牌是否一致
     *
     * @return true 用户重复提交了表单,false 用户没有重复提交表单
     */
    private boolean isRepeatSubmit(HttpServletRequest request) {
        String client_token = request.getParameter(SESSION_KEY);
        //1、如果用户提交的表单数据中没有token，则用户是重复提交了表单
        if (client_token == null) return true;

        //取出存储在Session中的token
        String server_token = (String) request.getSession().getAttribute(SESSION_KEY);
        //2、如果当前用户的Session中不存在Token(令牌)，则用户是重复提交了表单
        if (server_token == null) return true;

        //3、存储在Session中的Token(令牌)与表单提交的Token(令牌)不同，则用户是重复提交了表单
        if (!client_token.equals(server_token)) return true;

        return false;
    }

}
