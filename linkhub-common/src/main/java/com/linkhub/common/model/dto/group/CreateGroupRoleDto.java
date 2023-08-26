package com.linkhub.common.model.dto.group;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author winter
 * @create 2023-08-20 下午11:31
 */
@Data
public class CreateGroupRoleDto {
    @NotNull
    private String groupId;
    @NotNull
    private String name;
    @NotNull
    private List<String> permissions;
}
