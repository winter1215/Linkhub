package com.linkhub.portal.service.impl;

import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.common.model.pojo.Message;
import com.linkhub.portal.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 聊天记录 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
