package com.linkhub.common.model.dto.group;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author winter
 * @create 2023-08-20 下午11:49
 */
@Data
public class DeleteGroupRoleDto {
    @NotNull
    private String groupId;
    @NotNull
    private String roleId;
}
