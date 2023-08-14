package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.mapper.FriendMapper;
import com.linkhub.common.model.common.DeleteFriendRequest;
import com.linkhub.common.model.pojo.Friend;
import com.linkhub.common.model.pojo.User;
import com.linkhub.portal.service.IFriendService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 好友表 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-11
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements IFriendService {

    @Autowired
    FriendMapper friendMapper;

    @Override
    public int removeFriend(User user, DeleteFriendRequest deleteFriendRequest) {
        // 判空
        if (ObjectUtils.isEmpty(user) || StringUtils.isEmpty(user.getId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR.getMessage(), ErrorCode.PARAMS_ERROR.getCode());
        }

        if (ObjectUtils.isEmpty(deleteFriendRequest) || StringUtils.isEmpty(deleteFriendRequest.getFriendUserId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR.getMessage(), ErrorCode.PARAMS_ERROR.getCode());
        }

        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getFrom, user.getId());
        wrapper.eq(Friend::getTo, deleteFriendRequest.getFriendUserId());

        return baseMapper.delete(wrapper);
    }
}
