package com.linkhub.common.model.dto.group;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/18 16:13
 */
@Data
public class GroupInviteRequest {

    @NotNull(message = "要创建的群组邀请的id不能为空")
    @ApiModelProperty(value = "群组id")
    private String groupId;

    @NotNull(message = "要创建的群组邀请的邀请类型不能为空")
    @ApiModelProperty(value = "邀请类型")
    private String inviteType;
}
