package com.linkhub.common.enums;

/**
 * 错误码的规范
 * @author: winter
 * @date: 2022/11/24 上午10:48
 */
public enum ErrorCode {

    SUCCESS("00000", "success"),
    CLIENT_ERROR("A0001", "客户端错误"),
    REGISTER_ERROR("A0100", "注册错误"),
    PARAMS_ERROR("A0200", "请求参数错误"),
    NOT_LOGIN_ERROR("A0300", "未登录"),
    NO_AUTH_ERROR("A0301", "无权限"),
    FORBIDDEN_ERROR("A0302", "禁止访问"),
    USERNAME_PASSWORD_ERROR("A0303", "用户名或密码错误"),
    AUTHENTICATION_ERROR("A0304","token认证失败！"),
    ADMIN_CODE_ERROR("A305","验证码错误！"),
    PASSWORD_ERROR("A0306","密码错误!"),
    NOT_FOUND_ERROR("A0400", "请求数据不存在"),
    SYSTEM_ERROR("B0000", "系统内部异常"),
    OPERATION_ERROR("B0001", "操作失败");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}