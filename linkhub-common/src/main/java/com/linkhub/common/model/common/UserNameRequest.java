package com.linkhub.common.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author LinkCastling
 * @date 2023/8/11 15:44
 */
@Data
public class UserNameRequest implements Serializable {
    private String nickname;
}
