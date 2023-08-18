package com.linkhub.common.model.dto.converse;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/18 1:34
 */
@Data
public class AppendDMConverseMemberRequest {
    @NotNull(message = "会话id不能为空")
    private String converseId;
    @NotNull(message = "需要选择的用户id列表不能为空")
    private String[] membersIds;
}
