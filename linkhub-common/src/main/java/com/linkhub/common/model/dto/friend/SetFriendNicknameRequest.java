package com.linkhub.common.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/16 17:33
 */
@Data
public class SetFriendNicknameRequest {
    @NotNull(message = "目标好友的id不能为空")
    @ApiModelProperty(value = "目标好友的id")
    private String targetId;

    @NotNull(message = "昵称")
    @ApiModelProperty(value = "昵称")
    private String nickname;
}
