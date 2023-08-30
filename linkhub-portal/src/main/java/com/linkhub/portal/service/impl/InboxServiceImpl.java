package com.linkhub.portal.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linkhub.common.enums.ClientEventEnum;
import com.linkhub.common.enums.IMNotifyTypeEnum;
import com.linkhub.common.enums.InboxTypeEnum;
import com.linkhub.common.model.dto.inbox.RemoveInboxDto;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.Inbox;
import com.linkhub.common.mapper.InboxMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IInboxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void asyncInsertMsgInbox(SendMsgDto sendMsgDto) {
        List<String> mentions = new ArrayList<>();
        if (sendMsgDto.getMeta() != null) {
            mentions = sendMsgDto.getMeta().getMentions();
        }
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

        // 通知更新 inbox
        this.saveBatch(inboxes);
        // 批量通知
        inboxes.forEach(inbox -> {
            this.notifyUsersInboxAppend(new String[] {inbox.getUserId()}, inbox);
        });
    }

    // 删除 mention 的收件箱消息
    public boolean removeMsgInbox(RemoveInboxDto removeInboxDto) {
        String userId = removeInboxDto.getUserId();
        String type = removeInboxDto.getType();
        String conserveId = removeInboxDto.getConserveId();
        String messageId = removeInboxDto.getMessageId();
        String groupId = removeInboxDto.getGroupId();

        QueryWrapper<Inbox> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("type", type);
        if (ObjectUtils.isNotEmpty(groupId)) {
            wrapper.eq("payload -> '$.groupId'", groupId);
        } else {
            wrapper.eq("payload -> '$.conserveId'", conserveId);
        }
        wrapper.eq("payload -> '$.messageId'", messageId);

        // 通知更新收件箱
        this.notifyUsersInboxUpdate(new String[]{userId});
        return this.remove(wrapper);
    }

    @Override
    public List<Inbox> all(String userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Inbox>()
                .eq(Inbox::getUserId, userId));
    }

    @Override
    public boolean ack(List<String> inboxIds) {
        String userId = SecurityUtils.getLoginUserId();
        List<Inbox> inboxes = this.list(new LambdaQueryWrapper<Inbox>()
                .in(Inbox::getId, inboxIds));
        // 收集出合法的 inbox id
        List<String> validIdList = inboxes.stream()
                .filter(item -> item != null && userId.equals(item.getUserId()))
                .map(Inbox::getId)
                .collect(Collectors.toList());

        List<Inbox> updateInboxes = new ArrayList<>();
        validIdList.forEach(id -> {
            Inbox inbox = new Inbox();
            inbox.setId(id);
            inbox.setReaded(true);
        });
        return this.updateBatchById(updateInboxes);
    }

    @Override
    public void clearAll() {
        String userId = SecurityUtils.getLoginUserId();
        baseMapper.delete(new LambdaQueryWrapper<Inbox>().eq(Inbox::getUserId,userId));
        this.notifyUsersInboxUpdate(new String[]{userId});
    }

    public void notifyUsersInboxAppend(String[] userIds, Object data) {
        String json = JSONUtil.toJsonStr(data);
        IMUtil.notify(userIds, IMNotifyTypeEnum.LIST_CAST, ClientEventEnum.INBOX_APPEND, json);
    }

    public void notifyUsersInboxUpdate(String[] userIds) {
        IMUtil.notify(userIds, IMNotifyTypeEnum.LIST_CAST, ClientEventEnum.INBOX_UPDATED, "");
    }

}
