package com.linkhub.common.model.dto.friend.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/11 18:09
 */
@Data
public class UserIdsRequest {

    @NotNull(message = "需要获取的用户id列表不能为空")
    private String[] userIds;
}
