package com.linkhub.tmp.service.impl;

import com.linkhub.common.model.pojo.User;
import com.linkhub.tmp.mapper.UserMapper;
import com.linkhub.tmp.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
