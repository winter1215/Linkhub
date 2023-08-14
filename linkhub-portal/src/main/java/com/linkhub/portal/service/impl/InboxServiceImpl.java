package com.linkhub.portal.service.impl;

import com.linkhub.common.enums.InboxTypeEnum;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.Inbox;
import com.linkhub.common.mapper.InboxMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IInboxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.linkhub.common.enums.CommonConstants.LINKHUB_THREAD_POOL;

/**
 * <p>
 * 收件箱 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-14
 */
@Service
@Slf4j
public class InboxServiceImpl extends ServiceImpl<InboxMapper, Inbox> implements IInboxService {

    @Override
    @Async(LINKHUB_THREAD_POOL)
    public void insertMsgInbox(SendMsgDto sendMsgDto) {
        List<String> mentions = sendMsgDto.getMeta().getMentions();
        if (ObjectUtils.isEmpty(mentions)) {
            return;
        }

        String loginUserId = SecurityUtils.getLoginUserId();
        List<Inbox> inboxes = new ArrayList<>();
        mentions.forEach(mention -> {
            if (mention.equals(loginUserId)) {
                log.warn("不能 @ 自己({})", loginUserId);
                return;
            }

            Inbox inbox = Inbox.sendMsgDtoConvertDomain(sendMsgDto, mention);
            inboxes.add(inbox);
        });

        this.saveBatch(inboxes);
    }
}
