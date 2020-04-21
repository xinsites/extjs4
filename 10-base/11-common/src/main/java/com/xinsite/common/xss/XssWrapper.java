package com.xinsite.common.xss;

import com.xinsite.common.uitls.codec.EncodeUtils;
import com.xinsite.common.uitls.codec.JsoupUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * XSS过滤处理
 */
public class XssWrapper extends HttpServletRequestWrapper {
    //敏感信息是否直接过滤，true:直接过滤掉；false:进行特殊字符转换
    private boolean is_xss_filter = false;

    /**
     * @param request
     */
    public XssWrapper(HttpServletRequest request) {
        super(request);
    }

    private String clean(String value) {
        if (value == null) return value;
        if (is_xss_filter) return JsoupUtils.clean(value);
        else return EncodeUtils.xssFilter(value);
    }

    /**
     * 覆盖getParameter方法，将参数值做xss过滤。
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return clean(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                //values[i] = Jsoup.clean(values[i], Whitelist.relaxed()).trim();  //防xss攻击和过滤前后空格
                values[i] = clean(values[i]);
            }
        }
        return values;
    }

    /**
     * 覆盖getHeader方法，将参数值做xss过滤。
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return clean(value);
    }

}