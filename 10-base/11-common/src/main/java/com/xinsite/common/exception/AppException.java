package com.xinsite.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(AppException.class);

    private int code;
    private String error;

    public AppException() {
        error = "应用程序出错";
    }

    public AppException(String error) {
        this.error = error;
        printErrorLog(error);
    }

    private static void printErrorLog(String error) {
        log.error("==================AppException========================");
        log.error(error);
    }

    public AppException(int code, String error) {
        super(error);
        this.code = code;
        this.error = error;
        printErrorLog(error);
    }

    public AppException(int code) {
        this.code = code;
        this.error = "error";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
