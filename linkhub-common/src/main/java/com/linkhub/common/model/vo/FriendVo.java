package com.linkhub.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LinkCastling
 * @date 2023/8/16 16:15
 */
@Data
public class FriendVo {

    @ApiModelProperty(value = "好友的userId")
    private String id;

    @ApiModelProperty(value = "好友的备注")
    private String nickname;
}
