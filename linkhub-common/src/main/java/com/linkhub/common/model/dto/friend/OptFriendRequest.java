package com.linkhub.common.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/13 19:09
 */
@Data
public class OptFriendRequest {
    @NotNull(message = "好友请求id不能为空")
    @ApiModelProperty(value = "好友请求idid")
    private String requestId;
}
