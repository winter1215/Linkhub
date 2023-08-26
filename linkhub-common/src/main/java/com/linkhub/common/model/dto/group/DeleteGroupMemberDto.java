package com.linkhub.common.model.dto.group;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author winter
 * @create 2023-08-21 下午6:48
 */
@Data
public class DeleteGroupMemberDto {
    @NotNull
    private String groupId;
    @NotNull
    private String memberId;
}
