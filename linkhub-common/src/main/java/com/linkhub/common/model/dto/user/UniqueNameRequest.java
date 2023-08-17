package com.linkhub.common.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/11 16:30
 */
@Data
public class UniqueNameRequest {
    @NotNull(message = "搜索的唯一标识不能为空")
    private String uniqueName;
}
