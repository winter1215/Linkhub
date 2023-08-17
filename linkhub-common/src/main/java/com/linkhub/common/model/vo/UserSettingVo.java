package com.linkhub.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LinkCastling
 * @date 2023/8/16 22:47
 */
@Data
public class UserSettingVo {
    @ApiModelProperty(value = "是否虚拟化")
    private Boolean messageListVirtualization;

    @ApiModelProperty(value = "是否禁用消息提示音")
    private Boolean notification;
}
