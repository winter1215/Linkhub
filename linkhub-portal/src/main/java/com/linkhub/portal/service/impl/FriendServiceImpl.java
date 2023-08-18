package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.mapper.FriendMapper;
import com.linkhub.common.model.dto.friend.DeleteFriendRequest;
import com.linkhub.common.model.dto.friend.SetFriendNicknameRequest;
import com.linkhub.common.model.pojo.Friend;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.vo.FriendVo;
import com.linkhub.portal.service.IFriendService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    public List<FriendVo> getAllFriendsById(String userId) {
        // 判空
        if (StringUtils.isEmpty(userId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getFrom, userId);
        List<Friend> friendList = friendMapper.selectList(wrapper);
        List<FriendVo> res = new ArrayList<>();
        friendList.forEach(friend -> {
            FriendVo tmp = new FriendVo();
            tmp.setId(friend.getTo());
            tmp.setNickname(friend.getRemark());
            res.add(tmp);
        });
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setFriendNickname(String userId, SetFriendNicknameRequest setFriendNicknameRequest) {
        // 判空
        if (StringUtils.isEmpty(userId) || ObjectUtils.isEmpty(setFriendNicknameRequest)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR.getMessage(), ErrorCode.PARAMS_ERROR.getCode());
        }

        if (StringUtils.isEmpty(setFriendNicknameRequest.getTargetId()) || StringUtils.isEmpty(setFriendNicknameRequest.getNickname())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR.getMessage(), ErrorCode.PARAMS_ERROR.getCode());
        }

        // 查找是否存在好友关系
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getFrom, userId).eq(Friend::getTo, setFriendNicknameRequest.getTargetId());
        Friend res = baseMapper.selectOne(wrapper);

        if (ObjectUtils.isEmpty(res)) {
            throw new GlobalException(ErrorCode.NOT_FOUND_ERROR.getMessage(), ErrorCode.NOT_FOUND_ERROR.getCode());
        }

        // 更新 注意lambdaUpdate() 方法返回的LambdaUpdateChainWrapper不能作为查询的参数
        boolean update = lambdaUpdate()
                .eq(Friend::getId, res.getId())
                .set(Friend::getRemark, setFriendNicknameRequest.getNickname())
                .update();

        return update;
    }
}
