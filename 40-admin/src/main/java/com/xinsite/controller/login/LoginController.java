package com.xinsite.controller.login;

import com.xinsite.common.constant.MyConstant;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.codec.RSAUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.network.IpUtils;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.common.base.BaseController;
import com.xinsite.core.utils.ValidCodeUtils;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.mybatis.helper.DataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-08-02
 * object name: 用户登录操作
 */

@Controller
public class LoginController extends BaseController {
    // Session超时时间，单位为毫秒（默认30分钟）
    @Value("${shiro.session.timeout}")
    private int timeout;

    @Autowired
    private UserCacheService userCacheService;

    /**
     * 去注册和登录的页面
     */
    @GetMapping("/")
    public String toAllLogin() {
        if (ShiroUtils.isRemembered()) return "redirect:main";
        return "redirect:login.html";
    }

    /**
     * 进入登录页面
     */
    @GetMapping("login")
    public String toLogin() {
        return "redirect:login.html";
    }

    /**
     * shiro认证登录
     */
    @ResponseBody
    @PostMapping("login")
    public String ajaxLogin(HttpServletRequest request, String username, String password, String validcode, Boolean rememberme) {
        String code_msg = ValidCodeUtils.validate(request, validcode);
        if (!StringUtils.isEmpty(code_msg)) {
            return ret.getFailResult(code_msg);
        }
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ret.getFailResult("用户名或密码为空...");
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            DataSource.clearDataSource();
            token.setRememberMe(rememberme);
            subject.login(token);
            userCacheService.syncFlag();
            LogUtils.addLogByLogin("登录", username, IpUtils.getRemoteAddr(request));  //登录日志
            return res.getSuccessResult();
        } catch (AuthenticationException e) {
            String msg = "用户或密码错误...";
            if (StringUtils.isNotEmpty(e.getMessage())) msg = e.getMessage();
            if (msg.length() > 40) msg = "用户或密码错误...";
            ValidCodeUtils.setValidateCode();  //请求验证码更换
            LogUtils.addLogByLogin("登录", msg, username, IpUtils.getRemoteAddr(request));
            return ret.getFailResult(msg);
        }
    }

    /**
     * RSA加密获取公钥
     */
    @ResponseBody
    @PostMapping("publicKey")
    public String publicKey(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            Map<Integer, String> keyMap = RSAUtils.randomKeyPair();
            if (keyMap.size() == 2) {
                session.setAttribute(MyConstant.SESSION_RSA_PRIVATE_KEY, keyMap.get(1)); //随机生成的私钥
                return res.getSuccessResult(keyMap.get(0)); //随机生成的公钥
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

}
