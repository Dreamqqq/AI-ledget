package com.jizhang.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    
    PARAM_ERROR(400, "参数错误"),
    PARAMS_ERROR(400, "参数错误"),
    SYSTEM_ERROR(500, "系统错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PHONE_FORMAT_ERROR(1003, "手机号格式错误"),
    PASSWORD_ERROR(1004, "密码错误"),
    INVALID_PASSWORD(1005, "密码错误"),
    
    TRANSACTION_NOT_FOUND(2001, "账单不存在"),
    
    TOKEN_INVALID(3001, "Token无效"),
    TOKEN_EXPIRED(3002, "Token已过期");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
