package com.xinsite.common.response;

import com.xinsite.common.enums.ApiEnum;

/**
 * Ajax请求返回的实体
 *
 * @author ZhangXiaXin
 */
public class ReturnObj {

    //常用返回信息
    public enum Response {

        /**
         * app_信息成功返回
         */
        API_SUCCESS(ApiEnum.信息成功返回),

        /**
         * app_参数签名错误
         */
        API_SIGNFAIL(ApiEnum.参数签名错误),

        /**
         * app_参数不足
         */
        API_PARAM_FAIL(ApiEnum.参数不足);

        private String msg;
        private int code;

        private Response(ApiEnum api) {
            this.msg = api.name();
            this.code = api.getCode();
        }

        public String getMsg() {
            return msg;
        }

        public int getCode() {
            return code;
        }
    }

    private boolean success;
    private String msg;
    private int code;
    private Object data;

    public ReturnObj() {
    }

    public ReturnObj(boolean success, String msg) {
        super();
        this.success = success;
        this.msg = msg;
    }

    public ReturnObj(Response response) {
        this.success = response.getCode() == 0;
        this.code = response.getCode();
        this.msg = response.getMsg();
    }

    public ReturnObj(Response response, Object data) {
        this(response);
        this.data = data;
    }

    public ReturnObj(ApiEnum api) {
        this.success = api.getCode() == 0;
        this.code = api.getCode();
        this.msg = api.name();
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }


    public Object getData() {
        return data;
    }


    public void setData(Object data) {
        this.data = data;
    }


}
