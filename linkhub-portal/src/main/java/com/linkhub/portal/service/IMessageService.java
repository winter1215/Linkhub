package com.linkhub.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.dto.message.*;
import com.linkhub.common.model.pojo.Message;
import com.linkhub.common.model.vo.MessageVo;
import com.linkhub.common.utils.R;

import java.util.List;

/**
 * <p>
 * 聊天记录 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
public interface IMessageService extends IService<Message> {

    /**
    * 发送消息
    */
    void sendMessage(SendMsgDto sendMsgDto);

    /**
     * 拉取某个会话的消息,上拉刷新的效果,每次返回 limit 条(分页也可以实现,就不需要 id 是递增的)
     * @param fetchConverseMessageDto:
     * @return: java.util.List<com.linkhub.common.model.pojo.Message>
     * @author: winter
     * @date: 2023/8/16 上午12:22
     * @description:
     */
    List<MessageVo> fetchConverseMessage(FetchConverseMessageDto fetchConverseMessageDto);

    /**
     * 拉取某条消息前后的数据
     * @param fetchNearbyMessageDto:
     * @return: java.util.List<com.linkhub.common.model.vo.MessageVo>
     * @author: winter
     * @date: 2023/8/16 上午11:45
     * @description:
     */
    List<MessageVo> fetchNearbyMessage(FetchNearbyMessageDto fetchNearbyMessageDto);

    /**
    * 撤回消息
    */
    MessageVo recallMessage(Long messageId);

    /**
    * 删除消息 (群组管理员)
    */
    MessageVo deleteMessage(Long messageId);

    List<String> fetchConverseLastMessages(List<String> converseIds);

    boolean addReaction(ReactionDto reactionDto);

    boolean removeReaction(ReactionDto reactionDto);

    void sendSysMessage(SendMsgDto sendMsgDto);
}
