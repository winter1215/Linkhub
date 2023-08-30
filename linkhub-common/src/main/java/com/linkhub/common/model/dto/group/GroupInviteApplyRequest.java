package com.linkhub.common.model.dto.group;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/18 19:10
 */

@Data
public class GroupInviteApplyRequest {

    @ApiModelProperty
    @NotNull(message = "要加入的群组的code不能为空")
    private String code;
}
