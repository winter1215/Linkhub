package com.linkhub.portal.im.handler;

import com.corundumstudio.socketio.annotation.OnEvent;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.service.IConverseService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 事件监听器
 * @author winter
 * @create 2023-08-13 下午4:05
 */
@Component
public class DefaultEventListener {
    @Resource
    private IConverseService converseService;

    @OnEvent("chat.converse.findAndJoinRoom")
    public void findAndJoinRoom() {
        Set<String> converseIds = converseService.getUserAllConverseIds();
        IMUtil.joinRoom(converseIds);
    }
}
