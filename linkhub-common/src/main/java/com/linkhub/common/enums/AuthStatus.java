package com.linkhub.common.enums;

public enum AuthStatus {
    OK(0,"正常"),
    USER_RESTRICT(1,"禁言限制,不能评论与发帖(admin)"),
    USER_BANNED(2,"封禁,不能登录(admin)"),
    POST_RESTRICT(3,"禁言限制,禁止评论(admin)"),
    POST_BANNED(4,"封禁(admin)");
    private final int code;
    private final String info;

    AuthStatus(int code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
