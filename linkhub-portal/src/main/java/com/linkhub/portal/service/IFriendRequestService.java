package com.linkhub.portal.service;

import com.linkhub.common.model.dto.friend.AddFriendDto;
import com.linkhub.common.model.dto.friend.OptFriendRequest;
import com.linkhub.common.model.pojo.FriendRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.User;

import java.util.List;

/**
 * <p>
 * 好友申请表 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
public interface IFriendRequestService extends IService<FriendRequest> {

    FriendRequest addFriend(AddFriendDto addFriendDto);

    List<FriendRequest> allRelated(String userId);

    int accept(String userId, OptFriendRequest optFriendRequest);

    int deny(String userId, OptFriendRequest optFriendRequest);

    int cancel(String userId, OptFriendRequest optFriendRequest);
}
