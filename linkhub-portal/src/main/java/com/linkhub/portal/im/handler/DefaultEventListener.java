package com.linkhub.portal.im.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.service.IConverseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 事件监听器
 * @author winter
 * @create 2023-08-13 下午4:05
 */
@Component
@Slf4j
public class DefaultEventListener {
    @Resource
    private IConverseService converseService;

    @OnEvent("chat.converse.findAndJoinRoom")
    public void findAndJoinRoom(SocketIOClient client, Object data, AckRequest ackSender) {
        log.info("event listener");
        String userId = IMUtil.getUserIdByClient(client);
        Set<String> converseIds = converseService.getUserAllConverseIds(userId);
        IMUtil.joinRoom(converseIds);
    }
}
