package com.linkhub.portal.service.impl;

import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.Inbox;
import com.linkhub.common.mapper.InboxMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.service.IInboxService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收件箱 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-14
 */
@Service
public class InboxServiceImpl extends ServiceImpl<InboxMapper, Inbox> implements IInboxService {

    @Override
    public void insertMsgInbox(SendMsgDto sendMsgDto) {

    }
}
