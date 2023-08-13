package com.linkhub.common.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/13 2:05
 */
@Data
public class FriendRequest {
    @NotNull(message = "要添加的好友Id不能为空")
    @ApiModelProperty(value = "好友Id")
    private String to;
}
