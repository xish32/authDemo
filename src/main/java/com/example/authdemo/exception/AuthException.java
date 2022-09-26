package com.example.authdemo.exception;

import com.example.authdemo.constant.AuthResult;

public class AuthException extends Exception {
    /** 异常码 */
    private String code;
    /** 异常消息 */
    private String msg;
    /** 原始异常，可为空 */
    private Exception originException;

    public AuthException(String code, String msg, Exception ex) {
        this.code = code;
        this.msg = msg;
        this.originException = ex;
    }

    public AuthException(AuthResult result, Exception ex) {
        this.code = result.getRetCode();
        this.msg = result.getRetMsg();
        this.originException = ex;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Exception getOriginException() {
        return originException;
    }

    public void setOriginException(Exception originException) {
        this.originException = originException;
    }

    @Override
    public String toString() {
        return "AuthException{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", originException=" + originException +
                '}';
    }
}
