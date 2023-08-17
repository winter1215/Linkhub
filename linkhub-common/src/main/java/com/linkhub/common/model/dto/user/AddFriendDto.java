package com.linkhub.common.model.dto.friend.user;

import lombok.Data;

/**
 * @author LinkCastling
 * @date 2023/8/13 2:08
 */
@Data
public class AddFriendDto {

    // 好友请求记录的Id
    private String id;

    // 发起添加的人
    private String from;

    // 目标
    private String to;

}
