package com.linkhub.common.model.common;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LinkCastling
 * @date 2023/8/11 16:30
 */
@Data
public class TokenRequest {
    @NotNull(message = "token不能为空")
    private String token;
}
