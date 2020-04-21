package com.xinsite.core.utils;

import com.xinsite.common.constant.MyConstant;
import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.model.ValidateCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ValidCodeUtils {
    /**
     * 验证码验证
     */
    public static String validate(HttpServletRequest request, String validCode) {
        try {
            HttpSession session = request.getSession();
            ValidateCode validateCode = (ValidateCode) session.getAttribute(MyConstant.SESSION_KEY_PATCHA);
            if (validateCode != null && validCode.equalsIgnoreCase(validateCode.getCode())) {
                return validateCode.isExpried() ? "验证码过期，请刷新验证码..." : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "验证码错误...";
    }

    /**
     * 获取页面生成的验证码
     */
    public static String getValidateCode() {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                HttpSession session = request.getSession();
                ValidateCode validateCode = (ValidateCode) session.getAttribute(MyConstant.SESSION_KEY_PATCHA);
                if (validateCode != null) {
                    return validateCode.isExpried() ? "" : validateCode.getCode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 服务端重新生成验证码，让页面重新刷新再次生效
     */
    public static void setValidateCode() {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                HttpSession session = request.getSession();
                String code = IdGenerate.buildUUID().substring(0, 3);
                ValidateCode validateCode = new ValidateCode(code, MyConstant.IMG_EXPIRE_SECOND);
                session.setAttribute(MyConstant.SESSION_KEY_PATCHA, validateCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取RSA加密私钥
     */
    public static String getPrivateKey() {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                HttpSession session = request.getSession();
                return (String) session.getAttribute(MyConstant.SESSION_RSA_PRIVATE_KEY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
