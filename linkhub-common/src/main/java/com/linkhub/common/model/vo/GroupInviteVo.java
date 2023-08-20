package com.linkhub.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author LinkCastling
 * @date 2023/8/18 16:53
 */
@Data
public class GroupInviteVo {

    @ApiModelProperty("群组邀请id")
    private String id;

    @ApiModelProperty("群组邀请代码")
    private String code;

    @ApiModelProperty("群组创建者的id")
    private String creator;

    @ApiModelProperty("群组id")
    private String groupId;

    @ApiModelProperty("使用量")
    private Integer usage;

    @ApiModelProperty("过期时间")
    private LocalDateTime expireAt;

    @ApiModelProperty("创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateAt;
}
