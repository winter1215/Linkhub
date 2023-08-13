package com.linkhub.portal.service.impl;

import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.mapper.MessageMapper;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.pojo.Message;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.service.IGroupService;
import com.linkhub.portal.service.IMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
    @Resource
    private IGroupService groupService;

    @Override
    public void sendMessage(SendMsgDto sendMsgDto) {
        // 是否群消息
        String groupId = sendMsgDto.getGroupId();

        if (StringUtils.isNotEmpty(groupId)) {
            GroupMember member = groupService.checkMeInGroup(groupId);
            LocalDateTime muteUtil = member.getMuteUtil();
            if (muteUtil.isAfter(LocalDateTime.now())) {
                throw new GlobalException(ErrorCode.CLIENT_ERROR, "用户正在被禁言中");
            }
        }

        // 缓存优化
        Message message = Message.coverToDomain(sendMsgDto);
        int flag = baseMapper.insert(message);

        // 将消息 jsonify 后广播给房间的用户,
        // 触发更新事件

    }
}
