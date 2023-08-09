package com.linkhub.portal.service.impl;

import com.linkhub.common.model.pojo.UserSetting;
import com.linkhub.common.mapper.UserSettingMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.service.IUserSettingService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户设置 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements IUserSettingService {

}
