package com.linkhub.portal.im.handler;

import com.linkhub.portal.im.model.message.HeartbeatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 心跳处理器
 * @author winter
 * @create 2023-05-17 下午2:06
 */

@Component
@Slf4j
public class HeartbeatHandler implements MessageHandler<HeartbeatRequest>{
    private final static TextMessage PONG = new TextMessage("pong");
    @Override
    public void execute(WebSocketSession session, HeartbeatRequest message) {

        try {
            if ("ping".equals(message.getValue())) {
                session.sendMessage(PONG);
            }
        } catch (IOException e) {
            log.error("心跳发送异常 session: {}", session);
        }
    }

    @Override
    public String getType() {
        return HeartbeatRequest.TYPE;
    }
}
