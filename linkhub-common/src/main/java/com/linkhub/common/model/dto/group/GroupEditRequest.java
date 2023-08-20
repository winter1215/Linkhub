package com.linkhub.common.model.dto.group;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author LinkCastling
 * @date 2023/8/18 17:40
 */
@Data
public class GroupEditRequest {
    @ApiModelProperty("群组邀请的code")
    private String code;

    @ApiModelProperty("过期时间，是一个时间戳")
    private Long expireAt;

    @ApiModelProperty("群组id")
    private String groupId;

    @ApiModelProperty("可使用次数")
    private Integer usageLimit;
}
