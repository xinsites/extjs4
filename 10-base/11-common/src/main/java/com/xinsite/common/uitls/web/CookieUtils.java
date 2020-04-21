package com.xinsite.common.uitls.web;

import com.xinsite.common.uitls.codec.EncodeUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 *
 * @author www.xinsite.vip
 * @version 2013-01-15
 */
public class CookieUtils {

    /**
     * 设置 Cookie（生成时间为1天）
     *
     * @param name  名称
     * @param value 值
     */
    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, 60 * 60 * 24);
    }

    /**
     * 设置 Cookie
     *
     * @param name  名称
     * @param value 值
     * @param path  路径
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path) {
        setCookie(response, name, value, path, 60 * 60 * 24);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        setCookie(response, name, value, "/", maxAge);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）
     * @param path   路径
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
        if (StringUtils.isNotBlank(name)) {
            name = EncodeUtils.encodeUrl(name);
            value = EncodeUtils.encodeUrl(value);
            Cookie cookie = new Cookie(name, null);
            cookie.setPath(path);
            cookie.setMaxAge(maxAge);
            cookie.setValue(value);
            response.addCookie(cookie);
        }
    }

    /**
     * 获得指定Cookie的值
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        return getCookie(request, null, name, false);
    }

    /**
     * 获得指定Cookie的值，并删除。
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        return getCookie(request, response, name, false);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param name     名字
     * @param isRemove 是否移除
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name, boolean isRemove) {
        return getCookie(request, response, name, "/", false);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param name     名字
     * @param isRemove 是否移除
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name, String path, boolean isRemove) {
        String value = null;
        if (StringUtils.isNotBlank(name)) {
            name = EncodeUtils.encodeUrl(name);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name)) {
                        value = EncodeUtils.decodeUrl(cookie.getValue());
                        if (isRemove && response != null) {
                            cookie.setPath(path);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }
            }
        }
        return value;
    }

    /**
     * 设置 Cookie的maxAge
     */
    public static void setCookie(String name, String value, int maxAge) {
        if (StringUtils.isNotBlank(name)) {
            HttpServletRequest request = ServletUtils.getRequest();
            HttpServletResponse response = ServletUtils.getResponse();
            boolean is_exists = false;
            String path = StringUtils.EMPTY;
            if (request != null) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (!StringUtils.isEmpty(path)) path = cookie.getPath();
                        if (cookie.getName().equals(name)) {
                            cookie.setMaxAge(maxAge);
                            is_exists = true;
                            if (response != null) response.addCookie(cookie);
                        }
                    }
                }
            }
            if (!is_exists && response != null) {
                setCookie(response, name, value, path, maxAge);
            }
        }
    }
}
