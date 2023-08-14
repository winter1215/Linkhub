package com.linkhub.portal.service;

import com.linkhub.common.model.common.OptFriendRequest;
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

    FriendRequest addFriend(FriendRequest friendRequest);

    List<FriendRequest> allRelated(User user);

    int accept(User user, OptFriendRequest optFriendRequest);

    int deny(User user, OptFriendRequest optFriendRequest);

    int cancel(User user, OptFriendRequest optFriendRequest);
}
