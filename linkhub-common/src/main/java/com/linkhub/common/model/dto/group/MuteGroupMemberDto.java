package com.linkhub.common.model.dto.group;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author winter
 * @create 2023-08-21 下午5:17
 */
@Data
public class MuteGroupMemberDto {
    @NotNull
    private String groupId;
    @NotNull
    private String memberId;
    @NotNull
    private Integer muteMs;
}
