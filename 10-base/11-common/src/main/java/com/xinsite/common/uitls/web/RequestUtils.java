package com.xinsite.common.uitls.web;

import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class RequestUtils {
    /**
     * 获取Post、Get中指定的参数（String型）
     */
    public static String getParaString(HttpServletRequest request, String key, String... defaultValue) {
        Object text = request.getParameterMap().get(key);
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            text = multipartRequest.getParameter(key);
        }
        if (text != null) {
            if (text instanceof String[]) {
                String[] value = (String[]) text;
                value = StringUtils.arrayToRepeat(value);
                return StringUtils.join(value, ",");
            } else {
                return text.toString();
            }
        }
        if (defaultValue.length > 0) return defaultValue[0];
        return null;
    }


    /**
     * 根据DataType获取Value
     */
    public static String getDataTypeValue(HttpServletRequest request, String key, String data_type) {
        switch (data_type) {  //数据库类型
            case "int":
            case "numeric":
                return RequestUtils.getParaString(request, key, "0");
            case "datetime":
                return RequestUtils.getParaString(request, key, DateUtils.getDate("yyyy-MM-dd"));
            default:
                return RequestUtils.getParaString(request, key, "");
        }
    }

    /**
     * 获取Post、Get中指定的参数（int型）
     */
    public static <T> T getParaValue(HttpServletRequest request, String key, T defaultValue) {
        //String value = request.getParameter(key);  //转义字符
        String value = RequestUtils.getParaString(request, key, "");
        if (StringUtils.isEmpty(value) && "null".equals(defaultValue)) return null;
        return ValueUtils.tryParse(value, defaultValue);
    }

    /**
     * 获取Post、Get中指定的参数（有文件上传）
     */
    public static <T> T getParaValue(List<FileItem> files, String key, T defaultValue) {
        for (FileItem item : files) {
            if (item.isFormField()) {
                if (key.equalsIgnoreCase(item.getFieldName())) {
                    try {
                        String value = item.getString("UTF-8");
                        if (StringUtils.isEmpty(value) && "null".equals(defaultValue)) return null;
                        return ValueUtils.tryParse(value, defaultValue);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return defaultValue;
    }

    /**
     * 根据XType获取Value
     */
    public static String getXTypeValue(String xtype, String Value) {
        if ("new Date()".equalsIgnoreCase(Value)) {
            switch (xtype) {  //页面输入框类型
                case "my97date":
                case "datefield":
                    Value = DateUtils.getDate("yyyy-MM-dd");
                    break;
                case "datetimefield":
                    Value = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
                    break;
                case "timefield":
                    Value = DateUtils.getDate("HH:mm:ss");
                    break;
            }
        }
        return Value;
    }

}
