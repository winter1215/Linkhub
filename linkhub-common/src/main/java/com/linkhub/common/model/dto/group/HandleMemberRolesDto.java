package com.linkhub.common.model.dto.group;

import lombok.Data;

import java.util.List;

/**
 * @author winter
 * @create 2023-08-20 下午12:52
 */
@Data
public class HandleMemberRolesDto {
    private String groupId;
    private List<String> memberIds;
    private List<String> roles;
}
