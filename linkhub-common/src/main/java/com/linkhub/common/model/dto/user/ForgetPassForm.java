package com.linkhub.common.model.dto.friend.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author CYY
 * @date 2022年11月27日 下午3:33
 * @description
 */
@Data
public class ForgetPassForm {
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱不合法")
    private String email;

    @NotNull(message = "新密码不能为空")
    private String password;

    @NotNull(message = "验证码不能为空")
    @Length(min = 4, max = 4)
    private String otp;
}
