package com.linkhub.common.model.dto.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/14 21:11
 */
@Data
public class DeleteFriendRequest {
    @NotNull(message = "要删除的好友的id不能为空")
    @ApiModelProperty(value = "好友id")
    private String friendUserId;
}
