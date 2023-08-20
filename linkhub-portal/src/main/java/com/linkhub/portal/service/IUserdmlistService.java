package com.linkhub.portal.service;

import com.linkhub.common.model.dto.converse.ConverseIdRequest;
import com.linkhub.common.model.dto.userdmlist.UserdmlistDto;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.pojo.Userdmlist;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户的会话列表 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
public interface IUserdmlistService extends IService<Userdmlist> {

    UserdmlistDto addConverse(String userId, String converseId);
}
