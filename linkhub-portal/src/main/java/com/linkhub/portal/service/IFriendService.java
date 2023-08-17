package com.linkhub.portal.service;

import com.linkhub.common.model.dto.friend.DeleteFriendRequest;
import com.linkhub.common.model.dto.friend.SetFriendNicknameRequest;
import com.linkhub.common.model.pojo.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.vo.FriendVo;

import java.util.List;

/**
 * <p>
 * 好友表 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-11
 */
public interface IFriendService extends IService<Friend> {

    int removeFriend(User user, DeleteFriendRequest deleteFriendRequest);

    List<FriendVo> getAllFriendsById(String userId);

    boolean setFriendNickname(String userId, SetFriendNicknameRequest setFriendNicknameRequest);
}
