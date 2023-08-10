package com.linkhub.portal.im.handler;

import com.linkhub.portal.im.model.message.Message;
import org.springframework.web.socket.WebSocketSession;

/**
 * 消息处理器接口
 */
public interface MessageHandler<T extends Message> {

    /**
     * 执行处理消息
     *
     * @param session 会话
     * @param message 消息
     */
    void execute(WebSocketSession session, T message);

    /**
     * @return 消息类型，即每个 Message 实现类上的 TYPE 静态字段, 将 type: handler 放进 map 时会用到
     */
    String getType();

}
