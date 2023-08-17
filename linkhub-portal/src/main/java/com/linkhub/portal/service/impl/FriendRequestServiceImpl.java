package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.mapper.FriendMapper;
import com.linkhub.common.model.dto.friend.OptFriendRequest;
import com.linkhub.common.model.pojo.Friend;
import com.linkhub.common.model.pojo.FriendRequest;
import com.linkhub.common.mapper.FriendRequestMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.User;
import com.linkhub.portal.service.IFriendRequestService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 好友申请表 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements IFriendRequestService {

    @Autowired
    FriendRequestMapper friendRequestMapper;

    @Autowired
    FriendMapper friendMapper;

    @Override
    public FriendRequest addFriend(FriendRequest friendRequest) {
        // 判空
        if (ObjectUtils.isEmpty(friendRequest) || StringUtils.isEmpty(friendRequest.getFrom()) || StringUtils.isEmpty(friendRequest.getTo())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否添加自己
        if (friendRequest.getFrom().equals(friendRequest.getTo())) {
            throw new GlobalException("不能添加自己为好友", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 不能发送重复的好友请求
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getFrom, friendRequest.getFrom());
        wrapper.eq(FriendRequest::getTo, friendRequest.getTo());
        FriendRequest exist = baseMapper.selectOne(wrapper);

        if (ObjectUtils.isNotEmpty(exist)) {
            throw new GlobalException("不能发送重复的好友请求", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 判断是否已经是好友
        LambdaQueryWrapper<Friend> isFriendWrapper = new LambdaQueryWrapper<>();
        isFriendWrapper.eq(Friend::getFrom, friendRequest.getFrom());
        isFriendWrapper.eq(Friend::getTo, friendRequest.getTo());

        Friend isFriend = friendMapper.selectOne(isFriendWrapper);

        if (ObjectUtils.isNotEmpty(isFriend)) {
            throw new GlobalException("对方已经是您的好友, 不能再次添加", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 添加request记录到数据库
        baseMapper.insert(friendRequest);
        return friendRequest;
    }

    @Override
    public List<FriendRequest> allRelated(User user) {
        // 判空
        if (ObjectUtils.isEmpty(user) ||StringUtils.isEmpty(user.getId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 根据用户的userId，查询所有to为该userId的friendRequest
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getTo, user.getId());
        return baseMapper.selectList(wrapper);
    }

    @Override
    public int accept(String userId, OptFriendRequest optFriendRequest) {
        // 判空
        if (StringUtils.isEmpty(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        if (ObjectUtils.isEmpty(optFriendRequest) || StringUtils.isEmpty(optFriendRequest.getRequestId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 在friendRequest中查找是否存在
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getId, optFriendRequest.getRequestId());
        FriendRequest friendRequest = baseMapper.selectOne(wrapper);

        if (ObjectUtils.isEmpty(friendRequest)) {
            throw new GlobalException("该好友请求未找到", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 判断一下接收好友请求的人和数据库中是否对应
        if (!friendRequest.getTo().equals(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 存入Friend，两条记录
        Friend fromTo = new Friend();
        Friend toFrom = new Friend();
        fromTo.setFrom(friendRequest.getFrom());
        fromTo.setTo(friendRequest.getTo());

        toFrom.setFrom(friendRequest.getTo());
        toFrom.setTo(friendRequest.getFrom());

        friendMapper.insert(fromTo);
        return friendMapper.insert(toFrom);
    }

    @Override
    public int deny(String userId, OptFriendRequest optFriendRequest) {
        // 判空
        if (StringUtils.isEmpty(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        if (ObjectUtils.isEmpty(optFriendRequest) || StringUtils.isEmpty(optFriendRequest.getRequestId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 在friendRequest中查找是否存在
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getId, optFriendRequest.getRequestId());
        FriendRequest friendRequest = baseMapper.selectOne(wrapper);

        if (ObjectUtils.isEmpty(friendRequest)) {
            throw new GlobalException("该好友请求未找到", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 判断一下拒绝好友请求的人和数据库中是否对应
        if (!friendRequest.getTo().equals(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        LambdaQueryWrapper<FriendRequest> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FriendRequest::getId, optFriendRequest.getRequestId());
        return baseMapper.delete(deleteWrapper);
    }

    @Override
    public int cancel(String userId, OptFriendRequest optFriendRequest) {
        // 判空
        if (StringUtils.isEmpty(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        if (ObjectUtils.isEmpty(optFriendRequest) || StringUtils.isEmpty(optFriendRequest.getRequestId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 在friendRequest中查找是否存在
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getId, optFriendRequest.getRequestId());
        FriendRequest friendRequest = baseMapper.selectOne(wrapper);

        if (ObjectUtils.isEmpty(friendRequest)) {
            throw new GlobalException("该好友请求未找到", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 判断一下取消好友请求的人和数据库中是否对应
        if (!friendRequest.getFrom().equals(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 删除请求
        LambdaQueryWrapper<FriendRequest> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FriendRequest::getId, optFriendRequest.getRequestId());
        return baseMapper.delete(deleteWrapper);
    }
}
