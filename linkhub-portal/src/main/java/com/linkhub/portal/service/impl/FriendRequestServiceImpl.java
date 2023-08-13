package com.linkhub.portal.service.impl;

import com.linkhub.common.model.pojo.FriendRequest;
import com.linkhub.common.mapper.FriendRequestMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.service.IFriendRequestService;
import org.springframework.stereotype.Service;

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

}
