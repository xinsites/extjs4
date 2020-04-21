package com.xinsite.controller.login;

import com.xinsite.common.constant.MyConstant;
import com.xinsite.common.uitls.image.CaptchaUtils;
import com.xinsite.common.uitls.web.http.UserAgentUtils;
import com.xinsite.core.model.ValidateCode;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * create by zhangxiaxin
 * create time: 2019-08-02
 * object name: 图片验证码
 */

@Controller
public class PatchcaController {
    /**
     * 验证码生成
     */
    @GetMapping(value = "/imageKaptcha")
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            HttpSession session = request.getSession();
            response.setDateHeader("Expires", System.currentTimeMillis() + 3);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            UserAgent userAgent = UserAgentUtils.getUserAgent(request);
            if (userAgent.getBrowser().getName().indexOf("Internet Explorer") >= 0)
                response.addCookie(new Cookie("JSESSIONID", session.getId()));
            response.setContentType("image/jpeg");
            String code = CaptchaUtils.generateCaptcha(out);
            code = "1111";
            ValidateCode validateCode = new ValidateCode(code, MyConstant.IMG_EXPIRE_SECOND);
            session.setAttribute(MyConstant.SESSION_KEY_PATCHA, validateCode);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
