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

    List<MessageVo> fetchConverseMessage(String converseId, Long startId, int limit);

    List<MessageVo> fetchNearbyMessage(String converseId, Long messageId, Integer num);

    List<String> selectConverseLastMessages(List<String> converseIds);
}
