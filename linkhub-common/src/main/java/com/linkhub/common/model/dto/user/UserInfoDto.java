package com.linkhub.common.model.dto.friend.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author LinkCastling
 * @date 2023/8/11 0:56
 */
@Data
public class UserInfoDto {
    private String id;

    private String email;

    private String nickname;

    private Boolean temporary;

    private String avatar;

    private String type;

    private Boolean emailVerified;

    private Boolean banned;

    private String discriminator;

    private LocalDateTime createAt;
}
