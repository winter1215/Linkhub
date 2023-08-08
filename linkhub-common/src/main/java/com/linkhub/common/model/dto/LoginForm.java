package com.linkhub.common.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author CYY
 * @date 2022年11月27日 下午3:00
 * @description
 */
@Data
public class LoginForm {
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    private String password;
}
