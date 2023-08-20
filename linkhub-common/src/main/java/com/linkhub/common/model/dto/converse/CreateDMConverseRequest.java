package com.linkhub.common.model.dto.converse;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/17 22:39
 */

@Data
public class CreateDMConverseRequest {
    @NotNull(message = "需要选择的用户id列表不能为空")
    String[] memberIds;
}
