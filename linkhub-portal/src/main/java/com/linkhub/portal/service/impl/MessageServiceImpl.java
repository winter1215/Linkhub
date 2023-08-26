package com.linkhub.portal.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.*;
import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.common.model.dto.message.FetchConverseMessageDto;
import com.linkhub.common.model.dto.message.FetchNearbyMessageDto;
import com.linkhub.common.model.dto.message.ReactionDto;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.pojo.Message;
import com.linkhub.common.model.pojo.MsgExtra;
import com.linkhub.common.model.pojo.Reaction;
import com.linkhub.common.model.vo.MessageVo;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IGroupService;
import com.linkhub.portal.service.IInboxService;
import com.linkhub.portal.service.IMessageService;
import com.linkhub.portal.service.IMsgExtraService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
    /**
    * pair: message 对应 mention 个数的 extra
    */
    private static final List<Pair<Message, List<MsgExtra>>> MESSAGE_BUFFER = new CopyOnWriteArrayList<>();
    private static final int BATCH_SIZE = 32;
    @Resource
    private IMsgExtraService msgExtraService;
    @Resource
    private IGroupService groupService;
    @Resource
    private IInboxService iInboxService;



    @Override
    public void sendMessage(SendMsgDto sendMsgDto) {
        sendMessage(sendMsgDto, SecurityUtils.getLoginUserId());
    }

    public void sendMessage(SendMsgDto sendMsgDto, String author) {
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

        Message message = Message.convertToDomain(sendMsgDto, author);
        List<MsgExtra> msgExtras = MsgExtra.convertToDomain(sendMsgDto);
        asyncHandleMessage(message, msgExtras);

        // 将消息 jsonify 后广播给房间的用户,
        IMUtil.notify(new String[]{converseId}, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.MESSAGE_ADD, message);
        // 更新收件箱(@ sb)
        iInboxService.insertMsgInbox(sendMsgDto);
    }

    @Override
    public List<MessageVo> fetchConverseMessage(FetchConverseMessageDto fetchConverseMessageDto) {
        String converseId = fetchConverseMessageDto.getConverseId();
        Long startId = fetchConverseMessageDto.getStartId();

        syncMessageBuffer();
        return baseMapper.fetchConverseMessage(converseId, startId, CommonConstants.CONVERSE_MESSAGE_LIMIT);
    }

    @Override
    public List<MessageVo> fetchNearbyMessage(FetchNearbyMessageDto fetchNearbyMessageDto) {
        String converseId = fetchNearbyMessageDto.getConverseId();
        Long messageId = fetchNearbyMessageDto.getMessageId();
        Integer num = fetchNearbyMessageDto.getNum();

        syncMessageBuffer();
        // todo: 可能有越权漏洞
        Message message = baseMapper.selectById(messageId);
        if (ObjectUtils.isEmpty(message)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        if (num == null || num.equals(0)) {
            num = CommonConstants.CONVERSE_NEARBY_MESSAGE_LIMIT;
        }
        return baseMapper.fetchNearbyMessage(converseId, messageId, num);
    }

    @Override
    public MessageVo recallMessage(Long messageId) {
        String userId = SecurityUtils.getLoginUserId();

        // sync buffer
        syncMessageBuffer();
        MessageVo message = baseMapper.findMsgVoById(messageId);
        String converseId = message.getConverseId();
        String groupId = message.getGroupId();

        if (ObjectUtils.isEmpty(message)) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        if (message.getHasRecall()) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "消息已被撤回");
        }
        // 判断是否超过 5min
        LocalDateTime createAt = message.getCreateAt();
        Duration duration = Duration.between(createAt, LocalDateTime.now());
        if (duration.toMinutes() > 5) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "无法撤回发送时间超过五分钟的消息");
        }

        // 是否为群消息,是否为管理员 || 是否为消息发送者
        if (ObjectUtils.isNotEmpty(groupId) && !groupService.checkUserIsOwner(groupId, userId)) {
            if (!userId.equals(message.getAuthor())) {
                throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 更新
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(Message::getId, messageId)
                        .set(Message::getHasRecall, true);

        baseMapper.update(null, wrapper);
        // 组播
        message.setHasRecall(true);
        String jsonStr = JSONUtil.toJsonStr(message);
        IMUtil.notify(new String[]{converseId}, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.MESSAGE_UPDATE, jsonStr);
        return message;
    }

    @Override
    public MessageVo deleteMessage(Long messageId) {
        String userId = SecurityUtils.getLoginUserId();
        MessageVo message = baseMapper.findMsgVoById(messageId);
        String groupId = message.getGroupId();
        String converseId = message.getConverseId();

        if (ObjectUtils.isEmpty(message)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        if (ObjectUtils.isEmpty(groupId)) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR, "无法删除私人消息");
        }
        boolean hasPermission = groupService.checkUserPermission(userId, groupId, GroupPermissionEnum.DELETE_MESSAGE, null);
        if (!hasPermission) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR, "群组权限不足");
        }
        // delete
        baseMapper.deleteById(messageId);
        // todo: 删除对应的 reaction
        // notify
        String json = JSONUtil.toJsonStr(message);
        IMUtil.notify(new String[]{converseId}, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.MESSAGE_DELETE, json);
        return message;
    }

    @Override
    public List<String> fetchConverseLastMessages(List<String> converseIds) {
        if (ObjectUtils.isEmpty(converseIds)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 同步 buffer
        syncMessageBuffer();
        return baseMapper.selectConverseLastMessages(converseIds);
    }

    @Override
    public boolean addReaction(ReactionDto reactionDto) {
        Long messageId = reactionDto.getMessageId();
        String emoji = reactionDto.getEmoji();
        String userId = SecurityUtils.getLoginUserId();

        // todo: 检查当前登录用户是否在 群组/会话 中 (caffeine)
        MessageVo messageVo = baseMapper.findMsgVoById(messageId);
        if (ObjectUtils.isEmpty(messageVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 若用户已经为该条消息添加相同反应,则添加失败
        List<Reaction> reactions = messageVo.getReactions();
        for (Reaction reaction : reactions) {
            if (userId.equals(reaction.getAuthor()) && emoji.equals(reaction.getName())) {
                return false;
            }
        }
        // 添加
        MsgExtra msgExtra = new MsgExtra();
        msgExtra.setMsgId(messageId);
        msgExtra.setName(emoji);
        msgExtra.setAuthor(userId);

        return msgExtraService.save(msgExtra);
    }

    @Override
    public boolean removeReaction(ReactionDto reactionDto) {
        Long messageId = reactionDto.getMessageId();
        String emoji = reactionDto.getEmoji();
        String userId = SecurityUtils.getLoginUserId();

        MessageVo messageVo = baseMapper.findMsgVoById(messageId);
        if (ObjectUtils.isEmpty(messageVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 检查是否存在这个 reaction
        List<Reaction> reactions = messageVo.getReactions();
        boolean isExist = false;
        for (Reaction reaction : reactions) {
            if (userId.equals(reaction.getAuthor()) && emoji.equals(reaction.getName())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            return false;
        }

        LambdaQueryWrapper<MsgExtra> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgExtra::getAuthor, userId)
                .eq(MsgExtra::getName, emoji);
        return msgExtraService.remove(wrapper);
    }

    /**
     * 发送系统信息
     * @param sendMsgDto
     */
    @Override
    public void sendSysMessage(SendMsgDto sendMsgDto) {
        sendMessage(sendMsgDto);
    }

    @Override
    public void addGroupSystemMessage(String groupId, String message) {
        String lobbyConverseId = groupService.getGroupLobbyConverseId(groupId);

        if (StringUtils.isEmpty(lobbyConverseId)) { // 如果没有文本频道则跳过
            return ;
        }
        SendMsgDto sendMsgDto = new SendMsgDto();
        sendMsgDto.setContent(message);
        sendMsgDto.setConverseId(lobbyConverseId);
        sendMsgDto.setGroupId(groupId);
        sendMessage(sendMsgDto, CommonConstants.SYSTEM_USERID);
    }


    /**
     * 异步处理发送消息的实例化
     */
    private void asyncHandleMessage(Message message, List<MsgExtra> msgExtras) {
        if (MESSAGE_BUFFER.size() >= BATCH_SIZE) {
            // 拷贝
            List<Pair<Message, List<MsgExtra>>> bak_buffer = new ArrayList<>(MESSAGE_BUFFER);
            // 异步批量导入数据库
            CompletableFuture.supplyAsync(() -> insertPairListToMessage(bak_buffer))
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
        MESSAGE_BUFFER.add(Pair.of(message, msgExtras));
    }

    /**
     * 同步的将 Message_buffer 插入到数据库,保证缓存的 message 不会影响数据一致性
     */
    public boolean syncMessageBuffer() {
        List<Pair<Message, List<MsgExtra>>> bak_buffer = new ArrayList<>(MESSAGE_BUFFER);
        MESSAGE_BUFFER.clear();

        return insertPairListToMessage(bak_buffer);
    }

    public boolean insertPairListToMessage(List<Pair<Message, List<MsgExtra>>> bak_buffer) {
        List<Message> messages = bak_buffer.stream()
                .map(Pair::getLeft)
                .collect(Collectors.toList());
        boolean msgFlag = this.saveBatch(messages);

        // id 回填到 messages
        List<MsgExtra> msgExtras = new ArrayList<>();
        for (Pair<Message, List<MsgExtra>> pair : bak_buffer) {
            List<MsgExtra> extras = pair.getRight();
            // 不为空, mention 有值
            if (ObjectUtils.isNotEmpty(extras)) {
                extras.forEach(item -> {
                    item.setMsgId(pair.getLeft().getId());
                });
            }
            msgExtras.addAll(extras);
        }
        if (ObjectUtils.isEmpty(msgExtras)) {
            return  msgFlag;
        }
        return msgExtraService.saveBatch(msgExtras);
    }
}
