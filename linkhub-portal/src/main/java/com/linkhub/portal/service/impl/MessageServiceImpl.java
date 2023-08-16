package com.linkhub.portal.service.impl;

import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ClientEventEnum;
import com.linkhub.common.enums.CommonConstants;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.IMNotifyTypeEnum;
import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.common.model.dto.message.FetchConverseMessageDto;
import com.linkhub.common.model.dto.message.FetchNearbyMessageDto;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.pojo.Message;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.vo.MessageVo;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.service.IGroupService;
import com.linkhub.portal.service.IInboxService;
import com.linkhub.portal.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 聊天记录 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    private static final List<Message> MESSAGE_BUFFER = new ArrayList<>();
    private static final int BATCH_SIZE = 32;
    @Resource
    private IGroupService groupService;
    @Resource
    private IInboxService iInboxService;

    @Override
    public void sendMessage(SendMsgDto sendMsgDto) {
        // 是否群消息
        String groupId = sendMsgDto.getGroupId();
        String converseId = sendMsgDto.getConverseId();

        if (StringUtils.isNotEmpty(groupId)) {
            // todo: 待优化,缓存
            GroupMember member = groupService.checkMeInGroup(groupId);
            LocalDateTime muteUtil = member.getMuteUtil();
            if (muteUtil.isAfter(LocalDateTime.now())) {
                throw new GlobalException(ErrorCode.CLIENT_ERROR, "用户正在被禁言中");
            }
        }

        // 缓存优化
        Message message = Message.coverToDomain(sendMsgDto);
        asyncHandleMessage(message);

        // 将消息 jsonify 后广播给房间的用户,
        ArrayList<String> converseIds = new ArrayList<>();
        converseIds.add(converseId);
        IMUtil.notify(converseIds, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.MESSAGE_ADD, message);
        // 更新收件箱(@ sb)
        iInboxService.insertMsgInbox(sendMsgDto);
    }

    @Override
    public List<MessageVo> fetchConverseMessage(FetchConverseMessageDto fetchConverseMessageDto) {
        String converseId = fetchConverseMessageDto.getConverseId();
        Long startId = fetchConverseMessageDto.getStartId();
        return baseMapper.fetchConverseMessage(converseId, startId, CommonConstants.CONVERSE_MESSAGE_LIMIT);
    }

    @Override
    public List<MessageVo> fetchNearbyMessage(FetchNearbyMessageDto fetchNearbyMessageDto) {
        String converseId = fetchNearbyMessageDto.getConverseId();
        Long messageId = fetchNearbyMessageDto.getMessageId();
        Integer num = fetchNearbyMessageDto.getNum();

        if (num == null || num.equals(0)) {
            num = CommonConstants.CONVERSE_NEARBY_MESSAGE_LIMIT;
        }
        return baseMapper.fetchNearbyMessage(converseId, messageId, num);
    }


    /**
    * 异步处理发送消息的实例化
    */
    private void asyncHandleMessage(Message message) {
        if (MESSAGE_BUFFER.size() >= BATCH_SIZE) {
            // 拷贝
            List<Message> messages = new ArrayList<>(MESSAGE_BUFFER);
            // 异步批量导入数据库
            CompletableFuture.supplyAsync(() -> this.saveBatch(messages))
                    .exceptionally(ex -> {
                        log.warn("message 持久化失败");
                        return false;
                    })
                    .thenAccept(res -> {
                        log.info("消息持久化状态: {}", res);
                    });
            MESSAGE_BUFFER.clear();
        }

        // 设置消息发送时间
        message.setCreateAt(LocalDateTime.now());
        message.setUpdateAt(LocalDateTime.now());
        MESSAGE_BUFFER.add(message);
    }

    /**
    * 同步的将 Message_buffer 插入到数据库,保证缓存的 message 不会影响数据一致性
    */
    public boolean syncMessageBuffer() {
        List<Message> messages = new ArrayList<>(MESSAGE_BUFFER);
        MESSAGE_BUFFER.clear();
        return this.saveBatch(messages);
    }
}
