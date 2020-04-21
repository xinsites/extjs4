package com.xinsite.common.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.enums.ErrorEnum;
import com.xinsite.common.uitls.extjs.JsonForm;
import com.xinsite.common.uitls.gson.GsonUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Ajax请求返回的任意Json
 *
 * @author ZhangXiaXin
 */
@Component
public class ReturnMap {
    private Map<String, Object> resultMap;

    public ReturnMap() {
        this.resultMap = new HashMap<String, Object>();
    }

    public ReturnMap addMap(String key, Object value) {
        resultMap.put(key, value);
        return this;
    }

    public ReturnMap clear() {
        resultMap.clear();
        return this;
    }

    public String getAjaxJson(String key, Object value) {
        resultMap.put(key, value);
        return GsonUtils.toJson(resultMap);
    }


    //失败，不带消息
    public String getFailResult() {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.put("success", false);
        return GsonUtils.toJson(resultMap);
    }

    //成功，不带消息
    public String getSuccessResult() {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.put("success", true);
        return GsonUtils.toJson(resultMap);
    }

    //失败，附带消息
    public String getFailResult(String message) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.clear();
        resultMap.put("success", false);
        resultMap.put("msg", message);
        return GsonUtils.toJson(resultMap);
    }

    //失败，附带消息
    public String getFailResult(int code, String message) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.clear();
        resultMap.put("success", false);
        resultMap.put("code", code);
        resultMap.put("msg", message);
        return GsonUtils.toJson(resultMap);
    }

    public String getFailResult(String key, Object value) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.clear();
        resultMap.put("success", false);
        resultMap.put(key, value);
        return GsonUtils.toJson(resultMap);
    }

    //成功，自定义消息
    public String getSuccessResult(String message) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.clear();
        resultMap.put("success", true);
        resultMap.put("msg", message);
        return GsonUtils.toJson(resultMap);
    }

    //成功，自定义返回消息
    public String getSuccessResult(String key, Object value) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.clear();
        resultMap.put("success", true);
        resultMap.put(key, value);
        return GsonUtils.toJson(resultMap);
    }

    //成功，自定义返回消息
    public String getSuccessResult(long id) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        resultMap.clear();
        resultMap.put("success", true);
        resultMap.put("id", id);
        return GsonUtils.toJson(resultMap);
    }

    //成功，返回Form表单信息
    public String getFormJson(JsonArray array, String table_name) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        if (array != null) {
            StringBuilder json = new StringBuilder("{success:true,");
            json.append(JsonForm.getFormJson(array, table_name));
            json.append("}");
            return json.toString();
        } else {
            return "{success:false}";
        }
    }

    //成功，返回Form表单信息
    public String getFormJson(JsonObject jsonObject, String table_name) {
        if (jsonObject != null) {
            StringBuilder json = new StringBuilder("{success:true,");
            json.append(JsonForm.getFormJson(jsonObject, table_name));
            json.append("}");
            return json.toString();
        } else {
            return "{success:false}";
        }
    }

    //成功，返回Form表单信息
    public String getFormJson(Map<String, JsonArray> hts) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        if (hts != null && hts.size() > 0) {
            StringBuilder json = new StringBuilder("{success:true");
            for (String key : hts.keySet()) {
                json.append(",").append(JsonForm.getFormJson(hts.get(key), key));
            }
            json.append("}");
            return json.toString();
        } else {
            return "{success:false}";
        }
    }

    //成功，返回Form表单信息
    public String getFormJson(Map<String, JsonArray> hts, long idleaf) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        if (hts != null && hts.size() > 0) {
            StringBuilder json = new StringBuilder("{success:true");
            json.append(",idleaf:").append(idleaf);
            for (String key : hts.keySet()) {
                json.append(",").append(JsonForm.getFormJson(hts.get(key), key));
            }
            json.append("}");
            return json.toString();
        } else {
            return "{success:false}";
        }
    }

    //成功，返回Form表单信息
    public String getArrayJson(JsonArray array, String table_name) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        if (array != null) {
            StringBuilder json = new StringBuilder("{success:true,");
            json.append(table_name).append(":");
            json.append(GsonUtils.toJson(array));
            json.append("}");
            return json.toString();
        } else {
            return "{success:false}";
        }
    }

    //Session超时，返回的信息
    public static String getSessionOut() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", false);
        resultMap.put("code", ErrorEnum.Session超时.getCode());
        resultMap.put("relogin", true);
        resultMap.put("error_msg", "Session超时,请重新登录！");
        return GsonUtils.toJson(resultMap);
    }

    //不允许重复登录，返回的信息
    public static String getRepeatLogin() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", false);
        resultMap.put("code", ErrorEnum.重复登录.getCode());
        resultMap.put("relogin", true);
        resultMap.put("error_msg", "该账号已经在其他地方登录，请重新登录！");
        return GsonUtils.toJson(resultMap);
    }

    //重新登录，返回的信息
    public static String getReLoginFail(int code, String msg) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", false);
        resultMap.put("code", code);
        resultMap.put("relogin", true);
        resultMap.put("error_msg", msg);
        return GsonUtils.toJson(resultMap);
    }

    //重新登录，返回的信息
    public static String getReLoginFail(String msg) {
        return getReLoginFail(0, msg);
    }
}
