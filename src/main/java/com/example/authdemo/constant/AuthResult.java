package com.example.authdemo.constant;

/**
 * 异常信息码和返回码
 * 主要的请求都有异常
 */
public enum AuthResult {
    SUCCESS("000000", "成功"),
    USER_NOTEXIST("000001", "用户不存在"),
    ROLE_NOTEXIST("000002", "角色不存在"),
    INVALID_PASSWORD("000003", "密码不正确"),
    INVALID_TOKEN("000004", "TOKEN不正确或已过期"),

    USER_ALREADY_EXIST("000011", "用户已存在"),
    ROLE_ALREADY_EXIST("000012", "角色已存在"),

    UNKONWN_ERROR("999999", "未知错误");

    /** 异常码 */
    private String retCode = null;
    /** 异常信息 */
    private String retMsg = null;

    private AuthResult(String retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
