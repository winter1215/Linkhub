package com.linkhub.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author LinkCastling
 * @date 2023/8/18 16:31
 */
@Data
public class GroupVo {

    @ApiModelProperty("群组邀请的id")
    private String _id;

    @ApiModelProperty("邀请链接的代码")
    private String code;

    @ApiModelProperty("群组的创建者")
    private String creator;

    @ApiModelProperty("过期时间，如果是永久的，则没有这个过期时间")
    private LocalDateTime expiredAt;

    @ApiModelProperty("使用量")
    private Integer usage;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedAt;
}
