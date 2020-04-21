package com.xinsite.common.response;

import com.xinsite.common.enums.ApiEnum;
import com.xinsite.common.uitls.gson.GsonUtils;
import org.springframework.stereotype.Component;

/**
 * 接口信息生成工具
 */
@Component
public class ResultApi {

    //成功
    public String getSuccessResult() {
        return GsonUtils.toJson(new ReturnObj(ReturnObj.Response.API_SUCCESS));
    }

    //成功，附带额外数据
    public String getSuccessResult(Object data) {
        ReturnObj returnMsg = new ReturnObj(ReturnObj.Response.API_SUCCESS);
        returnMsg.setData(data);
        return GsonUtils.toJson(returnMsg);
    }

    //成功，自定义消息及数据
    public String getSuccessResult(String message, Object data) {
        ReturnObj returnMsg = new ReturnObj(ReturnObj.Response.API_SUCCESS);
        returnMsg.setMsg(message);
        returnMsg.setData(data);
        return GsonUtils.toJson(returnMsg);
    }

    //失败，参数签名错误
    public String getSignFailResult() {
        ReturnObj returnMsg = new ReturnObj(ReturnObj.Response.API_SIGNFAIL);
        return GsonUtils.toJson(returnMsg);
    }

    //失败，参数不足
    public String getParamFailResult() {
        ReturnObj returnMsg = new ReturnObj(ReturnObj.Response.API_PARAM_FAIL);
        return GsonUtils.toJson(returnMsg);
    }

    //失败，自定义消息及数据
    public String getFailResult(ApiEnum api) {
        ReturnObj returnMsg = new ReturnObj(api);
        return GsonUtils.toJson(returnMsg);
    }

    //失败，自定义消息及数据
    public String getFailResult(String message, Object data) {
        ReturnObj returnMsg = new ReturnObj(ApiEnum.通用错误);
        returnMsg.setMsg(message);
        returnMsg.setData(data);
        return GsonUtils.toJson(returnMsg);
    }

    public String AjaxJson(Object obj) {
        return GsonUtils.toJson(obj);
    }


}
