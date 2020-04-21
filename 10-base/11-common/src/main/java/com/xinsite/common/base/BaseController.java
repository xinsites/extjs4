package com.xinsite.common.base;


import com.xinsite.common.bean.Editors;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.response.ResultApi;
import com.xinsite.common.response.ReturnGrid;
import com.xinsite.common.response.ReturnMap;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.web.RequestUtils;
import com.xinsite.common.uitls.web.http.ServletUtils;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class BaseController {

    @Autowired
    protected ResultApi res; //自定义返回提示信息

    @Autowired
    protected ReturnMap ret;  //只返回成功失败，添加返回值

    @Autowired
    protected ReturnGrid retGrid;  //列表返回

    /**
     * 获取Post、Get中指定的参数
     */
    public <T> T getParaValue(HttpServletRequest request, String key, T defaultValue) {
        return RequestUtils.getParaValue(request, key, defaultValue);
    }

    /**
     * 获取参数，不允许全空格，全是空格为默认字符串
     */
    public String getParaNotSpace(HttpServletRequest request, String key, String defaultValue) {
        String value = RequestUtils.getParaValue(request, key, defaultValue);
        if (StringUtils.isEmpty(value.trim())) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取Post、Get中指定的参数（String型）
     */
    public String getParaValue(HttpServletRequest request, String key, String xtype, String defaultValue) {
        defaultValue = defaultValue.replace("{#Current_Date}", DateUtils.getDate());
        String value = RequestUtils.getParaString(request, key, defaultValue);
        return RequestUtils.getXTypeValue(xtype, value);
    }

    /**
     * 获取Post、Get中指定的参数（String型）
     */
    public String getParaValue(HttpServletRequest request, Editors editor) {
        String value = "";
        if (editor.isNull) {
            value = RequestUtils.getParaString(request, editor.field_name);   //这三种类型空值必须是null，数据库才能存储空
        } else {
            value = RequestUtils.getDataTypeValue(request, editor.field_name, editor.data_type);
        }
        return RequestUtils.getXTypeValue(editor.xtype, value);
    }

    /**
     * 获取Post、Get中指定的参数（有文件上传）
     */
    public <T> T getParaValue(List<FileItem> files, String key, T defaultValue) {
        return RequestUtils.getParaValue(files, key, defaultValue);
    }

    /**
     * 获取Post、Get中指定的参数
     */
    public String getParaString(String key, String... defaultValue) {
        HttpServletRequest request = ServletUtils.getRequest();
        String defaultVal = StringUtils.EMPTY;
        if (defaultValue != null) defaultVal = defaultValue[0];
        return RequestUtils.getParaValue(request, key, defaultVal);
    }

    /**
     * 是否是AppException异常
     */
    public boolean isAppException(Exception ex) {
        return ex.getCause() != null && ex.getCause() instanceof AppException;
    }

    /**
     * 是否是AppException异常
     */
    public String getExceptionString(Exception ex) {
        if (ex.getCause() != null && ex.getCause() instanceof AppException) {
            AppException app_ex = (AppException) ex.getCause();
            return ret.clear().addMap("code", app_ex.getCode()).addMap("msg", app_ex.getError()).getFailResult();
        }
        return StringUtils.EMPTY;
    }
}




