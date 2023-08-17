package com.linkhub.common.model.dto.converse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/12 19:02
 */
@Data
public class ConverseIdRequest {
    @NotNull(message = "会话id不能为空")
    @ApiModelProperty(value = "会话id")
    private String converseId;
}
