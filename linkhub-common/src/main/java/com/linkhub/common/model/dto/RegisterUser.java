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
    @NotNull(message = "密码不能为空")
    private String password;

    @NotNull(message = "nickname 不能为空")
    private String nickname;

    @NotNull
    @Email(message = "请输入合法的邮箱")
    private String email;

    private String code; // 验证码
}
