package com.linkhub.common.model.dto.friend;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/13 2:08
 */
@Data
public class AddFriendDto {
    // 目标
    @NotNull
    private String to;

}
