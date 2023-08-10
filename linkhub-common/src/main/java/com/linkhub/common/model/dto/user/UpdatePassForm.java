package com.linkhub.common.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author CYY
 * @date 2022年11月27日 下午3:33
 * @description
 */
@Data
public class UpdatePassForm {
    @NotNull(message = "旧密码不能为空")
    private String oldPassword;

    @NotNull(message = "新密码不能为空")
    private String newPassword;

}
