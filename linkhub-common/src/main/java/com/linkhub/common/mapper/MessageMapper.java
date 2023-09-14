package com.linkhub.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkhub.common.model.pojo.Message;
import com.linkhub.common.model.vo.MessageVo;

import java.util.List;

/**
 * <p>
 * 聊天记录 Mapper 接口
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
public interface MessageMapper extends BaseMapper<Message> {

    MessageVo findMsgVoById(Long id);

    /**
     * startId 为空自动忽略
     * @return: java.util.List<com.linkhub.common.model.vo.MessageVo>
     * @author: winter
     * @date: 2023/9/13 下午5:34
     * @description:
     */
    List<MessageVo> fetchConverseMessage(String converseId, Long startId, int limit);

    List<MessageVo> fetchNearbyMessage(String converseId, Long messageId, Integer num);

    List<String> selectConverseLastMessages(List<String> converseIds);
}
