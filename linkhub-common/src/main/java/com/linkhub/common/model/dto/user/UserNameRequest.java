package com.linkhub.common.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author LinkCastling
 * @date 2023/8/11 15:44
 */
@Data
public class UserNameRequest implements Serializable {
    @NotNull(message = "昵称不能为空")
    private String nickname;
}
