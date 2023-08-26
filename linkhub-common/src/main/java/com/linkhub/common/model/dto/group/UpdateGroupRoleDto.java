package com.linkhub.common.model.dto.group;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author winter
 * @create 2023-08-21 上午12:07
 */
@Data
public class UpdateGroupRoleDto {
    @NotNull
    private String groupId;
    @NotNull
    private String roleId;
    private String name;
    private List<String> permissions;
}
