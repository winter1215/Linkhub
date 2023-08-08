package com.linkhub.common.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * 注册用户的 VO 对象
 * @author winter
 * @create 2022-11-21 下午10:38
 */
@Data
public class RegisterUser {
    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;

    @NotNull(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "学号不能为空")
    private String studentNo; // 学号

    @NotNull
    private String realName; // 真名

    @NotNull
    private Integer gender; // 性别

    @NotNull
    @Email(message = "请输入合法的邮箱")
    private String mail;

    @NotNull
    private String code; // 验证码
}
