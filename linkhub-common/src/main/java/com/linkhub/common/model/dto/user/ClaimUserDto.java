package com.linkhub.common.model.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/10 16:58
 */
@Data
public class ClaimUserDto {
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱不合法")
    private String email;

    @NotNull(message = "新密码不能为空")
    private String password;

    @NotNull(message = "请求参数错误")
    private String userId;
}
