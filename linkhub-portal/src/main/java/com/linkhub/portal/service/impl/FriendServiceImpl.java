package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.mapper.FriendMapper;
import com.linkhub.common.model.pojo.Friend;
import com.linkhub.portal.service.IFriendService;
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

}
